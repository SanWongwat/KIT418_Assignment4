ECHO OFF
:: java -jar [worker jar file] [worker ip] [worker port] [wordcount jar file] [wordcount initial port]
java -jar worker.jar localhost 1250 localhost 1260 \mockwordcount.jar 1270
PAUSE