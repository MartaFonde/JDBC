
public class App {

    public static void main(String[] args) throws Exception {
        // Class.forName("org.mariadb.jdbc.Driver");
        EjerciciosJDBC c = new EjerciciosJDBC();
        c.abrirConexion("add", "localhost", "root", "");

        //c.consultarAlumnos("alumnos", "nombre","fr");
        //c.insertarAlumno("Iris", "Seijo", 165, 5);
        //c.insertarMateria(12, "filosofía");
        //c.eliminarAlumnoMateria("asignaturas", "cod", 12);
        //c.modificarAlumno(10, "Iris", "Seijo", 175, 20);
        //c.aulasConAlumnos("nombreAula", "aulas", "alumnos", "numero", "aula");

        //c.alumAprobaronAlguna();

        //Materias sen alumnos --> materias sen nota
        //c.materiasSenAlumnos("nombre", "asignaturas", "notas", "cod", "asignatura");

        //c.consultarAlumno("a", 180);
        //c.consultarAlumnoPS("%a%", 160);

        // long inicio = System.currentTimeMillis();        
        // for (int i = 0; i < 100000; i++) {
        //     //c.consultarAlumno("a", 180);
        //     c.consultarAlumnoPS("%a%", 180);
        // }
        // long fin = System.currentTimeMillis();
        // System.out.println((fin - inicio) +" milisegundos");

        //10    Sen ps 13 mseg. Con ps 21 mseg
        //100    Sen ps 124 mseg. Con ps 77 mseg
        //1000  Sen ps 608 mseg. Con ps 571 mseg 
        //10000 Sen ps 4928 mseg. Con ps 5144 mseg 
        //100000 Sen ps 54957 mseg. Con ps 50048 mseg


        c.pecharConexion();
    }
}
