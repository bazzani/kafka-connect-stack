FROM confluentinc/cp-server-connect-base:7.6.0
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

USER root
RUN yum install jq -y
USER appuser

COPY connect-scripts /connect-scripts
COPY connect-connector-configs /connect-connector-configs
COPY ./connect-smt-lib/build/libs/connect-smt-lib-*.jar /usr/share/java/bevans

ENTRYPOINT ["sh","/connect-scripts/connect-entrypoint.sh"]
