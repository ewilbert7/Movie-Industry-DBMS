
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class InitialiseDBTest {

    
    String dbFileName = "test-movies";
    String dbUrl = "jdbc:sqlite:"+dbFileName;
    String testScriptFilePath = "tests/test_script.sql";
    File testScript = new File("tests/test_script.sql");

    
    @Before
    public void createDatabaseFile() throws SQLException, IOException{
        InitialiseDB.initialiseDB(dbFileName);
    }
    
    @Test
    public void checkIfDatabaseExists(){

        assertTrue(InitialiseDB.databaseExists(dbUrl));
    }

    @Test
    public void checkIfDatabaseWasDeleted(){
        InitialiseDB.deleteDatabase(dbUrl);

        File dbFile = new File(dbFileName);
        
        assertFalse(dbFile.exists());
    }







}


