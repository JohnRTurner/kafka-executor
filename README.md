# Kafka Executor
## Description
This customizable load generator will create producers and consumers.
Features include controllable parallel execution of different workloads.
There is an easy-to-use Swagger front end that can be used to integrate
with other programming languages such as React, Angular, Python...
The intention is to use JMeter or even siege to run and monitor the tests
using simple http calls.

## Architecture
![Architecture](architecture.png)

## Terraform


## OpenAPI - Swagger v3
### How to access Swagger
* The GUI interface for Swagger:
http://127.0.0.1:8080/api/swagger-ui/index.html#/ 
  * Note: change the ip for above swagger addresses with your server
* The swagger document is here http://127.0.0.1:8080/api/v3/api-docs
  * Note: change the ip for above swagger addresses with your server
* Auto-generating a programmatic interface based upon Swagger: https://github.com/swagger-api/swagger-codegen?tab=readme-ov-file#overview
* An easy client to generate a quick load against the API is siege installable by
  * Source Code: https://github.com/JoeDog/siege/
  * MAC: `brew install siege`
  * Linux: `sudo apt-get install siege`
  * Windows: download https://siege-windows.googlecode.com/svn/trunk/siege-windows-3.0.5.zip then unzip...
* A client with better metrics reporting is JMeter.  See JMeter directory for more details.
### Swagger Documentation also known as OpenAPI
https://swagger.io/docs/specification/about/
