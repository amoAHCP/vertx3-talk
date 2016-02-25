FROM java:openjdk-8-jdk

ADD target/vertx-aggregator-1.0-SNAPSHOT-fat.jar /router/
ENV httpPort 9090
EXPOSE $httpPort

CMD ["java", "-jar", "/router/vertx-aggregator-1.0-SNAPSHOT-fat.jar", "-cluster"]