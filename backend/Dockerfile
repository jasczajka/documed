FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx768m"
ENTRYPOINT ["java", "-jar", "app.jar"]
