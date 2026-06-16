
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl ProductService -am -DskipTests

RUN find /app/ProductService/target \
    -type f \
    -name "*.jar" \
    ! -name "*.jar.original" \
    -exec cp {} /product-service.jar \;

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /product-service.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]