#!/bin/zsh

./gradlew build

if [ $? -eq 0 ]; then
    docker-compose -f docker-compose.yml up
else
    echo "Build failed. Docker Compose will not be run."
fi
