#!/bin/zsh

docker-compose -f docker-compose.yml --profile test up -d

./gradlew build

docker build -t domainservice domainservice/
docker build -t gateway gateway/

docker-compose -f docker-compose.yml --profile test down

docker-compose -f docker-compose.yml --profile dev up --force-recreate
