# # Use a base image with Java 17
# FROM openjdk:17-jdk-slim

# # Set the working directory inside the container
# WORKDIR /app

# # Copy the built .jar file into the container
# COPY target/myapp-1.0-SNAPSHOT.jar /app/myapp.jar

# # Expose the port that your app will run on
# EXPOSE 8081

# # Run the JAR file when the container starts
# ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
# Use a base image with Java 17
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built .jar file into the container
COPY target/myapp-1.0-SNAPSHOT.jar /app/myapp.jar

# Expose the port that your app will run on
EXPOSE 8081

# Add environment variable
ENV ENVIRONMENT=production

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-Denvironment=${ENVIRONMENT}", "-jar", "/app/myapp.jar"]