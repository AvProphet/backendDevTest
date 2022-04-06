FROM maven:3.8.4-openjdk-11 as BUILD_STAGE
COPY pom.xml .
RUN mvn -B -f pom.xml dependency:go-offline

COPY src ./src
RUN mvn -B clean package

FROM openjdk:11-jre-slim as RUN_STAGE
RUN mkdir -p "/backtest"

COPY --from=BUILD_STAGE target/backtest-0.0.1-SNAPSHOT.jar ../backtest/backtest.jar

EXPOSE 5000

ENTRYPOINT java -jar /backtest/backtest.jar