FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY ./build/libs/StudyWithMe.jar /app/StudyWithMe.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-javaagent:pinpoint/pinpoint-bootstrap-2.5.2.jar", "-Dpinpoint.applicationName=StudyWithMe", "-Dpinpoint.agentId=sjiwon", "-Dpinpoint.config=pinpoint/pinpoint-root.config", "-Dspring.profiles.active=prod", "-jar", "StudyWithMe.jar"]
