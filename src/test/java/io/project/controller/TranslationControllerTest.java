package io.project.controller;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import io.project.dto.TranslationRequestDTO;
//import io.project.dto.TranslationResponseDTO;
//import io.project.model.Translation;
//import io.project.repository.TranslationRepository;
//import io.project.service.TranslationService;
//import io.project.service.yandex.YandexTranslateService;
//import io.project.util.ModelGenerator;
//import org.instancio.Instancio;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.http.MediaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.mockito.ArgumentMatchers.anyString;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class TranslationControllerTest {
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @MockBean
//    private YandexTranslateService translationService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper om;
//
//    @Autowired
//    private TranslationRepository translationRepository;
//
//    @Autowired
//    private ModelGenerator modelGenerator;
//
//    private Translation testTranslation;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//        testTranslation = Instancio.of(modelGenerator.getTranslationModel()).create();
//
//    }
//
//    @AfterEach
//    public void clean() {
//        translationRepository.deleteAll();
//    }
//
//    //    @Test
////    public void testSuccessfulTranslation() throws Exception {
////        mockMvc.perform(post("/translate")
////                        .param("text", "Hello world, this is my first program")
////                        .param("sourceLang", "en")
////                        .param("targetLang", "ru"))
////                .andExpect(status().isOk())
////                .andExpect(content().string("Привет мир, это является моей первой программой"));
////    }
////
////    @Test
////    public void testTranslationResourceAccessException() throws Exception {
////        mockMvc.perform(post("/translate")
////                        .param("text", "Hello world, this is my first program")
////                        .param("sourceLang", "en")
////                        .param("targetLang", "ru"))
////                .andExpect(status().isBadRequest())
////                .andExpect(content().string("Ошибка доступа к ресурсу перевода"));
////    }
//
//
//    @Test
//    public void testSuccessfulTranslation() throws Exception {
//        String requestBody = "{\"inputText\":\"Hello world\",\"sourceLang\":\"en\",\"targetLang\":\"ru\"}";
////        TranslationRequestDTO dto = new TranslationRequestDTO();
////        dto.setInputText("Hello world");
////        dto.setTargetLanguage("ru");
////        dto.setSourceLanguage("en");
//
//        mockMvc.perform(post("/translate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(requestBody)))
//                        //.content(om.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(content().json("{\"translatedText\":\"Привет мир\"}"));
//        //Assert
//    }
//
//    @Test
//    public void testTranslate() throws Exception {
//        TranslationRequestDTO request = new TranslationRequestDTO();
//        request.setSourceLanguage("en");
//        request.setTargetLanguage("ru");
//        request.setInputText("Hello world");
//
//        TranslationResponseDTO response = new TranslationResponseDTO();
//        response.setTranslatedText("Привет мир");
//
//        when(translationService.translate(anyString(), anyString(), anyString(), anyString()))
//                .thenReturn("Привет мир");
//
//        mockMvc.perform(post("/translate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request))
//                        .header("X-Forwarded-For", "127.0.0.1"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(new ObjectMapper().writeValueAsString(response)));
//    }
//
//    @Test
//    public void testTranslateInvalidLanguage() throws Exception {
//        TranslationRequestDTO request = new TranslationRequestDTO();
//        request.setSourceLanguage("invalid");
//        request.setTargetLanguage("ru");
//        request.setInputText("Hello world");
//
//        when(translationService.translate(anyString(), anyString(), anyString(), anyString()))
//                .thenThrow(new IllegalArgumentException("Не найден язык исходного сообщения"));
//
//        mockMvc.perform(post("/translate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request))
//                        .header("X-Forwarded-For", "127.0.0.1"))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Не найден язык исходного сообщения"));
//    }
//
//    @Test
//    public void testTranslateExternalServiceError() throws Exception {
//        TranslationRequestDTO request = new TranslationRequestDTO();
//        request.setSourceLanguage("en");
//        request.setTargetLanguage("ru");
//        request.setInputText("Hello world");
//
//        when(translationService.translate(anyString(), anyString(), anyString(), anyString()))
//                .thenThrow(new RuntimeException("Ошибка доступа к ресурсу перевода"));
//
//        mockMvc.perform(post("/translate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request))
//                        .header("X-Forwarded-For", "127.0.0.1"))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Ошибка доступа к ресурсу перевода"));
//    }
//}
