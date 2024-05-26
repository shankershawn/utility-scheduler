FROM eclipse-temurin:eclipse-temurin:17.0.11_9-jre
VOLUME /app
ARG GITHUB_SHA
COPY build/libs/utility-scheduler-1.0.0_$GITHUB_SHA.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]