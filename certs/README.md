# Please add cert files here

Require client.keystore.p12 and client.truststore.jks

## Steps to generate files:
1. Download service.key and service.cert from the dashboard.
2. Create with the following commands, noting the passwords for the next step.
```shell
# Set CERT_PASS
export CERT_PASS=YourPasswordHere

# Create PKCS12 keystore
RUN openssl pkcs12 -export \
    -inkey service.key \
    -in service.cert \
    -out client.keystore.p12 \
    -name service_key \
    -passout pass:${CERT_PASS}

# Import CA certificate into truststore
RUN keytool -import \
    -file ca.pem \
    -alias CA \
    -keystore client.truststore.jks \
    -storepass ${CERT_PASS} \
    -noprompt
```
3.Update the application.properties files in the src/resources directory.  If you don't have one, copy 
application.properties from the same directory and update the locations and passwords.