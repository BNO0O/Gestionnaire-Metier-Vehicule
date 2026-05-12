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
        setTitle("Gestionnaire de Flotte de Véhicules");
        setSize(900, 600);
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

        // ===== ONGLETS =====
        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("🚗 Véhicules", creerPanelVehicules());
        onglets.addTab("👤 Chauffeurs", creerPanelChauffeurs());
        onglets.addTab("📋 Missions", creerPanelMissions());
        onglets.addTab("📊 Statistiques", creerPanelStats());
        add(onglets, BorderLayout.CENTER);

        // ===== BARRE DU BAS =====
        labelStats = new JLabel(" Véhicules disponibles : " + gestionnaire.getNbVehiculesDisponibles());
        labelStats.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(labelStats, BorderLayout.SOUTH);
    }

    // ===== PANEL VEHICULES =====
    private JPanel creerPanelVehicules() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tableau
        String[] colonnes = { "Immatriculation", "Type", "État", "Kilométrage" };
        tableModel = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableVehicules = new JTable(tableModel);
        tableVehicules.setRowHeight(25);
        tableVehicules.getTableHeader().setReorderingAllowed(false);
        rafraichirTableVehicules();

        panel.add(new JScrollPane(tableVehicules), BorderLayout.CENTER);

        // Boutons
        JPanel boutons = new JPanel(new FlowLayout());

        JButton btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.addActionListener(e -> dialogAjouterVehicule());

        JButton btnSupprimer = new JButton("🗑️ Supprimer");
        btnSupprimer.addActionListener(e -> supprimerVehicule());

        JButton btnFiltrer = new JButton("🔍 Filtrer disponibles");
        btnFiltrer.addActionListener(e -> filtrerDisponibles());

        JButton btnTrier = new JButton("⬆️ Trier par km");
        btnTrier.addActionListener(e -> trierParKm());

        boutons.add(btnAjouter);
        boutons.add(btnSupprimer);
        boutons.add(btnFiltrer);
        boutons.add(btnTrier);
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
            labelStats.setText(" Véhicules disponibles : " + gestionnaire.getNbVehiculesDisponibles());
        }
    }

    private void dialogAjouterVehicule() {
        JTextField immat = new JTextField();
        JTextField km = new JTextField();
        String[] types = { "Léger", "Lourd", "Spécial" };
        JComboBox<String> type = new JComboBox<>(types);

        Object[] fields = { "Immatriculation :", immat, "Type :", type, "Kilométrage :", km };
        int result = JOptionPane.showConfirmDialog(this, fields, "Ajouter un véhicule", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String i = immat.getText().trim();
                int k = Integer.parseInt(km.getText().trim());
                switch ((String) type.getSelectedItem()) {
                    case "Léger":
                        gestionnaire.ajouterVehicule(new VehiculeLeger(i, k));
                        break;
                    case "Lourd":
                        gestionnaire.ajouterVehicule(new VehiculeLourd(i, k, 10.0));
                        break;
                    case "Spécial":
                        gestionnaire.ajouterVehicule(new VehiculeSpecial(i, k, "Urgence"));
                        break;
                }
                rafraichirTableVehicules();
                JOptionPane.showMessageDialog(this, "Véhicule ajouté !");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Kilométrage invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerVehicule() {
        int row = tableVehicules.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un véhicule !");
            return;
        }
        List<Vehicule> liste = gestionnaire.getTousVehicules();
        gestionnaire.supprimerVehicule(liste.get(row));
        rafraichirTableVehicules();
    }

    private void filtrerDisponibles() {
        tableModel.setRowCount(0);
        for (Vehicule v : gestionnaire.filtrerVehicules("disponible", "", 0)) {
            tableModel.addRow(new Object[] {
                    v.getImmatriculation(), v.getType(), v.getEtat(), v.getKilometrage() + " km"
            });
        }
    }

    private void trierParKm() {
        tableModel.setRowCount(0);
        for (Vehicule v : gestionnaire.trierVehicules("kilometrage", true)) {
            tableModel.addRow(new Object[] {
                    v.getImmatriculation(), v.getType(), v.getEtat(), v.getKilometrage() + " km"
            });
        }
    }

    // ===== PANEL CHAUFFEURS =====
    private JPanel creerPanelChauffeurs() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] colonnes = { "Nom", "Prénom", "Permis", "Disponibilité" };
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
                    c.isDisponible() ? "✅ Disponible" : "❌ Indisponible"
            });
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel boutons = new JPanel(new FlowLayout());
        JButton btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.addActionListener(e -> {
            JTextField nom = new JTextField();
            JTextField prenom = new JTextField();
            JTextField permis = new JTextField();
            Object[] fields = { "Nom :", nom, "Prénom :", prenom, "Permis :", permis };
            int r = JOptionPane.showConfirmDialog(this, fields, "Ajouter un chauffeur", JOptionPane.OK_CANCEL_OPTION);
            if (r == JOptionPane.OK_OPTION) {
                Chauffeur c = new Chauffeur(nom.getText(), prenom.getText(), permis.getText());
                gestionnaire.ajouterChauffeur(c);
                model.addRow(new Object[] { c.getNom(), c.getPrenom(), c.getNumeroPerm(), "✅ Disponible" });
            }
        });
        boutons.add(btnAjouter);
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
        return panel;
    }

    // ===== PANEL STATISTIQUES =====
    private JPanel creerPanelStats() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("🚗 Véhicules disponibles : " + gestionnaire.getNbVehiculesDisponibles(), JLabel.CENTER));
        panel.add(new JLabel("📏 Kilométrage moyen : " + String.format("%.0f km", gestionnaire.getKilometragesMoyen()),
                JLabel.CENTER));
        panel.add(new JLabel("📋 Missions en cours : " + gestionnaire.getNbMissionsEnCours(), JLabel.CENTER));

        StringBuilder sb = new StringBuilder("<html>📊 Véhicules par état :<br>");
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