public class AppMetadata {
    public static void main(String[] args) {
        EjerMetadatos md = new EjerMetadatos();

        // md.infoBD("add");
        // md.getAllDB("add");
        // md.showTables("add");
        // md.showViews("add");
        // md.comb("add", "add");
        // md.showStoredProcedure("add");
        // md.infoColumnsTables("add", "a%");
        // md.keys("add");
        String query = "select *, nombre as nom from alumnos";
        // md.infoConsulta("add", query);

        md.listDrivers("add");

    }
}
