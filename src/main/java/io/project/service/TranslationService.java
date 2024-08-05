package io.project.service;
//
//import io.project.exception.LanguageNotFoundException;
//import io.project.exception.TranslationResourceAccessException;
//import io.project.model.SupportedLanguagesResponse;
//import io.project.repository.TranslationRepository;
//import io.project.model.Translation;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.net.URL;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Set;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//
//@Service
//public class TranslationService {
//    private static final String API_KEY = "YOUR_API_KEY";
//    // Google
//    private static final String TRANSLATE_URL =
//            "https://api.translation.service/translate?text={text}&source={source}&target={target}";
//    // Yandex
////    private static final String TRANSLATE_URL =
////            "https://translate.yandex.net/api/v1.5/tr.json/translate?key={key}&text={text}&lang={lang}";
//
//    private static final int MAX_THREADS = 10;
//    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
//    private final RestTemplate restTemplate;
//    private final TranslationRepository repository;
//
//    public TranslationService(RestTemplate restTemplate,
//                              TranslationRepository repository) {
//        this.restTemplate = restTemplate;
//        this.repository = repository;
//    }
//
//
//    public String translate(String inputText, String sourceLang, String targetLang, String ipAddress) {
//        // Валидация на уровне сервиса, запрос ко внешнему API для получения списка поддерживаемых языков:
//        if (!supportedLanguages.contains(sourceLang)) {
//            throw new LanguageNotFoundException("Не найден исходный язык: " + sourceLang);
//        }
//        if (!supportedLanguages.contains(targetLang)) {
//            throw new LanguageNotFoundException("Не найден целевой язык: " + targetLang);
//        }
//
//        List<CompletableFuture<String>> translationFutures = new ArrayList<>();
//
//        // translate words
//        for (String word : inputText.split(" ")) {
//            CompletableFuture<String> future = CompletableFuture.supplyAsync(
//                    () -> translateWord(word, sourceLang, targetLang), executor);
//            translationFutures.add(future);
//        }
//
//        // formation of the translated text
//        String finishedText = translationFutures.stream()
//                .map(CompletableFuture::join)
//                .collect(Collectors.joining(" "));
//
//        // save to DB
//        Translation request = new Translation();
//        request.setIpAddress(ipAddress);
//        request.setInputText(inputText);
//        request.setTranslatedText(finishedText);
//        repository.save(request);
//
//        executor.shutdown();
//        return finishedText;
//    }
//
//    // РЕАКТИВНЫЙ ПОДХОД
///*    public Mono<String> translate(String inputText, String sourceLang, String targetLang, String ipAddress) {
//            return Flux.fromArray(inputText.split(" "))
//                    .parallel()
//                    .runOn(Schedulers.elastic())
//                    .map(word -> translateWord(word, sourceLang, targetLang))
//                    .sequential()
//                    .collectList()
//                    .map(translations -> String.join(" ", translations))
//                    .doOnSuccess(finishedText -> {
//                        Translation request = new Translation();
//                        request.setIpAddress(ipAddress);
//                        request.setInputText(inputText);
//                        request.setTranslatedText(finishedText);
//                        repository.save(request);
//                    });
//        }*/
//
//    private String translateWord(String word, String sourceLang, String targetLang) {
//
//        // Вызов API Яндекса для перевода слова
////        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=YOUR_API_KEY&text="
////        + word + "&lang=" + sourceLang + "-" + targetLang;
////        String url = TRANSLATE_URL.replace("{key}", API_KEY)
////                .replace("{text}", word)
////                .replace("{lang}", sourceLang + "-" + targetLang);
//
//        // calling the Google translation service using RestTemplate
//        String url = "https://api.translation.service/translate?text={text}&source={source}&target={target}";
//
//        try {
//            return restTemplate.getForObject(url, String.class, word, sourceLang, targetLang);
//        } catch (RestClientException e) {
//            throw new TranslationResourceAccessException("Ошибка доступа к ресурсу перевода");
//        }
//    }
//
//
////    private Set<String> fetchSupportedLanguages() {
////        // Запрос к Yandex для получения списка поддерживаемых языков
////        String urlString = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" + API_KEY + "&ui=en";
////
////        SupportedLanguagesResponse response = restTemplate.getForObject(apiUrl, SupportedLanguagesResponse.class);
////
////        Set<String> languages = new HashSet<>();
////
////        try {
////            URL url = new URI(urlString).toURL();
////            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
////            connection.setRequestMethod("GET");
////
////
////            int responseCode = connection.getResponseCode();
////            if (responseCode == HttpURLConnection.HTTP_OK) {
////                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
////                String inputLine;
////                StringBuilder response = new StringBuilder();
////
////                while ((inputLine = in.readLine()) != null) {
////                    response.append(inputLine);
////                }
////                in.close();
////
////                // Парсинг JSON-ответа
////                JSONObject jsonResponse = new JSONObject(response.toString());
////                JSONObject langs = jsonResponse.getJSONObject("langs");
////
////                // Создание Set для хранения поддерживаемых языков
////                Iterator<String> keys = langs.keys();
////                while (keys.hasNext()) {
////                    String langCode = keys.next();
////                    String langName = langs.getString(langCode);
////                    languages.add(langCode + ": " + langName);
////                }
////
////            } else {
////                System.out.println("Ошибка: " + responseCode);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return languages;
////    }
//}
