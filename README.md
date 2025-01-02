# AI Chat Assistant App Server

Welcome to the server part of the [AI Chat Assistant App](https://github.com/OpenFuturePlatform/OpenAiX)

## How to run

### Prepare environment
1. Run Kurento Media Server
```shell
cd ./run-kurento-media-server.sh
```
2. Run Postgres
```shell
cd docker-compose && docker-compose -f docker-compose.yaml up -d
```
The server app utilizes external cloud services such as AWS S3, AWS Transcribe, AWS Cognito, Google Gemini, Google Firebase
and internal service Open State therefore there are keys and urls for every of these services need
to be configured in order to run the app.
Those keys are configured in application.yaml of the Spring Boot.
```yaml

aws:
  access-key: [AWS access key]
  secret-key: [AWS secret key]
  region: [region]
  attachments-bucket: [attachments-bucket]
  recordings-bucket: [recordings-bucket]
  transcripts-bucket: [transcripts-bucket]
  cognito:
    user-pool-id: [user-pool-id]
    app-client-id: [app-client-id]
    app-client-secret: [app-client-secret]

jwks:
  url: [jwks key provided by AWS cognito]

gemini:
  api:
    key: [Gemini API key]
    url: [URL to a model]

kms:
  url: ws://127.0.0.1:8888/kurento

# STATE
state:
  base-url: ${OPEN_STATE_URL:http://localhost:8545/api}

# FIREBASE
app:
  firebase-configuration-file: [json configuration file]
```

## Technologies Used

1. Java 17
2. Kotlin 2.0.0
3. Spring Boot 3.2.3
4. Gradle 7.6
5. PostgreSQL 17
6. Websocket
7. AWS: Cognito(1.12.681), S3(1.12.687), Transcribe(1.12.761)
8. Kurento 7.1.0
9. Google Firebase 9.2.0
10. Docker
11. Google Gemini


## Contributing

We welcome contributions from the community! If you'd like to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -am 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a pull request.

Please ensure that your pull request follows the repository's code style and guidelines.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.


Feel free to customize and expand upon this template to better fit your project's specific details and requirements!
