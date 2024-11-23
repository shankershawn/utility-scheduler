FROM eclipse-temurin:17-jdk-jammy AS build
COPY . .
ARG GITHUB_SHA
ARG RUN_NUMBER
ENV GITHUB_VERSION=$GITHUB_SHA
ENV RUN_NUMBER=$RUN_NUMBER
RUN ./gradlew clean assemble

FROM eclipse-temurin:17.0.11_9-jre AS app
ARG GITHUB_SHA
ARG RUN_NUMBER
ENV JAVA_OPTS="--add-opens=java.base/java.util.concurrent=ALL-UNNAMED"
COPY --from=build ./build/libs/utility-scheduler-1.0.${RUN_NUMBER}_${GITHUB_SHA}.jar app.jar
ENTRYPOINT ["java", "${JAVA_OPTS}", "-jar", "app.jar"]