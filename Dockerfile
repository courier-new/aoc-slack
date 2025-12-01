FROM gradle:8.4-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and project files to the container
COPY . .

# Build the application
RUN ./gradlew clean build

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/build/libs/aoc-slack.jar app.jar

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
