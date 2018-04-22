#!/bin/sh

time_per_mvn=6;

mvn clean generate-sources
cd uddi-naming
mvn install
cd ../station-ws
gnome-terminal -e "mvn compile exec:java"
sleep $time_per_mvn
cd ../station-ws-cli
mvn install
cd ..

