curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
--data '{"schemaType":"JSON","schema": "{\"title\":\"user-data\",\"description\":\"User Data sourced from Kafka Connect\",\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"number\",\"minimum\":1},\"name\":{\"type\":\"string\",\"maxLength\":20},\"age\":{\"type\":\"number\",\"minimum\":0,\"maximum\":125}}}"}' \
http://localhost:8081/subjects/jdbc-source-mysql-Json-Schema__user_data-value/versions

curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
--data @connect-spring-boot-app/src/integration/resources/json/user-data_schema_wrapped_for_curl.json \
http://localhost:8081/subjects/jdbc-source-mysql-Json-Schema__user_data-value/versions



--data '{"schemaType":"JSON","schema": "{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\",\"maxLength\":20},\"age\":{\"type\":\"number\",\"minimum\":0,\"maximum\":125}}}"}' \

--data '{"schemaType":"JSON","schema": "{\"title\":\"user-data\",\"description\":\"User Data sourced from Kafka Connect\",\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\",\"maxLength\":20},\"name\":{\"type\":\"string\",\"maxLength\":20},\"age\":{\"type\":\"number\",\"minimum\":0,\"maximum\":125}}}"}' \

curl -X DELETE \
http://localhost:8081/subjects/jdbc-source-mysql-Json-Schema__user_data-value

--data '{"schemaType":"JSON","schema": "{\"title\":\"user-data\",\"description\":\"User Data sourced from Kafka Connect\",\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\",\"maxLength\":20},\"id\":{\"type\":\"string\",\"minimum\":1},\"age\":{\"type\":\"string\",\"minimum\":0,\"maximum\":125}}}"}' \

--data '{"schemaType":"JSON","schema": "{\"title\":\"user-data\",\"description\":\"User Data sourced from Kafka Connect\",\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"number\",\"minimum\":1},\"name\":{\"type\":\"string\",\"maxLength\":20},\"age\":{\"type\":\"number\",\"minimum\":0,\"maximum\":125}}}"}' \

curl -X DELETE http://localhost:8081/subjects/jdbc-source-mysql-Json-Schema__user_data-value/;
curl -X DELETE http://localhost:8081/subjects/jdbc-source-mysql-Json-Schema__user_data-value/?permanent=true;

curl http://localhost:8081/subjects/jdbc-source-mysql-Json-Schema__user_data-value/versions/1 | jq -r .schema | jq

#-=-=-

curl -X POST http://localhost:8083/connectors/UserData_Json-Schema_Incrementing_Mysql_Source_Connector/restart\?includeTasks=true
