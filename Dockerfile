# Build stage
FROM gradle:8.7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

# Run stage
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8081
COPY --from=build /home/gradle/src/build/libs/portfolio-0.0.1-SNAPSHOT.jar /app/portfolio.jar
ENTRYPOINT ["java", "-jar", "/app/portfolio.jar"]
