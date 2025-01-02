FROM openjdk:17-jdk-slim
EXPOSE 8080
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} open-chat.jar

ENTRYPOINT ["java","-jar","/open-chat.jar"]
