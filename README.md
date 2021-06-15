# OutreachByWeather
Determine outreach to people for a cities 5-day forecast
Outreach Channels Email, SMS, IVR, UNKNOWN

# Technologies
* Framework: Spring Boot
* Language: Kotlin
* Build Tool: Gradle
* Linting: ktlint
* Unit tests: kotest
* Integration tests: Spring test

# TODO: 
* Swagger
* Caching to Open weather
* Web UI
* Migration script to load valid city, state code and country code in DB. Use this for validation on the request
* Use Github Actions to create a CI/CD pipeline
* Handle Open Weather api key. local application properties file is git ignore. Pass the api key in the pipeline through secure environment variable.
* Automatic code coverage in pipeline
