# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
COPY . /home/app
WORKDIR /home/app
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon -x test

# Run stage
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8081
COPY --from=build /home/app/build/libs/portfolio-0.0.1-SNAPSHOT.jar /app/portfolio.jar
ENTRYPOINT ["java", "-jar", "/app/portfolio.jar"]
