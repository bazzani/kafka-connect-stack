FROM confluentinc/cp-server-connect-base:7.6.0

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:latest &
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.6.5
