package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.AffectationDAO;
import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.database.dao.UtilisateurDAO;
import nexus_bmb_soft.models.Affectation;
import nexus_bmb_soft.models.Vehicule;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.models.RoleUtilisateur;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Interface de recherche avancée des affectations
 * Design harmonisé avec FormGestionVehicules
 * 
 * @author BlaiseMUBADI
 */
public class FormRechercheAffectations extends JPanel {
    
    private AffectationDAO affectationDAO;
    private VehiculeDAO vehiculeDAO;
    private UtilisateurDAO utilisateurDAO;
    
    private JTabbedPane tabbedPane;
    
    // Onglet Critères
    private JComboBox<String> cmbVehicule;
    private JComboBox<String> cmbConducteur;
    private JTextField txtDateDebut;
    private JTextField txtDateFin;
    private JComboBox<String> cmbStatut;
    private JTextField txtMotifContient;
    private JButton btnRechercher;
    private JButton btnReinitialiser;
    
    // Onglet Résultats
    private JTable tableResultats;
    private DefaultTableModel modelResultats;
    private JLabel lblNombreResultats;
    private JButton btnExporterCSV;
    private JButton btnVoirDetails;
    
    // Onglet Analyse
    private JLabel lblTotalAffectations;
    private JLabel lblDureeMoyenne;
    private JLabel lblVehiculePlusUtilise;
    private JLabel lblConducteurPlusActif;
    private JTextArea txtAnalyseDetaillee;
    
    public FormRechercheAffectations() {
        initDAO();
        initComponents();
        chargerDonneesInitiales();
    }
    
    private void initDAO() {
        this.affectationDAO = new AffectationDAO();
        this.vehiculeDAO = new VehiculeDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec bordure
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal avec style harmonisé
        JLabel lblTitrePrincipal = new JLabel("🔍 Recherche Avancée des Affectations", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180));
        lblTitrePrincipal.setForeground(Color.WHITE);
        
        // JTabbedPane avec style harmonisé
        tabbedPane = new JTabbedPane();
        
        // Onglet 1: Critères de recherche
        JPanel panelCriteres = creerOngletCriteres();
        tabbedPane.addTab(" Critères", IconUtils.createSearchIcon(new Color(52, 152, 219), 16), panelCriteres);
        
        // Onglet 2: Résultats
        JPanel panelResultats = creerOngletResultats();
        tabbedPane.addTab(" Résultats", IconUtils.createListIcon(new Color(46, 204, 113), 16), panelResultats);
        
