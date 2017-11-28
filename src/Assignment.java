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

public class Assignment {

    private static final String CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    private static final String INSERTION = "INSERT INTO MEDIAITEMS (TITLE, PROD_YEAR) VALUES (?,?)";
    private static final String INSERT_SIMILARITY = "INSERT INTO similarity VALUES(?,?,SimCalculation(?,?,MaximalDistance()))";
    private static final String SELECT = "SELECT MID,PROD_YEAR FROM MEDIAITEMS";
    private static final String SELECT_SIMILAR = "select TITLE from mediaitems, similarity "
                                                + "where ((similarity.mid1=?"
                                                + "and similarity.similarity is not null) and similarity.similarity<>0"
                                                + "and mediaitems.mid=similarity.mid2 )"
                                                + "or ((similarity.mid2=?"
                                                + "and similarity.similarity is not null) and similarity.similarity<>0"
                                                + "and mediaitems.mid=similarity.mid1 )"
                                                + "order by SIMILARITY desc";

    private Connection driver;
    private String _username;
    private String _password;
    private String _url;

    private Connection connect() throws SQLException{ return DriverManager.getConnection(_url, _username, _password); }

    public Assignment(String connection, String db_username, String db_password){
        _url = connection;
        _username = db_username;
        _password = db_password;
    }

    public void fileToDataBase(String path){

        String title;
        String year;
        PreparedStatement insert = null;
        BufferedReader buffer = null;

        try{
            //Initiating Connection
            Class.forName(CLASS_NAME);
            driver = connect();

            //Opening The File
            File file = new File(path);
            buffer  = new BufferedReader(new FileReader(file));

            //Query Constructing
            insert = driver.prepareStatement(INSERTION);

            String line;
            while (null != (line = buffer.readLine())) {
                String[] parsed = line.split(",");
                title = parsed[0];
                year = parsed[1];
                insert.setString(1, title);
                insert.setString(2, year);
                insert.executeUpdate();
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
                insert.close();
                if (null != driver) driver.close();
            }

            catch(Exception ex){
                System.out.println("Oh man...somthing bad happened, somthing REAL bad...\\n[Exception Stack Trace]: " + ex.getStackTrace());
            }
        }
    }

    public void calculateSimilarity(){

        PreparedStatement select = null;
        PreparedStatement insert = null;
        ResultSet result;

        try{
            Class.forName(CLASS_NAME);
            driver = connect();

            //Initiating selection query
            select = driver.prepareStatement(SELECT);

            //Initiation insertion query
            insert = driver.prepareStatement(INSERT_SIMILARITY);

            //Retrieving all MIDS from the DB
            result = select.executeQuery();

            ArrayList<Integer> a_mids = new ArrayList<>();
            while (result.next()){
                a_mids.add(result.getInt("MID"));
            }

            for (int i = 0; i < a_mids.size(); i++){
                for (int j = 0; j < a_mids.size(); j++){
                    insert.setInt(1, a_mids.get(i));
                    insert.setInt(2, a_mids.get(j));
                    insert.setInt(3, a_mids.get(i));
                    insert.setInt(4, a_mids.get(j));
                    insert.executeUpdate();
                }
            }
        }

        catch (Exception ex){
            System.out.println("Something went wrong...\n[Exception]: " + ex.getStackTrace());
        }

        finally{
            try{
                select.close();
                insert.close();
                if (null != driver) driver.close();
            }

            catch (Exception ex){
                System.out.println("An error! oh no!\n[Exception]: " + ex.getStackTrace());
            }
        }
    }

    public void printSimilarItems(long mid){

        PreparedStatement select = null;
        ResultSet result = null;

        try{
            Class.forName(CLASS_NAME);
            driver = connect();
            select = driver.prepareStatement(SELECT_SIMILAR);
            select.setLong(1, mid);
            select.setLong(2, mid);

            result = select.executeQuery();

            while (result.next()) {
                System.out.println(result.getString("title"));
            }
        }

        catch(Exception ex){
            System.out.println("You tried retrieving the similar titles, but something went wrong...\n[Exception]: " + ex.getStackTrace());
        }

        finally {
            try{
                select.close();
                result.close();
                if(null != driver) driver.close();
            }
            catch (Exception ex){
                System.out.println("Your'e no gonna believe it, an error!\n[Exception]: " + ex.getStackTrace());
            }
        }
    }
}
