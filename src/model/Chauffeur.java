package model;

public class Chauffeur {
    private String nom;
    private String prenom;
    private String numeroPerm;
    private boolean disponible;

    public Chauffeur(String nom, String prenom, String numeroPerm) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroPerm = numeroPerm;
        this.disponible = true;
    }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNumeroPerm() { return numeroPerm; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return nom + " " + prenom + " | Permis: " + numeroPerm + " | " + (disponible ? "Disponible" : "Indisponible");
    }
}