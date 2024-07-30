FROM maven:3.9-amazoncorretto-21-debian AS build
LABEL authors="john.turner"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21

ARG CERT_PASS

RUN yum install -y openssl11 && \
    ln -s /usr/bin/openssl11 /usr/bin/openssl && \
    yum clean all

WORKDIR /certs
COPY ./certs/* /certs/
RUN echo "CERT_PASS is ${CERT_PASS}"

# Create PKCS12 keystore
RUN if [[ -n $CERT_PASS ]]; then openssl pkcs12 -export \
    -inkey service.key \
    -in service.cert \
    -out client.keystore.p12 \
    -name service_key \
    -passout pass:${CERT_PASS} ; fi

# Import CA certificate into truststore
RUN if [[ -n $CERT_PASS ]]; then keytool -import \
    -file ca.pem \
    -alias CA \
    -keystore client.truststore.jks \
    -storepass ${CERT_PASS} \
    -noprompt ; fi

RUN chown -R 1000:1000 /certs

WORKDIR /app
COPY --from=build /app/target/kafka_executor-0.0.1-SNAPSHOT.jar ./kafka_executor.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "kafka_executor.jar"]