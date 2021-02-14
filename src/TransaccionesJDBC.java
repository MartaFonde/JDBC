import java.sql.*;

public class TransaccionesJDBC {

    static String url;
    static String bd;
    static String usuario;
    static String password;
    static String servidor;

    static Connection conexion;
    static ConsultasJDBC j = new ConsultasJDBC();
    Savepoint p;
    PreparedStatement ps = null;
    PreparedStatement psPatron = null;

    static void insertConTransaccion() {
        abrirConexion(bd, servidor, usuario, password);
        try (Statement sta = conexion.createStatement()) {
            conexion.setAutoCommit(false);
            j.insertarAlumno("Alex", "Fonde", 180, 20);
            // p = this.conexion.setSavepoint("sp1");
            sta.executeQuery("INSERT INTO alumnos VALUES (5,'Luca','Smith','Pontevedra', 20)");
            conexion.commit(); // se non se produce ningún erro confírmanse os cambios
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
            try {
                if (conexion != null) {
                    System.out.println("Desfanse os cambios mediante un rollback ");
                    // this.conexion.rollback(p);
                    conexion.rollback(); // se se produciu un erro os cambios desfanse
                }
            } catch (SQLException e2) {
                System.out.println("Erro ao realizar o rollback: " + e.getLocalizedMessage());
            }
        }
        pecharConexion();
    }
    // Para evitar ter que desfacer todos os cambios, creamos SAVEPOINT
        

    // https://www.red-gate.com/simple-talk/sql/learn-sql-server/using-the-for-xml-clause-to-return-query-results-as-xml/
    public void exportXML(String bd, String tabla) {

    }

    static void abrirConexion(String bd, String servidor, String usuario, String password) {
        try {
            String url = String.format("jdbc:mariadb://%s:3306/%s", servidor, bd);
            conexion = DriverManager.getConnection(url, usuario, password);// Establecemos la conexión con la BD
            if (conexion != null)
                System.out.println("Conectado a la base de datos " + bd + " en " + servidor);
            else
                System.out.println("No se ha conectado a la base de datos " + bd + " en " + servidor);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    static void pecharConexion() {
        try {
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error o cerrar a conexión: " + e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {

        bd = "add";
        servidor = "localhost";
        usuario = "root";
        password = "";

        insertConTransaccion();
    }

}
