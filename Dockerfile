FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-XX:+UseG1GC"

COPY silence-auth-center-console/target/*.jar app.jar

EXPOSE 8096

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]