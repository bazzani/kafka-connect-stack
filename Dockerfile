FROM confluentinc/cp-server-connect-base:7.3.0
# You can also use confluentinc/cp-kafka-connect image to avoid the 30 day trial
# licensing limitation but this image will not provide Control Center UI compatibilty
# - https://docs.confluent.io/platform/current/connect/license.html#license-for-self-managed-connectors

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.7.6 && \
    confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.6.5

# build with the command `docker build -t bevans/kafka-connect .`
