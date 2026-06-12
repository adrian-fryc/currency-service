FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy plik JAR, który GitHub Actions zbuduje i prześle nam do folderu target
COPY target/*.jar app.jar

# Port, na którym faktycznie słucha Twój Spring Boot (widzę, że u Ciebie to 8082)
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]