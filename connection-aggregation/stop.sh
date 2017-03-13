#!/usr/bin/env bash

docker stop gui
docker stop router1
docker stop broker1

docker rm gui
docker rm router1
docker rm broker1
