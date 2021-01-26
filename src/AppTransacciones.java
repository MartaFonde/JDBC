public class AppTransacciones {
    public static void main(String[] args) {
        TransaccionesJDBC t = new TransaccionesJDBC();
        //t.getImage("imagen1%");
        //t.insertConTransaccion();
        // t.setImage("miImagen.jpg", "imagen.jpg");
        // t.getImage("miImagen.jpg");
        //t.executeGetAulas(25, "aula");
        //t.executeSuma();
        t.search("add", "a%");
    }
}
