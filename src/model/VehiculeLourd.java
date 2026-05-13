package model;

public class VehiculeLourd extends Vehicule implements Assignable {
    private double capaciteTonnage;
    private Chauffeur chauffeur;

    public VehiculeLourd(String immatriculation, int kilometrage, double capaciteTonnage) {
        super(immatriculation, "Lourd", kilometrage);
        this.capaciteTonnage = capaciteTonnage;
    }

    @Override
    public String getDescription() {
        return "Véhicule lourd : " + getImmatriculation() + " (" + capaciteTonnage + "T)";
    }

    @Override
    public void assigner(Chauffeur chauffeur) {
        this.chauffeur = chauffeur;
        chauffeur.setDisponible(false);
        setEtat("assigné");
    }

    @Override
    public void desassigner() {
        if (this.chauffeur != null) {
            this.chauffeur.setDisponible(true);
        }
        this.chauffeur = null;
        setEtat("disponible");
    }

    @Override
    public boolean estDisponible() {
        return getEtat().equals("disponible");
    }

    public double getCapaciteTonnage() {
        return capaciteTonnage;
    }

    public Chauffeur getChauffeur() {
        return chauffeur;
    }
}