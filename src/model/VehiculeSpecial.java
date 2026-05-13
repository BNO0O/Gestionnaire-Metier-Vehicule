package model;

public class VehiculeSpecial extends Vehicule implements Assignable {
    private String typeUrgence;
    private Chauffeur chauffeur;

    public VehiculeSpecial(String immatriculation, int kilometrage, String typeUrgence) {
        super(immatriculation, "Spécial", kilometrage);
        this.typeUrgence = typeUrgence;
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

    public String getTypeUrgence() {
        return typeUrgence;
    }

    public Chauffeur getChauffeur() {
        return chauffeur;
    }
}