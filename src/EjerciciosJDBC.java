import java.sql.*;

public class EjerciciosJDBC {
    private Connection conexion;
    PreparedStatement ps = null;

    public void abrirConexion(String bd, String servidor, String usuario, String password) {
        try {
            String url = String.format("jdbc:mariadb://%s:3306/%s?useServerPrepStmts=true", servidor, bd);
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

    public void consultarAlumnos(String tabla, String col, String patron) {
        try {
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery("select * from "+tabla+" where " + col + " like \"%" + patron + "%\"");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString("nombre") + "\t" + rs.getString("apellidos"));
            }
            // ResultSet total = st.executeQuery("select count(*) as total from alumnos
            // where nombre like \"%" + patron + "%\"");
            System.out.println("Número resultados: " + (rs.getRow() - 1));
            st.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    public void insertarAlumno(String nombre, String apellidos, int altura, int aula) {
        try {
            Statement sta = this.conexion.createStatement();
            if (existeDato("aulas", "numero", aula)) {
                int filasAfectadas = sta
                        .executeUpdate("INSERT INTO alumnos (nombre, apellidos, altura, aula) VALUES (\"" + nombre
                                + "\", \"" + apellidos + "\"," + altura + ", " + aula + ")");
                System.out.println("Filas insertadas: " + filasAfectadas);
            } else {
                System.out.println("Error: No existe ningún aula con ese código");
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void insertarMateria(int cod, String nombre) {
        try {
            Statement sta = this.conexion.createStatement();
            if (!existeDato("asignaturas", "cod", cod)) {
                int filasAfectadas = sta.executeUpdate(
                        "INSERT INTO asignaturas (cod, nombre) VALUES (" + cod + ", \"" + nombre + "\")");
                System.out.println("Filas inseridas: " + filasAfectadas);
            } else {
                System.out.println("Error: Ya existe un aula con ese código");
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void eliminarAlumnoMateria(String tabla, String col, int cod) {
        try {
            Statement sta = this.conexion.createStatement();
            if (existeDato(tabla, col, cod)) {
                int filasAfectadas = sta.executeUpdate("DELETE FROM " + tabla + " WHERE " + col + "=" + cod);
                System.out.println("Filas eliminadas: " + filasAfectadas);
            } else {
                System.out.println("Error: No existe ninguna fila con ese código");
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void modificarAlumno(int cod, String nombre, String apellidos, int altura, int aula) {
        try {
            Statement sta = this.conexion.createStatement();
            if (existeDato("alumnos", "codigo", cod)) {
                if(existeDato("aulas", "numero", aula)){
                    int filasAfectadas = sta.executeUpdate("UPDATE alumnos set nombre='" + nombre + "', apellidos='"
                        + apellidos + "', altura=" + altura + ", aula=" + aula + " WHERE codigo = " + cod);
                System.out.println("Filas modificadas: " + filasAfectadas);
                }else{
                    System.out.println("Error: No existe ningún aula con ese código");
                }                
            }else{
                System.out.println("Error: No existe ningún alumno con ese código");
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void modificarMateria(int cod, String nombre) {
        try {
            Statement sta = this.conexion.createStatement();
            if (existeDato("asignaturas", "cod", cod)) {
                int filasAfectadas = sta
                        .executeUpdate("UPDATE asignaturas set nombre='" + nombre + "' WHERE codigo = " + cod);
                System.out.println("Filas modificadas: " + filasAfectadas);
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    // Nombre aulas con alumnos
    public void aulasConAlumnos(String col, String tabla1, String tabla2, String colTabla1, String colTabla2) {
        try {
            Statement sta = this.conexion.createStatement();
            ResultSet rs = sta.executeQuery("select distinct " + tabla1 + "." + col + " from " + tabla1 + " join "
                    + tabla2 + " on " + tabla1 + "." + colTabla1 + "=" + tabla2 + "." + colTabla2);
            while (rs.next()) {
                // if(!rs.getString("nombreAula").equals("null"))
                System.out.println(rs.getString("nombreAula"));
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void materiasSenAlumnos(String col, String t1, String t2, String colT1, String colT2) {
        try {
            Statement sta = this.conexion.createStatement();
            ResultSet rs = sta.executeQuery("select " + t1 + "." + col + " from " + t1 + " left join " + t2 + " on "
                    + t1 + "." + colT1 + " = " + t2 + "." + colT2 + " where " + "nota is null");
            while (rs.next()) {
                System.out.println(rs.getString(col));
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void alumAprobaronAlguna() {
        try {
            Statement sta = this.conexion.createStatement();
            ResultSet rs = sta.executeQuery(
                    "select alumnos.nombre, asignaturas.NOMBRE, notas.nota from alumnos, asignaturas, notas where alumnos.codigo = notas.alumno and asignaturas.COD = notas.asignatura and notas.nota >= 5");
            while (rs.next()) {
                System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getInt(3));
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void consultarAlumno(String cadena, int altura) {
        try {
            Statement sta = this.conexion.createStatement();
            ResultSet rs = sta.executeQuery(
                    "select nombre from alumnos where nombre like '%" + cadena + "%' and altura>" + altura);
            while (rs.next()) {
                rs.getString(1);
                System.out.println(rs.getString("nombre"));
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public void consultarAlumnoPS(String cadena, int altura) {
        String query = "select nombre from alumnos where nombre like ? and altura > ?";
        try {
            // this.conexion = DriverManager.getConnection("jdbc:mariadb://localhost:3306/add?useServerPrepStmts=true", "root",
            //         ""); --> (Error: Too many connections)
            if (this.ps == null)
                this.ps = this.conexion.prepareStatement(query);
            ps.setString(1, cadena);
            ps.setInt(2, altura);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    public boolean existeDato(String tabla, String col, int cod) {
        try {
            String query = "SELECT " + col + " FROM " + tabla;
            Statement sta = this.conexion.createStatement();
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt(col) == cod) {
                    // System.out.println("OK");
                    return true;
                }
            }
            sta.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
        return false;
    }

}
