FROM gradle:7.6-jdk17 AS builder
WORKDIR /workspace
COPY build.gradle settings.gradle gradle /workspace/
RUN ./gradlew dependencies --no-daemon
COPY . /workspace
RUN ./gradlew clean test bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D -H -s /bin/sh spring
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]