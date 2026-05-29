import java.util.*;

public class MergeSortGames {

    // Punto de entrada público
    public static void mergeSort(List<SteamGame> games, int left, int right) {
        if (left >= right) return; // caso base: 0 o 1 elemento

        int mid = left + (right - left) / 2; // punto medio (evita overflow)

        mergeSort(games, left, mid);       // ordenar mitad izquierda
        mergeSort(games, mid + 1, right);  // ordenar mitad derecha
        merge(games, left, mid, right);    // combinar ambas mitades
    }

    private static void merge(List<SteamGame> games, int left, int mid, int right) {

        // Copias temporales de cada mitad
        List<SteamGame> leftPart  = new ArrayList<>(games.subList(left, mid + 1));
        List<SteamGame> rightPart = new ArrayList<>(games.subList(mid + 1, right + 1));

        int i = 0;              // cursor en leftPart
        int j = 0;              // cursor en rightPart
        int k = left;           // cursor en la lista original

        while (i < leftPart.size() && j < rightPart.size()) {
            // DESCENDENTE: el mayor positive va primero
            if (leftPart.get(i).getPositive() >= rightPart.get(j).getPositive()) {
                games.set(k++, leftPart.get(i++));
            } else {
                games.set(k++, rightPart.get(j++));
            }
        }

        // Volcar los elementos restantes de cada mitad
        while (i < leftPart.size())  games.set(k++, leftPart.get(i++));
        while (j < rightPart.size()) games.set(k++, rightPart.get(j++));
    }
}