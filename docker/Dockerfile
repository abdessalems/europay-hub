# ---- Build stage ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline
COPY src ./src
RUN ./mvnw -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S europay && adduser -S europay -G europay
COPY --from=build /workspace/target/europay-hub-*.jar app.jar
USER europay
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
