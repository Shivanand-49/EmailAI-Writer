# ---------- Stage 1: Build ----------
FROM eclipse-temurin:24-jdk AS build

# Set work directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (dependency cache ke liye)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build project (jar generate hoga)
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:24-jdk AS runtime

WORKDIR /app

# Copy only the jar file from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
