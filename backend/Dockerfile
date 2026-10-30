#BACKEND DOCKERFILE

#builds java application; may comment out if already built
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY ./pom.xml .
COPY ./src ./src
RUN mvn -DskipTests package
#--

#run the built application; change COPY if using own build file
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/aspm-backend-*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]
