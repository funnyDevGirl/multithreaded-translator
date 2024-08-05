package io.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupportedLanguagesResponse {

    @JsonProperty("languages")
    private List<Language> languages;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @EqualsAndHashCode
    public static class Language {

        @JsonProperty("code")
        private String code;

        @JsonProperty("name")
        private String name;


        @Override
        public String toString() {
            return "Language{"
                    + "code='" + code + '\''
                    + ", name='" + name + '\''
                    + '}';
        }
    }

    @Override
    public String toString() {
        return "SupportedLanguagesResponse{"
                + "languages=" + languages
                + '}';
    }
}
