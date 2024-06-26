import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Initialises the 'movies' database by reading the DDL script
 * to create the relational schema.
 * Deletes the database file if it has already been created
 */
public class InitialiseDB {

    /**
     * Checks if the database file has already been created
     * if so, it is deleted
     * Creates the tables in the database and prints out the success message
     * 
     * @param dbFileName the name of the database file to be created
     * @throws SQLException if createTables throws SQL exception
     */
    public static void initialiseDB(String dbFileName) throws SQLException, IOException {

        // Create a File object to check that the database file exists
        String dbUrl = "jdbc:sqlite:" + dbFileName;

        //checks if the database already exists, if so deletes it
        if (databaseExists(dbUrl)) {
            if (deleteDatabase(dbUrl))
                System.out.println("Database deleted successfully");
            else
                System.out.println("Failed to delete database");

        }
        
        //tries to establish a conneciton to the SQLite database
        try (Connection connection = DriverManager.getConnection(dbUrl)) {

            //creates the tables in the database
            createTables(connection); 

            // testing function that checks that everything was created properly

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if the database file already exists
     * @param dbUrl the JDBC url to the SQLite database
     * @return returns true if the database already exists, otherwise returns false
     */
    public static boolean databaseExists(String dbUrl) {

        File dbFile = new File(dbUrl.replace("jdbc:sqlite:", ""));

        return dbFile.exists();

    }

    /**
     * Deletes the database file
     * @param dbUrl the JDBC url to the SQLite database
     * @return  true if the database was deleted successfully, otherwise returns false
     */
    public static boolean deleteDatabase(String dbUrl) {

        File dbFile = new File(dbUrl.replace("jdbc:sqlite:", ""));

        return dbFile.delete();
    }

    /**
     * Creates the tables in the database
     * @param conn  conneciton to the JDBC database
     * @throws SQLException,IOException if there is an SQL error in the DDL script or 
     * the file is unreadable or does not exist
     */
    private static void createTables(Connection conn) throws SQLException, IOException {

        Statement statement = conn.createStatement();
        String ddlFileName = "ddl.sql";

        try {
            //reads the DDL file 
            String ddlString = readScript(ddlFileName); 
            statement.executeUpdate(ddlString);
            statement.close();

            System.out
                    .println("Successfully created tables: actors, movies, movie_industry, awards, directors, ratings");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads each line of a file
     * returns the whole file as a String object
     * @param filepath  the filepath of the file to be read from
     * @return String of the whole file
     * @throws IOException  if the file is inaccessible or does not exist
     */
    public static String readScript(String filepath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();

        }

    }

    /**
     * Main method to read the command-line arguments
     * and initialise the database 
     * @param args  command-line arguments
     * @throws SQLException if there is an SQL error while initialization
     */
    public static void main(String[] args) throws SQLException, IOException {

        if(args.length < 1) {
            System.out.println("Usage: java InitialiseDB <database_file_name>");
            System.exit(0);
           }

        //sets the database file name to the first argument
        String dbFileName = args[0];
        
        initialiseDB(dbFileName);

    }

}