import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Assignment {

    private final String INSERTION = "INSERT INTO MEDIAITEMS (TITLE, PROD_YEAR) VALUES (?,?)";



    private Connection driver;
    private String _username;
    private String _password;
    private String _url;

    public Assignment(String connection, String db_username, String db_password /*What about security?!*/){
        _url = connection;
        _username = db_username;
        _password = db_password;
    }

    public void fileToDataBase(String path){

        String title;
        String year;
        PreparedStatement statement = null;
        BufferedReader buffer = null;

        try{
            //Initiating Connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            driver = DriverManager.getConnection(_url, _username, _password);

            //Opening The File
            File file = new File(path);
            buffer  = new BufferedReader(new FileReader(file));

            //Query Constructing
            statement = driver.prepareStatement(INSERTION);

            String line;
            while ((line = buffer.readLine()) != null) {
                String[] parsed = line.split(",");
                title = parsed[0];
                year = parsed[1];
                statement.setString(1, title);
                statement.setString(2, year);
                statement.executeUpdate();
            }
        }

        catch(IOException ioex){
            System.out.println("Oops, youre file could not be opened\\n[Exception Stack Trace]: " + ioex.getStackTrace());
        }

        catch(Exception ex){
            System.out.println("Oh snap!, We encountered an error..\\n[Exception Stack Trace]: " + ex.getStackTrace());
        }

        finally{

            try{
                buffer.close();
                statement.close();
                if (null != driver) driver.close();
            }

            catch(Exception ex){
                System.out.println("Oh man...somthing bad happened, somthing REAL bad...\\n[Exception Stack Trace]: " + ex.getStackTrace());
            }
        }
    }

    public void calculateSimilarity(){

    }

    public void printSimilarItems(long mid){

    }

}
