
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



/**
 * Queries the database to perform tasks
 * as said in the specification
 */
public class QueryDB {

    //field for the url to the SQLite database
    static final String dbUrl = "jdbc:sqlite:movies";

    /**
     * List all the movies in the database
     * @param conn connection to the database
     * @throws SQLException if the statement throws an SQL error
     */
    private static void listAllMovies(Connection conn) throws SQLException{
        
        try (Statement statement = conn.createStatement();) {
            ResultSet rs = statement.executeQuery("SELECT Title FROM movies");
            System.out.println("Movies in Database:");
            int counter = 1;
            while(rs.next()){
                String movieTitle = rs.getString("Title");
                System.out.println(counter + ". " + movieTitle);
                counter++;
            }
            
        } catch (SQLException e) {
                e.printStackTrace();
        }
    
        
    }

    /**
     * List all the actors that acted in a particular movie
     * @param conn connection to the database
     * @param movieTitle the title of the movie 
     */
    private static void listAllActorsInMovie(Connection conn, String movieTitle) {

        String getMovieIDQuery = "SELECT Movie_ID FROM movies WHERE Title = ?";
        String getActorRowsQuery = "SELECT * FROM actors_to_movies WHERE Movie_ID = (?)";

        try (PreparedStatement movieIDStatement = conn.prepareStatement(getMovieIDQuery); 
        PreparedStatement actorsRowsStatement = conn.prepareStatement(getActorRowsQuery)) {
            movieIDStatement.setString(1, movieTitle);
            int movie_ID = movieIDStatement.executeQuery().getInt("Movie_ID");
            
            actorsRowsStatement.setInt(1, movie_ID);
            ResultSet actorsResultSet = actorsRowsStatement.executeQuery();
            boolean hasRows = actorsResultSet.isBeforeFirst();
            if(!hasRows){
                System.out.println("No actors were found for \"" + movieTitle + "\" in the database");
            } else {
                System.out.println("The actors/actresses that acted in " + movieTitle + " are:");
                while(actorsResultSet.next()){
                    PreparedStatement getActorNameStatement = conn.prepareStatement("SELECT Name FROM actors WHERE Actor_ID = ?");
                    
                    int actor_ID = actorsResultSet.getInt("Actor_ID");
                    getActorNameStatement.setInt(1, actor_ID);
                    String actorName = getActorNameStatement.executeQuery().getString("Name");
    
                    System.out.println(actorName);
                    
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    /**
     * Get the plot of a movie with a particular actor and director
     * @param actorName name of the actor
     * @param directorName name of the director
     * @param conn connection to the database
     * @throws SQLException if the SQL statement throws an SQL error
     */
    private static void getPlotForMovieWithActorAndDirector(String actorName, String directorName, Connection conn) throws SQLException{

        //check if there is a movie with the actor and director passed in the method 
        String query = "SELECT Plot " +
               "FROM (SELECT movies.Movie_ID, Plot, directors.Director_ID, Actor_ID " +
                     "FROM movies " +
                     "INNER JOIN actors_to_movies ON movies.Movie_ID = actors_to_movies.Movie_ID " +
                     "INNER JOIN directors ON movies.Director_ID = directors.Director_ID) AS subquery " +
               "WHERE Actor_ID = ? AND subquery.Director_ID = ?";

        PreparedStatement plotPreparedStatement = conn.prepareStatement(query);

        int actor_ID;
        try (PreparedStatement actorIDStatement = conn.prepareStatement("SELECT Actor_ID FROM actors WHERE Name = ?")) {
            actorIDStatement.setString(1, actorName);
            ResultSet actorIDResult = actorIDStatement.executeQuery();
            if (actorIDResult.next()) {
                actor_ID = actorIDResult.getInt("Actor_ID");
            } else {
                // Actor not found
                System.out.println("Actor \"" + actorName + "\" not found in database");
                return;
            }
        }

        int director_ID;
        try (PreparedStatement directorIDStatement = conn.prepareStatement("SELECT Director_ID FROM directors WHERE Name = ?")) {
            directorIDStatement.setString(1, directorName);
            ResultSet directorIDResult = directorIDStatement.executeQuery();
            if (directorIDResult.next()) {
                director_ID = directorIDResult.getInt("Director_ID");
            } else {
                System.out.println("Director \"" + actorName + "\" not found in database");
                return;
            }
        }

        plotPreparedStatement.setInt(1, actor_ID);
        plotPreparedStatement.setInt(2, director_ID);
        ResultSet plots = plotPreparedStatement.executeQuery();

        while(plots.next()){
            String plot = plots.getString("Plot");
            int counter = 1;
            System.out.println("Plot "+ counter + ": " + plot );
            counter++;
        }

    }


    /**
     * Gets the director of movies with a particular actor
     * @param actorName name of the actor
     * @param conn connection the database
     * @throws SQLException if the statement throws an SQL error
     */
    private static void getDirectorOfMoviesWithActor(String actorName, Connection conn) throws SQLException{
        
        String query = "SELECT DISTINCT directors.Name AS Director_Name " +
                        "FROM movies " +
                        "INNER JOIN actors_to_movies ON movies.Movie_ID = actors_to_movies.Movie_ID " +
                        "INNER JOIN directors ON movies.Director_ID = directors.Director_ID " +
                        "INNER JOIN actors ON actors_to_movies.Actor_ID = actors.Actor_ID " +
                        "WHERE actors.Name = ?";

        PreparedStatement directorStatement = conn.prepareStatement(query);
        directorStatement.setString(1, actorName);
        
        ResultSet rs = directorStatement.executeQuery();

        System.out.println("Directors of movie with " + actorName + " : ");
        
        int counter = 1;
        while((rs.next())) {
            String directorName = rs.getString("Director_Name");
            System.out.println("Director " + counter + ": " + directorName);
            counter++;
        }

        if (counter == 1){
            System.out.println("No movies found with actor " + actorName);
        }

        directorStatement.close();

    }
    
    /**
     * Lists all the actors who acted in movies of a particular genre with awards 
     * @param genre the genre of the movies
     * @param conn  connection to the database
     * @throws SQLException if the statement throws an SQL error
     */
    private static void listActorsWithAwardsInGenre(String genre, Connection conn) throws SQLException{

        String query = "SELECT DISTINCT a.Name "+
                       "FROM actors a " +
                       "JOIN actors_to_awards ata ON a.Actor_ID = ata.Actor_ID " +
                       "JOIN actors_to_movies atm ON a.Actor_ID = atm.Actor_ID " +
                       "JOIN movies m ON atm.Movie_ID = m.Movie_ID " +
                       "WHERE m.Genre = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, genre);

        ResultSet actorNames = stmt.executeQuery();

        System.out.println("The actors that have won awards and acted in \"" + genre + "\" movies");
        int counter = 1;
        while((actorNames.next())){
            String actorName = actorNames.getString("Name");
            System.out.println(counter + ". " + actorName);
            counter++;
        }

        if(counter==1){
            System.out.println("No actors have won awards and acted in \"" + genre + "\" movies.");
        }

                       
        
    }

    /**
     *Lists the directors of all the movies with a rating above a certain number and awards 
     * @param rating the rating of the movie being searched for
     * @param conn connection to the SQlite database
     * @throws SQLException if the SQL command throws an error
     */
    private static void listDirectorsOfMoviesWithAwardsAndRating(Double rating, Connection conn) throws SQLException{

        String query = "SELECT DISTINCT d.Name " +
                       "FROM directors d " +
                       "JOIN movies m ON m.Title = d.Movie_Title " +
                       "JOIN ratings r ON r.Movie_ID = m.Movie_ID " +
                       "JOIN movies_to_awards mta ON mta.Movie_ID = m.Movie_ID " +
                       "WHERE r.Rating_Value > ?";
                       
                       
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDouble(1, rating);
        ResultSet directorNames = stmt.executeQuery();

        System.out.println("The directors of movies that have won awards and have a rating over " + rating + " are: " );

        int counter = 1;
        while((directorNames.next())){
            String directorName = directorNames.getString("Name");
            System.out.println(counter + ". " + directorName);
            counter++;
        }

        if(counter == 1){
            System.out.println("No directors have directed movies with awards and a rating over " + rating + ".");
        }
        
    }



    /**
     * Main method: executes the corresponding query depending on 
     * the user input
     * @param args command-line arguments
     * @throws IOException if any sub-method throws an I/O error
     * @throws SQLException if any SQL command in any sub-method throws an SQL error
     */
    public static void main(String[] args) throws IOException, SQLException{

       if(args.length != 2) {
        System.out.println("Usage: java QueryDB <database_file_name> <query_number>");
        return;
       }

       String dbFileName = args[0];
       String dbUrl = "jdbc:sqlite:"+dbFileName;
       File dbFile = new File(dbFileName);
       if(!(dbFile.exists())){
            System.err.println("Database \"" + dbFileName +"\" has not been initialized");
            return;
       }

       Connection conn = DriverManager.getConnection(dbUrl);
       Scanner scanner = new Scanner(System.in);
       String option = args[1];

        switch(option){
            case "1":
                //list all the movies in the database
                listAllMovies(conn);
                break;

            case "2":
                //list all the actors in a movie
                System.out.print("Enter movie name: ");
                String movieTitle = scanner.nextLine();
                listAllActorsInMovie(conn, movieTitle); //include a check to whether movie is in the db in method
                break;

            case "3":
                //get the plots/synopses of all the movies with a particular actor and a particular director
                System.out.print("Enter actor's/actress' name: ");
                String actorName = scanner.nextLine();
                System.out.print("Enter director's name: ");
                String directorName = scanner.nextLine();
                getPlotForMovieWithActorAndDirector(actorName, directorName, conn);

                break;
            case "4":
                //get the director of all movies where a certain actor appears
                System.out.print("Enter actor's/actress' name: ");
                actorName = scanner.nextLine();
                getDirectorOfMoviesWithActor(actorName, conn);
                break;

            case "5":
                String genre = "Action";
                //list the actors that have won awards and acted in movies of a certain genre 
                listActorsWithAwardsInGenre(genre, conn);
                break;

            case "6":
                //list the directors of all movies that have won awards and have ratings higher than 7.0 
                Double rating = 7.0;
                listDirectorsOfMoviesWithAwardsAndRating(rating, conn);
                
            break;
            default:
                System.err.println("Invalid value entered.");
                System.exit(0);
            break;
            
        }
        scanner.close();
    }
    
}
