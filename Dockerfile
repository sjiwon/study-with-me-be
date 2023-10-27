FROM openjdk:17-oracle

WORKDIR /app

COPY StudyWithMe.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
