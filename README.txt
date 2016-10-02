Iteration #1
Group 7
SYSC 3303 L2
Andrew Ward, Alex Hoecht, Connor Emery, Robert Graham, Saleem Karkabi
100898624,   100933730,   100980809,    100981086,     100944655
Fall Semester 2016

Contains the following files:
Client.java - Read/Write/Invalid requests
ErrorSimulator.java - Transfers requests between Client and Server
Server.java - Creates ACK's to Read/Write/Invalid requests
InputChecker.java - Used to stop the server from running
UML collaboration diagrams for the above classes
UCM Diamgram of the entire system
UML Diagrams for each class

How to Run the project
0. Set a project file path in the Server Class using file explorer (May need to change \'s to /'s, path must end with /)
1.Run the program as a Java Application
2.Compile and Run the Server.java file
3.Compile and Run the ErrorSimulator.java file
4.Compile and Run the Client.java file 
5.Choose Normal Mode then Verbose Mode
6. ****Client must write to Server first****
7. use test.txt as filename
8. Client will then write file to a Server Directory (Currently only works for files <512. files > 512 will be added in iteration 2)
9. The file will then be available to be read.

****NOTICE****
The Read from the Server currently does not work but it is being worked on and will be finished


Project Responsibilities

Andrew Ward:
  Assignment #1 used, UCM Diagram for entire System, UML Diagrams for each Class, Error Simulator Class
  
Alex Hoecht:
  Server Class - adding comments, implementing a directory, and handeling different data packets.
  
Connor Emery:
  Client Class
  
Robert Graham:
  Server CLass - adding comments, implementing a "nice" way to kill the thread.
  
Salem Karkabi:
  Client Class
