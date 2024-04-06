#!/bin/bash

log_message() {
  current_datetime=$(date +"%Y-%m-%d %H:%M:%S,%3N")
  echo "[$current_datetime]" "$1"
}

config_files_dir=/connector-configs
config_files=$(ls $config_files_dir)

log_message "Connect Startup: Creating connectors from config files at '$config_files_dir'..."

for config_file in $config_files; do
  connector_name=$(cat $config_files_dir/$config_file | jq -r .name)
  connector_config=$(cat "$config_files_dir/$config_file" | jq .config)

  log_message "Creating connector from config file: $connector_name :: $config_file"

  curl -s -X DELETE -H "Content-Type:application/json" http://localhost:8083/connectors/"${connector_name}"
  sleep 5

  curl -s -X PUT -H "Content-Type:application/json" http://localhost:8083/connectors/"${connector_name}"/config \
    -d "$connector_config"
  log_message
  sleep 10
done
