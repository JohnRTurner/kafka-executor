locals {
  IDX = index([for idx, comp in aiven_kafka.kafka1.components : idx if comp.component == "schema_registry"], 0)
}

resource "aws_instance" "data-generator" {
  ami = var.dg_ami_id
  count = var.dg_number_of_instances
  #subnet_id = var.dg_subnet_id
  instance_type = var.dg_instance_type
  ebs_optimized = true
  ebs_block_device {
    device_name = "/dev/sda1"
    delete_on_termination = true
    encrypted = false
    volume_size = var.dg_disk_gb
    volume_type = "gp2"
  }
  security_groups = [var.dg_sg_id]
  key_name = var.dg_key_pair_name
  user_data = templatefile("dataGenerator.tftpl",
    {
      CA_CERT=data.aiven_project.proj1.ca_cert
      ACCESS_CERT=aiven_kafka.kafka1.kafka[0].access_cert
      ACCESS_KEY=aiven_kafka.kafka1.kafka[0].access_key
      CERT_PASS="test1234" /* only used internally to encrypt/decrypt above */

      KAFKA_EXECUTOR_HOST=aiven_kafka.kafka1.components[0].host
      KAFKA_EXECUTOR_PORT=aiven_kafka.kafka1.components[0].port

      #KAFKA_EXECUTOR_SCHEMA_REGISTRY_HOST=aiven_kafka.kafka1.components[local.IDX].host
      #KAFKA_EXECUTOR_SCHEMA_REGISTRY_PORT=aiven_kafka.kafka1.components[local.IDX].port
      KAFKA_EXECUTOR_SCHEMA_REGISTRY_HOST=aiven_kafka.kafka1.components[6].host
      KAFKA_EXECUTOR_SCHEMA_REGISTRY_PORT=aiven_kafka.kafka1.components[6].port

      KAFKA_EXECUTOR_SCHEMA_REGISTRY_USER=aiven_kafka.kafka1.service_username
      KAFKA_EXECUTOR_SCHEMA_REGISTRY_PASSWORD=aiven_kafka.kafka1.service_password

      THANOS_REMOTE_WRITE_URL=aiven_thanos.thanos1.thanos[0].receiver_remote_write_uri
    }
  )
  tags = {
    Name = format("%s-%02d",var.dg_instance_name, count.index + 1)
  }
  timeouts {
    delete = "15m"
    update = "15m"
    create = "15m"
  }
  depends_on = [aiven_kafka.kafka1, data.aiven_project.proj1, aiven_thanos.thanos1]
}

output "dataGeneratorName"{
  value = [aws_instance.data-generator.*.tags.Name]
}

output "dataGeneratorIP"{
  value = [aws_instance.data-generator.*.public_ip]
}

output "dataGeneratorDNS"{
  value = [aws_instance.data-generator.*.public_dns]
}
