public class AppTransacciones {
    public static void main(String[] args) {
        TransaccionesJDBC t = new TransaccionesJDBC();

        // t.insertConTransaccion();
        // t.getImage("add", "imagen1%", "C:\\Users\\marta\\Desktop");
        // t.setImage("add", "girona.jpg", "C:\\Users\\marta\\Desktop\\girona.jpg");
        // t.executeGetAulas(25, "aula");
        // t.executeSuma();
        t.search("add", "a%");
    }
}
