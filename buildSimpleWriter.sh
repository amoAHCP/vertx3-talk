set -x #echo on
cd vertx-users-write
mvn clean package
cd ..
docker build -t write vertx-users-write/
