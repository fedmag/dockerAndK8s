# FROM eclipse-temurin:17-jdk-alpine
# VOLUME /tmp
# ENV BE_PORT=8081
# COPY build/libs/*.jar app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]


#Build stage
FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . . 
RUN gradle bootJar

# Package stage
FROM amazoncorretto:17
ENV JAR_NAME=docker-be-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
ENV BE_PORT=8081
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME  