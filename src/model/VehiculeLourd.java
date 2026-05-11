package model;

public class VehiculeLourd extends Vehicule implements Assignable, Maintenable {
    private double capaciteTonnage;
    private Chauffeur chauffeur;
    private boolean enMaintenance;

    public VehiculeLourd(String immatriculation, int kilometrage, double capaciteTonnage) {
        super(immatriculation, "Lourd", kilometrage);
        this.capaciteTonnage = capaciteTonnage;
        this.enMaintenance = false;
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

    @Override
    public void planifierMaintenance(String date) {
        this.enMaintenance = true;
        setEtat("maintenance");
    }

    @Override
    public void signalerIncident(String description) {
        System.out.println("Incident sur " + getImmatriculation() + " : " + description);
        setEtat("incident");
    }

    @Override
    public boolean necessiteMaintenance() {
        return enMaintenance || getKilometrage() > 80000;
    }

    public double getCapaciteTonnage() {
        return capaciteTonnage;
    }

    public Chauffeur getChauffeur() {
        return chauffeur;
    }
}