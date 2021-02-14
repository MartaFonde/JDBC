import java.sql.*;

public class EjerSQLite {

    static String urlSqLite = "jdbc:sqlite:/D:/DATOS/JDBC/data/addSqlLite.db";
    static String usuario = "root";
    static String password = "";

    static String nameSQLite = "org.sqlite.JDBC";
    static String nameMariaDB = "org.mariadb.jdbc.Driver";

    static String urlMariaDB = "jdbc:mariadb://localhost:3306/add";
    static String urlMariaDB2 = "jdbc:mariadb://localhost:3306/add?jdbcCompliantTruncation=false&zeroDateTimeBehavior=convertToNull";
   
    // Crear bd
    // sqlite3.exe ../JDBC/data/addSqlLite.db

    // Migrar tablas y datos
    // .read G:/DATOS/JDBC/data/tablasSqlLite.sql

    // 3. Listar a segunda e terceira clase con máis postos
    // SELECT * FROM aulas ORDER BY puestos DESC LIMIT 1,2;

    static void puestosMinAula(int puestosMin) {
        try (Connection c = DriverManager.getConnection(urlSqLite);
                Statement sta = c.createStatement()) {
            
            ResultSet rs = sta.executeQuery("select * from aulas where puestos >= " + puestosMin);
            while (rs.next()) {
                System.out
                        .println(rs.getInt("numero") + "\t" + rs.getString("nombreAula") + "\t" + rs.getInt("puestos"));
            }
        } catch (SQLException e) {
            System.out.println("Error: (" + e.getErrorCode() + ") " + e.getMessage());
        }
    }

