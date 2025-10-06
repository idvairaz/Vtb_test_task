FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/kafka-consumer-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]