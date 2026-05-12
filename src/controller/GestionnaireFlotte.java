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
            pw.println("[vehicules]");
            pw.println("immatriculation,type,etat,kilometrage,extra");
            for (Vehicule v : registreVehicules.getAll()) {
                String extra = "";
                if (v instanceof VehiculeLourd) extra = String.valueOf(((VehiculeLourd) v).getCapaciteTonnage());
                else if (v instanceof VehiculeSpecial) extra = ((VehiculeSpecial) v).getTypeUrgence();
                pw.println(v.getImmatriculation() + "," + v.getType() + "," + v.getEtat() + "," + v.getKilometrage() + "," + extra);
            }

            pw.println("[chauffeurs]");
            pw.println("nom,prenom,permis,disponible");
            for (Chauffeur c : chauffeurs) {
                pw.println(c.getNom() + "," + c.getPrenom() + "," + c.getNumeroPerm() + "," + c.isDisponible());
            }

            pw.println("[missions]");
            pw.println("id,type,description,date,statut,extra");
            for (Mission m : missions) {
                String extra = "";
                if (m instanceof MissionCourte) extra = String.valueOf(((MissionCourte) m).getDuree());
                else if (m instanceof MissionLongue) extra = ((MissionLongue) m).getItineraire();
                pw.println(m.getId() + "," + m.getType() + "," + m.getDescription() + "," + m.getDate() + "," + m.getStatut() + "," + extra);
            }
        }
    }

    public void chargerCSV(String chemin) throws IOException {
        registreVehicules = new Registre<>();
        fileUrgences.clear();
        chauffeurs.clear();
        missions.clear();
        agendaMissions.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String section = "";
            String ligne;
            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.startsWith("[")) {
                    section = ligne;
                    br.readLine(); // skip header
                    continue;
                }
                if (ligne.isEmpty()) continue;
                String[] p = ligne.split(",", -1);
                switch (section) {
                    case "[vehicules]":
                        if (p.length >= 4) {
                            Vehicule v;
                            String type = p[1].trim();
                            String extra = p.length >= 5 ? p[4].trim() : "";
                            if (type.equalsIgnoreCase("Lourd")) {
                                double tonnage = extra.isEmpty() ? 10.0 : Double.parseDouble(extra);
                                v = new VehiculeLourd(p[0], Integer.parseInt(p[3]), tonnage);
                            } else if (type.equalsIgnoreCase("Spécial") || type.equalsIgnoreCase("Special")) {
                                v = new VehiculeSpecial(p[0], Integer.parseInt(p[3]), extra.isEmpty() ? "Urgence" : extra);
                            } else {
                                v = new VehiculeLeger(p[0], Integer.parseInt(p[3]));
                            }
                            v.setEtat(p[2]);
                            ajouterVehicule(v);
                        }
                        break;
                    case "[chauffeurs]":
                        if (p.length >= 4) {
                            Chauffeur c = new Chauffeur(p[0], p[1], p[2]);
                            c.setDisponible(Boolean.parseBoolean(p[3]));
                            chauffeurs.add(c);
                        }
                        break;
                    case "[missions]":
                        if (p.length >= 5) {
                            String extra = p.length >= 6 ? p[5].trim() : "";
                            Mission m;
                            if (p[1].trim().equalsIgnoreCase("Mission courte")) {
                                int duree = extra.isEmpty() ? 1 : Integer.parseInt(extra);
                                m = new MissionCourte(p[0], p[2], p[3], duree);
                            } else {
                                m = new MissionLongue(p[0], p[2], p[3], extra.isEmpty() ? "A definir" : extra);
                            }
                            m.setStatut(p[4]);
                            ajouterMission(m);
                        }
                        break;
                }
            }
        }
    }
}