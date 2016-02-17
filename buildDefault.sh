#!/bin/bash
set -x #echo on
cd vertx-aggregator && mvn clean package
cp target/vertx-aggregator-1.0-SNAPSHOT-fat.jar ../demoOne/
cd ..
cd vertx-static && mvn clean package
cp target/vertx-static-1.0-SNAPSHOT-fat.jar ../demoOne/
cd ..
cd vertx-users-read && mvn clean package
cp target/vertx-users-read-1.0-SNAPSHOT-fat.jar ../demoOne/
cd ..
cd vertx-users-write && mvn clean package
cp target/vertx-users-write-1.0-SNAPSHOT-fat.jar ../demoOne/
cd ..
