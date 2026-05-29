import java.io.*;

public class HashTable {

    // ── Constantes
    private static final int CAPACITY    = 10007;

    private static final int MAX_INTENTS = CAPACITY;

    private HashEntry[] table;
    private int size;

    public HashTable() {
        this.table = new HashEntry[CAPACITY];
        this.size  = 0;
    }

    // ── Funciones hash
    private int h1(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++)
            hash = 31 * hash + key.charAt(i);
        return Math.abs(hash) % CAPACITY;
    }

    private int h2(String key) {
        final int R = 9973; // primo < CAPACITY
        int hash = 0;
        for (int i = 0; i < key.length(); i++)
            hash = 31 * hash + key.charAt(i);
        return R - (Math.abs(hash) % R); // valor en [1, R]
    }


    private int probe(String key, int i) {
        return (int)(((long) h1(key) + (long) i * h2(key)) % CAPACITY);
    }

    // ── INSERT
    public boolean insert(SteamGame game) {
        if (game == null)     return false;
        if (size >= CAPACITY) return false;

        // CORRECCIÓN: getAppId() retorna String directamente (ya no int)
        String key = game.getAppId();

        int firstDeleted = -1; // primer slot "lápida" encontrado

        for (int i = 0; i < MAX_INTENTS; i++) {
            int index = probe(key, i);

            if (table[index] == null) {
                // Slot libre: insertar aquí (o en la primera lápida vista)
                int target = (firstDeleted != -1) ? firstDeleted : index;
                table[target] = new HashEntry(key, game);
                size++;
                return true;
            }

            if (table[index].isDeleted()) {
                // Guardar primera lápida para reutilizarla si la clave no existe
                if (firstDeleted == -1) firstDeleted = index;
                continue;
            }

            if (table[index].getKey().equals(key)) {
                // Clave duplicada: actualizar valor
                table[index] = new HashEntry(key, game);
                return true;
            }
        }

        // Si recorrimos todos los slots y solo encontramos lápidas, reutilizar
        if (firstDeleted != -1) {
            table[firstDeleted] = new HashEntry(key, game);
            size++;
            return true;
        }

        System.out.println("No insertado (tabla llena): " + key);
        return false;
    }

    // ── SEARCH
    public SteamGame search(String appId) {
        for (int i = 0; i < MAX_INTENTS; i++) {
            int index = probe(appId, i);

            if (table[index] == null) return null; // cadena rota: no existe

            if (!table[index].isDeleted() && table[index].getKey().equals(appId))
                return table[index].getValue();
        }
        return null;
    }

    // ── DELETE lógico (lazy deletion / lápida)
    public boolean delete(String appId) {
        for (int i = 0; i < MAX_INTENTS; i++) {
            int index = probe(appId, i);

            if (table[index] == null) return false;

            if (!table[index].isDeleted() && table[index].getKey().equals(appId)) {
                table[index].markDeleted();
                size--;
                return true;
            }
        }
        return false;
    }

    // ── GUARDAR en CSV
    public void saveToCSV(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {

            pw.println("slot,appId,name,releaseDate,price," +
                    "recommendations,positive,negative,averagePlaytime," +
                    "developers,genres");

            for (int i = 0; i < CAPACITY; i++) {
                if (table[i] != null && !table[i].isDeleted()) {
                    SteamGame g = table[i].getValue();

                    pw.printf("%d,\"%s\",\"%s\",\"%s\",%.2f,%d,%d,%d,%.2f,\"%s\",\"%s\"%n",
                            i,
                            g.getAppId(),
                            g.getName(),
                            g.getReleaseDate(),
                            g.getPrice(),
                            g.getRecommendations(),
                            g.getPositive(),
                            g.getNegative(),
                            g.getAveragePlaytimeForever(),
                            g.getDevelopers(),
                            g.getGenres()
                    );
                }
            }

            System.out.println("HashTable guardada en: " + filePath +
                    " (" + size + " registros)");

        } catch (IOException e) {
            System.err.println("Error al guardar CSV: " + e.getMessage());
        }
    }

    // ── CARGAR desde CSV
    public void loadFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // saltar cabecera

                String[] f = parseLine(line);
                if (f == null || f.length < 11) continue;

                try {
                    int    slot          = Integer.parseInt(f[0].trim());
                    String appId         = f[1].trim();           // String, no int
                    String name          = f[2].trim();
                    String releaseDate   = f[3].trim();
                    double price         = Double.parseDouble(f[4].trim());
                    int    recommendations = Integer.parseInt(f[5].trim());
                    int    positive      = Integer.parseInt(f[6].trim());
                    int    negative      = Integer.parseInt(f[7].trim());
                    double avgPlaytime   = Double.parseDouble(f[8].trim());
                    String developers    = f[9].trim();
                    String genres        = f[10].trim();

                    SteamGame game = new SteamGame(
                            appId, name, releaseDate,
                            price, recommendations,
                            positive, negative,
                            avgPlaytime, developers, genres);

                    if (slot >= 0 && slot < CAPACITY && table[slot] == null) {
                        table[slot] = new HashEntry(appId, game);
                        size++;
                    }

                } catch (Exception e) {
                    // Fila inválida: se omite
                }
            }

            System.out.println("HashTable cargada desde: " + filePath +
                    " (" + size + " registros)");

        } catch (IOException e) {
            System.err.println("Error al cargar CSV: " + e.getMessage());
        }
    }

    // ── Parser CSV interno (respeta comillas dobles)
    private String[] parseLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); i++; // comilla escapada
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
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    // ── Estadísticas
    public int getSize()     { return size; }
    public int getCapacity() { return CAPACITY; }

    public void printStats() {
        System.out.println("========== HashTable Stats ==========");
        System.out.println("Capacidad      : " + CAPACITY);
        System.out.println("Elementos      : " + size);
        System.out.println("Max intentos   : " + MAX_INTENTS);
        System.out.println("=====================================");
    }
}