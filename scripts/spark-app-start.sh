#!/bin/bash

echo "+---------------------------------------------+"
echo "|     Spark Applicaion package and submit     |"
echo "+---------------------------------------------+"


pushd ./spark-app/

sbt clean package
${SPARK_HOME}/bin/spark-submit \
--class "SparkApplication" \
--master "local[4]" \
--packages "mysql:mysql-connector-java:8.0.15" \
target/scala-2.11/spark-app_2.11-0.1.jar \
"../resources/" \
"root" \
"mariadb" \
"yanolja" \
"mysql://localhost:3306"

popd

