package model;

public class MissionDejaAssigneeException extends Exception {
    public MissionDejaAssigneeException(String idMission) {
        super("La mission " + idMission + " est déjà assignée.");
    }
}