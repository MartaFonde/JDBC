import java.sql.*;

public class EjerUcanaccess {
    static String bd;
    static String tabla;
    static String query;

    static void consulta(String bd, String tabla, String query) {

        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://"+bd+"/"+tabla);
                Statement sta = conn.createStatement()) {            

            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {
                System.out.println(rs.getString(1) + "\t" + rs.getString(2));
            }
           
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    public static void main(String[] args) {

        // Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

        bd = "data";
        tabla = "Alumnos.mdb";

        query = "select nombre, apellidos from alumnos";

        consulta(bd, tabla, query);
    }
    
}
