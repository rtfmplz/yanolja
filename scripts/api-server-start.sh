#!/bin/bash

pushd ./api-server/

echo "+--------------------------------------------+"
echo "|               Start API Server             |"
echo "+--------------------------------------------+"
sbt run

popd
