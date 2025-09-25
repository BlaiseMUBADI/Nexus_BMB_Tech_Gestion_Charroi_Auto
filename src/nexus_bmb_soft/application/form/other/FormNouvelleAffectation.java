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
import javax.swing.border.TitledBorder;
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
    
    // === COMPOSANTS DE L'INTERFACE ===
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
        
        // Initialiser le récapitulatif après chargement
        SwingUtilities.invokeLater(() -> {
            mettreAJourRecapitulatif();
        });
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
        
        // Panel de contenu unique sans onglets
        JPanel contentPanel = createUnifiedContentPanel();
        
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Crée le panel de contenu unifié sans onglets
     */
    private JPanel createUnifiedContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel central avec toutes les sections
        JPanel centralPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Section 1: Sélection Véhicule et Conducteur
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        gbc.insets = new Insets(0, 0, 15, 0);
        centralPanel.add(createSelectionSection(), gbc);
        
        // Section 2: Planning et Dates
        gbc.gridy = 1;
        gbc.weighty = 0.4;
        centralPanel.add(createPlanningSection(), gbc);
        
        // Section 3: Validation et Actions
        gbc.gridy = 2;
        gbc.weighty = 0.3;
        gbc.insets = new Insets(15, 0, 0, 0);
        centralPanel.add(createValidationSection(), gbc);
        
        panel.add(centralPanel, BorderLayout.CENTER);
        
        return panel;
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
        // Les compteurs sont maintenant intégrés directement dans l'interface
        // Cette méthode peut être supprimée
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
    
    /**
     * Crée la section de sélection véhicule et conducteur
     */
    private JPanel createSelectionSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "🚗👤 Sélection Véhicule et Conducteur",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        // Panel du formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Ligne véhicule
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
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
        
        // Ligne conducteur
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
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
        
        // Panel d'informations à droite
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.3;
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("ℹ️ Informations"));
        
        JLabel lblVehiculesDispos = new JLabel("Véhicules disponibles: 0");
        lblVehiculesDispos.setForeground(new Color(46, 204, 113));
        infoPanel.add(lblVehiculesDispos);
        
        JLabel lblConducteursActifs = new JLabel("Conducteurs actifs: 0");
        lblConducteursActifs.setForeground(new Color(52, 152, 219));
        infoPanel.add(lblConducteursActifs);
        
        formPanel.add(infoPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Crée la section de planning des dates
     */
    private JPanel createPlanningSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "📅 Planning et Période d'Affectation",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Date de début
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel lblDateDebut = new JLabel("Date de début:");
        lblDateDebut.setFont(lblDateDebut.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblDateDebut, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        spnDateDebut = new JSpinner(new SpinnerDateModel());
        spnDateDebut.setEditor(new JSpinner.DateEditor(spnDateDebut, "dd/MM/yyyy"));
        spnDateDebut.addChangeListener(e -> calculerDuree());
        formPanel.add(spnDateDebut, gbc);
        
        // Date de fin
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblDateFin = new JLabel("Date de fin:");
        lblDateFin.setFont(lblDateFin.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblDateFin, gbc);
        
        gbc.gridx = 1;
        spnDateFin = new JSpinner(new SpinnerDateModel());
        spnDateFin.setEditor(new JSpinner.DateEditor(spnDateFin, "dd/MM/yyyy"));
        spnDateFin.addChangeListener(e -> calculerDuree());
        formPanel.add(spnDateFin, gbc);
        
        // Checkbox affectation ouverte
        gbc.gridx = 2; gbc.gridy = 1;
        chkAffectationOuverte = new JCheckBox("Affectation ouverte");
        chkAffectationOuverte.addActionListener(e -> {
            spnDateFin.setEnabled(!chkAffectationOuverte.isSelected());
            calculerDuree();
        });
        formPanel.add(chkAffectationOuverte, gbc);
        
        // Motif
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblMotif = new JLabel("Motif:");
        lblMotif.setFont(lblMotif.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblMotif, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtMotif = new JTextArea(2, 30);
        txtMotif.setLineWrap(true);
        txtMotif.setWrapStyleWord(true);
        JScrollPane scrollMotif = new JScrollPane(txtMotif);
        formPanel.add(scrollMotif, gbc);
        
        // Panel de validation à droite
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 3; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.3;
        JPanel validationPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        validationPanel.setBorder(BorderFactory.createTitledBorder("✅ Vérifications"));
        
        btnVerifierDisponibilite = new JButton("🔍 Vérifier Disponibilité");
        btnVerifierDisponibilite.setBackground(new Color(52, 152, 219));
        btnVerifierDisponibilite.setForeground(Color.WHITE);
        btnVerifierDisponibilite.addActionListener(e -> verifierDisponibilite());
        validationPanel.add(btnVerifierDisponibilite);
        
        lblDureeCalculee = new JLabel("Durée: Non calculée");
        lblDureeCalculee.setForeground(new Color(155, 89, 182));
        validationPanel.add(lblDureeCalculee);
        
        lblConflitsDetectes = new JLabel("Conflits: Non vérifiés");
        lblConflitsDetectes.setForeground(Color.GRAY);
        validationPanel.add(lblConflitsDetectes);
        
        formPanel.add(validationPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Crée la section de validation et sauvegarde
     */
    private JPanel createValidationSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
            "✅ Validation et Sauvegarde",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        // Panel principal avec layout en colonnes
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Panel gauche: Récapitulatif
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.5; gbc.weighty = 1.0;
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("📋 Récapitulatif"));
        
        // Table récapitulative
        String[] columns = {"Élément", "Valeur"};
        modelRecap = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tableRecap = new JTable(modelRecap);
        tableRecap.setRowHeight(25);
        JScrollPane scrollRecap = new JScrollPane(tableRecap);
        scrollRecap.setPreferredSize(new Dimension(400, 150));
        leftPanel.add(scrollRecap, BorderLayout.CENTER);
        mainPanel.add(leftPanel, gbc);
        
        // Panel milieu: Notes finales
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.3; gbc.weighty = 0.5;
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("📝 Notes finales"));
        
        txtNotesFinales = new JTextArea(4, 20);
        txtNotesFinales.setLineWrap(true);
        txtNotesFinales.setWrapStyleWord(true);
        txtNotesFinales.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollNotes = new JScrollPane(txtNotesFinales);
        notesPanel.add(scrollNotes, BorderLayout.CENTER);
        mainPanel.add(notesPanel, gbc);
        
        // Panel droit: Actions
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.3; gbc.weighty = 0.5;
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(5, 5, 5, 5);
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        
        // Statut validation
        gbcRight.gridx = 0; gbcRight.gridy = 0;
        lblStatutValidation = new JLabel("⏳ En attente...", JLabel.CENTER);
        lblStatutValidation.setFont(lblStatutValidation.getFont().deriveFont(Font.BOLD, 14f));
        rightPanel.add(lblStatutValidation, gbcRight);
        
        // Boutons d'action
        gbcRight.gridy = 1;
        btnSauvegarder = new JButton("💾 Créer l'Affectation");
        btnSauvegarder.setBackground(new Color(46, 204, 113));
        btnSauvegarder.setForeground(Color.WHITE);
        btnSauvegarder.setFont(btnSauvegarder.getFont().deriveFont(Font.BOLD));
        btnSauvegarder.addActionListener(e -> sauvegarderAffectation());
        rightPanel.add(btnSauvegarder, gbcRight);
        
        gbcRight.gridy = 2;
        JButton btnReset = new JButton("🔄 Réinitialiser");
        btnReset.setBackground(new Color(231, 76, 60));
        btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> reinitialiserFormulaire());
        rightPanel.add(btnReset, gbcRight);
        
        mainPanel.add(rightPanel, gbc);
        
        panel.add(mainPanel, BorderLayout.CENTER);
        return panel;
    }

    // Utilitaires
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}