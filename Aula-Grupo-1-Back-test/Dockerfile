# Build stage
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:22-alpine3.19-jdk

WORKDIR /app
COPY --from=build /build/target/*.jar /app/app.jar

RUN addgroup -S spring && adduser -S spring -G spring
RUN chown -R spring:spring /app

EXPOSE 8081

USER spring

CMD java -jar app.jar