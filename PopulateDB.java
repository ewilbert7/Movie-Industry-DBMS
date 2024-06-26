import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Populates the database with the data
 * stored in the 'data' directory
 */
public class PopulateDB {
    
    //directory of the data
   static final File dataDir = new File("data");
   
   /**
    * General method to insert values into 
    * @param elements array of elements to be entered into the table
    * @param statement  SQL statement to use to insert values
    * @param end_index  the index of the last value to be entered in the database
    * @param connection connection to the SQL database
    * @return   true if the operation was successful, otherwise returns false
    * @throws SQLException  if the statement execution causes an SQL error
    */
   public static boolean insertRecordsIntoTable(String[] elements, PreparedStatement statement, int end_index, Connection connection) throws SQLException {
   
       
    try {
        //inserts every element from the start to the end index into the array
        for (int j = 0; j <= end_index; j++) {

            statement.setString(j+1, elements[j] );
 
         }
 
        int rowsAffected = statement.executeUpdate();
        statement.close();

        
        //checks if any rows were affected by the statement execution
        if(rowsAffected > 0){
            return true;
        }
        else return false;
        
     }
     //catches the SQLException caused by the SQL error
     catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    
}

/**
 * Gets the 'Actor_ID' from the 'actors' table for the 'actorName' entered 
 * @param actorName the name of the actor whose ID is being searched for
 * @param conn  connection to the SQLite database
 * @return  the Actor_ID
 * @throws SQLException if the statement causes an SQL error
 */
public static int getActorID(String actorName, Connection conn) throws SQLException{

    int actor_ID = 0;

    try (PreparedStatement actorIDStatement = conn.prepareStatement("SELECT Actor_ID FROM actors WHERE Name = ?")) {
        actorIDStatement.setString(1, actorName);
        ResultSet actorIDResult = actorIDStatement.executeQuery();
        if (actorIDResult.next()) {
            actor_ID = actorIDResult.getInt("Actor_ID");
            
        } 
        return actor_ID;
    }
    catch(SQLException e){
        e.printStackTrace();
        return 0;
    }

}

/**
 * Gets the 'Movie_ID' from the 'movies' table for the 'movieTitle' entered
 * @param movieTitle the title of the movie being searched for 
 * @param conn connection to the SQLite database
 * @return the Movie_ID
 * @throws SQLException if the statement causes an SQL error
 */
public static int getMovieID(String movieTitle, Connection conn) throws SQLException{

    int movie_ID = 0;
     
    try (PreparedStatement movieIDStatement = conn.prepareStatement("SELECT Movie_ID FROM movies WHERE Title = ?")) {
        movieIDStatement.setString(1, movieTitle);
       ResultSet movieIDResult = movieIDStatement.executeQuery();
       if (movieIDResult.next()) {
                movie_ID = movieIDResult.getInt("Movie_ID");
       }
       
       return movie_ID;


    }
    catch(SQLException e){
        e.printStackTrace();
        return 0;
    }
}

/**
 * Gets the 'Award_ID' from the 'awards' table for the 'awardName' entered
 * @param awardName the name of the award being searched for
 * @param conn connection to the SQLite database
 * @return the Award_ID
 * @throws SQLException if the statement causes an SQL error
 */
public static int getAwardID(String awardName, Connection conn) throws SQLException{
    int award_ID = 0;

    try (PreparedStatement awardIDStatement = conn.prepareStatement("SELECT Award_ID FROM awards WHERE Award_Name = ?")) {
        awardIDStatement.setString(1, awardName);
       ResultSet awardIDResult = awardIDStatement.executeQuery();
       if (awardIDResult.next()) {
                award_ID = awardIDResult.getInt("Award_ID");
       } 
       return award_ID;
    }
    catch(SQLException e){
        e.printStackTrace();
        return 0;
    }
}

   
/**
 * inserts the foreign keys into the many-many tables 
 * @param conn connection to the SQLite database
 * @return true if the operation was successful, otherwise returns false
 * @throws SQLException if the SQL command causes an error
 * @throws IOException if the data files are unreadable or non-existant
 */
private static boolean insertForeignKeyValuesIntoTable(Connection conn) throws SQLException, IOException{

    File actorsWithMovies = new File("data/actors_to_movies.csv");
    File actorsWithAwards = new File("data/actors_to_awards.csv");
    File moviesWithAwards = new File("data/movies_to_awards.csv");
    File directorsWithMovies = new File("data/directors.csv");

    BufferedReader actorsMovieReader = new BufferedReader(new FileReader(actorsWithMovies));
    BufferedReader actorsAwardReader = new BufferedReader(new FileReader(actorsWithAwards));
    BufferedReader movieAwardReader = new BufferedReader(new FileReader(moviesWithAwards));
    BufferedReader directorReader = new BufferedReader(new FileReader(directorsWithMovies));

    PreparedStatement actorAwardStatement = conn.prepareStatement("INSERT INTO actors_to_awards VALUES (?, ?)");
    PreparedStatement actorMovieStatement = conn.prepareStatement("INSERT INTO actors_to_movies VALUES (?, ?)");
    PreparedStatement movieAwardStatement = conn.prepareStatement("INSERT INTO movies_to_awards VALUES(?, ?)");

    String line;

    //Sets values to 'actors_to_movies' table
    while((line = actorsMovieReader.readLine()) != null) {
        String[] record = line.split(",");
        String actorName = record[0];
        String movieTitle = record[1];

          //Sets the actor ID or skips if no ID was found
         int actor_ID = getActorID(actorName, conn);
         if(actor_ID == 0) continue;

        //Sets the movie ID or skips if no ID was found
        int movie_ID = getMovieID(movieTitle, conn);
        if(movie_ID == 0) continue;
        

        actorMovieStatement.setInt(1, actor_ID);
        actorMovieStatement.setInt(2, movie_ID);
        int rowsAffected = actorMovieStatement.executeUpdate();

        if(!(rowsAffected > 0)) return false;

    }

    actorMovieStatement.close();
    actorsMovieReader.close();

    //Sets the values to 'movies_to_awards'
    while((line = movieAwardReader.readLine()) != null) {
        String [] record = line.split(",");
        String movieTitle = record[0];
        String awardName = record[1];
        boolean hasAwards = (awardName.equals("No awards")) ? false : true;

        if(hasAwards){
            //Sets the movie ID or skips if no ID was found
            int movie_ID = getMovieID(movieTitle, conn);
                if(movie_ID == 0) continue;
            //Sets the award ID or skips if no ID was found
            int award_ID = getAwardID(awardName, conn); 
                if(award_ID == 0) continue;

        movieAwardStatement.setInt(1, movie_ID);
        movieAwardStatement.setInt(2, award_ID);
        int rowsAffected = movieAwardStatement.executeUpdate();
        
        //checks if any rows were affected by the operation
         if(!(rowsAffected > 0)) return false;
        }
        
    }
    
   movieAwardStatement.close();
   movieAwardReader.close();

   //Sets the valyes to 'actors_to_awards'
    while((line = actorsAwardReader.readLine()) != null) {
        String [] record = line.split(",");
        String actorName = record[0];
        String awardName = record[1];
        
        //Sets the 'actorID' or skips if no ID was found
        int actor_ID = getActorID(actorName, conn);
        if(actor_ID == 0) continue;

        //Sets the 'awardID' or skips if no ID was found
        int award_ID = getAwardID(awardName, conn);
        if(award_ID == 0) continue;


        actorAwardStatement.setInt(1, actor_ID);
        actorAwardStatement.setInt(2, award_ID);
        int rowsAffected = actorAwardStatement.executeUpdate();

        //checks if any rows were affected by the operation
        if(!(rowsAffected > 0)) return false;

     }
        actorAwardStatement.close();
        actorsAwardReader.close();

        //Sets the director IDs in the 'movies' table
        String updateQuery = "UPDATE movies "
                           + "SET Director_ID = (SELECT Director_ID FROM directors WHERE Movie_Title = movies.Title)";
        
        try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
            // Execute the update query
            int rowsAffected = updateStatement.executeUpdate();
            
            //checks if any rows were affected by the statement
            if(!(rowsAffected > 0)) return false;
        }
        
