resource "aws_instance" "data-generator" {
  ami   = var.dg_ami_id
  count = var.dg_number_of_instances
  #subnet_id = var.dg_subnet_id
  instance_type = var.dg_instance_type
  ebs_optimized = true
  ebs_block_device {
    device_name           = "/dev/sda1"
    delete_on_termination = true
    encrypted             = false
    volume_size           = var.dg_disk_gb
    volume_type           = "gp2"
  }
  security_groups = [var.dg_sg_id]
  key_name        = var.dg_key_pair_name
  user_data = templatefile("dataGenerator.tftpl",
    {
      GIT_CMD = var.git_cmd

      CA_CERT     = data.aiven_project.proj1.ca_cert
      ACCESS_CERT = var.kafka_populate ? aiven_kafka.kafka1[0].kafka[0].access_cert : ""
      ACCESS_KEY  = var.kafka_populate ? aiven_kafka.kafka1[0].kafka[0].access_key : ""
      CERT_PASS   = "test1234" /* only used internally to encrypt/decrypt above */

      KAFKA_ENABLE = var.kafka_populate

      KAFKA_EXECUTOR_HOST = var.kafka_populate ? aiven_kafka.kafka1[0].components[0].host : ""
      KAFKA_EXECUTOR_PORT = var.kafka_populate ? aiven_kafka.kafka1[0].components[0].port : ""

      KAFKA_EXECUTOR_SCHEMA_REGISTRY_HOST     = var.kafka_populate ? aiven_kafka.kafka1[0].components[6].host : ""
      KAFKA_EXECUTOR_SCHEMA_REGISTRY_PORT     = var.kafka_populate ? aiven_kafka.kafka1[0].components[6].port : ""
      KAFKA_EXECUTOR_SCHEMA_REGISTRY_USER     = var.kafka_populate ? aiven_kafka.kafka1[0].service_username : ""
      KAFKA_EXECUTOR_SCHEMA_REGISTRY_PASSWORD = var.kafka_populate ? aiven_kafka.kafka1[0].service_password : ""


      KAFKA_EXECUTOR_OPENSEARCH_ENABLE   = var.os_populate
      KAFKA_EXECUTOR_OPENSEARCH_HOST     = var.os_populate ? aiven_opensearch.os1[0].service_host : ""
      KAFKA_EXECUTOR_OPENSEARCH_PORT     = var.os_populate ? aiven_opensearch.os1[0].service_port : ""
      KAFKA_EXECUTOR_OPENSEARCH_USER     = var.os_populate ? aiven_opensearch.os1[0].service_username : ""
      KAFKA_EXECUTOR_OPENSEARCH_PASSWORD = var.os_populate ? aiven_opensearch.os1[0].service_password : ""

      THANOS_REMOTE_WRITE_URL = aiven_thanos.thanos1.thanos[0].receiver_remote_write_uri

      GRAFANA_URL       = aiven_grafana.grafana1.service_uri
      GRAFANA_USER_PASS = format("%s:%s", aiven_grafana.grafana1.service_username, aiven_grafana.grafana1.service_password)

      WEB_USER     = var.web_user
      WEB_PASSWORD = var.web_password
    }
  )
  tags = {
    Name = format("%s-%02d", var.dg_instance_name, count.index + 1)
  }
  timeouts {
    delete = "15m"
    update = "15m"
    create = "15m"
  }
  depends_on = [aiven_kafka.kafka1[0], data.aiven_project.proj1, aiven_thanos.thanos1]
}

output "dataGeneratorName" {
  value = [aws_instance.data-generator.*.tags.Name]
}

output "dataGeneratorIP" {
  value = [aws_instance.data-generator.*.public_ip]
}

output "dataGeneratorDNS" {
  value = [aws_instance.data-generator.*.public_dns]
}

output "dataGeneratorURL" {
  value = [for s in aws_instance.data-generator.*.public_dns : format("http://%s:8000", s)]
}

output "dataGeneratorUserPass" {
  value     = format("%s:%s", var.web_user, var.web_password)
  sensitive = true
}
