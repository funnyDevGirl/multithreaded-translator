package io.project.controller;

import io.project.dto.TranslationRequestDTO;
import io.project.dto.TranslationResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.project.service.yandex.YandexTranslateService;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/translate")
@AllArgsConstructor
public class SupportedLanguagesController {

    private final YandexTranslateService translationService;


    @PostMapping
    public ResponseEntity<TranslationResponseDTO> translate(@RequestBody TranslationRequestDTO requestDTO,
                                                            @RequestAttribute HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        String translatedText = translationService.translate

                (requestDTO.getInputText(),
                        requestDTO.getSourceLanguage(),
                        requestDTO.getTargetLanguage(),
                        ipAddress);

        TranslationResponseDTO responseDTO = new TranslationResponseDTO();
        responseDTO.setTranslatedText(translatedText);
        return ResponseEntity.ok(responseDTO);
    }
}
