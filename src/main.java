public class main {

    private static final String USER = "kulikr";
    private static final String PASSWORD = "abcd";
    private static final String SERVER = "jdbc:oracle:thin:@kulikr.ise.bgu.ac.il:1521/ORACLE";

    public static void main(String args[]){
        Assignment assignment = new Assignment(SERVER, USER, PASSWORD);
        assignment.fileToDataBase("files/films.csv");
        assignment.calculateSimilarity();
    }
}
