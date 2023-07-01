FROM cnfldemos/cp-server-connect-datagen:0.6.0-7.3.0

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:latest
