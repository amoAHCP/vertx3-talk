set -x #echo on
./buildSimpleReader.sh

docker-compose stop read
docker-compose build read
docker-compose up -d --no-deps read
#docker build -t read read-service/
