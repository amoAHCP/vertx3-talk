set -x #echo on
cd vertx-users-read-spring
mvn clean package
cp target/vertx-users-read-spring-1.0-SNAPSHOT-fat.jar ../read-service/read-service.jar
cd ..
docker-compose stop read
docker-compose build read
docker-compose up -d --no-deps read
#docker build -t read read-service/
