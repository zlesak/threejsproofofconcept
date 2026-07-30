FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml package.json package-lock.json tsconfig.json types.d.ts vite.config.ts ./
COPY .mvn .mvn
RUN --mount=type=cache,target=/root/.m2 mvn -B -Pproduction -DskipTests dependency:go-offline

COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn -B -Pproduction -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
COPY --from=build /build/src/main/webapp ./webapp
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
