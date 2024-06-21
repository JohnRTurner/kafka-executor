# Please add cert files here

Require client.keystore.p12 and client.truststore.jks

## Steps to generate files:
1. Download service.key and service.cert from the dashboard.
2. Create with the following commands, noting the passwords for the next step.
```shell
openssl pkcs12 -export -inkey service.key -in service.cert -out client.keystore.p12 -name service_key -password pass:test1234
keytool -import -file ca.pem -alias CA -keystore client.truststore.jks -keypass pass:test1234 -storepass pass:test1234 -noprompt  
```
3.Update the application.properties files in the src/resources directory.  If you don't have one, copy 
application.properties from the same directory and update the locations and passwords.