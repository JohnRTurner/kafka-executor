variable "aiven_api_token" {
  description = "Aiven console API token"
  type        = string
}

variable "project_name" {
  description = "Aiven console project name"
  type        = string
}

variable "cloud_name" {
  description = "Aiven cloud"
  type        = string
}

variable "maintenance_dow" {
  description = "Maintenance Window Day of Week"
  type        = string
}

variable "maintenance_time" {
  description = "Maintenance Window Time"
  type        = string
}


variable "AWS_ACCESS_KEY_ID" {
  description = "AWS Access Key"
  type        = string
}

variable "AWS_SECRET_ACCESS_KEY" {
  description = "AWS Secret Key"
  type        = string
}

variable "AWS_SESSION_TOKEN" {
  description = "AWS Session Token"
  type        = string
}

variable "aws_region" {
  description = "AWS Region"
  type        = string
}

variable "dg_instance_name" {
  description = "Name of the instance to be created"
  type        = string
  default     = "dataGenerator"
}

variable "dg_instance_type" {
  type = string
  # c6i large   = 2 cpu 4GB memory
  # c6i xlarge  = 4 cpu 8GB memory
  # c6i 2xlarge = 8 cpu 16GB memory
  default = "c6i.2xlarge"
}

variable "dg_disk_gb" {
  type    = number
  default = 16
}

variable "dg_ami_id" {
  description = "The AMI to use - Amazon Machine Image (Operating System)"
  type        = string
}

variable "dg_sg_id" {
  description = "The Security Group to use"
  type        = string
}


variable "dg_number_of_instances" {
  description = "Number of instances to be created"
  type        = number
  default     = 1
}

variable "dg_key_pair_name" {
  description = "Name of PEM file to be used"
  type        = string
}

variable "web_user" {
  description = "Executor Web User"
  type        = string
}

variable "web_password" {
  description = "Executor Web Password"
  type        = string
}

variable "git_cmd" {
  description = "ie. git clone -b yourBranch https://YOUR_PERSONAL_ACCESS_TOKEN@github.com/JohnRTurner/kafka_executor.git"
  type        = string
  default     = "git clone https://github.com/JohnRTurner/kafka_executor.git"
}

variable "kafka_plan" {
  description = "Aiven Kafka Plan Name"
  type        = string
  default     = "business-4"
}

variable "kafka_version" {
  description = "Version of Kafka"
  type        = string
  default     = "3.7"
}

variable "kafka_populate" {
  description = "Use Kafka"
  type        = bool
  default     = true
}

variable "os_plan" {
  description = "Aiven OpenSearch Plan Name"
  type        = string
  default     = "business-4"
}

variable "os_version" {
  description = "Version of OpenSearch"
  type        = string
  default     = "2"
}

variable "os_populate" {
  description = "Use OpenSearch"
  type        = bool
  default     = false
}

