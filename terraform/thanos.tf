resource "aiven_thanos" "thanos1" {
  project                 = var.project_name
  cloud_name              = var.cloud_name
  plan                    = "startup-4"
  service_name            = "thanos1"
  maintenance_window_dow  = var.maintenance_dow
  maintenance_window_time = var.maintenance_time
  /*thanos_user_config {
    public_access {
      query = true
    }
  }*/
}



output "thanos_query_frontend_uri"{
  value = aiven_thanos.thanos1.thanos[0].query_frontend_uri
  sensitive = true
}

output "thanos_query_uri"{
  value = aiven_thanos.thanos1.thanos[0].query_uri
  sensitive = true
}


output "thanos_receiver_remote_write_uri"{
  value = aiven_thanos.thanos1.thanos[0].receiver_remote_write_uri
  sensitive = true
}

output "thanos_receiver_ingesting_remote_write_uri"{
  value = aiven_thanos.thanos1.thanos[0].receiver_ingesting_remote_write_uri
  sensitive = true
}


output "thanos_user_pass"{ #same as host port
  value = format("%s:%s", aiven_thanos.thanos1.service_username, aiven_thanos.thanos1.service_password)
  sensitive = true
}