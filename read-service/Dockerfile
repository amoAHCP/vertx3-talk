FROM java:openjdk-8-jdk

ADD read-service.jar /router/
ENV httpPort 9090
EXPOSE $httpPort

CMD ["java", "-jar", "/router/read-service.jar", "-cluster"]