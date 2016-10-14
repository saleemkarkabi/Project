Iteration #2
Group 7
SYSC 3303 L2
Andrew Ward, Alex Hoecht, Connor Emery, Robert Graham, Saleem Karkabi
100898624,   100933730,   100980809,    100981086,     100944655
Fall Semester 2016


Contains the following files:
Client.java         - Sends a read/write request and transfers data to the server
ErrorSimulator.java - Intermediate host between a client and server
Server.java         - Receives requests, establiches initial connection to client, creates a sub server
SubServer.java      - Transfers data to and from the client
InputChecker.java   - Used to stop a host from running via user input
UML collaboration diagrams for the above classes
UCM Diamgram of the entire system
UML Diagrams for each class


How to Run the project
0. Set a project file path in the Server Class using file explorer (May need to change \'s to /'s, path must end with /)
1. Run the program as a Java Application
2. Compile and Run the Server.java file
3. Compile and Run the ErrorSimulator.java file
4. Compile and Run the Client.java file 
5. Choose Normal Mode then Verbose Mode
6. ****Client must write to Server first****
7. use test.txt as filename
8. Client will then write file to a Server Directory (Currently only works for files <512. files > 512 will be added in iteration 2)
9. The file will then be available to be read.


Project Responsibilities

Andrew Ward:
  UCM Diagram for entire System, UML Diagrams for each Class
  
Alex Hoecht:
  Server and client class rewrite
  
Connor Emery:
  Server and client class rewrite
  
Robert Graham:
  Error handling, read me file
  
Salem Karkabi:
  Server and client class rewrite
