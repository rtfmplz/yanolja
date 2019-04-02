#!/bin/bash

echo "+--------------------------------------------+"
echo "|  Initialize Docker container for Database  |"
echo "+--------------------------------------------+"


pushd ./infra/

docker stop mariadb
docker rm mariadb
docker stop adminer
docker rm adminer
docker-compose -f mariadb-compose.yaml up -d --build

popd