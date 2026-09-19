FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV PORT=8080
EXPOSE 8080
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
