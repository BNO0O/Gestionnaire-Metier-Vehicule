package model;

public abstract class Vehicule {
    private String immatriculation;
    private String type;
    private String etat;
    private int kilometrage;

    public Vehicule(String immatriculation, String type, int kilometrage) {
        this.immatriculation = immatriculation;
        this.type = type;
        this.etat = "disponible";
        this.kilometrage = kilometrage;
    }

    public abstract String getDescription();

    public String getImmatriculation() {
        return immatriculation;
    }

    public String getType() {
        return type;
    }

    public String getEtat() {
        return etat;
    }

    public int getKilometrage() {
        return kilometrage;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setKilometrage(int kilometrage) {
        this.kilometrage = kilometrage;
    }

    @Override
    public String toString() {
        return immatriculation + " | " + type + " | " + etat + " | " + kilometrage + " km";
    }
}