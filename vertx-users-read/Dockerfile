FROM java:openjdk-8-jdk

ADD target/vertx-users-read-1.0-SNAPSHOT-fat.jar /router/
ENV httpPort 7070
EXPOSE $httpPort

CMD ["java", "-jar", "/router/vertx-users-read-1.0-SNAPSHOT-fat.jar", "-cluster"]