import java.io.*;
import java.util.*;

public class CSVReader {

    private static final String DELIMITER = ",";
    private static final int MAX_RECORDS  = 100_000;

    public static List<SteamGame> readCSV(String filePath) {
        List<SteamGame> games = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String header = br.readLine(); // Saltar la cabecera
            if (header == null) {
                System.out.println("El archivo está vacío.");
                return games;
            }

            String line;
            int count = 0;

            while ((line = br.readLine()) != null && count < MAX_RECORDS) {

                // Parsear respetando campos entre comillas
                String[] fields = parseLine(line);

                // Verificar que haya suficientes columnas
                if (fields.length < 37) continue;

                try {
                    String appId           = fields[0].trim();
                    String name            = fields[1].trim();
                    String releaseDate     = fields[2].trim();
                    double price           = parseDouble(fields[6]);
                    int    positive        = parseInt(fields[22]);
                    int    negative        = parseInt(fields[23]);
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
                    // Si una fila tiene datos corruptos, la saltamos
                    System.out.println("Fila ignorada (datos inválidos): " + line);
                }
            }

            System.out.println("Total de juegos cargados: " + games.size());

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return games;
    }

    private static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes; // Entrar o salir de campo con comillas
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
