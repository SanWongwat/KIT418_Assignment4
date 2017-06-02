ECHO OFF
:: java -jar [master jar file] [master port] [max process] [workerlist]
java -jar master.jar 1250 5 \workerlist.txt
PAUSE