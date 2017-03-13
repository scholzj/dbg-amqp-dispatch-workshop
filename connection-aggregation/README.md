# Connection Aggregation

* Start brokers and routers:
```
./start.sh
```
* Open [GUI](http://localhost:8080)
* Send messages:
```
./sender.sh
```
* Receive messages:
```
./receiver.sh
```
* Start multiple receivers:
```
./receiver.sh &
```
* Show connections on the router:
```
./connections_router.sh
```
* Show connections on the broker:
```
./connections_broker.sh
```
* Stop everything:
```
./stop.sh
```
