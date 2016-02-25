#!/bin/bash
set -x #echo on

osascript -e 'tell app "Terminal" to do script "cd /Users/amo/Documents/development/talks2016/vertx/vertx3-talk/demoOne && java -jar vertx-static-1.0-SNAPSHOT-fat.jar"'
osascript -e 'tell app "Terminal" to do script "cd /Users/amo/Documents/development/talks2016/vertx/vertx3-talk/demoOne && java -jar vertx-aggregator-1.0-SNAPSHOT-fat.jar -cluster"'
osascript -e 'tell app "Terminal" to do script "cd /Users/amo/Documents/development/talks2016/vertx/vertx3-talk/demoOne && java -jar vertx-users-read-1.0-SNAPSHOT-fat.jar -cluster"'
osascript -e 'tell app "Terminal" to do script "cd /Users/amo/Documents/development/talks2016/vertx/vertx3-talk/demoOne && java -jar vertx-users-write-1.0-SNAPSHOT-fat.jar -cluster"'
