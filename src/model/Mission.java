package model;

public abstract class Mission {
    private String id;
    private String description;
    private String statut;
    private String date;

    public Mission(String id, String description, String date) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.statut = "en attente";
    }

    public abstract String getType();

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStatut() {
        return statut;
    }

    public String getDate() {
        return date;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return id + " | " + getType() + " | " + statut + " | " + date;
    }
}