        // Onglet 3: Analyse
        JPanel panelAnalyse = creerOngletAnalyse();
        tabbedPane.addTab(" Analyse", IconUtils.createHistoryIcon(new Color(155, 89, 182), 16), panelAnalyse);
        
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel creerOngletCriteres() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("🔎 Critères de Recherche"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Ligne 1: Véhicule et Conducteur
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblVehicule = new JLabel("Véhicule:");
        lblVehicule.setFont(lblVehicule.getFont().deriveFont(Font.BOLD));
        panel.add(lblVehicule, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbVehicule = new JComboBox<>();
        cmbVehicule.setPreferredSize(new Dimension(200, 30));
        panel.add(cmbVehicule, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblConducteur = new JLabel("Conducteur:");
        lblConducteur.setFont(lblConducteur.getFont().deriveFont(Font.BOLD));
        panel.add(lblConducteur, gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbConducteur = new JComboBox<>();
        cmbConducteur.setPreferredSize(new Dimension(200, 30));
        panel.add(cmbConducteur, gbc);
        
        // Ligne 2: Dates
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblDateDebut = new JLabel("Date début (AAAA-MM-JJ):");
        lblDateDebut.setFont(lblDateDebut.getFont().deriveFont(Font.BOLD));
        panel.add(lblDateDebut, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDateDebut = new JTextField();
        txtDateDebut.setPreferredSize(new Dimension(200, 30));
        txtDateDebut.setToolTipText("Format: AAAA-MM-JJ (ex: 2025-01-01)");
        panel.add(txtDateDebut, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblDateFin = new JLabel("Date fin (AAAA-MM-JJ):");
        lblDateFin.setFont(lblDateFin.getFont().deriveFont(Font.BOLD));
        panel.add(lblDateFin, gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDateFin = new JTextField();
        txtDateFin.setPreferredSize(new Dimension(200, 30));
        txtDateFin.setToolTipText("Format: AAAA-MM-JJ (ex: 2025-12-31)");
        panel.add(txtDateFin, gbc);
        
        // Ligne 3: Statut et Motif
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblStatut = new JLabel("Statut:");
        lblStatut.setFont(lblStatut.getFont().deriveFont(Font.BOLD));
        panel.add(lblStatut, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbStatut = new JComboBox<>(new String[]{"Tous", "Programmée", "En cours", "Terminée"});
        cmbStatut.setPreferredSize(new Dimension(200, 30));
        panel.add(cmbStatut, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblMotif = new JLabel("Motif contient:");
        lblMotif.setFont(lblMotif.getFont().deriveFont(Font.BOLD));
        panel.add(lblMotif, gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMotifContient = new JTextField();
        txtMotifContient.setPreferredSize(new Dimension(200, 30));
        txtMotifContient.setToolTipText("Recherche dans le motif de l'affectation");
        panel.add(txtMotifContient, gbc);
        
        // Ligne 4: Boutons d'action avec style harmonisé
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelBoutons = new JPanel(new FlowLayout());
        
        btnRechercher = new JButton("🔍 Rechercher");
        btnRechercher.setBackground(new Color(34, 139, 34)); // Vert harmonisé
        btnRechercher.setForeground(Color.WHITE);
        btnRechercher.setPreferredSize(new Dimension(150, 35));
        btnRechercher.setBorder(new CompoundBorder(
            new LineBorder(new Color(27, 111, 27), 1),
            new EmptyBorder(5, 15, 5, 15)
        ));
        
        btnReinitialiser = new JButton("🔄 Réinitialiser");
        btnReinitialiser.setBackground(new Color(255, 165, 0)); // Orange harmonisé
        btnReinitialiser.setForeground(Color.WHITE);
        btnReinitialiser.setPreferredSize(new Dimension(150, 35));
        btnReinitialiser.setBorder(new CompoundBorder(
            new LineBorder(new Color(255, 140, 0), 1),
            new EmptyBorder(5, 15, 5, 15)
        ));
        
        panelBoutons.add(btnRechercher);
        panelBoutons.add(btnReinitialiser);
        panel.add(panelBoutons, gbc);
        
        // Événements
        btnRechercher.addActionListener(e -> effectuerRecherche());
        btnReinitialiser.addActionListener(e -> reinitialiserCriteres());
        
        return panel;
    }
    
    private JPanel creerOngletResultats() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📊 Résultats de Recherche"));
        
        // Panneau supérieur avec informations et actions
        JPanel panelTop = new JPanel(new BorderLayout());
        
        lblNombreResultats = new JLabel("Aucune recherche effectuée");
        lblNombreResultats.setFont(lblNombreResultats.getFont().deriveFont(Font.BOLD));
        lblNombreResultats.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnExporterCSV = new JButton("📄 Exporter CSV");
        btnExporterCSV.setBackground(new Color(52, 152, 219));
        btnExporterCSV.setForeground(Color.WHITE);
        btnExporterCSV.setEnabled(false);
        
        btnVoirDetails = new JButton("👁 Voir Détails");
        btnVoirDetails.setBackground(new Color(142, 68, 173));
        btnVoirDetails.setForeground(Color.WHITE);
        btnVoirDetails.setEnabled(false);
        
        panelActions.add(btnExporterCSV);
        panelActions.add(btnVoirDetails);
        
        panelTop.add(lblNombreResultats, BorderLayout.WEST);
        panelTop.add(panelActions, BorderLayout.EAST);
        
        // Table des résultats
        String[] colonnes = {"ID", "Véhicule", "Conducteur", "Date Début", "Date Fin", "Durée (j)", "Motif", "Statut"};
        modelResultats = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableResultats = new JTable(modelResultats);
        tableResultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableResultats.setRowHeight(25);
        tableResultats.getTableHeader().setReorderingAllowed(false);
        
        // Largeurs des colonnes
        tableResultats.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableResultats.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableResultats.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableResultats.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableResultats.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableResultats.getColumnModel().getColumn(5).setPreferredWidth(80);
        tableResultats.getColumnModel().getColumn(6).setPreferredWidth(200);
        tableResultats.getColumnModel().getColumn(7).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tableResultats);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Événements
        btnExporterCSV.addActionListener(e -> exporterResultatsCSV());
        btnVoirDetails.addActionListener(e -> voirDetailsAffectation());
        
        tableResultats.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnVoirDetails.setEnabled(tableResultats.getSelectedRow() != -1);
            }
        });
        
        return panel;
    }
    
    private JPanel creerOngletAnalyse() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📈 Analyse des Résultats"));
        
        // Panneau des statistiques rapides
        JPanel panelStats = new JPanel(new GridLayout(2, 2, 10, 10));
        panelStats.setBorder(BorderFactory.createTitledBorder("Statistiques Rapides"));
        
        lblTotalAffectations = new JLabel("Total affectations: 0");
        lblTotalAffectations.setFont(lblTotalAffectations.getFont().deriveFont(Font.BOLD));
        
        lblDureeMoyenne = new JLabel("Durée moyenne: 0 jours");
        lblDureeMoyenne.setFont(lblDureeMoyenne.getFont().deriveFont(Font.BOLD));
        
        lblVehiculePlusUtilise = new JLabel("Véhicule le plus utilisé: Aucun");
        lblVehiculePlusUtilise.setFont(lblVehiculePlusUtilise.getFont().deriveFont(Font.BOLD));
        
        lblConducteurPlusActif = new JLabel("Conducteur le plus actif: Aucun");
        lblConducteurPlusActif.setFont(lblConducteurPlusActif.getFont().deriveFont(Font.BOLD));
        
        panelStats.add(lblTotalAffectations);
        panelStats.add(lblDureeMoyenne);
        panelStats.add(lblVehiculePlusUtilise);
        panelStats.add(lblConducteurPlusActif);
        
        // Zone d'analyse détaillée
        JPanel panelDetails = new JPanel(new BorderLayout());
        panelDetails.setBorder(BorderFactory.createTitledBorder("Analyse Détaillée"));
        
        txtAnalyseDetaillee = new JTextArea();
        txtAnalyseDetaillee.setEditable(false);
        txtAnalyseDetaillee.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtAnalyseDetaillee.setText("Effectuez une recherche pour voir l'analyse détaillée...");
        
        JScrollPane scrollAnalyse = new JScrollPane(txtAnalyseDetaillee);
        scrollAnalyse.setPreferredSize(new Dimension(600, 300));
        panelDetails.add(scrollAnalyse, BorderLayout.CENTER);
        
        panel.add(panelStats, BorderLayout.NORTH);
        panel.add(panelDetails, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void chargerDonneesInitiales() {
        // Charger les véhicules
        cmbVehicule.addItem("Tous les véhicules");
        List<Vehicule> vehicules = vehiculeDAO.getTousVehicules();
        for (Vehicule vehicule : vehicules) {
            cmbVehicule.addItem(vehicule.getMatricule() + " - " + vehicule.getMarque());
        }
        
        // Charger UNIQUEMENT les conducteurs (optimisé)
        cmbConducteur.addItem("Tous les conducteurs");
        List<Utilisateur> conducteurs = utilisateurDAO.getTousConducteurs();
        for (Utilisateur conducteur : conducteurs) {
            cmbConducteur.addItem(conducteur.getNom() + " " + (conducteur.getPrenom() != null ? conducteur.getPrenom() : ""));
        }
    }
    
    private void effectuerRecherche() {
        try {
            // Collecte des critères de recherche
            String vehiculeSelectionne = (String) cmbVehicule.getSelectedItem();
            String conducteurSelectionne = (String) cmbConducteur.getSelectedItem();
            String dateDebutStr = txtDateDebut.getText().trim();
            String dateFinStr = txtDateFin.getText().trim();
            String statutSelectionne = (String) cmbStatut.getSelectedItem();
            String motifRecherche = txtMotifContient.getText().trim();
            
            // Conversion des dates
            LocalDate dateDebut = null;
            LocalDate dateFin = null;
            
            if (!dateDebutStr.isEmpty()) {
                dateDebut = LocalDate.parse(dateDebutStr);
            }
            if (!dateFinStr.isEmpty()) {
                dateFin = LocalDate.parse(dateFinStr);
            }
            
            // Effectuer la recherche
            List<Affectation> resultats = effectuerRechercheAvancee(
                vehiculeSelectionne, conducteurSelectionne, dateDebut, dateFin, 
                statutSelectionne, motifRecherche
            );
            
            // Afficher les résultats
            afficherResultats(resultats);
            
            // Passer à l'onglet Résultats
            tabbedPane.setSelectedIndex(1);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la recherche: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<Affectation> effectuerRechercheAvancee(String vehicule, String conducteur, 
            LocalDate dateDebut, LocalDate dateFin, String statut, String motif) {
        
        // Pour une recherche simple, on commence par lister toutes les affectations
        List<Affectation> toutesAffectations = affectationDAO.listerHistorique(1000); // Limite élevée
        List<Affectation> resultatsFilters = new ArrayList<>();
        
        for (Affectation affectation : toutesAffectations) {
            boolean correspond = true;
            
            // Filtre véhicule
            if (vehicule != null && !vehicule.equals("Tous les véhicules")) {
                if (affectation.getVehicule() == null || 
                    !vehicule.contains(affectation.getVehicule().getMatricule())) {
                    correspond = false;
                }
            }
            
            // Filtre conducteur
            if (conducteur != null && !conducteur.equals("Tous les conducteurs")) {
                if (affectation.getConducteur() == null || 
                    !conducteur.contains(affectation.getConducteur().getNom())) {
                    correspond = false;
                }
            }
            
            // Filtre date début
            if (dateDebut != null && affectation.getDateDebut() != null) {
                if (affectation.getDateDebut().isBefore(dateDebut)) {
                    correspond = false;
                }
            }
            
            // Filtre date fin
            if (dateFin != null && affectation.getDateDebut() != null) {
                if (affectation.getDateDebut().isAfter(dateFin)) {
                    correspond = false;
                }
            }
            
            // Filtre statut
            if (statut != null && !statut.equals("Tous")) {
                String statutAffectation = affectation.getStatut();
                if (!statut.equals(statutAffectation)) {
                    correspond = false;
                }
            }
            
            // Filtre motif
            if (motif != null && !motif.isEmpty()) {
                if (affectation.getMotif() == null || 
                    !affectation.getMotif().toLowerCase().contains(motif.toLowerCase())) {
                    correspond = false;
                }
            }
            
            if (correspond) {
                resultatsFilters.add(affectation);
            }
        }
        
        return resultatsFilters;
    }
    
    private void afficherResultats(List<Affectation> resultats) {
        // Vider le modèle
        modelResultats.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Ajouter les résultats
        for (Affectation affectation : resultats) {
            Object[] row = new Object[8];
            row[0] = affectation.getId();
            row[1] = affectation.getVehicule() != null ? affectation.getVehicule().getMatricule() : "N/A";
            row[2] = affectation.getConducteur() != null ? affectation.getConducteur().getNom() : "N/A";
            row[3] = affectation.getDateDebut() != null ? affectation.getDateDebut().format(formatter) : "N/A";
            row[4] = affectation.getDateFin() != null ? affectation.getDateFin().format(formatter) : "En cours";
            row[5] = affectation.getDureeDansJours();
            row[6] = affectation.getMotif() != null ? affectation.getMotif() : "N/A";
            row[7] = affectation.getStatut();
            
            modelResultats.addRow(row);
        }
        
        // Mettre à jour le label de nombre de résultats
        lblNombreResultats.setText("Nombre de résultats: " + resultats.size());
        
        // Activer le bouton d'export si des résultats
        btnExporterCSV.setEnabled(resultats.size() > 0);
        
        // Calculer et afficher les analyses
        calculerAnalyse(resultats);
    }
    
    private void calculerAnalyse(List<Affectation> resultats) {
        if (resultats.isEmpty()) {
            lblTotalAffectations.setText("Total affectations: 0");
            lblDureeMoyenne.setText("Durée moyenne: 0 jours");
            lblVehiculePlusUtilise.setText("Véhicule le plus utilisé: Aucun");
            lblConducteurPlusActif.setText("Conducteur le plus actif: Aucun");
            txtAnalyseDetaillee.setText("Aucun résultat à analyser.");
            return;
        }
        
        // Statistiques de base
        int totalAffectations = resultats.size();
        long dureeTotale = resultats.stream().mapToLong(Affectation::getDureeDansJours).sum();
        double dureeMoyenne = (double) dureeTotale / totalAffectations;
        
        lblTotalAffectations.setText("Total affectations: " + totalAffectations);
        lblDureeMoyenne.setText("Durée moyenne: " + String.format("%.1f", dureeMoyenne) + " jours");
        
        // Analyse détaillée
        StringBuilder analyse = new StringBuilder();
        analyse.append("=== ANALYSE DÉTAILLÉE DES RÉSULTATS ===\n\n");
        analyse.append("Nombre total d'affectations: ").append(totalAffectations).append("\n");
        analyse.append("Durée totale: ").append(dureeTotale).append(" jours\n");
        analyse.append("Durée moyenne: ").append(String.format("%.2f", dureeMoyenne)).append(" jours\n\n");
        
        // Répartition par statut
        long programmees = resultats.stream().filter(a -> a.getStatut().equals("Programmée")).count();
        long enCours = resultats.stream().filter(a -> a.getStatut().equals("En cours")).count();
        long terminees = resultats.stream().filter(a -> a.getStatut().equals("Terminée")).count();
        
        analyse.append("=== RÉPARTITION PAR STATUT ===\n");
        analyse.append("Programmées: ").append(programmees).append(" (").append(String.format("%.1f", programmees * 100.0 / totalAffectations)).append("%)\n");
        analyse.append("En cours: ").append(enCours).append(" (").append(String.format("%.1f", enCours * 100.0 / totalAffectations)).append("%)\n");
        analyse.append("Terminées: ").append(terminees).append(" (").append(String.format("%.1f", terminees * 100.0 / totalAffectations)).append("%)\n\n");
        
        txtAnalyseDetaillee.setText(analyse.toString());
        
        // Passer à l'onglet Analyse
        tabbedPane.setSelectedIndex(2);
    }
    
    private void reinitialiserCriteres() {
        cmbVehicule.setSelectedIndex(0);
        cmbConducteur.setSelectedIndex(0);
        txtDateDebut.setText("");
        txtDateFin.setText("");
        cmbStatut.setSelectedIndex(0);
        txtMotifContient.setText("");
        
        // Vider les résultats
        modelResultats.setRowCount(0);
        lblNombreResultats.setText("Critères réinitialisés");
        btnExporterCSV.setEnabled(false);
        btnVoirDetails.setEnabled(false);
        
        // Réinitialiser l'analyse
        lblTotalAffectations.setText("Total affectations: 0");
        lblDureeMoyenne.setText("Durée moyenne: 0 jours");
        lblVehiculePlusUtilise.setText("Véhicule le plus utilisé: Aucun");
        lblConducteurPlusActif.setText("Conducteur le plus actif: Aucun");
        txtAnalyseDetaillee.setText("Effectuez une recherche pour voir l'analyse détaillée...");
        
        // Retourner à l'onglet Critères
        tabbedPane.setSelectedIndex(0);
    }
    
    private void exporterResultatsCSV() {
        JOptionPane.showMessageDialog(this, 
            "Fonctionnalité d'export CSV à implémenter\n" +
            "Les résultats seront exportés au format CSV.", 
            "Export CSV", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void voirDetailsAffectation() {
        int selectedRow = tableResultats.getSelectedRow();
        if (selectedRow == -1) return;
        
        int affectationId = (Integer) modelResultats.getValueAt(selectedRow, 0);
        
        JOptionPane.showMessageDialog(this, 
            "Fonctionnalité de visualisation détaillée à implémenter\n" +
            "Affichage des détails de l'affectation ID: " + affectationId, 
            "Détails Affectation", JOptionPane.INFORMATION_MESSAGE);
    }
}