FROM eclipse-temurin:11.0.19_7-jre-jammy
VOLUME /app
ENV VAULT_TOKEN=""
COPY build/libs/utility-scheduler-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Dvault.token=${VAULT_TOKEN}", "-jar", "app.jar"]