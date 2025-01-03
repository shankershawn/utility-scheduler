FROM --platform=linux/arm64 eclipse-temurin:17 AS build
COPY . .
ARG RUN_NUMBER
ARG ARTIFACTORY_URL
ARG ARTIFACTORY_USERNAME
ARG ARTIFACTORY_PASSWORD
ARG SSL_KEYSTORE_BASE64
ENV RUN_NUMBER=$RUN_NUMBER
ENV ARTIFACTORY_URL=$ARTIFACTORY_URL
ENV ARTIFACTORY_USERNAME=$ARTIFACTORY_USERNAME
ENV ARTIFACTORY_PASSWORD=$ARTIFACTORY_PASSWORD
RUN echo -n $SSL_KEYSTORE_BASE64 | base64 -d > src/main/resources/keystore.jks
RUN ./gradlew clean assemble

FROM eclipse-temurin:17-jre AS app
ARG RUN_NUMBER
ENV JAVA_OPTS="--add-opens=java.base/java.util.concurrent=ALL-UNNAMED"
COPY --from=build ./build/libs/utility-scheduler-1.0.${RUN_NUMBER}.jar app.jar
ENTRYPOINT java ${JAVA_OPTS} -jar app.jar