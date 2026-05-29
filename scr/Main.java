import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static List<SteamGame> games        = new ArrayList<>();
    private static HashTable       hashTable     = new HashTable();
    private static boolean         hashTableLoaded = false;

    // Copias ordenadas
    private static List<SteamGame> sortedByPrice    = null;
    private static List<SteamGame> sortedByPositive = null;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        games = CSVReader.readCSV("games.csv");
        if (games.isEmpty()) {
            System.out.println("No se cargaron datos. Verifica la ruta.");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Opción: ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1" -> menuQuickSort(sc);
                case "2" -> menuMergeSort(sc);
                case "3" -> menuHashTable(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Opción no válida.");
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


    private static void menuQuickSort(Scanner sc) {
        if (sortedByPrice == null) {
            System.out.println("\n  Ordenando " + String.format("%,d", games.size()) + " juegos por precio...");
            sortedByPrice = new ArrayList<>(games);
            long t1 = System.currentTimeMillis();
            QuickSortGames.quickSort(sortedByPrice, 0, sortedByPrice.size() - 1);
            long t2 = System.currentTimeMillis();
            System.out.printf("  Quick Sort completado en %d ms.%n", t2 - t1);
        }

        boolean back = false;
        while (!back) {
            System.out.println("""

                  ┌──────────────────────────────────────┐
                  │           QUICK SORT                 │
                  ├──────────────────────────────────────┤
                  │  1. Mostrar todos los juegos         │
                  │     ordenados por precio             │
                  │  2. Top 10 juegos más caros          │
                  │  3. Top 10 juegos más baratos        │
                  │  0. Volver al menú principal         │
                  └──────────────────────────────────────┘""");
            System.out.print("  Opción: ");
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1" -> quickSortMostrarTodos();
                case "2" -> quickSortMasCaros();
                case "3" -> quickSortMasBaratos();
                case "0" -> back = true;
                default  -> System.out.println("  Opción no válida.");
            }
        }
    }

    private static void quickSortMostrarTodos() {
        System.out.println("\n  TODOS LOS JUEGOS ORDENADOS POR PRECIO (Quick Sort)");
        System.out.println("  " + "─".repeat(60));
        for (int i = 0; i < sortedByPrice.size(); i++) {
            SteamGame g = sortedByPrice.get(i);
            System.out.printf("  %5d. $%8.2f  —  %s%n", i + 1, g.getPrice(), g.getName());
        }
        System.out.println("  " + "─".repeat(60));
        System.out.printf("  Total: %,d juegos%n", sortedByPrice.size());
    }

    private static void quickSortMasCaros() {
        System.out.println("\n  TOP 10 JUEGOS MÁS CAROS (Quick Sort)");
        System.out.println("  " + "─".repeat(60));
        int n = sortedByPrice.size();
        for (int i = n - 1; i >= Math.max(0, n - 10); i--) {
            SteamGame g = sortedByPrice.get(i);
            System.out.printf("  %2d. $%8.2f  —  %s%n", n - i, g.getPrice(), g.getName());
        }
    }

    private static void quickSortMasBaratos() {
        List<SteamGame> paid = sortedByPrice.stream()
                .filter(g -> g.getPrice() > 0.0)
                .collect(Collectors.toList());

        System.out.println("\n  TOP 10 JUEGOS MÁS BARATOS (No gratuitos)");
        System.out.println("  " + "─".repeat(60));
        for (int i = 0; i < Math.min(10, paid.size()); i++) {
            SteamGame g = paid.get(i);
            System.out.printf("  %2d. $%8.2f  —  %s%n", i + 1, g.getPrice(), g.getName());
        }
    }

    private static void menuMergeSort(Scanner sc) {
        if (sortedByPositive == null) {
            System.out.println("\n  Ordenando " + String.format("%,d", games.size()) + " juegos por reseñas positivas...");
            sortedByPositive = new ArrayList<>(games);
            long t1 = System.currentTimeMillis();
            MergeSortGames.mergeSort(sortedByPositive, 0, sortedByPositive.size() - 1);
            long t2 = System.currentTimeMillis();
            System.out.printf("  Merge Sort completado en %d ms.%n", t2 - t1);
        }

        boolean back = false;
        while (!back) {
            System.out.println("""

                  ┌──────────────────────────────────────┐
                  │           MERGE SORT                 │
                  ├──────────────────────────────────────┤
                  │  1. Mostrar todos los juegos         │
                  │     ordenados por reseñas (+)        │
                  │  2. Top 10 con más reseñas positivas │
                  │  0. Volver al menú principal         │
                  └──────────────────────────────────────┘""");
            System.out.print("  Opción: ");
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1" -> mergeSortMostrarTodos();
                case "2" -> mergeSortTop10();
                case "0" -> back = true;
                default  -> System.out.println("  Opción no válida.");
            }
        }
    }

    private static void mergeSortMostrarTodos() {
        System.out.println("\n  TODOS LOS JUEGOS ORDENADOS POR RESEÑAS POSITIVAS (Merge Sort)");
        System.out.println("  " + "─".repeat(60));
        for (int i = 0; i < sortedByPositive.size(); i++) {
            SteamGame g = sortedByPositive.get(i);
            System.out.printf("  %5d. %,8d  —  %s%n", i + 1, g.getPositive(), g.getName());
        }
        System.out.println("  " + "─".repeat(60));
        System.out.printf("  Total: %,d juegos%n", sortedByPositive.size());
    }

    private static void mergeSortTop10() {
        System.out.println("\n  TOP 10 CON MÁS RESEÑAS POSITIVAS (Merge Sort)");
        System.out.println("  " + "─".repeat(60));
        for (int i = 0; i < Math.min(10, sortedByPositive.size()); i++) {
            SteamGame g = sortedByPositive.get(i);
            System.out.printf("  %2d. %,8d  —  %s%n", i + 1, g.getPositive(), g.getName());
        }
    }

    private static void menuHashTable(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("""

                  ┌─────────────────────────────────────┐
                  │           HASH TABLE                │
                  ├─────────────────────────────────────┤
                  │  a. Cargar los juegos               │
                  │  b. Buscar juego por AppID          │
                  │  c. Eliminar juego por AppID        │
                  │  d. Ver estadísticas                │
                  │  e. Guardar tabla en CSV            │
                  │  f. Imprimir tabla en consola       │
                  │  0. Volver al menú principal        │
                  └─────────────────────────────────────┘""");
            System.out.print("  Opción: ");
            String opt = sc.nextLine().trim().toLowerCase();

            switch (opt) {
                case "a" -> hashTableCargar();
                case "b" -> hashTableBuscar(sc);
                case "c" -> hashTableEliminar(sc);
                case "d" -> hashTable.printStats();
                case "e" -> hashTableGuardar(sc);
                case "f" -> hashTableImprimir();
                case "0" -> back = true;
                default  -> System.out.println("  Opción no válida.");
            }
        }
    }

    private static void hashTableCargar() {
        if (hashTableLoaded) {
            System.out.println("  La tabla ya tiene datos (" + hashTable.getSize() + " juegos).");
            return;
        }


        int insertados = 0;
        for (int i = 0; i < games.size() && hashTable.getSize() < 10007; i++) {
            if (hashTable.insert(games.get(i))) insertados++;
        }

        hashTableLoaded = true;
        System.out.printf("  %,d juegos insertados ", insertados);
        hashTable.printStats();
    }

    private static void hashTableBuscar(Scanner sc) {
        if (!hashTableLoaded) { System.out.println("  Primero carga los juegos (opción a)."); return; }

        System.out.print("  Ingresa el AppID a buscar: ");
        String id = sc.nextLine().trim();

        long t1 = System.nanoTime();
        SteamGame found = hashTable.search(id);
        long t2 = System.nanoTime();

        if (found != null) {
            System.out.println("\n  Juego encontrado:");
            System.out.println("  " + "─".repeat(50));
            System.out.printf("  AppID        : %s%n",    found.getAppId());
            System.out.printf("  Nombre       : %s%n",    found.getName());
            System.out.printf("  Fecha        : %s%n",    found.getReleaseDate());
            System.out.printf("  Precio       : $%.2f%n", found.getPrice());
            System.out.printf("  Desarrollador: %s%n",    found.getDevelopers());
            System.out.printf("  Géneros      : %s%n",    found.getGenres());
            System.out.printf("  Positivos    : %,d%n",   found.getPositive());
            System.out.printf("  Negativos    : %,d%n",   found.getNegative());
            System.out.printf("  Recomendados : %,d%n",   found.getRecommendations());
            System.out.printf("  Avg playtime : %.1f min%n", found.getAveragePlaytimeForever());
            System.out.printf("  Búsqueda en  : %,d ns%n", t2 - t1);
        } else {
            System.out.println("  No se encontró ningún juego con AppID: " + id);
        }
    }

    private static void hashTableEliminar(Scanner sc) {
        if (!hashTableLoaded) { System.out.println("  Primero carga los juegos (opción a)."); return; }

        System.out.print("  Ingresa el AppID a eliminar: ");
        String id = sc.nextLine().trim();

        SteamGame found = hashTable.search(id);
        if (found == null) { System.out.println("  AppID no encontrado: " + id); return; }

        System.out.println("  Juego a eliminar: " + found.getName() + " [$" + found.getPrice() + "]");
        System.out.print("  ¿Confirmar eliminación? (s/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("s")) {
            System.out.println(hashTable.delete(id) ? "  Eliminado correctamente." : "  No se pudo eliminar.");
        } else {
            System.out.println("  Cancelado.");
        }
    }

    private static void hashTableGuardar(Scanner sc) {
        System.out.print("  Nombre del archivo (Enter = hashtable.csv): ");
        String file = sc.nextLine().trim();
        if (file.isEmpty()) file = "hashtable.csv";
        hashTable.saveToCSV(file);
    }

    private static void hashTableImprimir() {
        if (!hashTableLoaded) {
            System.out.println("  Primero carga los juegos (opción a).");
            return;
        }

        System.out.println("\n  TABLA HASH — slots ocupados");
        System.out.println("  " + "─".repeat(70));
        System.out.printf("  %-6s  %-8s  %-35s  %s%n", "Slot", "AppID", "Nombre", "Precio");
        System.out.println("  " + "─".repeat(70));

        hashTable.printTableConsola();
    }
}