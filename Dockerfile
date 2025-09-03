# Spring Boot (Gradle) build + runtime for Auth service
FROM gradle:8.8-jdk21-alpine AS build
WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

