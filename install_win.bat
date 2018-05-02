SET /A time_per_mvn = 6

call mvn clean 

cd uddi-naming
call mvn generate-sources
call mvn install

cd ../station-ws
call mvn generate-sources
start cmd.exe @cmd /k "mvn compile exec:java"
sleep %time_per_mvn%

cd ../station-ws-cli
call mvn generate-sources
call mvn install

cd ../binas-ws
call mvn generate-sources

cd ../binas-ws-cli
call mvn generate-sources

cd ..
