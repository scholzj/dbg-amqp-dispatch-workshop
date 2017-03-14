# Queue sharding

* Start brokers and routers:
```
./start.sh
```
* Open [GUI](http://localhost:8080)
* Add second broker to the GUI
* Show the same empty queues on both brokers
* Send messages:
```
./sender.sh
```
* Show the queues with messages on both brokers
* Receive messages:
```
./receiver.sh
```
* Show empty queues on both brokers
* Stop everything:
```
./stop.sh
```
