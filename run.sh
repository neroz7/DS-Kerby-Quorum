#!/bin/sh

time_per_mvn=6;

cd station-ws
gnome-terminal -e "mvn compile exec:java"
sleep $time_per_mvn;
gnome-terminal -e "mvn compile exec:java -Dws.i=2"
sleep $time_per_mvn;
gnome-terminal -e "mvn compile exec:java -Dws.i=3"
sleep $((2*$time_per_mvn));
cd ..
cd binas-ws
gnome-terminal -e "mvn compile exec:java"
cd ..
cd binas-ws-cli
sleep $((2*$time_per_mvn));
#gnome-terminal -e "mvn compile exec:java"
gnome-terminal -e "mvn verify"
