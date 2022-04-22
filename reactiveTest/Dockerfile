FROM maven:3.6.3-adoptopenjdk-11 as BUILD_STAGE
COPY pom.xml .
RUN mvn -B -f pom.xml dependency:go-offline

COPY src ./src
RUN mvn -B clean package

FROM openjdk:11-jre-slim as RUN_STAGE
RUN mkdir -p "/reactiveTest"

COPY --from=BUILD_STAGE target/reactiveTest-0.0.1-SNAPSHOT.jar ../reactiveTest/reactiveTest.jar

EXPOSE 5000

ENTRYPOINT java -jar /reactiveTest/reactiveTest.jar