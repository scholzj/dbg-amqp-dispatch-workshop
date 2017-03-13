#!/usr/bin/env bash

docker run -t -i --link router1:router1 -e QPID_LOG_ENABLE=trace+ scholzj/qpid-cpp:latest qpid-send -b router1:5672 --connection-options "{protocol: amqp1.0}" -a "myQueue; {node: { type: queue }, create: never, assert: never}" -m 1000
