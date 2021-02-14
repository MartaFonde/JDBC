import java.sql.*;

public class EjerBuscaDato {

    static String url;
    static String bd;
    static String usuario;
    static String password;
    static String servidor;

    static PreparedStatement psPatron = null;
 
    static void search(String bd, String patron) {
        String tabla, columna;
        try (Connection c = DriverManager.getConnection(url, usuario, password); 
            Statement sta = c.createStatement()) {
            DatabaseMetaData mt = c.getMetaData();
            ResultSet rs = mt.getTables(bd, null, "%", null);

            while (rs.next()) {
                if (rs.getString("TABLE_TYPE").equals("TABLE")) {
                    tabla = rs.getString("TABLE_NAME");
                    ResultSet cols = c.getMetaData().getColumns(bd, null, rs.getString("TABLE_NAME"), "%");
                    while (cols.next()) {
                        if (cols.getString("TYPE_NAME").equals("CHAR")
                                || cols.getString("TYPE_NAME").equals("VARCHAR")) {
                            columna = cols.getString("COLUMN_NAME");
                            // psFilasPatron(bd, tabla, columna, patron);
                            ResultSet rss = sta.executeQuery("SELECT " + columna + " FROM " + tabla + " WHERE "
                                    + columna + " LIKE '" + patron + "'");
                            while (rss.next()) {
                                System.out.println("BD: " + bd + "\tTabla: " + tabla + "\tCol: " + columna + "\tDato: "
                                        + rss.getString(1));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.printf("Error1: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
    }

    //NON FUNCIONA. non pilla tabla
    static void psFilasPatron(String bd, String tabla, String col, String patron) {
        try (Connection c = DriverManager.getConnection(url, usuario, password)){
            if (psPatron == null) {
                String query = "select ? from ? where ? like ?";
                psPatron = c.prepareStatement(query);
            }
            psPatron.setString(1, col);
            psPatron.setString(2, tabla);
            psPatron.setString(3, col);
            psPatron.setString(4, patron);
            ResultSet rdo = psPatron.executeQuery();
            while (rdo.next()) {
                System.out.println("BD: " + bd + "\tTabla: " + tabla + "\tCol: " + col + "\tDato: " + rdo.getString(1));
            }
            psPatron.close();
        } catch (SQLException e) {
            System.out.printf("Error2: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
    }


    public static void main(String[] args) {

        bd = "add";
        servidor = "localhost";
        usuario = "root";
        password = "";

        url = String.format("jdbc:mariadb://%s:3306/%s", servidor, bd);

        search("add", "a%");        
    }
}
