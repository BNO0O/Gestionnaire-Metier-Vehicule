package model;

public interface Maintenable {
    void planifierMaintenance(String date);

    void signalerIncident(String description);

    boolean necessiteMaintenance();
}