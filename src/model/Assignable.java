package model;

public interface Assignable {
    void assigner(Chauffeur chauffeur);
    void desassigner();
    boolean estDisponible();
}