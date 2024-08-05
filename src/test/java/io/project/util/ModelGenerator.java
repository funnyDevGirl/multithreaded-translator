package io.project.util;

import io.project.model.Translation;
import lombok.Getter;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import java.net.UnknownHostException;

@Getter
@Component
public class ModelGenerator {

    private final Faker faker;
    private Model<Translation> translationModel;

    @Autowired
    public ModelGenerator(Faker faker) {
        this.faker = faker;
    }


    @PostConstruct
    private void init() {

        translationModel = Instancio.of(Translation.class)
                .ignore(Select.field(Translation::getId))
                .supply(Select.field(Translation::getIpAddress), () -> {
                    try {
                        return faker.internet().getIpV4Address().getHostAddress();
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                })
                .supply(Select.field(Translation::getInputText), () -> faker.lorem().sentence())
                .ignore(Select.field(Translation::getTranslatedText))
                .toModel();
    }
}
