kafka-efesto
============

Kafka setup
-----------

1. download [zookeper](https://zookeeper.apache.org/releases.html)
2. extract it
3. make scripts executable
4. create a `zoo.cfg` config file under `../conf`
    ```
   tickTime=2000
   dataDir=/your/location/here/apache-zookeeper-3.8.0-bin/data
   clientPort=2181
   initLimit=5
   syncLimit=2
   ```
5. start zookeeper server: `./bin/zkServer.sh start`   
6. download [kafka](https://kafka.apache.org/downloads)
7. extract it
8. make scripts executable
9. start kafka server: `./bin/kafka-server-start.sh config/server.properties` (this will start the Kafka server and listen for connections on the default port (9092))