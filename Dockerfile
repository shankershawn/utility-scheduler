FROM eclipse-temurin:11.0.19_7-jre-jammy
VOLUME /app
ARG GITHUB_SHA
COPY build/libs/utility-scheduler-1.0.0_$GITHUB_SHA.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]