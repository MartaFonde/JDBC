import java.sql.*;

public class EjerSQLite {
    

    public static void main(String[] args) {
        Connection conexion;
        try{
            Class.forName ("org.sqlite.JDBC");
            conexion = DriverManager.getConnection("jdbc:sqlite:/D:/DATOS/JDBC/add.db");
        }catch(SQLException e){
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }catch(ClassNotFoundException o){
            System.out.println("Error :"+o.getMessage());
        }
    }
}


