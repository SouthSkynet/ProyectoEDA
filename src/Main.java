import java.util.*;
import java.util.stream.Collectors;

public class Main {

    // ── Estado global de la sesión ────────────────────────────────────────────
    private static List<SteamGame> games = new ArrayList<>();
    private static HashTable hashTable = new HashTable();
    private static boolean hashTableLoaded = false;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Cargar el CSV
        String path = "games.csv";

        games = CSVReader.readCSV(path);
        if (games.isEmpty()) {
            System.out.println("No se cargaron datos. Verifica la ruta.");
            return;
        }

        // Menú principal
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Opción: ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1" -> menuQuickSort();
                case "2" -> menuMergeSort();
                case "3" -> menuHashTable(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Opción no válida. Intenta de nuevo.");
            }
        }

        System.out.println("¡Hasta luego!");
        sc.close();
    }


    private static void printMenu() {
        System.out.println("""

                ╔══════════════════════════════════════╗
                ║            STEAM GAMES               ║
                ╠══════════════════════════════════════╣
                ║  1. Quick Sort  — por precio         ║
                ║  2. Merge Sort  — por reseñas (+)    ║
                ║  3. Hash Table  — buscar / gestionar ║
                ║  0. Salir                            ║
                ╚══════════════════════════════════════╝""");
    }


    private static void menuQuickSort() {
        System.out.println("\n── Quick Sort por precio ");

        List<SteamGame> sorted = new ArrayList<>(games);

        long t1 = System.currentTimeMillis();
        QuickSortGames.quickSort(sorted, 0, sorted.size() - 1);
        long t2 = System.currentTimeMillis();

        System.out.printf("  Ordenados %,d juegos en %d ms%n", sorted.size(), t2 - t1);

        // Más baratos — excluye gratuitos (price == 0.0)
        List<SteamGame> paid = sorted.stream()
                .filter(g -> g.getPrice() > 0.0)
                .collect(Collectors.toList());

        System.out.println("\n  TOP 10 MÁS BARATOS (No gratuitos)");
        System.out.println("  " + "─".repeat(50));
        for (int i = 0; i < Math.min(10, paid.size()); i++) {
            System.out.printf("  %2d. $%7.2f  —  %s%n",
                    i + 1, paid.get(i).getPrice(), paid.get(i).getName());
        }

        // Más caros
        System.out.println("\n  TOP 10 MÁS CAROS");
        System.out.println("  " + "─".repeat(50));
        int n = sorted.size();
        int top = Math.min(10, n);
        for (int i = n - top; i < n; i++) {
            System.out.printf("  %2d. $%7.2f  —  %s%n",
                    n - i, sorted.get(i).getPrice(), sorted.get(i).getName());
        }
    }

    private static void menuMergeSort() {
        System.out.println("\n── Merge Sort por reseñas positivas ──");

        List<SteamGame> sorted = new ArrayList<>(games);

        long t1 = System.currentTimeMillis();
        MergeSortGames.mergeSort(sorted, 0, sorted.size() - 1);
        long t2 = System.currentTimeMillis();

        System.out.printf("  Ordenados %,d juegos en %d ms%n", sorted.size(), t2 - t1);

        // Top 10 más queridos
        System.out.println("\n  TOP 10 CON MÁS RESEÑAS POSITIVAS");
        System.out.println("  " + "─".repeat(50));
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            SteamGame g = sorted.get(i);
            System.out.printf("  %2d. %,8d ✓  —  %s%n",
                    i + 1, g.getPositive(), g.getName());
        }

    }

    private static void menuHashTable(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("""

                  ┌─────────────────────────────────┐
                  │         HASH TABLE              │
                  ├─────────────────────────────────┤
                  │  a. Cargar todos los juegos     │
                  │  b. Buscar juego por AppID      │
                  │  c. Eliminar juego por AppID    │
                  │  d. Ver estadísticas            │
                  │  e. Guardar tabla en CSV        │
                  │  0. Volver al menú principal    │
                  └─────────────────────────────────┘""");
            System.out.print("  Opción: ");
            String opt = sc.nextLine().trim().toLowerCase();

            switch (opt) {
                case "a" -> hashTableCargar();
                case "b" -> hashTableBuscar(sc);
                case "c" -> hashTableEliminar(sc);
                case "d" -> hashTable.printStats();
                case "e" -> hashTableGuardar(sc);
                case "0" -> back = true;
                default  -> System.out.println("  Opción no válida.");
            }
        }
    }

    private static void hashTableCargar() {
        if (hashTableLoaded) {
            System.out.println("  La tabla ya tiene datos (" + hashTable.getSize() + " juegos).");
            System.out.println("  Para recargar reinicia el programa.");
            return;
        }

        int limit = Math.min(games.size(), hashTable.getCapacity());

        long t1 = System.currentTimeMillis();
        int insertados = 0;
        for (int i = 0; i < limit; i++) {
            if (hashTable.insert(games.get(i))) insertados++;
        }
        long t2 = System.currentTimeMillis();

        hashTableLoaded = true;
        System.out.printf("  %,d juegos insertados en %d ms.%n", insertados, t2 - t1);
        hashTable.printStats();
    }

    private static void hashTableBuscar(Scanner sc) {
        if (!hashTableLoaded) {
            System.out.println("Primero carga los juegos (opción a).");
            return;
        }

        System.out.print("Ingresa el AppID a buscar: ");
        String id = sc.nextLine().trim();

        long t1 = System.nanoTime();
        SteamGame found = hashTable.search(id);
        long t2 = System.nanoTime();

        if (found != null) {
            System.out.println("\nJuego encontrado: ");
            System.out.println("  " + "─".repeat(50));
            System.out.printf("  AppID       : %s%n", found.getAppId());
            System.out.printf("  Nombre      : %s%n", found.getName());
            System.out.printf("  Fecha       : %s%n", found.getReleaseDate());
            System.out.printf("  Precio      : $%.2f%n", found.getPrice());
            System.out.printf("  Desarrollador: %s%n", found.getDevelopers());
            System.out.printf("  Géneros     : %s%n", found.getGenres());
            System.out.printf("  Positivos   : %,d%n", found.getPositive());
            System.out.printf("  Negativos   : %,d%n", found.getNegative());
            System.out.printf("  Recomendados: %,d%n", found.getRecommendations());
            System.out.printf("  Avg playtime: %.1f min%n", found.getAveragePlaytimeForever());
        } else {
            System.out.println(" No se encontró ningún juego con AppID: " + id);
        }
    }

    private static void hashTableEliminar(Scanner sc) {
        if (!hashTableLoaded) {
            System.out.println(" Primero carga los juegos (opción a).");
            return;
        }

        System.out.print("  Ingresa el AppID a eliminar: ");
        String id = sc.nextLine().trim();

        // Mostrar el juego antes de borrar
        SteamGame found = hashTable.search(id);
        if (found == null) {
            System.out.println(" AppID no encontrado: " + id);
            return;
        }

        System.out.println("  Juego a eliminar: " + found.getName() + " [$" + found.getPrice() + "]");
        System.out.print("  ¿Confirmar eliminación? (s/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("s")) {
            boolean ok = hashTable.delete(id);
            System.out.println(ok
                    ? " Eliminado correctamente."
                    : " No se pudo eliminar.");
        } else {
            System.out.println(" Cancelado.");
        }
    }

    private static void hashTableGuardar(Scanner sc) {
        System.out.print("  Nombre del archivo (Enter = hashtable.csv): ");
        String file = sc.nextLine().trim();
        if (file.isEmpty()) file = "hashtable.csv";
        hashTable.saveToCSV(file);
    }

}