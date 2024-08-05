package io.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SupportedLanguagesResponse {

    @JsonProperty("languages")
    private List<Language> languages;

    @Getter
    @Setter
    public static class Language {

        @JsonProperty("code")
        private String code;

        @JsonProperty("name")
        private String name;
    }
}
