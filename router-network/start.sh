#!/usr/bin/env bash

IFSBAK=$IFS
IFS=""
# Load config from files
ROUTER1_OPTIONS=$(cat ./router1.conf)
ROUTER2_OPTIONS=$(cat ./router2.conf)
ROUTER3_OPTIONS=$(cat ./router3.conf)
ROUTER4_OPTIONS=$(cat ./router4.conf)
IFS=$IFSBAK

router4=$(docker run --name router4 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER4_OPTIONS" -d scholzj/qpid-dispatch:0.7.0)
router3=$(docker run --name router3 --link router4:router4 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER3_OPTIONS" -d scholzj/qpid-dispatch:0.7.0)
router2=$(docker run --name router2 --link router4:router4 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER2_OPTIONS" -d scholzj/qpid-dispatch:0.7.0)
router1=$(docker run --name router1 --link router2:router2 --link router3:router3 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER1_OPTIONS" -d scholzj/qpid-dispatch:0.7.0)
