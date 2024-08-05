package io.project.service.yandex;

import io.project.exception.LanguageNotFoundException;
import io.project.exception.TranslationResourceAccessException;
import io.project.model.SupportedLanguagesResponse;
import io.project.model.SupportedLanguagesResponse.Language;

import io.project.model.Translation;
import io.project.repository.TranslationRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Service
public class YandexTranslateService {

    private static final String API_KEY = "YOUR_API_KEY"; // Если требуется авторизация
    private static final String API_URL = "https://translate.api.cloud.yandex.net/translate/v2/languages";
    private static final String FOLDER_ID = "YOUR_FOLDER_ID";


    private final RestTemplate restTemplate;
    private final TranslationRepository repository;
    private static final int MAX_THREADS = 10;
    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

    private final List<Language> supportedLanguages = fetchSupportedLanguages();;

    public YandexTranslateService(RestTemplate restTemplate,
                                  TranslationRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }


    public String translate(String inputText, String sourceLang, String targetLang, String ipAddress) {
        // Validation
        if (isContainsLang(sourceLang, supportedLanguages)) {
            throw new LanguageNotFoundException("Не найден исходный язык: " + sourceLang);
        }
        if (isContainsLang(targetLang, supportedLanguages)) {
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

    private boolean isContainsLang(String lang, List<Language> languages) {
        return languages.stream().noneMatch(l -> l.getCode().equals(lang));
    }

    public List<Language> fetchSupportedLanguages() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создание тела запроса
        String requestBody = "{\"folderId\": \"" + FOLDER_ID + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<SupportedLanguagesResponse> responseEntity =
                restTemplate.postForEntity(API_URL, requestEntity, SupportedLanguagesResponse.class);
        //       restTemplate.getForObject(API_URL, SupportedLanguagesResponse.class);

        return Objects.requireNonNull(responseEntity.getBody()).getLanguages();
    }

// другой способ:
//    public List<Language> fetchSupportedLanguages() throws IOException, InterruptedException, URISyntaxException {
//        HttpClient client = HttpClient.newHttpClient();
//
//        // Тело запроса
//        String requestBody = new ObjectMapper().writeValueAsString(Map.of("folderId", FOLDER_ID));
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(API_URL))
//                .header("Content-Type", "application/json")
//                //.header("Authorization", "Bearer " + API_KEY) // Если требуется авторизация
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        SupportedLanguagesResponse supportedLanguagesResponse = new SupportedLanguagesResponse();
//        if (response.statusCode() == 200) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            supportedLanguagesResponse = objectMapper.readValue(response.body(), SupportedLanguagesResponse.class);
//        } else {
//            System.err.println("Error: " + response.statusCode() + " - " + response.body());
//        }
//        return supportedLanguagesResponse.getLanguages();
//    }
}
