#!/usr/bin/env bash

docker run -e QPID_LOG_ENABLE=trace+ --link router4:router4 -t -i scholzj/qpid-cpp:latest qpid-receive -b router4:5672 --connection-options "{protocol: amqp1.0}" -a "'/myAddress'" -m 1 -f --print-headers yes
