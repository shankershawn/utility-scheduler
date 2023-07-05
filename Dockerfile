FROM eclipse-temurin:11-jdk-alpine
VOLUME /app
ARG JAR_FILE
ENV VAULT_TOKEN=""
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dvault.token=${VAULT_TOKEN}", "-jar", "app.jar"]