# Kafka service
resource "aiven_kafka" "kafka1" {
  project                 = var.project_name
  cloud_name              = var.cloud_name
  plan                    = "business-4"
  service_name            = "kafka1"
   maintenance_window_dow  = var.maintenance_dow
  maintenance_window_time = var.maintenance_time
  kafka_user_config {
    kafka_connect = true
    kafka_rest    = true
    schema_registry = true
    kafka_version = "3.7"
    kafka {
      group_max_session_timeout_ms = 70000
      log_retention_bytes          = 20000000000
      auto_create_topics_enable  = true
      num_partitions             = 24
      default_replication_factor = 2
      min_insync_replicas        = 2
    }
    public_access {
      kafka = true
      kafka_connect = true
      kafka_rest = true
    }
    kafka_authentication_methods {
      certificate = true
    }
  }
}

resource "aiven_service_integration" "kafka1_to_thanos1" {
  project                  = var.project_name
  integration_type         = "metrics"
  source_service_name      = aiven_kafka.kafka1.service_name
  destination_service_name = aiven_thanos.thanos1.service_name
  depends_on = [aiven_kafka.kafka1, aiven_thanos.thanos1]
}

output "kafka1_service_uri"{ #same as host port
  value = aiven_kafka.kafka1.service_uri
  sensitive = true
}

output "kafka1_schema_uri"{
  value = format("%s:%s", aiven_kafka.kafka1.components[2].host, aiven_kafka.kafka1.components[2].port)
}

output "kafka1_user_pass"{
  value = format("%s:%s", aiven_kafka.kafka1.service_username, aiven_kafka.kafka1.service_password)
  sensitive = true
}

data "aiven_project" "proj1" {
  project = var.project_name
}

output "kafka1_access_cert"{
  value = aiven_kafka.kafka1.kafka[0].access_cert
  sensitive = true
}

output "kafka1_access_key"{
  value = aiven_kafka.kafka1.kafka[0].access_key
  sensitive = true
}

output "kafka1_ca_cert"{
  value = data.aiven_project.proj1.ca_cert
  sensitive = true
}
