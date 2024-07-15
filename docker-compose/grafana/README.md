# Grafana Setup

## Upload Dashboards Manually from terraform directory

```shell
export PROM_UID=$(curl -X GET -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/datasources|jq -r '.[0].uid')
curl -X POST -H "Content-Type: application/json" -d "$(cat ../docker-compose/grafana/Executor_Dashboard_api.json|sed "s/XXXPROMXXX/${PROM_UID}/")" -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/dashboards/import
curl -X POST -H "Content-Type: application/json" -d "$(cat ../docker-compose/grafana/JVM-Micro_api.json|sed "s/XXXPROMXXX/${PROM_UID}/")" -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/dashboards/import
curl -X POST -H "Content-Type: application/json" -d "$(cat ../docker-compose/grafana/Node_Exporter_api.json|sed "s/XXXPROMXXX/${PROM_UID}/")" -u $(terraform output -raw grafana_user_pass) --url $(terraform output -raw grafana_uri)/api/dashboards/import
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