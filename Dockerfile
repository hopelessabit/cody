# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

# Extract Spring Boot JAR using its built-in layering
RUN java -Djarmode=layertools -jar target/cody-app-0.0.1-SNAPSHOT.jar extract

# Copy models to the correct location
COPY src/main/resources/models ./dependencies/BOOT-INF/lib/models

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copy the extracted layers in the correct order
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]