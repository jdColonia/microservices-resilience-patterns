FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY FutureXCourseApp/pom.xml .
RUN mvn dependency:go-offline -B

COPY FutureXCourseApp/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/FutureXCourseApp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]
