FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B verify

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/upi-offline-mesh-0.0.1-SNAPSHOT.jar app.jar
USER app
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0"
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.h2.console.enabled=false", "--server.error.include-stacktrace=never"]
