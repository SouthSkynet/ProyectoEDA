public class HashEntry {

    private String    key;
    private SteamGame value;
    private boolean   deleted;

    public HashEntry(String key, SteamGame value) {
        this.key     = key;
        this.value   = value;
        this.deleted = false;
    }

    public String    getKey()      { return key; }
    public SteamGame getValue()    { return value; }
    public boolean   isDeleted()   { return deleted; }
    public void      markDeleted() { this.deleted = true; }
}