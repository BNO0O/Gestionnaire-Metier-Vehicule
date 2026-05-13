package model;

public class VehiculeSpecial extends Vehicule implements Assignable {
    private String typeUrgence;
    private Chauffeur chauffeur;
    private boolean enMaintenance;

    public VehiculeSpecial(String immatriculation, int kilometrage, String typeUrgence) {
        super(immatriculation, "Spécial", kilometrage);
        this.typeUrgence = typeUrgence;
        this.enMaintenance = false;
    }

    @Override
    public String getDescription() {
        return "Véhicule spécial : " + getImmatriculation() + " [" + typeUrgence + "]";
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

    public void planifierMaintenance(String date) {
        this.enMaintenance = true;
        setEtat("maintenance");
    }

    public void signalerIncident(String description) {
        System.out.println("Incident urgent sur " + getImmatriculation() + " : " + description);
        setEtat("incident");
    }

    public boolean necessiteMaintenance() {
        return enMaintenance || getKilometrage() > 50000;
    }

    public String getTypeUrgence() {
        return typeUrgence;
    }

    public Chauffeur getChauffeur() {
        return chauffeur;
    }
}