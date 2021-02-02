import java.sql.*;
import java.util.Calendar;
import java.util.Date;

import javax.print.DocFlavor.STRING;
import javax.xml.catalog.Catalog;

public class EjerSQLite {

    // Crear bd
    // sqlite3.exe ../JDBC/data/addSqlLite.db

    // Migrar tablas y datos
    // .read G:/DATOS/JDBC/data/tablasSqlLite.sql

    // 3. Listar a segunda e terceira clase con máis postos
    // SELECT * FROM aulas ORDER BY puestos DESC LIMIT 1,2;

    static void puestosMinAula(Connection c, int puestosMin) {
        try (Statement sta = c.createStatement()) {
            ResultSet rs = sta.executeQuery("select * from aulas where puestos >= " + puestosMin);
            while (rs.next()) {
                System.out
                        .println(rs.getInt("numero") + "\t" + rs.getString("nombreAula") + "\t" + rs.getInt("puestos"));
            }
        } catch (SQLException e) {
            System.out.println("Error: (" + e.getErrorCode() + ") " + e.getMessage());
        }
    }

    static void psPuestosMinAula(Connection c, int puestosMin) {
        String query = "select * from aulas where puestos >= ?";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, puestosMin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out
                        .println(rs.getInt("numero") + "\t" + rs.getString("nombreAula") + "\t" + rs.getInt("puestos"));
            }
        } catch (SQLException e) {
            System.out.println("Error: (" + e.getErrorCode() + ") " + e.getMessage());
        }
    }

    static void insertarAula(Connection c, int numero, String nombreAula, int puestos) {
        String query = String.format("INSERT INTO aulas (numero, nombreAula, puestos) VALUES (%d, '%s', %d);", numero,
                nombreAula, puestos);
        int filasAfectadas = executeUPDATE(c, query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas: " + filasAfectadas);
        }
    }

    // https://www.mysqltutorial.org/mysql-replace.aspx
    static void replaceAula(Connection c, int numero, String nombreAula, int puestos) {
        String query = String.format("REPLACE INTO aulas (numero, nombreAula, puestos) VALUES (%d, '%s',%d);", numero,
                nombreAula, puestos);
        int filasAfectadas = executeUPDATE(c, query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas: " + filasAfectadas);
        }
    }

    static int executeUPDATE(Connection c, String query) {
        try (Statement sta = c.createStatement()) {
            int filasAfectadas = sta.executeUpdate(query);
            return filasAfectadas;
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
        return -1;
    }

    static void insertSecAlumnos(Connection c, Connection cSec, String nombre, String apellidos, int altura, int aula) {
        String query = String.format("INSERT INTO alumnos (nombre, apellidos, altura, aula) VALUES ('%s','%s',%d,%d)",
                nombre, apellidos, altura, aula);
        int filasAfectadas = executeUPDATE(c, query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas SqlLite: " + filasAfectadas);
        }
        filasAfectadas = executeUPDATE(cSec, query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas MySQL: " + filasAfectadas);
        }
    }

    static void buscaNombreAula(Connection c, Connection cSec, String patron) {
        String query = "select * from aulas where nombreAula like '" + patron + "'";
        try (Statement sta = c.createStatement(); Statement staSec = cSec.createStatement()) {
            ResultSet rs = sta.executeQuery(query);
            ResultSet rsSec = staSec.executeQuery(query);

            System.out.println("-- SQLite");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getInt(3));
            }
            System.out.println("-- MySQL");
            while (rsSec.next()) {
                System.out.println(rsSec.getInt(1) + "\t" + rsSec.getString(2) + "\t" + rsSec.getInt(3));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
    }

    // SQLite se busca se dúas cadeas son similares (like) non distingue entre
    // maiúsculas e minúsculas
    // aínda que si que o fai entre vogais acentuadas ou sen acentuar
    // MySQL non distingue entre maiúsculas e minúsculas ou entre vogais acentuadas
    // ou sen acentuar.

    static void insertSecTransaccionAulas(Connection c, Connection cSec, int numero, String nombreAula, int puestos) {
        String query = String.format("INSERT INTO aulas(numero, nombreAula, puestos) VALUES (%d,'%s', %d)", numero,
                nombreAula, puestos);
        try (Statement sta = c.createStatement(); Statement staSec = cSec.createStatement()) {
            c.setAutoCommit(false);
            cSec.setAutoCommit(false);
            executeUPDATE(c, query);
            executeUPDATE(cSec, query);
            c.commit(); // se non se produce ningún erro confírmanse os cambios
            cSec.commit();
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
            try {
                if (c != null && cSec != null) {
                    System.out.println("Producíuse un erro. Desfanse os cambios");
                    c.rollback();
                    cSec.rollback();
                }
            } catch (SQLException e2) {
                System.out.println("Erro ao realizar o rollback: " + e.getLocalizedMessage());
            }
        }
    }

    static void crearTabla(Connection c) {
        String query = "CREATE TABLE `fechas`(`nombre` VARCHAR(10),`fecha` DATETIME not null);";
        executeUPDATE(c, query);
    }

    static void insertFechas(Connection c, Connection cSec, String nombre, int y, int m, int d, int h, int min, int seg, int nano) {
        Timestamp ts = new Timestamp(y-1900, m-1, d-1, h, min, seg, nano);
        //Date da = new Date(y, m, d, h, m, seg);
        psFecha(c, nombre, ts);
        psFecha(cSec, nombre, ts);        
    }

    static void psFecha(Connection c, String nombre, Timestamp dt) {
        String query = "INSERT INTO fechas (nombre, fecha) VALUES (?,?);";
        try(PreparedStatement ps = c.prepareStatement(query)){
            ps.setString(1, nombre);
            ps.setTimestamp(2, dt);    
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
    }

    static Connection abrirConexion(String name, String url, String usuario, String password) {
        Connection conexion = null;
        try {
            Class.forName(name);
            conexion = DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        } catch (ClassNotFoundException o) {
            System.out.println("Error :" + o.getMessage());
        }
        return conexion;
    }

    static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getLocalizedMessage());
            }
        }
    }

    public static void main(String[] args) {
        String nameSQLite = "org.sqlite.JDBC";
        String nameMariaDB = "org.mariadb.jdbc.Driver";

        String url = "jdbc:sqlite:/D:/DATOS/JDBC/data/addSqlLite.db";
        String urlSec = "jdbc:mariadb://localhost:3306/add";

        Connection c = abrirConexion(nameSQLite, url, "", "");
        Connection cSec = abrirConexion(nameMariaDB, urlSec, "root", "");

        // puestosMinAula(c, 30);
        // psPuestosMinAula(c, 30);
        // insertarAula(c, 8, "Laboratorio", 25);
        // replaceAula(c, 8, "Salon de actos", 32);

        // insertSecAlumnos(c, cSec, "Laura", "Fondevila", 165, 20);

        // buscaNombreAula(c, cSec, "a%");
        // buscaNombreAula(c, cSec, "Anatomía");
        // insertSecTransaccionAulas(c, cSec, 18, "Laboratorio", 30);

        // crearTabla(c);
        //crearTabla(cSec);

        // String urlTres =
        // "jdbc:mariadb://localhost:3306/add?jdbcCompliantTruncation=false&zeroDateTimeBehavior=convertToNull";
        // Connection cTres = abrirConexion(nameMariaDB, urlTres, "root", "");

        //insertFechas(c, cSec, "hoxe", 2020, 2, 2, 17, 38, 52, 12);

        

        cerrarConexion(c);
        cerrarConexion(cSec);
        // cerrarConexion(cTres);
    }
}
