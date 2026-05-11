package model;

public class VehiculeIndisponibleException extends Exception {
    public VehiculeIndisponibleException(String immatriculation) {
        super("Le véhicule " + immatriculation + " est indisponible.");
    }
}