set -x #echo on
cd vertx-users-read
mvn clean package
cp target/vertx-users-read-1.0-SNAPSHOT-fat.jar ../read-service/read-service.jar
cd ..
