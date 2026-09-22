FROM amazoncorretto:21-alpine
COPY target/medical-clinic-0.0.1-SNAPSHOT.jar medical-clinic.jar
ENTRYPOINT ["java", "-jar", "medical-clinic.jar"]