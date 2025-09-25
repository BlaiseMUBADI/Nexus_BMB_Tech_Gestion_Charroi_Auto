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
import javax.swing.table.DefaultTableModel;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.stream.Collectors;

/**
 * Interface harmonisée pour créer de nouvelles affectations véhicule-conducteur
 * Style harmonisé avec FormGestionVehicules (template design)
 * 
 * @author BlaiseMUBADI
 */
public class FormNouvelleAffectation extends JPanel {
    
    private AffectationDAO affectationDAO;
    private VehiculeDAO vehiculeDAO;
    private UtilisateurDAO utilisateurDAO;
    
    // Onglets
    private JTabbedPane tabbedPane;
    
    // === ONGLET SÉLECTION ===
    private JComboBox<VehiculeItem> cmbVehicule;
    private JComboBox<ConducteurItem> cmbConducteur;
    private JLabel lblVehiculeStatut;
    private JLabel lblConducteurStatut;
    private JButton btnVerifierDisponibilite;
    
    // === ONGLET PLANNING ===
    private JSpinner spnDateDebut;
    private JSpinner spnDateFin;
    private JCheckBox chkAffectationOuverte;
    private JTextArea txtMotif;
    private JLabel lblDureeCalculee;
    private JLabel lblConflitsDetectes;
    
    // === ONGLET VALIDATION ===
    private JTable tableRecapitulatif;
    private DefaultTableModel modelRecap;
    private JTextArea txtNotesFinales;
    private JButton btnSauvegarder;
    private JButton btnReinitialiser;
    private JLabel lblStatutValidation;
    
    // Classes helper pour les ComboBox
    private static class VehiculeItem {
        private final Vehicule vehicule;
        
        public VehiculeItem(Vehicule vehicule) {
            this.vehicule = vehicule;
        }
        
        public Vehicule getVehicule() { return vehicule; }
        
        @Override
        public String toString() {
            return String.format("%s - %s %s (%s)", 
                vehicule.getMatricule(),
                vehicule.getMarque(),
                vehicule.getType(),
                vehicule.isDisponible() ? "Disponible" : "Affecté");
        }
    }
    
    private static class ConducteurItem {
        private final Utilisateur conducteur;
        
        public ConducteurItem(Utilisateur conducteur) {
            this.conducteur = conducteur;
        }
        
        public Utilisateur getConducteur() { return conducteur; }
        
        @Override
        public String toString() {
            return String.format("%s %s (%s)", 
                conducteur.getNom(),
                conducteur.getPrenom() != null ? conducteur.getPrenom() : "",
                conducteur.getRole().toString());
        }
    }
    
    public FormNouvelleAffectation() {
        affectationDAO = new AffectationDAO();
        vehiculeDAO = new VehiculeDAO();
        utilisateurDAO = new UtilisateurDAO();
        init();
        chargerDonnees();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        // Panel principal avec titre
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal harmonisé
        JLabel lblTitrePrincipal = new JLabel("🚗➤👤 Nouvelle Affectation Véhicule-Conducteur", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180)); // Style harmonisé
        lblTitrePrincipal.setForeground(Color.WHITE);
        
        // Créer les onglets avec le design harmonisé
        tabbedPane = new JTabbedPane();
        
        // Onglet 1: Sélection (Véhicule + Conducteur)
        JPanel panelSelection = createSelectionPanel();
        tabbedPane.addTab(" Sélection", IconUtils.createCarIcon(new Color(52, 152, 219), 16), panelSelection);
        
        // Onglet 2: Planning (Dates + Motif)
        JPanel panelPlanning = createPlanningPanel();
        tabbedPane.addTab(" Planning", IconUtils.createCalendarIcon(new Color(46, 204, 113), 16), panelPlanning);
        
        // Onglet 3: Validation (Récapitulatif + Sauvegarde)
        JPanel panelValidation = createValidationPanel();
        tabbedPane.addTab(" Validation", IconUtils.createSaveIcon(new Color(155, 89, 182), 16), panelValidation);
        
