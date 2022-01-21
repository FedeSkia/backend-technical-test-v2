FROM openjdk:11
COPY target/backend-technical-test-2.0.0-SNAPSHOT.jar ./backend-technical-test-2.0.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/backend-technical-test-2.0.0-SNAPSHOT.jar"]