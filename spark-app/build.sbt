name := "spark-app"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-sql" % "2.4.0",
    "mysql" % "mysql-connector-java" % "8.0.15"
)