        // Listener pour valider automatiquement lors du changement d'onglet
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 2) { // Onglet Validation
                mettreAJourRecapitulatif();
            } else if (selectedIndex == 1) { // Onglet Planning
                verifierDisponibilite();
            }
        });
        
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("🎯 Sélection Véhicule et Conducteur"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Sélection véhicule
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblVehicule = new JLabel("Véhicule:");
        lblVehicule.setFont(lblVehicule.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblVehicule, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbVehicule = new JComboBox<>();
        cmbVehicule.setPreferredSize(new Dimension(400, 30));
        cmbVehicule.addActionListener(e -> verifierStatutVehicule());
        formPanel.add(cmbVehicule, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        lblVehiculeStatut = new JLabel();
        formPanel.add(lblVehiculeStatut, gbc);
        
        // Sélection conducteur
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel lblConducteur = new JLabel("Conducteur:");
        lblConducteur.setFont(lblConducteur.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblConducteur, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbConducteur = new JComboBox<>();
        cmbConducteur.setPreferredSize(new Dimension(400, 30));
        cmbConducteur.addActionListener(e -> verifierStatutConducteur());
        formPanel.add(cmbConducteur, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        lblConducteurStatut = new JLabel();
        formPanel.add(lblConducteurStatut, gbc);
        
        // Bouton de vérification
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnVerifierDisponibilite = new JButton("🔍 Vérifier Disponibilité");
        btnVerifierDisponibilite.setBackground(new Color(52, 152, 219));
        btnVerifierDisponibilite.setForeground(Color.WHITE);
        btnVerifierDisponibilite.addActionListener(e -> verifierDisponibilite());
        formPanel.add(btnVerifierDisponibilite, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Panel d'informations
        JPanel infoPanel = createInfoSelectionPanel();
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("ℹ️ Informations"));
        
        // Info véhicules
        JPanel vehiculesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vehiculesPanel.add(new JLabel("Véhicules disponibles:"));
        JLabel lblVehiculesDispos = new JLabel("0");
        lblVehiculesDispos.setForeground(new Color(46, 204, 113));
        lblVehiculesDispos.setFont(lblVehiculesDispos.getFont().deriveFont(Font.BOLD));
        vehiculesPanel.add(lblVehiculesDispos);
        
        // Info conducteurs  
        JPanel conducteursPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conducteursPanel.add(new JLabel("Conducteurs actifs:"));
        JLabel lblConducteursActifs = new JLabel("0");
        lblConducteursActifs.setForeground(new Color(52, 152, 219));
        lblConducteursActifs.setFont(lblConducteursActifs.getFont().deriveFont(Font.BOLD));
        conducteursPanel.add(lblConducteursActifs);
        
        panel.add(vehiculesPanel);
        panel.add(conducteursPanel);
        
        return panel;
    }
    
    private JPanel createPlanningPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("📅 Planning de l'Affectation"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Date de début
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblDateDebut = new JLabel("Date de début:");
        lblDateDebut.setFont(lblDateDebut.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblDateDebut, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        SpinnerDateModel modelDebut = new SpinnerDateModel();
        spnDateDebut = new JSpinner(modelDebut);
        JSpinner.DateEditor editorDebut = new JSpinner.DateEditor(spnDateDebut, "dd/MM/yyyy");
        spnDateDebut.setEditor(editorDebut);
        spnDateDebut.setValue(new Date()); // Date actuelle par défaut
        spnDateDebut.addChangeListener(e -> calculerDuree());
        formPanel.add(spnDateDebut, gbc);
        
        // Date de fin
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblDateFin = new JLabel("Date de fin:");
        lblDateFin.setFont(lblDateFin.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblDateFin, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        SpinnerDateModel modelFin = new SpinnerDateModel();
        spnDateFin = new JSpinner(modelFin);
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spnDateFin, "dd/MM/yyyy");
        spnDateFin.setEditor(editorFin);
        
        // Date de fin par défaut = début + 1 jour
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        spnDateFin.setValue(cal.getTime());
        spnDateFin.addChangeListener(e -> calculerDuree());
        formPanel.add(spnDateFin, gbc);
        
        // Checkbox affectation ouverte
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        chkAffectationOuverte = new JCheckBox("Affectation ouverte (sans date de fin)");
        chkAffectationOuverte.addActionListener(e -> {
            spnDateFin.setEnabled(!chkAffectationOuverte.isSelected());
            calculerDuree();
        });
        formPanel.add(chkAffectationOuverte, gbc);
        
        // Motif
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblMotif = new JLabel("Motif:");
        lblMotif.setFont(lblMotif.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblMotif, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtMotif = new JTextArea(4, 30);
        txtMotif.setLineWrap(true);
        txtMotif.setWrapStyleWord(true);
        txtMotif.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollMotif = new JScrollPane(txtMotif);
        formPanel.add(scrollMotif, gbc);
        
        // Labels d'information
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0; gbc.anchor = GridBagConstraints.WEST;
        lblDureeCalculee = new JLabel("Durée: Non calculée");
        lblDureeCalculee.setForeground(new Color(52, 152, 219));
        formPanel.add(lblDureeCalculee, gbc);
        
        gbc.gridy = 5;
        lblConflitsDetectes = new JLabel("Conflits: Vérification en cours...");
        lblConflitsDetectes.setForeground(new Color(230, 126, 34));
        formPanel.add(lblConflitsDetectes, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createValidationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("✅ Validation et Sauvegarde"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Tableau récapitulatif
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("📋 Récapitulatif"));
        
        String[] colonnes = {"Élément", "Valeur", "Statut"};
        modelRecap = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableRecapitulatif = new JTable(modelRecap);
        tableRecapitulatif.setRowHeight(25);
        JScrollPane scrollRecap = new JScrollPane(tableRecapitulatif);
        scrollRecap.setPreferredSize(new Dimension(600, 200));
        tablePanel.add(scrollRecap, BorderLayout.CENTER);
        
        // Notes finales
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("📝 Notes finales"));
        txtNotesFinales = new JTextArea(3, 30);
        txtNotesFinales.setLineWrap(true);
        txtNotesFinales.setWrapStyleWord(true);
        txtNotesFinales.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollNotes = new JScrollPane(txtNotesFinales);
        notesPanel.add(scrollNotes, BorderLayout.CENTER);
        
        // Boutons d'action harmonisés
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnReinitialiser = new JButton("🔄 Réinitialiser");
        btnReinitialiser.setBackground(new Color(255, 165, 0)); // Orange harmonisé
        btnReinitialiser.setForeground(Color.WHITE);
        btnReinitialiser.setPreferredSize(new Dimension(150, 35));
        btnReinitialiser.addActionListener(e -> reinitialiserFormulaire());
        
        btnSauvegarder = new JButton("💾 Créer Affectation");
        btnSauvegarder.setBackground(new Color(34, 139, 34)); // Vert harmonisé
        btnSauvegarder.setForeground(Color.WHITE);
        btnSauvegarder.setPreferredSize(new Dimension(170, 35));
        btnSauvegarder.addActionListener(e -> sauvegarderAffectation());
        
        buttonPanel.add(btnReinitialiser);
        buttonPanel.add(btnSauvegarder);
        
        // Statut validation
        lblStatutValidation = new JLabel("⏳ En attente de validation...");
        lblStatutValidation.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatutValidation.setFont(lblStatutValidation.getFont().deriveFont(Font.BOLD));
        
        JPanel mainValidationPanel = new JPanel(new BorderLayout(10, 10));
        mainValidationPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(notesPanel, BorderLayout.CENTER);
        bottomPanel.add(lblStatutValidation, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainValidationPanel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(mainValidationPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void chargerDonnees() {
        // Charger les données dans un seul thread pour éviter les problèmes de connexion DB
        try {
            // Charger les véhicules
            List<Vehicule> vehicules = vehiculeDAO.getTousVehicules();
            
            // Charger les utilisateurs une seule fois
            List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
            List<Utilisateur> conducteurs = utilisateurs.stream()
                .filter(u -> u.getRole() == RoleUtilisateur.CONDUCTEUR || 
                            u.getRole() == RoleUtilisateur.CONDUCTEUR_SENIOR)
                .filter(u -> "ACTIF".equals(u.getStatut()))
                .collect(Collectors.toList());
            
            // Mettre à jour l'interface dans l'EDT
            SwingUtilities.invokeLater(() -> {
                // Charger les véhicules
                cmbVehicule.removeAllItems();
                cmbVehicule.addItem(null); // Option vide
                
                for (Vehicule vehicule : vehicules) {
                    cmbVehicule.addItem(new VehiculeItem(vehicule));
                }
                
                // Charger les conducteurs
                cmbConducteur.removeAllItems();
                cmbConducteur.addItem(null); // Option vide
                
                for (Utilisateur conducteur : conducteurs) {
                    cmbConducteur.addItem(new ConducteurItem(conducteur));
                }
                
                // Force un refresh des ComboBox
                cmbVehicule.revalidate();
                cmbConducteur.revalidate();
                
                // Mettre à jour les compteurs
                mettreAJourCompteurs(vehicules, conducteurs);
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement des données: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            });
        }
    }
    
    private void chargerConducteurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
            List<Utilisateur> conducteurs = utilisateurs.stream()
                .filter(u -> u.getRole() == RoleUtilisateur.CONDUCTEUR || 
                            u.getRole() == RoleUtilisateur.CONDUCTEUR_SENIOR)
                .filter(u -> "ACTIF".equals(u.getStatut()))
                .collect(Collectors.toList());
            
            cmbConducteur.removeAllItems();
            cmbConducteur.addItem(null); // Option vide
            
            for (Utilisateur conducteur : conducteurs) {
                cmbConducteur.addItem(new ConducteurItem(conducteur));
            }
            
            // Force un refresh du ComboBox
            cmbConducteur.revalidate();
            cmbConducteur.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des conducteurs: " + e.getMessage(), 
                "Erreur de chargement", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mettreAJourCompteurs(List<Vehicule> vehicules, List<Utilisateur> conducteurs) {
        long vehiculesDisponibles = vehicules.stream().filter(Vehicule::isDisponible).count();
        long conducteursActifs = conducteurs.size();
        
        Component[] components = ((JPanel)tabbedPane.getComponentAt(0)).getComponents();
        // Logique pour mettre à jour les labels de compteur (code simplifié)
    }
    
    private void verifierStatutVehicule() {
        VehiculeItem selected = (VehiculeItem) cmbVehicule.getSelectedItem();
        if (selected != null) {
            Vehicule vehicule = selected.getVehicule();
            if (vehicule.isDisponible()) {
                lblVehiculeStatut.setText("✅ Disponible");
                lblVehiculeStatut.setForeground(new Color(34, 139, 34));
            } else {
                lblVehiculeStatut.setText("❌ Affecté");
                lblVehiculeStatut.setForeground(Color.RED);
            }
        } else {
            lblVehiculeStatut.setText("");
        }
    }
    
    private void verifierStatutConducteur() {
        ConducteurItem selected = (ConducteurItem) cmbConducteur.getSelectedItem();
        if (selected != null) {
            Utilisateur conducteur = selected.getConducteur();
            if ("ACTIF".equals(conducteur.getStatut())) {
                lblConducteurStatut.setText("✅ Actif");
                lblConducteurStatut.setForeground(new Color(34, 139, 34));
            } else {
                lblConducteurStatut.setText("❌ " + conducteur.getStatut());
                lblConducteurStatut.setForeground(Color.RED);
            }
        } else {
            lblConducteurStatut.setText("");
        }
    }
    
    private void verifierDisponibilite() {
        VehiculeItem vehiculeItem = (VehiculeItem) cmbVehicule.getSelectedItem();
        if (vehiculeItem == null) {
            lblConflitsDetectes.setText("⚠️ Sélectionnez un véhicule");
            lblConflitsDetectes.setForeground(Color.RED);
            return;
        }
        
        LocalDate dateDebut = convertToLocalDate((Date) spnDateDebut.getValue());
        LocalDate dateFin = chkAffectationOuverte.isSelected() ? 
            null : convertToLocalDate((Date) spnDateFin.getValue());
        
        boolean disponible = affectationDAO.verifierDisponibiliteVehicule(
            vehiculeItem.getVehicule().getId(), dateDebut, dateFin);
        
        if (disponible) {
            lblConflitsDetectes.setText("✅ Véhicule disponible pour cette période");
            lblConflitsDetectes.setForeground(new Color(34, 139, 34));
        } else {
            lblConflitsDetectes.setText("❌ Conflit détecté - Véhicule déjà affecté");
            lblConflitsDetectes.setForeground(Color.RED);
        }
    }
    
    private void calculerDuree() {
        if (chkAffectationOuverte.isSelected()) {
            lblDureeCalculee.setText("Durée: Affectation ouverte");
            lblDureeCalculee.setForeground(new Color(52, 152, 219));
            return;
        }
        
        LocalDate dateDebut = convertToLocalDate((Date) spnDateDebut.getValue());
        LocalDate dateFin = convertToLocalDate((Date) spnDateFin.getValue());
        
        if (dateFin.isBefore(dateDebut)) {
            lblDureeCalculee.setText("⚠️ Date de fin antérieure au début");
            lblDureeCalculee.setForeground(Color.RED);
            return;
        }
        
        long jours = dateDebut.until(dateFin).getDays() + 1;
        lblDureeCalculee.setText(String.format("Durée: %d jour(s)", jours));
        lblDureeCalculee.setForeground(new Color(52, 152, 219));
    }
    
    private void mettreAJourRecapitulatif() {
        modelRecap.setRowCount(0);
        
        // Véhicule sélectionné
        VehiculeItem vehiculeItem = (VehiculeItem) cmbVehicule.getSelectedItem();
        String vehiculeText = vehiculeItem != null ? vehiculeItem.toString() : "Non sélectionné";
        String vehiculeStatut = vehiculeItem != null && vehiculeItem.getVehicule().isDisponible() ? "✅ OK" : "❌ Problème";
        modelRecap.addRow(new Object[]{"Véhicule", vehiculeText, vehiculeStatut});
        
        // Conducteur sélectionné
        ConducteurItem conducteurItem = (ConducteurItem) cmbConducteur.getSelectedItem();
        String conducteurText = conducteurItem != null ? conducteurItem.toString() : "Non sélectionné";
        String conducteurStatut = conducteurItem != null ? "✅ OK" : "❌ Problème";
        modelRecap.addRow(new Object[]{"Conducteur", conducteurText, conducteurStatut});
        
        // Période
        String periodeText;
        if (chkAffectationOuverte.isSelected()) {
            periodeText = String.format("Du %s (ouverte)", 
                formatDate(convertToLocalDate((Date) spnDateDebut.getValue())));
        } else {
            periodeText = String.format("Du %s au %s", 
                formatDate(convertToLocalDate((Date) spnDateDebut.getValue())),
                formatDate(convertToLocalDate((Date) spnDateFin.getValue())));
        }
        modelRecap.addRow(new Object[]{"Période", periodeText, "ℹ️ Info"});
        
        // Motif
        String motif = txtMotif.getText().trim();
        String motifStatut = motif.isEmpty() ? "⚠️ Vide" : "✅ OK";
        modelRecap.addRow(new Object[]{"Motif", motif.isEmpty() ? "(Aucun motif spécifié)" : motif, motifStatut});
        
        // Validation globale
        boolean peutSauvegarder = vehiculeItem != null && conducteurItem != null && !motif.isEmpty();
        if (peutSauvegarder) {
            lblStatutValidation.setText("✅ Prêt pour sauvegarde");
            lblStatutValidation.setForeground(new Color(34, 139, 34));
            btnSauvegarder.setEnabled(true);
        } else {
            lblStatutValidation.setText("❌ Informations manquantes");
            lblStatutValidation.setForeground(Color.RED);
            btnSauvegarder.setEnabled(false);
        }
    }
    
    private void sauvegarderAffectation() {
        try {
            VehiculeItem vehiculeItem = (VehiculeItem) cmbVehicule.getSelectedItem();
            ConducteurItem conducteurItem = (ConducteurItem) cmbConducteur.getSelectedItem();
            
            if (vehiculeItem == null || conducteurItem == null) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner un véhicule et un conducteur", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate dateDebut = convertToLocalDate((Date) spnDateDebut.getValue());
            LocalDate dateFin = chkAffectationOuverte.isSelected() ? 
                null : convertToLocalDate((Date) spnDateFin.getValue());
            String motif = txtMotif.getText().trim();
            String notes = txtNotesFinales.getText().trim();
            
            // Créer l'affectation
            Affectation affectation = new Affectation();
            affectation.setVehiculeId(vehiculeItem.getVehicule().getId());
            affectation.setConducteurId(conducteurItem.getConducteur().getId());
            affectation.setDateDebut(dateDebut);
            affectation.setDateFin(dateFin);
            affectation.setMotif(motif + (notes.isEmpty() ? "" : "\n\nNotes: " + notes));
            
            boolean success = affectationDAO.creer(affectation);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Affectation créée avec succès!\n\nID: " + affectation.getId(), 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                reinitialiserFormulaire();
                tabbedPane.setSelectedIndex(0); // Retour au premier onglet
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de la création de l'affectation", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reinitialiserFormulaire() {
        cmbVehicule.setSelectedIndex(0);
        cmbConducteur.setSelectedIndex(0);
        spnDateDebut.setValue(new Date());
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        spnDateFin.setValue(cal.getTime());
        
        chkAffectationOuverte.setSelected(false);
        spnDateFin.setEnabled(true);
        txtMotif.setText("");
        txtNotesFinales.setText("");
        
        lblVehiculeStatut.setText("");
        lblConducteurStatut.setText("");
        lblDureeCalculee.setText("Durée: Non calculée");
        lblConflitsDetectes.setText("Conflits: Vérification en cours...");
        lblStatutValidation.setText("⏳ En attente de validation...");
        
        modelRecap.setRowCount(0);
    }
    
    // Utilitaires
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}