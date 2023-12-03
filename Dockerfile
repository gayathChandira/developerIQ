# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY target/developerIQ-1.0.jar /app

# Make port 8080 available to the world outside this container
EXPOSE 8081

# Define the command to run your application
CMD ["java", "-jar", "developerIQ-1.0.jar"]
