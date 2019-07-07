cd ~/IdeaProjects/VergerCo/Backend
sbt 'run 9000' &

cd ~/IdeaProjects/VergerCo/SmartFruit
sbt 'run 9001' &

cd ~/IdeaProjects/VergerCo/Backend/app/SparkConsumer
sbt run &

cd ~/kafka_2.12-2.2.0
./bin/zookeeper-server-start.sh config/zookeeper.properties &
./bin/kafka-server-start.sh config/server.properties &

# bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic1 --from-beginning
