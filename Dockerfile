# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy the built artifact from build stage
COPY --from=build /app/target/banktrade-0.0.1-SNAPSHOT.jar ./app.jar

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV SPRING_PROFILES_ACTIVE="prod"

# Expose port
EXPOSE 8080

# Set entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 