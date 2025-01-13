# ========== Stage 1: Cache Gradle dependencies ==========
FROM gradle:8.12.0-jdk17 AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home

# 複製跟依賴有關的檔案（包含 gradle/ 資料夾、build.gradle、settings.gradle 等）
COPY build.gradle.* gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle

WORKDIR /home/gradle/app
# 先做一次 gradle build 來下載所有依賴
RUN gradle clean build -i --stacktrace

# ========== Stage 2: Build Application (Fat JAR) ==========
FROM gradle:8.12.0-jdk17 AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/app/

WORKDIR /usr/src/app
# Build Fat JAR
RUN gradle buildFatJar --no-daemon

# ========== Stage 3: Create the Runtime Image ==========
FROM amazoncorretto:17 AS runtime
EXPOSE 8080
RUN mkdir /app

# 從上一個階段複製產生出來的 fat JAR
COPY --from=build /usr/src/app/build/libs/*.jar /app/ktor-docker-sample.jar

ENTRYPOINT ["java", "-jar", "/app/ktor-docker-sample.jar"]