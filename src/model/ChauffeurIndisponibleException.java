package model;

public class ChauffeurIndisponibleException extends Exception {
    public ChauffeurIndisponibleException(String nom) {
        super("Le chauffeur " + nom + " est indisponible.");
    }
}