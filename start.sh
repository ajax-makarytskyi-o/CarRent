#!/bin/zsh

docker build -t domainservice domainservice/
docker build -t gateway gateway/

docker-compose -f docker-compose.yml --profile dev up