        return true;
 }

    /**
     * Populates the 'ratings' table 
     * @param conn connection to the SQLite database
     * @return true or false depending on whether the operation was successful or not
     * @throws SQLException if the SQL command throws an error
     */
    private static boolean populateRatingsTable(Connection conn) throws SQLException{

        PreparedStatement stmt = null;

        try {
    
            // Prepare the statement for inserting data into the ratings table
            stmt = conn.prepareStatement("INSERT INTO ratings (Rating_Value, Rating_Source, Movie_ID) VALUES (?, ?, ?)");

            // Read data from the file and insert into the ratings table
            BufferedReader reader = new BufferedReader(new FileReader("data/ratings.csv"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                double ratingValue = Double.parseDouble(data[0]);
                String ratingSource = data[1];
                String movieTitle = data[2];;

                int movieId = getMovieID(movieTitle, conn);

                    // Insert the rating data into the ratings table
                    stmt.setDouble(1, ratingValue);
                    stmt.setString(2, ratingSource);
                    stmt.setInt(3, movieId);
                   int rowsAffected =  stmt.executeUpdate();

                   //checks if any rows were affected by the operation
                   if(!(rowsAffected > 0)) return false;

            }

            // Close resources
            reader.close();
            stmt.close();
        
        return true;

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }

    }
                
    /**
     * Main method: goes through all the files in the data directory
     * reads in its contents and populates the appropriate table
     * prints out success or error messages depending on whether each 
     * operation was successful or not
     * @param args  command-line arguments
     * @throws SQLException if any statement throws an SQL error
     * @throws IOException  if the data directory is unreadabale or does not exist
     */
    public static void main(String[] args) throws SQLException, IOException {

        if(args.length < 1) {
            System.out.println("Usage: java PopulateDB <database_file_name>");
            return;
        }

        String dbFileName = args[0];
        File dbFile = new File(dbFileName);

        if(!(dbFile.exists())){
            System.err.println("Database \"" + dbFileName + "\" has not been initialized");
            return;
        }

        String dbUrl = "jdbc:sqlite:"+args[0];
        
        Connection connection = DriverManager.getConnection(dbUrl);

        if(dataDir.exists() && dataDir.canRead()){

            boolean recordsInserted = true;

            for(File file :  dataDir.listFiles()){

                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;

                while((line = reader.readLine()) != null) {

                    String [] dataArr = line.split(",");

                    //preparedStatement and end_index to be initialized later
                    PreparedStatement preparedStatement;
                    int end_index;

                    //checks the file name and sets the preparedStatement and end index depending on the table
                    switch(file.getName()){
                        case "actors.csv":
                            preparedStatement = connection.prepareStatement("INSERT INTO actors VALUES(NULL,?,?)");
                            end_index = 1;
                            break;
                        case "awards.csv":
                            preparedStatement = connection.prepareStatement("INSERT INTO awards VALUES(NULL, ?)");
                            end_index = 0;
                            break;
                        case "directors.csv":
                            preparedStatement = connection.prepareStatement("INSERT INTO directors VALUES(NULL, ?, ?)");
                            end_index = 1;
                            break;
                        case "movies.csv":
                            preparedStatement = connection.prepareStatement("INSERT INTO movies VALUES(NULL, ?, ?, ?, ?, ?, NULL)");
                            end_index = 4;
                            break;
                        case "ratings.csv":
                            continue;
                        case "actors_to_awards.csv":
                            continue;
                        case "movies_to_awards.csv":
                            continue;
                        case "actors_to_movies.csv":
                            continue;
                        default:
                            System.err.println("Unexpected data file found in 'data' directory.");
                            return;
                    
                    }
                
                    //checks if the record insertion failed, if so sets 'recordsInserted' to false
                    if(!insertRecordsIntoTable(dataArr, preparedStatement, end_index, connection)) {

                        recordsInserted = false;
                        break; //prevents the boolean from being reset to false
                    } 
                }

                reader.close();

                //if the records were not inserted
                if(!recordsInserted) System.out.println("Records failed to be inserted into database from " + file.getName());
                else System.out.println("Records inserted successfully from " + file.getName());
            }

            //checks if the foreign key insertion or 'ratings table' insertion failed
            if(!insertForeignKeyValuesIntoTable(connection) || !populateRatingsTable(connection)){
                recordsInserted = false;
            }
            if(!recordsInserted) System.out.println("Foreign key values failed to be inserted.");
            else System.out.println("Foreign key values inserted succesfully.");
          
        }
        else System.out.println("Directory does not exist");
       

        }

    }

         




