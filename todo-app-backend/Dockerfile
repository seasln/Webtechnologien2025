# --- Build Stage ---
# Wir nutzen hier gradle mit JDK 21
FROM gradle:8.10-jdk21 AS builder
WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# --- FIX: Ausführungsrechte geben ---
RUN chmod +x gradlew

# Download dependencies
# Das "|| true" sorgt dafür, dass der Build nicht abbricht, wenn kein Source da ist
RUN ./gradlew build --no-daemon || true

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew clean build -x test --no-daemon

# --- Run Stage ---
# Wir nutzen hier das JRE 21 zum Ausführen
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]