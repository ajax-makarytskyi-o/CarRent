#!/bin/zsh

docker-compose -f docker-compose.yml --profile test up -d
docker exec test_mongo mongosh -u test -p test --eval 'db.dropDatabase()'

./gradlew build -parallel

docker build -t domainservice domainservice/
docker build -t gateway gateway/

docker-compose -f docker-compose.yml --profile test down

docker-compose -f docker-compose.yml --profile dev up --force-recreate
