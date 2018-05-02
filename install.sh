#!/bin/sh

time_per_mvn=6;

mvn clean 

cd uddi-naming
mvn install
cd ../station-ws
mvn generate-sources
gnome-terminal -e "mvn compile exec:java"
sleep $time_per_mvn
cd ../station-ws-cli
mvn generate-sources
mvn install

cd ../binas-ws
mvn generate-sources
cd ../binas-ws-cli
mvn generate-sources
cd ..

