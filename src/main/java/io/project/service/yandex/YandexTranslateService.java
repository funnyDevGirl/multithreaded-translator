package io.project.service.yandex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.project.exception.LanguageNotFoundException;
import io.project.exception.TranslationResourceAccessException;
import io.project.model.SupportedLanguagesResponse;
import io.project.model.SupportedLanguagesResponse.Language;

import io.project.model.Translation;
import io.project.repository.TranslationRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private static final String TRANSLATE_URL = "https://translate.api.cloud.yandex.net/translate/v2/translate";
    private static final String API_URL = "https://translate.api.cloud.yandex.net/translate/v2/languages";
    private static final String FOLDER_ID = "YOUR_FOLDER_ID";

    private final RestTemplate restTemplate;
    private final TranslationRepository repository;
    private static final int MAX_THREADS = 10;
    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

    public YandexTranslateService(RestTemplate restTemplate,
                                  TranslationRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }


    public String translate(String inputText, String sourceLang, String targetLang, String ipAddress) {
        // validation
        List<Language> supportedLanguages = fetchSupportedLanguages();

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

    public String translateWord(String word, String sourceLang, String targetLang) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        String requestBody = String.format(
                "{\"sourceLanguageCode\":\"%s\",\"targetLanguageCode\":\"%s\",\"texts\":[\"%s\"]}",
                sourceLang, targetLang, word);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                TRANSLATE_URL, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return parseTranslatedWord(response.getBody());
        } else {
            throw new TranslationResourceAccessException("Ошибка доступа к ресурсу перевода"
                    + response.getStatusCode());
        }
    }

    private String parseTranslatedWord(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode translationsNode = rootNode.path("translations");

            if (translationsNode.isArray() && !translationsNode.isEmpty()) {
                return translationsNode.get(0).path("text").asText();
            } else {
                throw new TranslationResourceAccessException(
                        "Ошибка доступа к ресурсу перевода: неверный формат ответа");
            }
        } catch (Exception e) {
            throw new TranslationResourceAccessException("Ошибка доступа к ресурсу перевода: "
                    + e.getMessage());
        }
    }

    private boolean isContainsLang(String lang, List<Language> languages) {
        return languages.stream().noneMatch(l -> l.getCode().equals(lang));
    }

    public List<Language> fetchSupportedLanguages() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<SupportedLanguagesResponse> response = restTemplate.exchange(
                API_URL, HttpMethod.GET, entity, SupportedLanguagesResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(response.getBody()).getLanguages();
        } else {
            throw new RuntimeException("Не удалось получить список поддерживаемых языков: "
                    + response.getStatusCode());
        }
    }
}
