import java.sql.*;

public class ConsultasJDBC {
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

    public void cerrarConexion() {
        try {
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getLocalizedMessage());
        }
    }

    public void consultarAlumnos(String tabla, String col, String patron) {
        abrirConexion("add", "localhost", "root", "");
        try (Statement st = conexion.createStatement()) {
            ResultSet rs = st.executeQuery("select * from " + tabla + " where " + col + " like \"%" + patron + "%\"");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString("nombre") + "\t" + rs.getString("apellidos"));
            }
            System.out.println("Número resultados: " + (rs.getRow() - 1));
            // select count(*) as total from alumnos where nombre like '%" + patron +"%'
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
        cerrarConexion();
    }

    public void insertarAlumno(String nombre, String apellidos, int altura, int aula) {
        abrirConexion("add", "localhost", "root", "");
        if (existeDato("aulas", "numero", aula)) {
            String query = String.format(
                    "INSERT INTO alumnos (nombre, apellidos, altura, aula) VALUES (\"%s\", \"%s\", %d, %d)", nombre,
                    apellidos, altura, aula);
            int filasAfectadas = executeUPDATE(query);
            if (filasAfectadas != -1) {
                System.out.println("Filas insertadas: " + filasAfectadas);
            }
        } else {
            System.out.println("Error: No existe ningún aula con ese código");
        }
        cerrarConexion();
    }

    public void insertarMateria(int cod, String nombre) {
        abrirConexion("add", "localhost", "root", "");
        if (!existeDato("asignaturas", "cod", cod)) {
            int filasAfectadas = executeUPDATE(
                    "INSERT INTO asignaturas (cod, nombre) VALUES (" + cod + ", '" + nombre + "')");
            if (filasAfectadas != -1) {
                System.out.println("Filas inseridas: " + filasAfectadas);
            }
        } else {
            System.out.println("Error: Ya existe un aula con ese código");
        }
        cerrarConexion();
    }

    public void eliminarAlumnoMateria(String tabla, String col, int cod) {
        abrirConexion("add", "localhost", "root", "");
        if (existeDato(tabla, col, cod)) {
            int filasAfectadas = executeUPDATE("DELETE FROM " + tabla + " WHERE " + col + "=" + cod);
            if (filasAfectadas != -1) {
                System.out.println("Filas eliminadas: " + filasAfectadas);
            }
        } else {
            System.out.println("Error: No existe ninguna fila con ese código");
        }
        cerrarConexion();
    }

    public void modificarAlumno(int cod, String nombre, String apellidos, int altura, int aula) {
        abrirConexion("add", "localhost", "root", "");
        if (existeDato("alumnos", "codigo", cod)) {
            if (existeDato("aulas", "numero", aula)) {
                int filasAfectadas = executeUPDATE("UPDATE alumnos set nombre='" + nombre + "', apellidos='" + apellidos
                        + "', altura=" + altura + ", aula=" + aula + " WHERE codigo = " + cod);
                if (filasAfectadas != -1) {
                    System.out.println("Filas modificadas: " + filasAfectadas);
                }
            } else {
                System.out.println("Error: No existe ningún aula con ese código");
            }
        } else {
            System.out.println("Error: No existe ningún alumno con ese código");
        }
        cerrarConexion();
    }

    public void modificarMateria(int cod, String nombre) {
        abrirConexion("add", "localhost", "root", "");
        if (existeDato("asignaturas", "cod", cod)) {
            int filasAfectadas = executeUPDATE("UPDATE asignaturas set nombre='" + nombre + "' WHERE cod = " + cod);
            if (filasAfectadas != -1) {
                System.out.println("Filas modificadas: " + filasAfectadas);
            }
        }
        cerrarConexion();
    }

    // Nombre aulas con alumnos
    // c.aulasConAlumnos("nombreAula", "aulas", "alumnos", "numero", "aula");

    public void aulasConAlumnos(String col, String tabla1, String tabla2, String colTabla1, String colTabla2) {
        abrirConexion("add", "localhost", "root", "");
        try (Statement sta = this.conexion.createStatement()) {
            String query = String.format("select distinct %s.%s from %s join %s on %s.%s = %s.%s", tabla1, col, tabla1,
                    tabla2, tabla1, colTabla1, tabla2, colTabla2);
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {
                System.out.println(rs.getString("nombreAula"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
        cerrarConexion();
    }

    public void materiasSenAlumnos() {
        abrirConexion("add", "localhost", "root", "");
        try (Statement sta = this.conexion.createStatement()) {
            String query = "select asignaturas.nombre from asignaturas left join notas on asignaturas.cod = notas.asignatura where nota is null";
            ResultSet rs = sta.executeQuery(query);
            while (rs.next()) {
                System.out.println(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
        cerrarConexion();
    }

    public void alumAprobaronAlguna() {
        abrirConexion("add", "localhost", "root", "");
        try (Statement sta = this.conexion.createStatement()) {
            ResultSet rs = sta.executeQuery("select distinct alumnos.nombre, alumnos.apellidos from notas "
                    + " join asignaturas on asignaturas.COD = notas.asignatura "
                    + " join alumnos on alumnos.codigo = notas.alumno where notas.nota >= 5");
            while (rs.next()) {
                System.out.println(rs.getString(1) + " " + rs.getString(2) + "\t");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
        cerrarConexion();
    }

    public void consultarAlumnoSinPS(String cadena, int altura) {
        abrirConexion("add", "localhost", "root", "");
        try (Statement sta = this.conexion.createStatement()) {
            ResultSet rs = sta.executeQuery(
                    "select nombre from alumnos where nombre like '%" + cadena + "%' and altura>" + altura);
            while (rs.next()) {
                rs.getString(1);
                System.out.println(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
        cerrarConexion();
    }

    public void consultarAlumnoPS(String cadena, int altura) {
        abrirConexion("add", "localhost", "root", "");
        String query = "select nombre from alumnos where nombre like ? and altura > ?";
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, cadena);
            ps.setInt(2, altura);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
        cerrarConexion();
    }

    public void agregarColumna(String tabla, String nombreCol, String tipoDato, String propiedades) {
        abrirConexion("add", "localhost", "root", "");
        String query = String.format("ALTER TABLE %s ADD %s %s %s", tabla, nombreCol, tipoDato, propiedades);
        int col = executeUPDATE(query);
        if (col != 1) {
            System.out.println("Columna agregada");
        }
        cerrarConexion();
    }

    public int executeUPDATE(String query) {
        try (Statement sta = this.conexion.createStatement()) {
            int filasAfectadas = sta.executeUpdate(query);
            return filasAfectadas;
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
        return -1;
    }

    public boolean existeDato(String tabla, String col, int cod) {
        String query = "SELECT " + col + " FROM " + tabla + " WHERE " + col + "=" + cod;
        try (Statement sta = this.conexion.createStatement()) {
            ResultSet rs = sta.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.printf("Error: (%d) %s", e.getErrorCode(), e.getLocalizedMessage());
        }
        return false;
    }
}
