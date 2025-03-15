# Build stage: This first stage compiles the application and prepares the JAR file
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy only the dependency definitions first to optimize Docker caching
# (If dependencies don't change, this layer won't be rebuilt)
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn dependency:go-offline

# Copy source code and build the application
# The -DskipTests flag skips running tests during the build process
COPY src /app/src
RUN mvn clean package -DskipTests

# Runtime stage: This stage creates the final, smaller image that will run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user for security
# Running containers as non-root users prevents potential container breakouts
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy only the built JAR file from the build stage
# This keeps the final image small
COPY --from=build /app/target/neuralforge_api-1.0.0.jar /app/neuralforge_api.jar

# Document which port the app will use
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "neuralforge_api.jar"]