# Builder
FROM gradle:9.1.0-jdk21 AS builder
WORKDIR /app

COPY build.gradle gradlew ./
COPY gradle ./gradle

# ✅ Dar permisos de ejecución al script Gradle Wrapper
RUN chmod +x gradlew

# Descargar dependencias
RUN ./gradlew dependencies

COPY src ./src

# Construir el jar
RUN ./gradlew build --no-daemon -x test

# Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
