# --- Build Stage ---
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# Download dependencies
RUN ./gradlew build --no-daemon || true

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew clean build -x test --no-daemon

# --- Run Stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]
