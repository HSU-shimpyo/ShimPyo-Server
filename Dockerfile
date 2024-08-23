FROM openjdk:17-alpine

ARG JAR_FILE=/build/libs/shimpyoo-0.0.1-SNAPSHOT.jar

# JAR 파일을 컨테이너 내부에 복사
COPY ${JAR_FILE} /shimpyoo.jar

# 애플리케이션을 실행
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod", "/shimpyoo.jar"]