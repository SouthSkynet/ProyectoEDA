import java.util.List;

public class QuickSortGames {

    public static void quickSort(List<SteamGame> games, int low, int high) {

        if (low >= high) {
            return;
        }

        int i = low;
        int j = high;

        // pivote central
        double pivot = games.get(low + (high - low) / 2).getPrice();

        while (i <= j) {

            while (games.get(i).getPrice() < pivot) {
                i++;
            }

            while (games.get(j).getPrice() > pivot) {
                j--;
            }

            if (i <= j) {

                swap(games, i, j);

                i++;
                j--;
            }
        }

        // Recursión SOLO si hay rango válido
        if (low < j) {
            quickSort(games, low, j);
        }

        if (i < high) {
            quickSort(games, i, high);
        }
    }

    private static void swap(List<SteamGame> games, int i, int j) {

        SteamGame temp = games.get(i);
        games.set(i, games.get(j));
        games.set(j, temp);
    }
}