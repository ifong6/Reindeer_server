# Run Stage
FROM openjdk:17-jdk-slim

# Set the working directory, using service name to differentiate
WORKDIR /app/notification-service

# Copy the built JAR file from the build stage to the run stage
ARG JAR_FILE
COPY ${JAR_FILE} notification-service.jar

# Set environment variable to specify Spring profile
ENV SPRING_PROFILES_ACTIVE=test

# Expose the application port
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "/app/notification-service/notification-service.jar"]