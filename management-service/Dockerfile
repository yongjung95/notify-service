# (1) 빌드 스테이지: JAR 파일을 만들기 위한 환경
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# gradlew, build.gradle 등 빌드에 필요한 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# gradlew에 실행 권한 부여
RUN chmod +x ./gradlew

# 애플리케이션 빌드: 이 명령어 실행 후 build/libs 폴더에 jar 파일이 생성됨
RUN ./gradlew clean bootJar -x test

# (2) 실행 스테이지: 빌드된 JAR 파일만 실행하는 환경
FROM eclipse-temurin:17-jre

# 최종 이미지의 작업 디렉터리 설정
WORKDIR /app

# 빌드 스테이지에서 만든 JAR 파일을 가져옴 (경로 주의!)
# /app/build/libs/*.jar 파일을 /app/app.jar로 복사
COPY --from=builder /app/build/libs/*.jar /app/management-service.jar

# 컨테이너가 시작될 때 실행될 명령어 (경로 수정)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=real", "/app/management-service.jar"]

# 애플리케이션 포트
EXPOSE 8082