FROM openjdk:17-oracle

WORKDIR /app

COPY StudyWithMe.jar app.jar

ENTRYPOINT ["java", "-javaagent:pinpoint/pinpoint-bootstrap-2.5.2.jar", "-Dpinpoint.applicationName=StudyWithMe", "-Dpinpoint.agentId=sjiwon", "-Dpinpoint.config=pinpoint/pinpoint-root.config", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
