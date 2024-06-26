import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PopulateDBTest {

   static String dbFileName = "test-movies";
   static String dbUrl = "jdbc:sqlite:"+dbFileName;
   

   @BeforeClass
   public static void initialiseDB() throws SQLException,IOException{
      InitialiseDB.initialiseDB(dbFileName);
   }

   @Test 
   public void insertRecordsIntoTableTest() throws SQLException{
      Connection conn = DriverManager.getConnection(dbUrl);
      String [] elements = {"Johnny Depp","1963-06-09","Pirates of the Caribbean: The Curse of the Black Pearl","Best Actor"};
      PreparedStatement stmt = conn.prepareStatement("INSERT INTO actors VALUES(NULL,?,?)");
      int end_index = 1;
      assertTrue(PopulateDB.insertRecordsIntoTable(elements, stmt, end_index, conn));
   }

   @Test 
   public void getActorIDTest() throws SQLException{
      Connection conn = DriverManager.getConnection(dbUrl);

      int actor_ID = PopulateDB.getActorID("Johnny Depp", conn);
      assertTrue(actor_ID == 1);
   }
    
   @Before
   public void initialiseMovies() throws SQLException{
      Connection conn = DriverManager.getConnection(dbUrl);
      PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO movies VALUES(NULL, ?, ?, ?, ?, ?, NULL)");
      int end_index = 4;
      String[] elements = {"Pirates of the Caribbean: The Curse of the Black Pearl", "Action", "143 minutes", "Blacksmith Will Turner teams up with eccentric pirate Captain Jack Sparrow to save his love the governor's daughter from Jack's former pirate allies who are now undead.","2003-07-09","Gore Verbinski"};
      PopulateDB.insertRecordsIntoTable(elements, preparedStatement, end_index, conn);
   }

   @Test 
   public void getMovieIDTest() throws SQLException{
      Connection conn = DriverManager.getConnection(dbUrl);

      int movie_ID = PopulateDB.getMovieID("Pirates of the Caribbean: The Curse of the Black Pearl", conn);
      assertTrue(movie_ID == 1);
   }

   @Before
   public void initialiseAwards() throws SQLException{
      Connection conn = DriverManager.getConnection(dbUrl);
      PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO awards VALUES(NULL, ?)");
      int end_index = 0;
      String[] elements = {"Best Actor"};
      PopulateDB.insertRecordsIntoTable(elements, preparedStatement, end_index, conn);

   }
   @Test 
   public void getAwardIDTest() throws SQLException{

      Connection conn = DriverManager.getConnection(dbUrl);

      int award_ID = PopulateDB.getAwardID("Best Actor", conn);
      assertTrue(award_ID == 1);
     
   }

    
}
