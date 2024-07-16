# Grafana Setup

## Dashboards
* **Aiven Kafka** - Default dashboard for Aiven Kafka - **included automatically by Aiven**
* **Executor Dashboard** - Shows how many rows are being produced/consumed.
* **JVM (Micrometer)** - JVM stats for Executor
* **Node Exporter Full** - Node information for Executor Node

Please note that there are two files for each dashboard, the one marked _api is for the Grafana API, the other can be used with the GUI.

## Upload Dashboards Manually from terraform directory

```shell
export PROM_UID=$(curl -X GET -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/datasources|jq -r '.[0].uid')
cat ../docker-compose/grafana/Executor_Dashboard_api.json|sed "s/XXXPROMXXX/${PROM_UID}/"|curl -X POST -H "Content-Type: application/json" -d @- -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/dashboards/import
cat ../docker-compose/grafana/JVM-Micro_api.json|sed "s/XXXPROMXXX/${PROM_UID}/"|curl -X POST -H "Content-Type: application/json" -d @- -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/dashboards/import
cat ../docker-compose/grafana/Node_Exporter_api.json|sed "s/XXXPROMXXX/${PROM_UID}/"|curl -X POST -H "Content-Type: application/json" -d @- -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/dashboards/import
```

## How to convert regular Grafana dashboard exports into api export
1. Create new file and start with:
```json
{
  "inputs": [
    {
      "name": "DS_PROMETHEUS",
      "type": "datasource",
      "pluginId": "prometheus",
      "value": "XXXPROMXXX"
    }
  ],
  "dashboard":
```
2. insert the file after the above text
3. append with following text:
```json
,
  "folderId": 0,
  "overwrite": true
}
```
4. Replace the ```id: whatever_number_is_here``` with ```id: null``` at the dashboard level if it isn't already set to null.
5. Add the additional file in terraform/dataGenerator.tftpl