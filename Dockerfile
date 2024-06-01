FROM eclipse-temurin:17.0.11_9-jre
VOLUME /app
ARG GITHUB_SHA
ENV JAVA_OPTS="--add-opens=java.base/java.util.concurrent=ALL-UNNAMED"
COPY build/libs/utility-scheduler-1.0.0_$GITHUB_SHA.jar app.jar
ENTRYPOINT ["java", "${JAVA_OPTS}", "-jar", "app.jar"]