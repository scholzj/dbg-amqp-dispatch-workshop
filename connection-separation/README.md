# Connection Separation

* Start brokers and routers:
```
./start.sh
```
* Start the receiver (ConnectionSeparationDemonstrator)
* Send message to first broker:
```
./send_broker1.sh
```
* Send message to second broker:
```
./send_broker2.sh
```
* Show how the client received both messages
* Stop everything:
```
./stop.sh
```
