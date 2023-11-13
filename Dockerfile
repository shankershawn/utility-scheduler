FROM eclipse-temurin:11.0.19_7-jre-jammy
VOLUME /app
ENV VAULT_TOKEN=""
ARG GITHUB_SHA
COPY build/libs/utility-scheduler-1.0.0_$GITHUB_SHA.jar app.jar
ENTRYPOINT ["java", "-Dvault.token=${VAULT_TOKEN}", "-jar", "app.jar"]