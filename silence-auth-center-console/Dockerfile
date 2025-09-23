FROM openjdk:11-jre-slim

ENV SPRING_PROFILES_ACTIVE=prd

WORKDIR /app

COPY ./target/silence-auth-center-console-1.0.0-SNAPSHOT.jar /app/silence-auth-center-console-1.0.0-SNAPSHOT.jar

EXPOSE 8096

ENTRYPOINT ["java", "-jar", "/app/silence-auth-center-console-1.0.0-SNAPSHOT.jar"]