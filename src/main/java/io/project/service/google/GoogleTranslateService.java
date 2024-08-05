package io.project.service.google;

import io.project.exception.LanguageNotFoundException;
import io.project.exception.TranslationResourceAccessException;
import io.project.model.Translation;
import io.project.repository.TranslationRepository;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class GoogleTranslateService {

    private static final String API_KEY = "YOUR_API_KEY";
    private static final String API_URL
            = "https://translation.googleapis.com/language/translate/v2/languages?key=" + API_KEY;

    private final RestTemplate restTemplate;
    private final TranslationRepository repository;
    private static final int MAX_THREADS = 10;
    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

    public GoogleTranslateService(RestTemplate restTemplate,
                                  TranslationRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

//    public GoogleLanguagesResponse getSupportedLanguages() {
//        return restTemplate.getForObject(API_URL, GoogleLanguagesResponse.class);
//    }

    public String translate(String inputText,
                            String sourceLang,
                            String targetLang,
                            String ipAddress) throws Exception {
        // validation
        var supportedLanguages = fetchSupportedLanguages();
        if (!supportedLanguages.contains(sourceLang)) {
            throw new LanguageNotFoundException("Не найден исходный язык: " + sourceLang);
        }
        if (!supportedLanguages.contains(targetLang)) {
            throw new LanguageNotFoundException("Не найден целевой язык: " + targetLang);
        }

        List<CompletableFuture<String>> translationFutures = new ArrayList<>();

        // translate words
        for (String word : inputText.split(" ")) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(
                    () -> translateWord(word, sourceLang, targetLang), executor);
            translationFutures.add(future);
        }

        // formation of the translated text
        String finishedText = translationFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        // save to DB
        Translation request = new Translation();
        request.setIpAddress(ipAddress);
        request.setInputText(inputText);
        request.setTranslatedText(finishedText);
        repository.save(request);

        executor.shutdown();
        return finishedText;
    }

    private String translateWord(String word, String sourceLang, String targetLang) {
        // calling the Google translation service using RestTemplate
        String url = "https://api.translation.service/translate?text={text}&source={source}&target={target}";

        try {
            return restTemplate.getForObject(url, String.class, word, sourceLang, targetLang);
        } catch (RestClientException e) {
            throw new TranslationResourceAccessException("Ошибка доступа к ресурсу перевода");
        }
    }

        private boolean isValid(String lang, Set<String> languages) {
        return languages.contains(lang);
    }

    public Set<String> fetchSupportedLanguages() throws Exception {
        Set<String> languages = new HashSet<>();

        String response = restTemplate.getForObject(API_URL, String.class);
        if (response != null) {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject data = jsonResponse.getJSONObject("data");
            JSONArray languageArray = data.getJSONArray("languages");

            for (int i = 0; i < languageArray.length(); i++) {
                JSONObject languageObject = languageArray.getJSONObject(i);
                String languageCode = languageObject.getString("language");
                languages.add(languageCode);
            }
        }
        return languages;
    }
}
