FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml for dependency caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy libs (JAR + models)
COPY libs ./libs

# Make mvnw executable
RUN chmod +x ./mvnw

# Extract VnCoreNLP JAR, remove log4j.properties, and repackage
RUN mkdir -p temp && \
    cd temp && \
    jar -xf ../libs/VnCoreNLP-1.2.jar && \
    rm -f log4j.properties && \
    jar -cf ../libs/VnCoreNLP-1.2-clean.jar * && \
    cd .. && \
    rm -rf temp

# Install VnCoreNLP JAR into local Maven repo
RUN ./mvnw install:install-file \
  -Dfile=libs/VnCoreNLP-1.2-clean.jar \
  -DgroupId=vn.pipeline \
  -DartifactId=VnCoreNLP \
  -Dversion=1.2 \
  -Dpackaging=jar

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create a simple log4j.properties to suppress warnings
RUN echo "log4j.rootLogger=WARN, console" > /app/log4j.properties && \
    echo "log4j.appender.console=org.apache.log4j.ConsoleAppender" >> /app/log4j.properties && \
    echo "log4j.appender.console.layout=org.apache.log4j.PatternLayout" >> /app/log4j.properties && \
    echo "log4j.appender.console.layout.ConversionPattern=%m%n" >> /app/log4j.properties

# Copy models to the same directory as where we'll run the app
COPY libs/models ./models

# Expose port
EXPOSE 8080

# Run the application with log4j configuration
ENTRYPOINT ["java", "-Dlog4j.configuration=file:log4j.properties", "-jar", "target/cody-app-0.0.1-SNAPSHOT.jar"]