FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/client-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9081
ENTRYPOINT ["java", "-jar", "app.jar"]
