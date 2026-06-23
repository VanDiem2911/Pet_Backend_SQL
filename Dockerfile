# Stage 1: Build với Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml trước để cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime với JRE Jammy (có đầy đủ SSL cho MongoDB Atlas)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/pet-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]