import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

public class TransaccionesJDBC {
    Connection conexion;
    ConsultasJDBC j = new ConsultasJDBC();
    Savepoint p;
    PreparedStatement ps = null;
    PreparedStatement psPatron = null;

    public void insertConTransaccion() {
        abrirConexion("add", "localhost", "root", "");
        try (Statement sta = this.conexion.createStatement()) {
            this.conexion.setAutoCommit(false);
            j.insertarAlumno("Alex", "Fonde", 180, 20);
            // p = this.conexion.setSavepoint("sp1");
            sta.executeQuery("INSERT INTO alumnos VALUES (5,'Luca','Smith','Pontevedra', 20)");
            this.conexion.commit(); // se non se produce ningún erro confírmanse os cambios
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
            try {
                if (this.conexion != null) {
                    System.out.println("Desfanse os cambios mediante un rollback ");
                    // this.conexion.rollback(p);
                    this.conexion.rollback(); // se se produciu un erro os cambios desfanse
                }
            } catch (SQLException e2) {
                System.out.println("Erro ao realizar o rollback: " + e.getLocalizedMessage());
            }
        }
        pecharConexion();
    }
    // Para evitar ter que desfacer todos os cambios, creamos SAVEPOINT

    // http://www.herongyang.com/JDBC/Oracle-BLOB-getBinaryStream.html
    public void getImage(String bd, String patron, String ruta) {
        abrirConexion(bd, "localhost", "root", "");
        try (Statement sta = this.conexion.createStatement()) {
            ResultSet rs = sta.executeQuery("SELECT nombre,imagen FROM imagenes where nombre like '" + patron + "'");
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                InputStream is = rs.getBinaryStream("imagen");
                saveInputStream(is, ruta.concat("\\" + nombre));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
        pecharConexion();
    }

    public void saveInputStream(InputStream in, String ruta) {
        int c;
        byte[] buffer = new byte[1000];
        try (OutputStream out = new FileOutputStream(ruta)) {
            while ((c = in.read(buffer)) != -1) {
                out.write(buffer, 0, c);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar archivo: " + e.getMessage());
        }
    }

    // http://www.herongyang.com/JDBC/MySQL-BLOB-setBinaryStream.html
    public void setImage(String bd, String nombre, String ruta) {
        abrirConexion(bd, "localhost", "root", "");
        try (InputStream in = new FileInputStream(ruta)) {
            if (this.ps == null)
                this.ps = this.conexion.prepareStatement("INSERT INTO imagenes(nombre,imagen) VALUES (?,?)");
            ps.setString(1, nombre);
            File f = new File(ruta);
            ps.setBinaryStream(2, in, (int) f.length());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        } catch (IOException ex) {
            System.out.println("Error al acceder al archivo: " + ex.getMessage());
        }
        pecharConexion();
    }

    public void executeGetAulas(int puestos, String nombre) {
        abrirConexion("add", "localhost", "root", "");
        try {
            CallableStatement cs = this.conexion.prepareCall("CALL getAulas(?,?)");
            cs.setInt(1, puestos);
            cs.setString(2, nombre);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString("nombreAula") + "\t" + rs.getInt("puestos"));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
        pecharConexion();
    }

    public void executeSuma() {
        abrirConexion("add", "localhost", "root", "");
        try {
            PreparedStatement ps = this.conexion.prepareStatement("SELECT suma()");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
        pecharConexion();
    }

    public void search(String bd, String patron) {
        abrirConexion(bd, "localhost", "root", "");
        String tabla, columna;
        try (Statement sta = this.conexion.createStatement()) {
            DatabaseMetaData mt = this.conexion.getMetaData();
            ResultSet rs = mt.getTables(bd, null, "%", null);
            while (rs.next()) {
                if (rs.getString("TABLE_TYPE").equals("TABLE")) {
                    tabla = rs.getString("TABLE_NAME");
                    ResultSet cols = this.conexion.getMetaData().getColumns(bd, null, rs.getString("TABLE_NAME"), "%");
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
        pecharConexion();
    }

    // non pilla tabla
    public void psFilasPatron(String bd, String tabla, String col, String patron) {
        try {
            if (this.psPatron == null) {
                String query = "select ? from ? where ? like ?";
                this.psPatron = this.conexion.prepareStatement(query);
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

    // https://www.red-gate.com/simple-talk/sql/learn-sql-server/using-the-for-xml-clause-to-return-query-results-as-xml/
    public void exportXML(String bd, String tabla) {

    }

    public void access() {
        try {
            // Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://data/Alumnos.mdb");
            if (conn != null) {
                System.out.println("Conectado a la base de datos");
                String query = "select nombre, apellidos from alumnos";
                Statement sta = conn.createStatement();
                ResultSet rs = sta.executeQuery(query);
                while (rs.next()) {
                    System.out.println(rs.getString(1) + "\t" + rs.getString(2));
                }
                sta.close();
                conn.close();
            } else {
                System.out.println("No se ha conectado a la base de datos");
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    public void abrirConexion(String bd, String servidor, String usuario, String password) {
        try {
            String url = String.format("jdbc:mariadb://%s:3306/%s", servidor, bd);
            this.conexion = DriverManager.getConnection(url, usuario, password);// Establecemos la conexión con la BD
            if (this.conexion != null)
                System.out.println("Conectado a la base de datos " + bd + " en " + servidor);
            else
                System.out.println("No se ha conectado a la base de datos " + bd + " en " + servidor);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    public void pecharConexion() {
        try {
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error o cerrar a conexión: " + e.getLocalizedMessage());
        }
    }

}