    static void psPuestosMinAula(int puestosMin) {
        String query = "select * from aulas where puestos >= ?";
        try (Connection c = DriverManager.getConnection(urlSqLite); 
                PreparedStatement ps = c.prepareStatement(query)) {
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

    static void insertarAula(int numero, String nombreAula, int puestos) {
        String query = String.format("INSERT INTO aulas (numero, nombreAula, puestos) VALUES (%d, '%s', %d);", numero,
                nombreAula, puestos);
        int filasAfectadas = executeUPDATE(urlSqLite, query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas: " + filasAfectadas);
        }
    }

    static int executeUPDATE(String url, String query) {
        try (Connection c = DriverManager.getConnection(url, usuario, password);
                Statement sta = c.createStatement()) {
            int filasAfectadas = sta.executeUpdate(query);
            return filasAfectadas;
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
        return -1;
    }
    //En SQLite non hai xestion de usuarios. Non farían falta parametros usuario e pass
    //Pero esta función tamén se usa con urlMariaDB e si serán necesarios.



    static void replaceAula(int numero, String nombreAula, int puestos) {
        String query = String.format("REPLACE INTO aulas (numero, nombreAula, puestos) VALUES (%d, '%s',%d);", numero,
                nombreAula, puestos);
        int filasAfectadas = executeUPDATE(urlSqLite, query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas: " + filasAfectadas);
        }
    }
  
    //inserta por duplicado en bd MySQL e SQLite
    static void insertAlumnos(String nombre, String apellidos, int altura, int aula) {
        String query = String.format("INSERT INTO alumnos (nombre, apellidos, altura, aula) VALUES ('%s','%s',%d,%d)",
                nombre, apellidos, altura, aula);
        int filasAfectadas = executeUPDATE(urlSqLite, query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas SqlLite: " + filasAfectadas);
        }

        filasAfectadas = executeUPDATE(urlMariaDB , query);
        if (filasAfectadas != -1) {
            System.out.println("Filas afectadas MySQL: " + filasAfectadas);
        }
    }

    static void buscaNombreAula(String patron) {
        String query = "select * from aulas where nombreAula like '" + patron + "'";
        try (Connection c = DriverManager.getConnection(urlSqLite); 
                Connection cSec = DriverManager.getConnection(urlMariaDB, usuario, password);
                Statement sta = c.createStatement(); 
                Statement staSec = cSec.createStatement()) {

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
    

    static void insertConTransaccionAulas(int numero, String nombreAula, int puestos) {
        Connection c = abrirConexion(nameSQLite, urlSqLite, "", "");
        Connection cSec = abrirConexion(nameMariaDB, urlMariaDB, "root", "");

        String query = String.format("INSERT INTO aulas(numero, nombreAula, puestos) VALUES (%d,'%s', %d)", numero,
                nombreAula, puestos);
        try (Statement sta = c.createStatement(); Statement staSec = cSec.createStatement()) {
            c.setAutoCommit(false);
            cSec.setAutoCommit(false);
            executeUPDATE(urlSqLite, query);
            executeUPDATE(urlMariaDB, query);
            c.commit(); // se non se produce ningún erro confírmanse os cambios en AMBAS
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
        finally{
            cerrarConexion(c);
            cerrarConexion(cSec);
        }
    }

    static void crearTabla() {
        String query = "CREATE TABLE `fechasPrueba`(`nombre` VARCHAR(10),`fecha` DATETIME not null);";
        executeUPDATE(urlSqLite, query);
        executeUPDATE(urlMariaDB, query);
    }

    static void insertFechas(String nombre, int y, int m, int d, int h, int min, int seg) {
        String dt = String.format("%d-%d-%d %d:%d:%d", y, m, d, h, min, seg);
        psFecha(urlSqLite, nombre, dt);
        psFecha(urlMariaDB, nombre, dt);
        psFecha(urlMariaDB2, nombre, dt);
    }

    static void psFecha(String url, String nombre, String dt) {
        String query = "INSERT INTO fechas (nombre, fecha) VALUES (?,?);";
        try (Connection c = DriverManager.getConnection(url, usuario, password);
            PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, dt);
            ps.executeUpdate();
        } catch (SQLException e) {
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
        } 
        catch (ClassNotFoundException o) {
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

        //FUNCIONA IGUAL SEN ISTO
        // try{
        //     Class.forName(nameSQLite);
        //
        // }catch (ClassNotFoundException o) {
        //     System.out.println("Error :" + o.getMessage());
        // }
        

        //puestosMinAula(30);
        //psPuestosMinAula(30);

        //insertarAula(88, "Laboratorio", 60);

        //replaceAula(88, "Salon de actos", 32);

        //insertAlumnos("Maria", "Zambrano", 165, 20);

        //buscaNombreAula("fisica");

        // SQLite busca se dúas cadeas son similares (like) non distingue entre
        // maiúsculas e minúsculas. Si distingue entre vogais acentuadas ou sen acentuar
        // MySQL non distingue entre maius e minus ou entre vogais acentuadas ou sen acentuar.

        //insertConTransaccionAulas(18, "Laboratorio", 30);
        
        //crearTabla();

        //insertFechas("truncatestacadlaaaaaarga", 2020, 10, 15, 13, 8, 00);

        // Cadena de más de 10 char  
        // En SQLite inserta
        // En MySQL conexion 1 non inserta. Error: (1406) (conn=14) Data too long for column
        // 'nombre' at row 1
        // Conexion 2 con parametros si inserta e trunca cad. 

        //Insertar con funcións propias de SGBD data e hora actual:
        // insert into fechas values("fechaNueva", datetime('now'));
        // SQLite almacena 1 hora menos. Solución:
        // insert into fechas values("fechaNueva2", datetime('now', 'localtime'));       

        //psFecha(urlSqLite, "sendata", "");
        // En SQLite crea fila sen data. Garda cadea baleira

        //psFecha(urlMariaDB, "sendata", "");
        // non crea fila -> Error: (1292) (conn=354) Incorrect datetime value:
        // '' for column `add`.`fechas`.`fecha` at row 1

        //psFecha(urlMariaDB2, "sendata2", "");
        //Garda a cadea "0000-00-00".
        
    }
}
