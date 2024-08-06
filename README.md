# Multithreaded-translator
### This is a Java web application for translating a set of words into another language using the Yandex translation service.

### Maintainability and Test Coverage
[![Maintainability](https://api.codeclimate.com/v1/badges/b4b965c1264757dab0ee/maintainability)](https://codeclimate.com/github/funnyDevGirl/multithreaded-translator/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/b4b965c1264757dab0ee/test_coverage)](https://codeclimate.com/github/funnyDevGirl/multithreaded-translator/test_coverage)
---
### Start the application
Requirements:
* JDK 21
* Gradle

To run the program, run: ```make dev```

Compile the code: ```make install```

---
### Authentication in the Translate API
To work with the Translate API, you need to get an IAM token for your Yandex account, which is required for authentication.

You can read the [instructions for getting the key on the Yandex website](https://yandex.cloud/ru/docs/translate/api-ref/authentication).

Pass the received token in the Authorization header of each request in the format:

```
Authorization: Bearer <IAM-токен>
```

##### Set up an API connection:
Get the key and enter it instead of "YOUR_API_KEY" in the application.yml file:
```yaml
api-key: "YOUR_API_KEY"
translate-url: "https://translate.api.cloud.yandex.net/translate/v2/translate"
api-url: "https://translate.api.cloud.yandex.net/translate/v2/languages"
```
---
### Methods for the Yandex Translate service
* ```fetchSupportedLanguages()``` - Retrieves the list of supported languages:

HTTP-запрос
```
POST https://translate.api.cloud.yandex.net/translate/v2/languages
```

Ответ: HTTP Code: 200 - OK
```
{
  "languages": [
    {
      "code": "string",
      "name": "string"
    }
  ]
}
```

* ```translate()``` - Translates the text into the specified language:

HTTP-запрос
```
POST https://translate.api.cloud.yandex.net/translate/v2/translate
```

```
{
  "sourceLanguageCode": "string",
  "targetLanguageCode": "string",
  "texts": [
    "string"
  ]
}
```

Ответ: HTTP Code: 200 - OK
```
{
  "translations": [
    {
      "text": "string"
    }
  ]
}
```
