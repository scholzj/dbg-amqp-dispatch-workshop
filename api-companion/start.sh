#!/usr/bin/env bash

IFSBAK=$IFS
IFS=""
# Load config from files
ROUTER1_OPTIONS=$(cat ./router1.conf)
IFS=$IFSBAK

router=$(docker run --name router1 -p 5672:5672 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER1_OPTIONS" -d scholzj/qpid-dispatch:0.7.0)
