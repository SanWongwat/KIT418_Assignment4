ECHO OFF
:: java -jar [worker jar file] [worker ip] [worker port] [wordcount jar file] [wordcount initial port]
java -jar worker.jar 192.168.1.9 1260 \mockwordcount.jar 1270
PAUSE