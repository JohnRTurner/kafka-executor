# Terraform

## Creates a Kafka Cluster, Thanos, Grafana, and a AppServer(s)

### Setup variables
* copy terraform.tfvars.sample to terraform.tfvars
* update terrform.tfvars with the relevant data
### Build
* `terraform init`
* `terraform apply -auto-approve`
*  The kafka_executors will build for a few minutes after terraform finishes.
### Connect
* Connect to the application servers using `terraform output dataGeneratorURL`
  * The username and password are in terraform.tfvars, and seen via `terraform output dataGeneratorUserPass`
* Can connect to Grafana through the application or through the Aiven console.


## Terraform commands
The commands must be issued inside the Terraform directory

While Thanos is in Beta please execute the following for Terraform... `export PROVIDER_AIVEN_ENABLE_BETA=true`

* `terraform init` - Used to initialize the Terraform client
* `terraform plan` - Used to check what Terraform plans to do if applied
* `terraform apply -auto-approve` - Used to execute the Terraform plan
* `terraform apply -replace="aws_instance.data-generator[0]" -auto-approve` - used to rebuild one component
* `terraform output` - Show output parameters from the previous apply
* `terraform plan -destroy` - Used to check what Terraform plans to do if a destroy command is issued
* `terraform destroy -auto-approve` - Used to remove the resources from the Terraform plan

## Terminal commands
* `ssh -i ~/Downloads/ohio-jturner.pem ubuntu@$(terraform output -json dataGeneratorDNS |jq -r '.[0][0]')`
* `ssh -i ~/Downloads/ohio-jturner.pem ubuntu@$(terraform output -json dataGeneratorDNS |jq -r '.[0][1]')` to get 2nd instance

**Please update the commands to use the pem file defined in terraform.tfvars**

## File Descriptions
| Filename                | Description                                                                          |
|-------------------------|--------------------------------------------------------------------------------------|
| dataGenerator.tf        | Creates cloud application server(s)                                                  |
| dataGenerator.tftpl     | Template file for setting up the application server(s)                               |
| grafana.tf              | Creates the Grafana instance                                                         |
| kafka.tf                | Creates a Kafka instance and 2 connectors used to pull data from Postgresql to MySQL |
| provider.tf             | Sets up the Aiven Terraform provider                                                 |
| terraform.tfvars.sample | Template to create the terraform.tfvars file.  Please set before attempting to run   |
| thanos.tf               | Creates the Thanos instance                                                          |
| variables.tf            | Creates variables that get set by the terraform.tfvars file                          |

