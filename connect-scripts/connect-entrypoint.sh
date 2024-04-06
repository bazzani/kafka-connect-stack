#!/bin/bash

log_message() {
  current_datetime=$(date +"%Y-%m-%d %H:%M:%S,%3N")
  echo "[$current_datetime]" "$1"
}

wait_for_connect() {
  log_message "Connect Startup: Waiting for Kafka Connect to start listening on localhost ⏳"
  while [ $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors) -ne 200 ]; do
    sleep 5
  done
  log_message "Connect Startup: Kafka Connect is ready! Listener HTTP state: $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors)"
}

log_message "Connect Startup: Starting Connect from Confluent script..."
sh /etc/confluent/docker/run &

wait_for_connect
source /connect-scripts/create-connectors.sh

sleep infinity
