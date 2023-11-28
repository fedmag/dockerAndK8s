# Dockerize a springBoot application
[springBoot documentation](https://spring.io/guides/topicals/spring-boot-docker/)

A basic Dockerfile:
```
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

the ARG keyword is used to pass the argument JAR_FILE from the command line when building the image.
In reality, the only reason for which we might need that is that maven and gradle place the JAR in different locations.

Once we have chosen the build tool we do not need it anymore, ex for Gradle: 
```
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```