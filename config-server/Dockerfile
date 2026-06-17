# Step 1: Use a lightweight OpenJDK runtime base image
FROM eclipse-temurin:17-jre-alpine

# Step 2: Set the internal working directory inside the container space
WORKDIR /app

# Step 3: Copy the compiled jar from your local target build folder into the image
COPY target/*.jar app.jar
EXPOSE 8888
# Step 4: Run the Java application when the container fires up
ENTRYPOINT ["java", "-jar", "app.jar"]