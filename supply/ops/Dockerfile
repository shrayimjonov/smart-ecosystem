# Multi-stage build for Spring Boot application
# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

# Build argument for Maven profile (dev or prod)
ARG MAVEN_PROFILE=dev

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (better layer caching)
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable!
RUN chmod +x mvnw

# Copy source code
COPY src ./src

# Build the application using local-settings.xml with specified Maven profile
# Maven profile affects build-time configuration, Spring profile is set at runtime
RUN ./mvnw -s .mvn/local-settings.xml -P${MAVEN_PROFILE} clean package -DskipTests=true

# Stage 2: Extract layers from Spring Boot JAR
FROM eclipse-temurin:21-jre-alpine AS extractor

WORKDIR /app
COPY --from=builder /app/target/smartpossupply-0.0.1-SNAPSHOT.jar app.jar

# Extract Spring Boot layers for better Docker layer caching
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

# Add metadata labels
LABEL maintainer="CDTI Team"
LABEL description="Supply Service"
LABEL version="0.0.1-SNAPSHOT"

# Install wget for healthcheck and tzdata for timezone support
RUN apk add --no-cache wget tzdata && \
    cp /usr/share/zoneinfo/Asia/Tashkent /etc/localtime && \
    echo "Asia/Tashkent" > /etc/timezone && \
    apk del tzdata

# Create a non-root user for security with specific UID/GID
RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring

# Set working directory
WORKDIR /app

# Create logs directory with proper permissions
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs

# Copy Spring Boot layers in order of least to most frequently changing
# This improves Docker layer caching
COPY --from=extractor --chown=spring:spring /app/dependencies/ ./
COPY --from=extractor --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=extractor --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=extractor --chown=spring:spring /app/application/ ./

# Switch to non-root user
USER ${USER}

# Expose the application ports
EXPOSE 8099 9292

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8099 || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xms512m -Xmx5G \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Tashkent"

# Set Spring profile (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=dev

# Run the application using Spring Boot's layered structure
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE org.springframework.boot.loader.launch.JarLauncher"]
