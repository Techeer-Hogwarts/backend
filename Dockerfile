FROM openjdk:21-slim AS builder
WORKDIR /app
RUN apt-get update && apt-get install -y findutils
COPY ./techeerzip/ /app/
RUN chmod +x ./gradlew
RUN ./gradlew clean spotlessApply bootJar --no-daemon -x test

FROM gcr.io/distroless/java21-debian12
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]