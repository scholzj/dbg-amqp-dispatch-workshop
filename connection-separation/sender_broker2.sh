#!/usr/bin/env bash

docker run -t -i --link broker2:broker2 -e QPID_LOG_ENABLE=trace+ scholzj/qpid-cpp:latest qpid-send -b admin/123456@broker2:5672 --connection-options "{protocol: amqp1.0}" -a "myQueue2; {node: { type: queue }, create: never, assert: never}" -m 1 --content-string "Hello world on broker 2"
