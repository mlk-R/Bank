FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests  # Пропускаем тесты при сборке

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/StartBank-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "StartBank-0.0.1-SNAPSHOT.jar"]