
***************************************************************
coordinator port is 9090 (SO DONT USE THIS FOR OTHER SERVERS) *
**************************************************************

HOW TO RUN SERVER

Put all the jars and all the .java files in a single folder
Navigate using terminal to that folder

Compiling: javac -cp .:*: MultiThreadedServer.java -Xlint:unchecked

Executing: java -cp .:*: MultiThreadedServer 9091

Here, 9091

 is portnumber.

HOW TO RUN CLIENT:

Compiling: javac -cp .:*: Client.java
Running : java -cp .:*: Client 1234

Here, 1234 is portnumber of server to connect to


HOW TO RUN COORDINATOR:

Compiling: javac -cp .:*: MultiThreadedCoordinator.java -Xlint:unchecked
Executing: java -cp .:*: MultiThreadedCoordinator 9091 9092 9093 9094 9095

hERE,9091 9092 9093 9094 9095 ARE THE PORTS OF THE SERVER IN THE SYSTEM

YOU NEED TO PUT PORTS OF ALL THE SERVERS WHILE RUNNING THE CORDINATOR


NOTE:

YOU MAY SEE SOME "log4j:WARN No appenders could be found" warning on server AND COORDINATOR. but its fine the server is running.
