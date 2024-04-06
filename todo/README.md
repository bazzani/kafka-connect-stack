This project has still got some work TODO:

1. [ ] Add docker compose commands etc, to run the stack from the CLI (
   including [docker compose v2](https://docs.docker.com/compose/migrate/))

2. [x] Add custom SMT to insert a value in the SinkRecord, and gradle build details etc.

3. [x] Add Postgres service to docker compose

4. [ ] Add script to Dockerfile to create simple `JdbcSinkConnector` on startup, including DB table `autocreate=true`
    1. [ ] Use AVRO Schema to produce message on topic to test

5. [ ] Add automated integration test to start docker compose stack, produce message on a topic, and verify data is in
   the DB

6. [ ] Add cURL commands etc. to view all connector statuses (see this page [Inspect Config and Status for a Connector
   ](https://developer.confluent.io/courses/kafka-connect/rest-api/#inspect-config-and-status-for-a-connector)) 

7. [ ] Add multiple connect instance services by exposing dynamic `CONNECT_REST_ADVERTISED_HOST_NAME`, ensuring
   connectors get created correctly on each deploy
   1. [ ] todo check consumer group name prefix to support multiple instances of connect

8. [ ] Improve logging etc. using ENV VARS (e.g. `CONNECT_LOG4J_APPENDER_STDOUT_LAYOUT_CONVERSIONPATTERN: "[%d] %p %X{connector.context}%m (%c:%L)%n"`)
