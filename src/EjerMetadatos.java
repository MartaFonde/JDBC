import java.sql.*;
import java.util.Enumeration;

//https://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html

public class EjerMetadatos {
    private Connection conexion;
    DatabaseMetaData dbmt;

    // exercicio 9a
    public void infoBD(String bd) {
        abrirConexion(bd, "localhost", "root", "");
        try {
            dbmt = this.conexion.getMetaData();
            System.out.printf(
                    "Nome do driver: %s\nVersión do driver: %s\nURL de conexión: %s\n"
                            + "Usuario: %s\nNome SGBD: %s\nVersión SGBD: %s\nPalabras reservadas SGBD: %s\n",
                    dbmt.getDriverName(), dbmt.getDriverVersion(), dbmt.getURL(), dbmt.getUserName(),
                    dbmt.getDatabaseProductName(), dbmt.getDatabaseProductVersion(), dbmt.getSQLKeywords());
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }

    // exercicio 9b
    // https://docs.microsoft.com/es-es/sql/connect/jdbc/reference/getcatalogs-method-sqlserverdatabasemetadata?view=sql-server-ver15
    public void getAllDB(String bd) {
        abrirConexion(bd, "localhost", "root", "");
        try {
            dbmt = this.conexion.getMetaData();
            ResultSet rs = dbmt.getCatalogs();
            while (rs.next()) {
                System.out.println(rs.getString(1)); //1 porque só hai unha columna (nome das bd)
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }

    //exercicio 9c 9d
    //Para todas as táboas de bases de datos ADD obtén: o nome das táboa e o tipo de táboa.
    //http://www.chuidiang.org/java/mysql/ResultSet-DataBase-MetaData.php
    public void showTablesViews(String bd, String type) { 
        //String query = "SHOW TABLES FROM `add`";
        abrirConexion(bd, "localhost", "root", "");
        try {
            //this.conexion.setCatalog(bd);
            DatabaseMetaData mt = this.conexion.getMetaData();
            ResultSet rs = mt.getTables(bd, null, "%", null);
            while(rs.next()){
                if(rs.getString("TABLE_TYPE").equals(type.toUpperCase())){      //TABLE - VIEW
                    System.out.println(rs.getString("TABLE_NAME"));    
                }
                //System.out.println(rs.getString("TABLE_NAME")+" - "+rs.getString("TABLE_TYPE"));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }

    //exercicio 9e  --combinar b + c
    public void comb(String infobd){
        abrirConexion("add", "localhost", "root", "");
        String bd;
        try {
            dbmt = this.conexion.getMetaData();
            ResultSet rs = dbmt.getCatalogs();
            while (rs.next()) {
                bd = rs.getString(1); //1 porque só hai unha columna (nome das bd)
                System.out.println(bd);
                if(bd.equals(infobd)){
                    ResultSet rs2 = dbmt.getTables(bd, null, "%", null);
                    while(rs2.next()){
                        System.out.println("\t"+rs2.getString("TABLE_NAME")+" - "+rs2.getString("TABLE_TYPE"));
                    }
                }                
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }

    //exercicio 9f
    public void showStoredProcedure(String bd){
        abrirConexion(bd, "localhost", "root", "");
        try {
            DatabaseMetaData mt = this.conexion.getMetaData();
            ResultSet rs = mt.getProcedures(bd, null, "%");
            while(rs.next()){     
                if(rs.getInt("PROCEDURE_TYPE") == 1)     
                System.out.println(rs.getString("PROCEDURE_NAME"));                      
                //System.out.println(rs.getString("PROCEDURE_NAME")+" "+rs.getString("PROCEDURE_TYPE"));                      
                //As funcións tamén son procedementos almacenados, type 2
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }

    //exercicio 9g
    //https://www.tutorialspoint.com/java-databasemetadata-getcolumns-method-with-example
    //https://dzone.com/articles/jdbc-tutorial-extracting-database-metadata-via-jdb
    public void infoColumnsTables(String bd, String patron){
        abrirConexion(bd, "localhost", "root", "");
        String cad = "\""+patron+"\"";
        try{
            DatabaseMetaData mt = this.conexion.getMetaData();
            ResultSet rs = mt.getTables(bd, null, "a%", null);
            while(rs.next()){
                if(rs.getString("TABLE_TYPE").equals("TABLE")){
                    ResultSet cols = this.conexion.getMetaData().getColumns(bd, null, rs.getString("TABLE_NAME"), "%");
                    while(cols.next()){
                        System.out.printf("Pos: %s\tBD: %s\tTabla: %s\tNombre: %s\t"
                        +"Tipo dato: %s\tTamaño: %s\tNull: %s\tAutoincrement: %s\n",
                            cols.getString("ORDINAL_POSITION"), cols.getString("TABLE_CAT"),
                            cols.getString("TABLE_NAME"), cols.getString("COLUMN_NAME"),
                            cols.getString("TYPE_NAME"), cols.getString("COLUMN_SIZE"),
                            cols.getString("IS_NULLABLE"), cols.getString("IS_AUTOINCREMENT"));
                    } 
                    System.out.println();
                }
            }    
        }catch(SQLException e){
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }    

    //exercicio 9h
    public void keys(String bd){
        abrirConexion(bd, "localhost", "root", "");
        try{
            DatabaseMetaData mt = this.conexion.getMetaData();
            ResultSet rs = mt.getTables(bd, null, "%", null);
            while(rs.next()){
                if(rs.getString("TABLE_TYPE").equals("TABLE")){  
                    System.out.println("Tabla->"+rs.getString("TABLE_NAME"));                  
                    ResultSet pk = this.conexion.getMetaData().getPrimaryKeys(bd, null, rs.getString("TABLE_NAME"));
                    while(pk.next()){
                        System.out.println(pk.getString("PK_NAME") +"\t"+pk.getString("COLUMN_NAME"));
                    } 
                    ResultSet expK = this.conexion.getMetaData().getExportedKeys(bd, null, rs.getString("TABLE_NAME"));
                    while(expK.next()){
                        System.out.println("Export tabla->"+expK.getString("FKTABLE_NAME")+" col->"+expK.getString("FKCOLUMN_NAME"));
                    }                    
                }
                System.out.println();
            }    
        }catch(SQLException e){
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }

    public void infoConsulta(){
        abrirConexion("add", "localhost", "root", "");
        String query = "select *, nombre as nom from alumnos";
        try(Statement sta = this.conexion.createStatement()){
            ResultSet rs = sta.executeQuery(query);
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                System.out.printf("Nombre: %s\tAlias: %s\tTipo dato: %s\tAutoincrement: %b\tNull: %s\n",
                    md.getColumnName(i), md.getColumnLabel(i), md.getColumnTypeName(i), 
                    md.isAutoIncrement(i), md.isNullable(i)==1?"True":"False");
            }
        }catch(SQLException e){
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage()); 
        }
        pecharConexion();
    }

    //http://lineadecodigo.com/java/listar-los-drivers-con-jdbc/
    public void listDrivers(String bd){
        abrirConexion(bd, "localhost", "root", "");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while(drivers.hasMoreElements()){
            Driver d = (Driver)drivers.nextElement();
            System.out.println(d.getClass().getName());
        }  
        pecharConexion();      
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
