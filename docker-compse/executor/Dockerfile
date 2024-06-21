FROM maven:3.9-amazoncorretto-21-debian AS build
LABEL authors="john.turner"

WORKDIR=/app
COPY ../../pom.xml .
COPY ../../src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21

WORKDIR=/certs
COPY ../../certs/* .

WORKDIR=/app
COPY --from=build /app/target/kafka_executor-0.0.1-SNAPSHOT.jar ./kafka_executor.jar

EXPOSE 8080

ENTRYPOINT java -jar kafka_executor.jar