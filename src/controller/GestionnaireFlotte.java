package controller;

import model.*;
import util.Registre;
import java.util.*;
import java.util.stream.*;
import java.io.*;

public class GestionnaireFlotte {

    private Registre<Vehicule> registreVehicules = new Registre<>();
    private List<Chauffeur> chauffeurs = new ArrayList<>();
    private List<Mission> missions = new ArrayList<>();
    private PriorityQueue<VehiculeSpecial> fileUrgences = new PriorityQueue<>(
            Comparator.comparing(v -> v.getTypeUrgence()));
    private TreeMap<String, Mission> agendaMissions = new TreeMap<>();

    // ===== VEHICULES =====
    public void ajouterVehicule(Vehicule v) {
        registreVehicules.ajouter(v);
        if (v instanceof VehiculeSpecial) {
            fileUrgences.add((VehiculeSpecial) v);
        }
    }

    public void supprimerVehicule(Vehicule v) {
        registreVehicules.supprimer(v);
    }

    public List<Vehicule> getTousVehicules() {
        return registreVehicules.getAll();
    }

    public List<Vehicule> filtrerVehicules(String etat, String type, int kmMax) {
        return registreVehicules.getAll().stream()
                .filter(v -> etat.isEmpty() || v.getEtat().equalsIgnoreCase(etat))
                .filter(v -> type.isEmpty() || v.getType().equalsIgnoreCase(type))
                .filter(v -> kmMax == 0 || v.getKilometrage() <= kmMax)
                .collect(Collectors.toList());
    }

    public List<Vehicule> trierVehicules(String critere, boolean croissant) {
        Comparator<Vehicule> comp;
        switch (critere) {
            case "kilometrage":
                comp = Comparator.comparingInt(Vehicule::getKilometrage);
                break;
            case "type":
                comp = Comparator.comparing(Vehicule::getType);
                break;
            default:
                comp = Comparator.comparing(Vehicule::getImmatriculation);
        }
        if (!croissant)
            comp = comp.reversed();
        return registreVehicules.getAll().stream()
                .sorted(comp)
                .collect(Collectors.toList());
    }

    // ===== CHAUFFEURS =====
    public void ajouterChauffeur(Chauffeur c) {
        chauffeurs.add(c);
    }

    public void supprimerChauffeur(Chauffeur c) {
        chauffeurs.remove(c);
    }

    public List<Chauffeur> getTousChauffeurs() {
        return chauffeurs;
    }

    public List<Chauffeur> getChauffeursDispo() {
        return chauffeurs.stream()
                .filter(Chauffeur::isDisponible)
                .collect(Collectors.toList());
    }

    // ===== MISSIONS =====
    public void ajouterMission(Mission m) {
        missions.add(m);
        agendaMissions.put(m.getDate() + "_" + m.getId(), m);
    }

    public void supprimerMission(Mission m) {
        missions.remove(m);
        agendaMissions.values().remove(m);
    }

    public List<Mission> getTousMissions() {
        return missions;
    }

    public List<Mission> filtrerMissions(String statut) {
        return missions.stream()
                .filter(m -> statut.isEmpty() || m.getStatut().equalsIgnoreCase(statut))
                .collect(Collectors.toList());
    }

    // ===== ASSIGNATION =====
    public void assignerVehicule(Vehicule v, Chauffeur c)
            throws VehiculeIndisponibleException, ChauffeurIndisponibleException {
        if (!v.getEtat().equals("disponible")) {
            throw new VehiculeIndisponibleException(v.getImmatriculation());
        }
        if (!c.isDisponible()) {
            throw new ChauffeurIndisponibleException(c.getNom());
        }
        if (v instanceof Assignable) {
            ((Assignable) v).assigner(c);
        }
    }

    // ===== STATISTIQUES =====
    public int getNbVehiculesDisponibles() {
        return (int) registreVehicules.getAll().stream()
                .filter(v -> v.getEtat().equals("disponible"))
                .count();
    }

    public double getKilometragesMoyen() {
        return registreVehicules.getAll().stream()
                .mapToInt(Vehicule::getKilometrage)
                .average()
                .orElse(0);
    }

    public int getNbMissionsEnCours() {
        return (int) missions.stream()
                .filter(m -> m.getStatut().equals("en cours"))
                .count();
    }

    public Map<String, Long> getVehiculesParEtat() {
        return registreVehicules.getAll().stream()
                .collect(Collectors.groupingBy(Vehicule::getEtat, Collectors.counting()));
    }

    // ===== PERSISTANCE CSV =====
    public void sauvegarderCSV(String chemin) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(chemin))) {
            pw.println("immatriculation,type,etat,kilometrage");
            registreVehicules.getAll().forEach(v -> pw.println(
                    v.getImmatriculation() + "," + v.getType() + "," + v.getEtat() + "," + v.getKilometrage()));
        }
    }

    public void chargerCSV(String chemin) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            br.readLine(); // skip header
            String ligne;
            while ((ligne = br.readLine()) != null) {
                String[] parts = ligne.split(",");
                if (parts.length >= 4) {
                    VehiculeLeger v = new VehiculeLeger(parts[0], Integer.parseInt(parts[3]));
                    v.setEtat(parts[2]);
                    registreVehicules.ajouter(v);
                }
            }
        }
    }
}