## back-end (java17-springboot) dockerfile
FROM gradle:7.6-jdk17 AS build

WORKDIR /home/gradle/project

# 캐시를 위한 gradle wrapper, build script 복사
COPY gradlew gradlew.bat gradle/ /home/gradle/project/gradle/

COPY build.gradle settings.gradle /home/gradle/project/

# 의존성 download
RUN gradle --no-daemon dependencies

# source 전체 복사
COPY . /home/gradle/project

# test, build 실행 (테스트실패 시 종료)
RUN ./gradlew clean test build --no-daemon

# test code 통과후에만 실행

FROM openjdk:17-jdk-alpine
WORKDIR /app

# build 단계에서 생성된 jar 파일 복사
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
