# Use official OpenJDK runtime as base image
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Compile Java application
RUN javac backend.java

# Expose port (Render will set PORT environment variable)
EXPOSE 8080

# Run the application
CMD ["java", "backend"]
