package model;

public class VehiculeLeger extends Vehicule implements Assignable, Maintenable {
    private Chauffeur chauffeur;
    private boolean enMaintenance;

    public VehiculeLeger(String immatriculation, int kilometrage) {
        super(immatriculation, "Léger", kilometrage);
        this.enMaintenance = false;
    }

    @Override
    public String getDescription() {
        return "Véhicule léger : " + getImmatriculation();
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
        return enMaintenance || getKilometrage() > 100000;
    }

    public Chauffeur getChauffeur() {
        return chauffeur;
    }
}