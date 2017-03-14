#!/usr/bin/env bash

docker stop router1
docker stop router2
docker stop router3
docker stop router4

docker rm router1
docker rm router2
docker rm router3
docker rm router4
