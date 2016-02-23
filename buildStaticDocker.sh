set -x #echo on
cd vertx-static
mvn clean package
cd ..
docker build -t static-verticle vertx-static/
