package model;

public class VehiculeLeger extends Vehicule implements Assignable {
    private Chauffeur chauffeur;

    public VehiculeLeger(String immatriculation, int kilometrage) {
        super(immatriculation, "Léger", kilometrage);

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

    public Chauffeur getChauffeur() {
        return chauffeur;
    }
}