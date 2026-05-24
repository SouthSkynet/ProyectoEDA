import java.util.*;
public class Main {
    public static void main(String[] args) {

        List<SteamGame> games = CSVReader.readCSV("games.csv");

        List<SteamGame> gamesParaSamara = new ArrayList<>(games);
        List<SteamGame> gamesParaJhon = new ArrayList<>(games);

    }
}
