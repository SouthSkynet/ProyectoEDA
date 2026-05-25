import java.util.*;

public class Main {

    public static void main(String[] args) {

        // Leer CSV
        List<SteamGame> games = CSVReader.readCSV("games.csv");

        // Verificar si se cargaron datos
        if (games.isEmpty()) {
            System.out.println("No se cargaron datos.");
            return;
        }

        // Copias para cada integrante
        List<SteamGame> gamesParaSamara = new ArrayList<>(games);
        List<SteamGame> gamesParaJhon = new ArrayList<>(games);

        // ===== QUICK SORT =====
        long inicio = System.currentTimeMillis();

        QuickSortGames.quickSort(
                gamesParaJhon,
                0,
                gamesParaJhon.size() - 1
        );

        long fin = System.currentTimeMillis();

        // ===== RESULTADOS =====
        System.out.println("Quick Sort completado.");
        System.out.println("Tiempo: " + (fin - inicio) + " ms");

        // Primeros 10 (más baratos)
        System.out.println("\nJUEGOS MÁS BARATOS:\n");

        for (int i = 0; i < 10; i++) {
            System.out.println(gamesParaJhon.get(i));
        }

        // Últimos 10 (más caros)
        System.out.println("\nJUEGOS MÁS CAROS:\n");

        for (int i = gamesParaJhon.size() - 10;
             i < gamesParaJhon.size();
             i++) {

            System.out.println(gamesParaJhon.get(i));
        }
    }
}