#!/usr/bin/env bash

docker exec -i broker1 qpid-config -b admin/123456@localhost:5672 list connection
