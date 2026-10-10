# syntax=docker/dockerfile:1

# ---- build stage ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Resolve dependencies first so they are cached unless pom.xml changes.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -B dependency:go-offline

# Build the application jar.
COPY src/ src/
RUN ./mvnw -q -B clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
