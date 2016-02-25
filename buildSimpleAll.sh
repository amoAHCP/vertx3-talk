set -x #echo on
./buildStaticDocker.sh
./buildSimpleAggregator.sh
./buildSimpleWriter.sh
./buildSimpleReader.sh
docker-compose up