FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ENV BE_PORT=8081
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]