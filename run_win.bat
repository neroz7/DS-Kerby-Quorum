SET /A time_per_mvn = 6
SET /A time_per_mvn2 = 12

cd station-ws
start cmd.exe /k "mvn compile exec:java"
call sleep %time_per_mvn%
start cmd.exe /k "mvn compile exec:java -Dws.i=2"
call sleep %time_per_mvn%
start cmd.exe /k "mvn compile exec:java -Dws.i=3"
cd ../binas-ws
call sleep %time_per_mvn2%
start cmd.exe /k "mvn compile exec:java"
cd ../binas-ws-cli
call sleep %time_per_mvn2%
start cmd.exe @cmd /k "mvn verify"