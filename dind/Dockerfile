FROM docker:dind

RUN #apk --no-cache add openjdk11
RUN apk --no-cache add openjdk17

RUN git clone https://github.com/bazzani/kafka-connect-stack.git && \
    cd kafka-connect-stack
#    \
#    && \
#    git checkout add-spring-boot-subproject \

# ./gradlew check --console=verbose


#####################################################
#
# docker remove dind -f; \
# docker build -t bevans/dind -f dind/Dockerfile .; \
# docker run --name dind -d --privileged bevans/dind; \
# docker exec -it dind sh;

# docker remove dind -f

# cd kafka-connect-stack/;./gradlew build

#FAILURE: Build failed with an exception.
 #
 #* What went wrong:
 #A problem occurred configuring root project 'kafka-connect-stack'.
 #> Could not resolve all artifacts for configuration ':classpath'.
 #   > Could not resolve org.springframework.boot:spring-boot-gradle-plugin:3.2.4.
 #     Required by:
 #         project : > org.springframework.boot:org.springframework.boot.gradle.plugin:3.2.4
 #      > No matching variant of org.springframework.boot:spring-boot-gradle-plugin:3.2.4 was found.
 #         The consumer was configured to find a library for use during runtime, compatible with Java 11, packaged as a
 #         jar, and its dependencies declared externally, as well as attribute 'org.gradle.plugin.api-version'
 #         with value '8.7' but:
 #          - Variant 'apiElements' declares a library, packaged as a jar, and its dependencies declared externally:
 #              - Incompatible because this component declares a component for use during compile-time, compatible with
 #                Java 17 and the consumer needed a component for use during runtime, compatible with Java 11
 #              - Other compatible attribute:
 #                  - Doesn't say anything about org.gradle.plugin.api-version (required '8.7')
