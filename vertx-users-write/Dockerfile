FROM java:openjdk-8-jdk

ADD target/vertx-users-write-1.0-SNAPSHOT-fat.jar /router/

CMD ["java", "-jar", "/router/vertx-users-write-1.0-SNAPSHOT-fat.jar", "-cluster"]