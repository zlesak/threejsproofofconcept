FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
COPY src/main/webapp ./webapp
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
