public class SteamGame {

    private String appId;
    private String name;
    private String releaseDate;
    private double price;
    private int recommendations;
    private int positive;
    private int negative;
    private double averagePlaytimeForever; //minutos
    private String developers;
    private String genres;


    public SteamGame(String appId, String name, String releaseDate,
                     double price, int recommendations, int positive,
                     int negative, double averagePlaytimeForever,
                     String developers, String genres) {
        this.appId              = appId;
        this.name               = name;
        this.releaseDate        = releaseDate;
        this.price              = price;
        this.recommendations    = recommendations;
        this.positive           = positive;
        this.negative           = negative;
        this.averagePlaytimeForever = averagePlaytimeForever;
        this.developers         = developers;
        this.genres             = genres;
    }

    // Getters
    public String getAppId()                  {
        return appId;
    }
    public String getName()                   {
        return name;
    }
    public String getReleaseDate()            {
        return releaseDate;
    }
    public double getPrice()                  {
        return price; }
    public int getRecommendations()        {
        return recommendations;
    }
    public int getPositive()               {
        return positive;
    }
    public int getNegative()               {
        return negative;
    }
    public double getAveragePlaytimeForever() {
        return averagePlaytimeForever;
    }
    public String getDevelopers()             {
        return developers;
    }
    public String getGenres()                 {
        return genres;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | $%.2f | Recomendaciones: %d | Género: %s",
                appId, name, price, recommendations, genres);
    }

}
