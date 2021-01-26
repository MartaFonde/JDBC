
public class AppConsultas {

    public static void main(String[] args) throws Exception {
        //Class.forName("org.mariadb.jdbc.Driver");
        ConsultasJDBC c = new ConsultasJDBC();

        c.consultarAlumnos("alumnos", "nombre", "fr");
        // c.insertarAlumno("Iris", "Seijo", 165, 5);
        // c.consultarAlumnos("alumnos", "nombre", "iris");
        // c.insertarMateria(11, "jjj");
        // c.eliminarAlumnoMateria("alumnos", "codigo", 1);
        // c.modificarAlumno(23, "Iris", "Clase", 175, 20);
        // c.modificarMateria(12, "historia");

        // c.aulasConAlumnos("nombreAula", "aulas", "alumnos", "numero", "aula");

        // //Materias sen alumnos --> materias sen nota
        // c.materiasSenAlumnos();

        // c.alumAprobaronAlguna();

        // c.consultarAlumnoSinPS("a", 180);
        // c.consultarAlumnoPS("%a%", 180);

        // long inicio = System.currentTimeMillis();
        // for (int i = 0; i < 100000; i++) {
        // //c.consultarAlumno("a", 180);
        // c.consultarAlumnoPS("%a%", 180);
        // }
        // long fin = System.currentTimeMillis();
        // System.out.println((fin - inicio) +" milisegundos");

        // 10 Sen ps 13 mseg. Con ps 21 mseg
        // 100 Sen ps 124 mseg. Con ps 77 mseg
        // 1000 Sen ps 608 mseg. Con ps 571 mseg
        // 10000 Sen ps 4928 mseg. Con ps 5144 mseg
        // 100000 Sen ps 54957 mseg. Con ps 50048 mseg

        //c.agregarColumna("alumnos", "edad", "int(2)", "unsigned not null default 18");

    }
}
