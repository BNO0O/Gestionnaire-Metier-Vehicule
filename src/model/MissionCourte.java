package model;

public class MissionCourte extends Mission {
    private int duree;

    public MissionCourte(String id, String description, String date, int duree) {
        super(id, description, date);
        this.duree = duree;
    }

    @Override
    public String getType() {
        return "Mission courte";
    }

    public int getDuree() {
        return duree;
    }

    @Override
    public String toString() {
        return super.toString() + " | Durée: " + duree + "h";
    }
}