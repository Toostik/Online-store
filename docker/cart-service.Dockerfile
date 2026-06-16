
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl CartService -am -DskipTests

FROM eclipse-temurin:21-jdk

COPY --from=build /app/ProductService/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]