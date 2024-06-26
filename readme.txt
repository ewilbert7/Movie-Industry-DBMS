CS1003 ASSIGNMENT 2 
___________________________________________
Student ID: 230032713 
____________________________________________


Please make sure that you are in the 'src' directory before attempting to run

SETTING THE CLASSPATH:
----------------------
Run the following commands in the terminal to set the classpath BEFORE running the program

export CLASSPATH=$CLASSPATH:./slf4j-api-1.7.36.jar
export CLASSPATH=$CLASSPATH:./slf4j-nop-1.7.36.jar
export CLASSPATH=$CLASSPATH:./sqlite-jdbc-3.45.1.0.jar
export CLASSPATH=$CLASSPATH:./junit-4.13.2.jar
export CLASSPATH=$CLASSPATH:./hamcrest-all-1.3.jar

HOW TO RUN:
-----------
1. In the Terminal, compile all programs by typing "javac *.java"

2. Initialise the database by typing "java InitialiseDB <database_file_name>"
e.g. java InitialiseDB movies

3. Populate the database by typing "java PopulateDB <database_file_name>"
e.g. java PopulateDB movies

4. Query the database by typing "java QueryDB <database_file_name> <query-number>"
e.g. java QueryDB movies 1 


NOTE: IF DATABASE HAS NOT BEEN INITIALIZED BEFORE 3 AND 4 AN ERROR MESSAGE WILL BE SHOWN