package io.project.model.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleLanguagesResponse {

    private Set<Language> languages;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Language {
        private String language;
        private String name;

        @Override
        public String toString() {
            return "Language{"
                    + "language='" + language + '\''
                    + ", name='" + name + '\''
                    + '}';
        }
    }

    @Override
    public String toString() {
        return "GoogleLanguagesResponse{"
                + "languages=" + languages
                + '}';
    }
}
