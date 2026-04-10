
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# 1. Сначала копируем только pom.xml и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Потом копируем весь исходный код
COPY src ./src

# 3. Сборка
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]