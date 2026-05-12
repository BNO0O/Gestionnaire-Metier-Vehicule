package view;

import controller.GestionnaireFlotte;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class FenetrePrincipale extends JFrame {

    private GestionnaireFlotte gestionnaire = new GestionnaireFlotte();
    private JTable tableVehicules;
    private DefaultTableModel tableModel;
    private JLabel labelStats;

    public FenetrePrincipale() {
        setTitle("Gestionnaire de Flotte de Vehicules");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initialiserDonneesTest();
        initUI();
    }

    private void initialiserDonneesTest() {
        gestionnaire.ajouterVehicule(new VehiculeLeger("AB-123-CD", 45000));
        gestionnaire.ajouterVehicule(new VehiculeLourd("EF-456-GH", 80000, 12.5));
        gestionnaire.ajouterVehicule(new VehiculeSpecial("IJ-789-KL", 55000, "Ambulance"));
        gestionnaire.ajouterChauffeur(new Chauffeur("Dupont", "Jean", "B-12345"));
        gestionnaire.ajouterChauffeur(new Chauffeur("Martin", "Marie", "C-67890"));
        gestionnaire.ajouterMission(new MissionCourte("M001", "Livraison Paris", "2024-01-15", 3));
        gestionnaire.ajouterMission(
                new MissionLongue("M002", "Transport Lyon-Marseille", "2024-01-16", "Lyon -> Marseille"));
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Vehicules", creerPanelVehicules());
        onglets.addTab("Chauffeurs", creerPanelChauffeurs());
        onglets.addTab("Missions", creerPanelMissions());
        onglets.addTab("Statistiques", creerPanelStats());
        add(onglets, BorderLayout.CENTER);

        labelStats = new JLabel("  Vehicules disponibles : " + gestionnaire.getNbVehiculesDisponibles());
        labelStats.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(labelStats, BorderLayout.SOUTH);
    }

    // ===== PANEL VEHICULES =====
    private JPanel creerPanelVehicules() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tableau
        String[] colonnes = { "Immatriculation", "Type", "Etat", "Kilometrage" };
        tableModel = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableVehicules = new JTable(tableModel);
        tableVehicules.setRowHeight(25);
        tableVehicules.getTableHeader().setReorderingAllowed(false);
        rafraichirTableVehicules();

        // Barre de filtrage et tri
        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblEtat = new JLabel("Etat :");
        String[] etats = { "Tous", "disponible", "assigne", "maintenance", "incident" };
        JComboBox<String> filtreEtat = new JComboBox<>(etats);

        JLabel lblType = new JLabel("Type :");
        String[] types = { "Tous", "Leger", "Lourd", "Special" };
        JComboBox<String> filtreType = new JComboBox<>(types);

        JLabel lblKm = new JLabel("Km max :");
        JTextField filtreKm = new JTextField("0", 6);

        JLabel lblTri = new JLabel("Trier par :");
        String[] tris = { "Immatriculation", "Kilometrage", "Type" };
        JComboBox<String> filtreTri = new JComboBox<>(tris);

        JLabel lblOrdre = new JLabel("Ordre :");
        String[] ordres = { "Croissant", "Decroissant" };
        JComboBox<String> filtreOrdre = new JComboBox<>(ordres);

        JButton btnAppliquer = new JButton("Appliquer");
        btnAppliquer.addActionListener(e -> {
            String etatChoisi = filtreEtat.getSelectedItem().equals("Tous") ? ""
                    : (String) filtreEtat.getSelectedItem();
            String typeChoisi = filtreType.getSelectedItem().equals("Tous") ? ""
                    : (String) filtreType.getSelectedItem();
            int kmMax = 0;
            try {
                kmMax = Integer.parseInt(filtreKm.getText().trim());
            } catch (NumberFormatException ex) {
                kmMax = 0;
            }
            String triChoisi = ((String) filtreTri.getSelectedItem()).toLowerCase();
            boolean croissant = filtreOrdre.getSelectedItem().equals("Croissant");

            List<Vehicule> resultats = gestionnaire.filtrerVehicules(etatChoisi, typeChoisi, kmMax);
            final String triFinal = triChoisi;
            final boolean croissantFinal = croissant;
            resultats.sort((a, b) -> {
                int cmp;
                switch (triFinal) {
                    case "kilometrage":
                        cmp = Integer.compare(a.getKilometrage(), b.getKilometrage());
                        break;
                    case "type":
                        cmp = a.getType().compareTo(b.getType());
                        break;
                    default:
                        cmp = a.getImmatriculation().compareTo(b.getImmatriculation());
                }
                return croissantFinal ? cmp : -cmp;
            });

            tableModel.setRowCount(0);
            for (Vehicule v : resultats) {
                tableModel.addRow(new Object[] {
                        v.getImmatriculation(), v.getType(), v.getEtat(), v.getKilometrage() + " km"
                });
            }
        });

        JButton btnReinitialiser = new JButton("Reinitialiser");
        btnReinitialiser.addActionListener(e -> {
            filtreEtat.setSelectedIndex(0);
            filtreType.setSelectedIndex(0);
            filtreKm.setText("0");
            filtreTri.setSelectedIndex(0);
            filtreOrdre.setSelectedIndex(0);
            rafraichirTableVehicules();
        });

        filtrePanel.add(lblEtat);
        filtrePanel.add(filtreEtat);
        filtrePanel.add(lblType);
        filtrePanel.add(filtreType);
        filtrePanel.add(lblKm);
        filtrePanel.add(filtreKm);
        filtrePanel.add(lblTri);
        filtrePanel.add(filtreTri);
        filtrePanel.add(lblOrdre);
        filtrePanel.add(filtreOrdre);
        filtrePanel.add(btnAppliquer);
        filtrePanel.add(btnReinitialiser);

        JPanel centre = new JPanel(new BorderLayout());
        centre.add(filtrePanel, BorderLayout.NORTH);
        centre.add(new JScrollPane(tableVehicules), BorderLayout.CENTER);
        panel.add(centre, BorderLayout.CENTER);

        // Boutons CRUD
        JPanel boutons = new JPanel(new FlowLayout());

        JButton btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> dialogAjouterVehicule());

        JButton btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> dialogModifierVehicule());

        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerVehicule());

        JButton btnAssigner = new JButton("Assigner chauffeur");
        btnAssigner.addActionListener(e -> dialogAssignerChauffeur());

        JButton btnSauvegarder = new JButton("Sauvegarder");
        btnSauvegarder.addActionListener(e -> {
            try {
                gestionnaire.sauvegarderCSV("resources/donnees.csv");
                JOptionPane.showMessageDialog(this, "Sauvegarde reussie !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCharger = new JButton("Charger");
        btnCharger.addActionListener(e -> {
            try {
                gestionnaire.chargerCSV("resources/donnees.csv");
                rafraichirTableVehicules();
                JOptionPane.showMessageDialog(this, "Donnees chargees !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        boutons.add(btnAjouter);
        boutons.add(btnModifier);
        boutons.add(btnSupprimer);
        boutons.add(btnAssigner);
        boutons.add(btnSauvegarder);
        boutons.add(btnCharger);
        panel.add(boutons, BorderLayout.SOUTH);

        return panel;
    }

    private void rafraichirTableVehicules() {
        tableModel.setRowCount(0);
        for (Vehicule v : gestionnaire.getTousVehicules()) {
            tableModel.addRow(new Object[] {
                    v.getImmatriculation(), v.getType(), v.getEtat(), v.getKilometrage() + " km"
            });
        }
        if (labelStats != null) {
            labelStats.setText("  Vehicules disponibles : " + gestionnaire.getNbVehiculesDisponibles());
        }
    }

    private void dialogAjouterVehicule() {
        JTextField immat = new JTextField();
        JTextField km = new JTextField();
        String[] types = { "Leger", "Lourd", "Special" };
        JComboBox<String> type = new JComboBox<>(types);

        Object[] fields = { "Immatriculation :", immat, "Type :", type, "Kilometrage :", km };
        int result = JOptionPane.showConfirmDialog(this, fields, "Ajouter un vehicule", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String i = immat.getText().trim();
                int k = Integer.parseInt(km.getText().trim());
                switch ((String) type.getSelectedItem()) {
                    case "Leger":
                        gestionnaire.ajouterVehicule(new VehiculeLeger(i, k));
                        break;
                    case "Lourd":
                        gestionnaire.ajouterVehicule(new VehiculeLourd(i, k, 10.0));
                        break;
                    case "Special":
                        gestionnaire.ajouterVehicule(new VehiculeSpecial(i, k, "Urgence"));
                        break;
                }
                rafraichirTableVehicules();
                JOptionPane.showMessageDialog(this, "Vehicule ajoute !");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Kilometrage invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogModifierVehicule() {
        int row = tableVehicules.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un vehicule a modifier !");
            return;
        }
        Vehicule v = gestionnaire.getTousVehicules().get(row);

        JTextField km = new JTextField(String.valueOf(v.getKilometrage()));
        String[] etats = { "disponible", "assigne", "maintenance", "incident" };
        JComboBox<String> etat = new JComboBox<>(etats);
        etat.setSelectedItem(v.getEtat());

        Object[] fields = { "Kilometrage :", km, "Etat :", etat };
        int result = JOptionPane.showConfirmDialog(this, fields, "Modifier " + v.getImmatriculation(),
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                v.setKilometrage(Integer.parseInt(km.getText().trim()));
                v.setEtat((String) etat.getSelectedItem());
                rafraichirTableVehicules();
                JOptionPane.showMessageDialog(this, "Vehicule modifie !");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Kilometrage invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogAssignerChauffeur() {
        int row = tableVehicules.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un vehicule !");
            return;
        }
        Vehicule v = gestionnaire.getTousVehicules().get(row);
        List<Chauffeur> dispos = gestionnaire.getChauffeursDispo();

        if (dispos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun chauffeur disponible !", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] noms = dispos.stream()
                .map(c -> c.getNom() + " " + c.getPrenom())
                .toArray(String[]::new);
        JComboBox<String> combo = new JComboBox<>(noms);

        int result = JOptionPane.showConfirmDialog(this, combo, "Choisir un chauffeur", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Chauffeur c = dispos.get(combo.getSelectedIndex());
                gestionnaire.assignerVehicule(v, c);
                rafraichirTableVehicules();
                JOptionPane.showMessageDialog(this, "Chauffeur assigne !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerVehicule() {
        int row = tableVehicules.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un vehicule !");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Supprimer",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            gestionnaire.supprimerVehicule(gestionnaire.getTousVehicules().get(row));
            rafraichirTableVehicules();
        }
    }

    // ===== PANEL CHAUFFEURS =====
    private JPanel creerPanelChauffeurs() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] colonnes = { "Nom", "Prenom", "Permis", "Disponibilite" };
        DefaultTableModel model = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);

        for (Chauffeur c : gestionnaire.getTousChauffeurs()) {
            model.addRow(new Object[] {
                    c.getNom(), c.getPrenom(), c.getNumeroPerm(),
                    c.isDisponible() ? "Disponible" : "Indisponible"
            });
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel boutons = new JPanel(new FlowLayout());

        JButton btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> {
            JTextField nom = new JTextField();
            JTextField prenom = new JTextField();
            JTextField permis = new JTextField();
            Object[] fields = { "Nom :", nom, "Prenom :", prenom, "Permis :", permis };
            int r = JOptionPane.showConfirmDialog(this, fields, "Ajouter un chauffeur", JOptionPane.OK_CANCEL_OPTION);
            if (r == JOptionPane.OK_OPTION) {
                Chauffeur c = new Chauffeur(nom.getText(), prenom.getText(), permis.getText());
                gestionnaire.ajouterChauffeur(c);
                model.addRow(new Object[] { c.getNom(), c.getPrenom(), c.getNumeroPerm(), "Disponible" });
            }
        });

        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Selectionnez un chauffeur !");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Supprimer",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gestionnaire.supprimerChauffeur(gestionnaire.getTousChauffeurs().get(row));
                model.removeRow(row);
            }
        });

        boutons.add(btnAjouter);
        boutons.add(btnSupprimer);
        panel.add(boutons, BorderLayout.SOUTH);

        return panel;
    }

    // ===== PANEL MISSIONS =====
    private JPanel creerPanelMissions() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] colonnes = { "ID", "Type", "Description", "Statut", "Date" };
        DefaultTableModel model = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);

        for (Mission m : gestionnaire.getTousMissions()) {
            model.addRow(new Object[] {
                    m.getId(), m.getType(), m.getDescription(), m.getStatut(), m.getDate()
            });
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel boutons = new JPanel(new FlowLayout());

        JButton btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> {
            JTextField id = new JTextField();
            JTextField desc = new JTextField();
            JTextField date = new JTextField("2024-01-01");
            String[] types = { "Courte", "Longue" };
            JComboBox<String> type = new JComboBox<>(types);

            Object[] fields = { "ID :", id, "Description :", desc, "Date :", date, "Type :", type };
            int r = JOptionPane.showConfirmDialog(this, fields, "Ajouter une mission", JOptionPane.OK_CANCEL_OPTION);
            if (r == JOptionPane.OK_OPTION) {
                Mission m;
                if (type.getSelectedItem().equals("Courte")) {
                    m = new MissionCourte(id.getText(), desc.getText(), date.getText(), 1);
                } else {
                    m = new MissionLongue(id.getText(), desc.getText(), date.getText(), "A definir");
                }
                gestionnaire.ajouterMission(m);
                model.addRow(new Object[] { m.getId(), m.getType(), m.getDescription(), m.getStatut(), m.getDate() });
            }
        });

        JButton btnModifierStatut = new JButton("Changer statut");
        btnModifierStatut.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Selectionnez une mission !");
                return;
            }
            String[] statuts = { "en attente", "en cours", "terminee", "annulee" };
            JComboBox<String> combo = new JComboBox<>(statuts);
            int r = JOptionPane.showConfirmDialog(this, combo, "Changer le statut", JOptionPane.OK_CANCEL_OPTION);
            if (r == JOptionPane.OK_OPTION) {
                Mission m = gestionnaire.getTousMissions().get(row);
                m.setStatut((String) combo.getSelectedItem());
                model.setValueAt(m.getStatut(), row, 3);
            }
        });

        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Selectionnez une mission !");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Supprimer",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gestionnaire.supprimerMission(gestionnaire.getTousMissions().get(row));
                model.removeRow(row);
            }
        });

        boutons.add(btnAjouter);
        boutons.add(btnModifierStatut);
        boutons.add(btnSupprimer);
        panel.add(boutons, BorderLayout.SOUTH);

        return panel;
    }

    // ===== PANEL STATISTIQUES =====
    private JPanel creerPanelStats() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Vehicules disponibles : " + gestionnaire.getNbVehiculesDisponibles(), JLabel.CENTER));
        panel.add(new JLabel("Kilometrage moyen : " + String.format("%.0f km", gestionnaire.getKilometragesMoyen()),
                JLabel.CENTER));
        panel.add(new JLabel("Missions en cours : " + gestionnaire.getNbMissionsEnCours(), JLabel.CENTER));

        StringBuilder sb = new StringBuilder("<html>Vehicules par etat :<br>");
        gestionnaire.getVehiculesParEtat().forEach(
                (etat, nb) -> sb.append("&nbsp;&nbsp;- ").append(etat).append(" : ").append(nb).append("<br>"));
        sb.append("</html>");
        panel.add(new JLabel(sb.toString(), JLabel.CENTER));

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FenetrePrincipale().setVisible(true));
    }
}