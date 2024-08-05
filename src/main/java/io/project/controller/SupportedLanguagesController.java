package io.project.controller;

import io.project.dto.TranslationRequestDTO;
import io.project.dto.TranslationResponseDTO;
import io.project.model.SupportedLanguagesResponse.Language;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import io.project.service.yandex.YandexTranslateService;
import java.util.List;


@RestController
@RequestMapping
@AllArgsConstructor
public class SupportedLanguagesController {

    private final YandexTranslateService translationService;

    @PostMapping("/translate")
    public ResponseEntity<TranslationResponseDTO> translate(@RequestBody TranslationRequestDTO requestDTO,
                                                            //@RequestAttribute HttpServletRequest request,
                                                            @RequestHeader("X-Forwarded-For") String ipAddress) {
        //String ipAddress = request.getRemoteAddr();

        String translatedText = translationService.translate(requestDTO.getInputText(),
                                                            requestDTO.getSourceLanguage(),
                                                            requestDTO.getTargetLanguage(),
                                                            ipAddress);

        TranslationResponseDTO responseDTO = new TranslationResponseDTO();
        responseDTO.setTranslatedText(translatedText);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/supported-languages")
    public List<Language> getSupportedLanguages() {
        return translationService.fetchSupportedLanguages();
    }
}
