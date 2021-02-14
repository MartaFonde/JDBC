import java.sql.*;

public class EjerProcedAlmacenados {

    static String url;
    static String bd;
    static String usuario;
    static String password;
    static String servidor;

    static void executeGetAulas(int puestos, String nombre) {
        try (Connection c = DriverManager.getConnection(url, usuario, password); 
            CallableStatement cs = c.prepareCall("CALL getAulas(?,?)")){            
            cs.setInt(1, puestos);
            cs.setString(2, nombre);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString("nombreAula") + "\t" + rs.getInt("puestos"));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
    }

    static void executeSuma() {
        try (Connection c = DriverManager.getConnection(url, usuario, password);
            PreparedStatement ps = c.prepareStatement("SELECT suma()")){           
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        bd = "add";
        servidor = "localhost";
        usuario = "root";
        password = "";

        url = String.format("jdbc:mariadb://%s:3306/%s", servidor, bd);

        //executeGetAulas(25, "aula");
        executeSuma();

    }
    
}
