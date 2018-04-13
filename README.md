# Projeto de Sistemas Distribuídos 2017/18 #

Grupo A09

Implement a distributed system for bicycles entreprise Binas

Pedro Mela ist178876 pedro.mela@ist.utl.pt

Frederico Delaeare ist179674 frederico.delaeare@gmail.com

Mustafa Samir Khalil ist427068 mustafa.samir.khalil@tecnico.ulisboa.pt

Instructions:

1- cd station; mvn clean;mvn generate-sources
   cd station-cli; mvn clean;mvn generate-sources
   cd binas; mvn clean;mvn generate-sources
   cd binas-cli; mvn clean;mvn generate-sources
2- cd station; mvn install; mvn exec:java -Dws.i=[number of the station]
3- cd station-cli; mvn install; mvn exec:java -Dws.i=[number of the station, for each station]
4- cd binas; mvn install; mvn exec:java
5- cd binas-cli; mvn verify;
   

-------------------------------------------------------------------------------
**FIM**
