resource "aiven_opensearch" "os1" {
  count                   = var.os_populate ? 1 : 0
  project                 = var.project_name
  cloud_name              = var.cloud_name
  plan                    = var.os_plan
  service_name            = "os1"
  maintenance_window_dow  = var.maintenance_dow
  maintenance_window_time = var.maintenance_time

  opensearch_user_config {
    opensearch_version = var.os_version

    opensearch_dashboards {
      enabled                    = true
      opensearch_request_timeout = 30000
    }

    public_access {
      opensearch            = true
      opensearch_dashboards = true
    }
  }
  /*
  provisioner "local-exec" {
    command = format("curl -X PUT %s/calls_2023-12  -H 'Content-Type: application/json' -d '{\"settings\": { \"number_of_shards\": 9, \"number_of_replicas\": 0} }'", aiven_opensearch.os1.service_uri)
  }
  */
}

resource "aiven_service_integration" "os1_to_thanos1" {
  count                    = var.os_populate ? 1 : 0
  project                  = var.project_name
  integration_type         = "metrics"
  source_service_name      = aiven_opensearch.os1[0].service_name
  destination_service_name = aiven_thanos.thanos1.service_name
  depends_on               = [aiven_opensearch.os1, aiven_thanos.thanos1]
}


output "os1_connect" {
  value     = var.os_populate ? aiven_opensearch.os1[0].service_uri : ""
  sensitive = true
}

output "os1_host_port" {
  value = var.os_populate ? format("%s:%s", aiven_opensearch.os1[0].service_host, aiven_opensearch.os1[0].service_port) : ""
}

output "os1_components" {
  value = var.os_populate ? aiven_opensearch.os1[0].components : []
}
