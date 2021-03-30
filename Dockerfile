FROM openjdk:8-jdk-alpine
MAINTAINER joseph
COPY target/api-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
