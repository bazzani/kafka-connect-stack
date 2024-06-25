curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
--data '{"schemaType":"JSON","schema": "{\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"PubSubData\",\"type\":\"object\",\"properties\":{\"OrderNumber\":{\"type\":\"string\"},\"SiteId\":{\"type\":\"string\"},\"Locale\":{\"type\":\"string\"},\"CreatedTime\":{\"type\":\"string\"},\"AddressId\":{\"type\":\"integer\"},\"OrderValue\":{\"type\":\"number\"}}}"}' \
http://localhost:8081/subjects/pub-sub-topic-value/versions;

# https://docs.confluent.io/platform/current/schema-registry/develop/api.html#post--subjects-(string-%20subject)-versions
