# Run Stage
FROM openjdk:17-jdk-slim

# Set the working directory, using service name to differentiate
WORKDIR /app/gift-service

# Copy the built JAR file from the build stage to the run stage
ARG JAR_FILE
COPY ${JAR_FILE} gift-service.jar

# Set environment variable to specify Spring profile
ENV SPRING_PROFILES_ACTIVE=test

# Expose the application port
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "/app/gift-service/gift-service.jar"]