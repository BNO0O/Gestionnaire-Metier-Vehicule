package model;

public class MissionLongue extends Mission implements Trackable {
    private String itineraire;
    private String position;

    public MissionLongue(String id, String description, String date, String itineraire) {
        super(id, description, date);
        this.itineraire = itineraire;
        this.position = "Départ";
    }

    @Override
    public String getType() {
        return "Mission longue";
    }

    @Override
    public String getPosition() {
        return position;
    }

    @Override
    public String getItineraire() {
        return itineraire;
    }

    @Override
    public void mettreAJourPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return super.toString() + " | Itinéraire: " + itineraire + " | Position: " + position;
    }
}