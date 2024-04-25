This project has still got some work TODO:

1. [ ] Add docker compose commands etc, to run the stack from the CLI (
   including [docker compose v2](https://docs.docker.com/compose/migrate/))

2. [x] Add custom SMT to insert a value in the SinkRecord, and gradle build details etc.

3. [x] Add Postgres service to docker compose

4. [x] Add script to Dockerfile to create simple `JdbcSinkConnector` on startup, including DB table `autocreate=true`
    - [x] Use AVRO Schema to produce message on topic to test

5. [x] Add automated integration test to start docker compose stack, produce message on a topic, and verify data is in
   the DB

6. [ ] Add cURL commands etc. to view all connector statuses (see this page [Inspect Config and Status for a Connector
   ](https://developer.confluent.io/courses/kafka-connect/rest-api/#inspect-config-and-status-for-a-connector)) 

7. [ ] Add multiple connect instance services by exposing dynamic `CONNECT_REST_ADVERTISED_HOST_NAME`, ensuring
   connectors get created correctly on each deploy
   1. [ ] todo check consumer group name prefix to support multiple instances of connect

8. [ ] Improve logging etc. using ENV VARS (e.g. `CONNECT_LOG4J_APPENDER_STDOUT_LAYOUT_CONVERSIONPATTERN: "[%d] %p %X{connector.context}%m (%c:%L)%n"`)

9. [ ] Add Spring Boot REST APIs to support POSTing new FF VII Ally updates

10. [x] Use a multi-project gradle setup, one for SMTs, one for Spring Boot app(s) - https://docs.gradle.org/current/userguide/multi_project_builds.html
    
11. [ ] Add jvm debug settings to `connect` container/service and info in README -- `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`

12. [x] Add gradle build command to docker compose to ensure the SMT jar is available?

13. [ ] Review usage of `GenericData.Record`, is this the best class to use?
    
14. [ ] Apply Jacoco aggregation plugin to get all jacoco coverage data in one report
    - https://docs.gradle.org/current/userguide/jacoco_report_aggregation_plugin.html#jacoco_report_aggregation_plugin
