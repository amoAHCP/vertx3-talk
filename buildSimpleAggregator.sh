set -x #echo on
cd vertx-aggregator
mvn clean package
cd ..
docker build -t aggregator vertx-aggregator/
