import java.io.*;
import java.util.*;

public class CSVReader {

    private static final String DELIMITER   = ",";
    private static final int    MAX_RECORDS = 100_000;

    /*
     * Columnas del CSV fronkongames/steam-games-dataset:
     *  0  AppID                    1  Name
     *  2  Release date             3  Estimated owners
     *  4  Peak CCU                 5  Required age
     *  6  Price                    7  DLC count
     *  8  About the game           9  Short description
     * 10  Supported languages     11  Full audio languages
     * 12  Reviews                 13  Header image
     * 14  Website                 15  Support URL
     * 16  Support email           17  Windows
     * 18  Mac                     19  Linux
     * 20  Metacritic score        21  Metacritic URL
     * 22  User score              23  Positive        ← OJO: no es col 22
     * 24  Negative                25  Score rank
     * 26  Achievements            27  Recommendations
     * 28  Notes                   29  Average playtime forever
     * 30  Avg playtime 2 weeks    31  Median playtime forever
     * 32  Median playtime 2 weeks 33  Developers
     * 34  Publishers              35  Categories
     * 36  Genres                  37  Screenshots
     * 38  Movies                  39  Tags
     */
    public static List<SteamGame> readCSV(String filePath) {
        List<SteamGame> games = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String header = br.readLine(); // Saltar cabecera
            if (header == null) {
                System.out.println("El archivo está vacío.");
                return games;
            }

            String line;
            int count = 0;

            while ((line = br.readLine()) != null && count < MAX_RECORDS) {

                String[] fields = parseLine(line);

                // El CSV tiene al menos 37 columnas (hasta "Genres")
                if (fields.length < 37) continue;

                try {
                    String appId           = fields[0].trim();
                    String name            = fields[1].trim();
                    String releaseDate     = fields[2].trim();
                    double price           = parseDouble(fields[6]);
                    int    positive        = parseInt(fields[23]); // col 23, NO 22
                    int    negative        = parseInt(fields[24]); // col 24, NO 23
                    int    recommendations = parseInt(fields[27]);
                    double avgPlaytime     = parseDouble(fields[29]);
                    String developers      = fields[33].trim();
                    String genres          = fields[36].trim();

                    games.add(new SteamGame(appId, name, releaseDate,
                            price, recommendations,
                            positive, negative,
                            avgPlaytime, developers, genres));
                    count++;

                } catch (Exception e) {
                    // Fila con datos corruptos: se omite
                }
            }

            System.out.println("Total de juegos cargados: " + games.size());

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return games;
    }

    // ── Parser CSV que respeta campos entre comillas ──────────────────────────
    private static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Comilla escapada ("") dentro de un campo entrecomillado
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString()); // Último campo
        return fields.toArray(new String[0]);
    }

    // ── Helpers de parseo seguro ──────────────────────────────────────────────
    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}