FROM openjdk:21-slim AS builder
WORKDIR /app
RUN apt-get update && apt-get install -y findutils
COPY ./techeerzip/gradlew .
COPY ./techeerzip/gradle gradle
COPY ./techeerzip/build.gradle .
COPY ./techeerzip/settings.gradle .
COPY ./techeerzip/src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJAR

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
