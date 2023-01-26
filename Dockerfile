FROM openjdk:11
MAINTAINER IV
COPY target/notification_service-1.0.0.jar notification_service-1.0.0.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/notification_service-1.0.0.jar"]