import java.io.*;
import java.sql.*;

public class EjerObjBinarios {
    static String url;
    static String bd;
    static String usuario;
    static String password;
    static String servidor;

    static PreparedStatement ps = null;
    

    // http://www.herongyang.com/JDBC/Oracle-BLOB-getBinaryStream.html
    static void getImage(String patron, String ruta) {
        try (Connection c = DriverManager.getConnection(url, usuario, password);
            Statement sta = c.createStatement()) {
            ResultSet rs = sta.executeQuery("SELECT nombre,imagen FROM imagenes where nombre like '" + patron + "'");
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                InputStream is = rs.getBinaryStream("imagen");
                saveInputStream(is, ruta.concat("\\" + nombre));
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }        
    }

    static void saveInputStream(InputStream in, String ruta) {
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
    static void setImage(String nombre, String ruta) {
        try (Connection c = DriverManager.getConnection(url, usuario, password); 
            InputStream in = new FileInputStream(ruta)) {
            if (ps == null)
                ps = c.prepareStatement("INSERT INTO imagenes(nombre,imagen) VALUES (?,?)");
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
    }


    public static void main(String[] args) {
        bd = "add";
        servidor = "localhost";
        usuario = "root";
        password = "";

        url = String.format("jdbc:mariadb://%s:3306/%s", servidor, bd);
        //this.conexion = DriverManager.getConnection(url, usuario, password);// Establecemos la conexión con la BD

        //getImage("imagen1%", "C:\\Users\\pc\\Desktop");
        setImage("nuevaImageen.jpg", "C:\\Users\\pc\\Desktop\\imagen.jpg");

    }




}
