FROM confluentinc/cp-server-connect-base:7.6.1
# You can also use confluentinc/cp-kafka-connect image to avoid the 30 day trial
# licensing limitation but this image will not provide Control Center UI compatibilty
# - https://docs.confluent.io/platform/current/connect/license.html#license-for-self-managed-connectors

###    Install JRE 17   ###
ENV ZULU_JRE_VERSION="zulu17-ca-jre-headless"

USER root
RUN microdnf install yum \
    && rpm --import https://www.azul.com/wp-content/uploads/2021/05/0xB1998361219BD9C9.txt \
    && yum install -y https://cdn.azul.com/zulu/bin/zulu-repo-1.0.0-1.noarch.rpm \
    && yum -y install ${ZULU_JRE_VERSION}
USER appuser
###########################

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.7.6 && \
    confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.6.5


### Add support for io.confluent.connect.gcp.pubsub.PubSubSourceConnector
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-gcp-pubsub:1.2.5 && \
    confluent-hub install --no-prompt jcustenborder/kafka-connect-json-schema:0.2.5
###########################


### Add support for com.google.pubsub.kafka.source.CloudPubSubSourceConnector
USER root
RUN wget https://repo1.maven.org/maven2/com/google/cloud/pubsub-group-kafka-connector/1.2.0/pubsub-group-kafka-connector-1.2.0.jar \
    -O /usr/share/java/kafka/pubsub-group-kafka-connector-1.2.0.jar

RUN wget https://repo1.maven.org/maven2/com/google/protobuf/protobuf-java/3.22.5/protobuf-java-3.22.5.jar \
    -O /usr/share/java/kafka/protobuf-java-3.22.5.jar
RUN wget https://repo1.maven.org/maven2/com/google/protobuf/protobuf-java-util/3.22.5/protobuf-java-util-3.22.5.jar \
    -O /usr/share/java/kafka/protobuf-java-util-3.22.5.jar
# todo try and remove all of these jars, will the LazyStringArrayList class get loaded from pubsub-group-kafka-connector-1.2.0.jar instead?
RUN rm /usr/share/java/kafka/protobuf-java-3.19.6.jar && \
    rm /usr/share/java/kafka/protobuf-java-util-3.19.6.jar

RUN rm /usr/share/java/confluent-security/connect/protobuf-java-util-3.19.6.jar \
       /usr/share/java/confluent-security/connect/protobuf-java-3.19.6.jar
USER appuser

RUN confluent-hub install --no-prompt jcustenborder/kafka-connect-transform-common:0.1.0.58
########################

USER root
RUN yum install jq -y
USER appuser

COPY connect-scripts /connect-scripts
COPY ./connect-smt-lib/build/libs/connect-smt-lib-*.jar /usr/share/java/kafka
COPY ./credentials /credentials
COPY schemas/gcp-pubsub-FromJson-schema.json /schemas/gcp-pubsub-FromJson-schema.json
COPY connect-connector-configs /connect-connector-configs

ENTRYPOINT ["sh","/connect-scripts/connect-entrypoint.sh"]
