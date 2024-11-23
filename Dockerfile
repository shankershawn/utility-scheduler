FROM eclipse-temurin:17.0.11_9-jre as build
COPY . .
ARG GITHUB_SHA
ARG RUN_NUMBER
ENV JAVA_OPTS="--add-opens=java.base/java.util.concurrent=ALL-UNNAMED"
RUN ls -lart
RUN ./gradlew clean assemble
COPY ./build/libs/utility-scheduler-1.0.${RUN_NUMBER}_${GITHUB_SHA}.jar app.jar
ENTRYPOINT java ${JAVA_OPTS} -jar app.jar