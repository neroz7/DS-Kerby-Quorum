# Projeto de Sistemas Distribuídos #


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
