#!/usr/bin/env bash

IFSBAK=$IFS
IFS=""
# Load config from files
ROUTER1_OPTIONS=$(cat ./router1.conf)
IFS=$IFSBAK

broker=$(docker run --name broker1 -e QPIDD_ADMIN_USERNAME=admin -e QPIDD_ADMIN_PASSWORD=123456 -e QPIDD_ACL_RULES="acl allow all all" -d scholzj/qpid-cpp:1.36.0)
gui=$(docker run --name gui --link broker1:broker1 -p 8080:8080 -e QMF_GUI_ADMIN_USERNAME=admin -e QMF_GUI_ADMIN_PASSWORD=123456 -d scholzj/qpid-cpp-gui:0.32 -a admin/123456@broker1:5672)
queue=$(docker exec -i broker1 qpid-config -a admin/123456@localhost:5672 add queue myQueue)
router=$(docker run --name router1 --link broker1:broker1 -p 5672:5672 -e QDROUTERD_ADMIN_USERNAME=admin -e QDROUTERD_ADMIN_PASSWORD=123456 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER1_OPTIONS" -d scholzj/qpid-dispatch:0.7.0)
