import java.util.List;

public class HashTableTest {

    public static void main(String[] args) {

        // ── 1. Cargar dataset y llenar tabla
        List<SteamGame> games = CSVReader.readCSV("games.csv");
        System.out.println("Juegos en el CSV: " + games.size());

        HashTable hashTable = new HashTable();

        for (SteamGame game : games) {
            if (hashTable.getSize() >= 10000) break;
            hashTable.insert(game);
        }

        hashTable.printStats();

        // ── 2. Guardar en CSV
        hashTable.saveToCSV("hashtable.csv");

        // ── 3. Cargar desde CSV en una tabla nueva
        System.out.println("\n--- Cargando desde CSV ---");
        HashTable loaded = new HashTable();
        loaded.loadFromCSV("hashtable.csv");
        loaded.printStats();

        // ── 4. Verificar búsquedas en la tabla cargada
        System.out.println("\n--- Búsquedas tras cargar ---");
        String[] testIds = {"10", "570", "730", "440", "271590"};
        for (String id : testIds) {
            SteamGame found = loaded.search(id);
            System.out.println(found != null
                    ? "ENCONTRADO : " + found
                    : "NO encontrado: " + id);
        }

        // ── 5. Eliminación lógica
        System.out.println("\n--- Eliminación ---");
        System.out.println("Eliminando appId=10 : " + loaded.delete("10"));
        System.out.println("Buscar appId=10 (esperado null): " + loaded.search("10"));
    }
}