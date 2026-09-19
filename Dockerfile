# ---- Stage 1: Build Frontend ----
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build

# ---- Stage 2: Build Backend with Frontend Assets ----
FROM maven:3.9-eclipse-temurin-17-alpine AS backend-builder
WORKDIR /app/backend

COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

COPY backend/src ./src

# Copy built frontend static assets into Spring Boot static resources directory
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

# ---- Stage 3: Run Spring Boot Full-Stack App ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV PORT=8080
EXPOSE 8080

COPY --from=backend-builder /app/backend/target/*.jar app.jar

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
