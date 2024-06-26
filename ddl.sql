CREATE TABLE actors(
    Actor_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
    Name VARCHAR(100) NOT NULL, 
    Birthday DATE 
    );

 CREATE TABLE movies(
    Movie_ID INTEGER PRIMARY KEY AUTOINCREMENT,
     Title VARCHAR(100), Genre VARCHAR(100), 
     Running_Time TIME, Plot VARCHAR(100), 
     Release_Date DATE, 
     Director_ID INTEGER,
     FOREIGN KEY (Director_ID) REFERENCES directors(Director_ID));  

    CREATE TABLE actors_to_movies(
        Actor_ID INTEGER, 
        Movie_ID INTEGER,
        FOREIGN KEY (Actor_ID) REFERENCES actors(Actor_ID), 
        FOREIGN KEY (Movie_ID) REFERENCES movies(Movie_ID));

    CREATE TABLE awards(
        Award_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        Award_Name VARCHAR(100));

    CREATE TABLE actors_to_awards(
        Actor_ID INTEGER,
        Award_ID INTEGER,
        FOREIGN KEY (Award_ID) REFERENCES awards(Award_ID),
        FOREIGN KEY (Actor_ID) REFERENCES actors(Actor_ID)
    );

    CREATE TABLE movies_to_awards(
        Movie_ID INTEGER,
        Award_ID INTEGER,
        FOREIGN KEY (Movie_ID) REFERENCES movies(Movie_ID),
        FOREIGN KEY (Award_ID) REFERENCES awards(Award_ID)
    );

    CREATE TABLE directors(
        Director_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        Name VARCHAR(100),
        Movie_Title VARCHAR(100));

    CREATE TABLE ratings(
        Rating_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
        Rating_Value INTEGER CHECK (Rating_Value >= 0 AND Rating_Value <=10), 
        Rating_Source VARCHAR(100), Movie_ID INTEGER,  
        FOREIGN KEY (Movie_ID) REFERENCES movies(Movie_ID)); 

