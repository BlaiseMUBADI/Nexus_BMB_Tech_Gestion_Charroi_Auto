package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Vehicule;
import nexus_bmb_soft.utils.IconUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Interface unifiée pour la gestion des véhicules (Ajout + Liste)
 * 
 * @author BlaiseMUBADI
 */
public class FormGestionVehicules extends JPanel {
    
    private VehiculeDAO vehiculeDAO;
    
    // Components pour l'ajout
    private JTextField txtMatricule;
    private JTextField txtMarque;
    private JTextField txtType;
    private JTextField txtAnnee;
    private JCheckBox chkDisponible;
    private JSpinner spnDateAssurance;
    private JSpinner spnDateVidange;
    private JSpinner spnDateVisite;
    private JButton btnSauvegarder;
    private JButton btnReinitialiser;
    
    // Components pour la liste
    private JTable tableVehicules;
    private DefaultTableModel modelTable;
    private JTextField txtRecherche;
    private JComboBox<String> cmbFiltre;
    private JButton btnActualiser;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JLabel lblTotal;
    
    // Onglets
    private JTabbedPane tabbedPane;
    
    // Colonnes du tableau
    private final String[] COLONNES = {
        "ID", "Matricule", "Marque", "Type", "Année", 
        "Disponible", "Assurance", "Vidange", "Visite Technique"
    };
    
    public FormGestionVehicules() {
        vehiculeDAO = new VehiculeDAO();
        init();
        chargerVehicules();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        // Panel principal avec titre
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal
        JLabel lblTitrePrincipal = new JLabel("🚗 Gestion des Véhicules", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180));
        lblTitrePrincipal.setForeground(Color.WHITE);
        
        // Créer les onglets
        tabbedPane = new JTabbedPane();
        
        // Onglet 1: Ajouter véhicule
        JPanel panelAjout = createAjoutPanel();
        tabbedPane.addTab(" Ajouter Véhicule", IconUtils.createAddIcon(new Color(52, 152, 219), 16), panelAjout);
        
        // Onglet 2: Liste véhicules
        JPanel panelListe = createListePanel();
        tabbedPane.addTab(" Liste Véhicules", IconUtils.createListIcon(new Color(46, 204, 113), 16), panelListe);
        
        // Changer d'onglet automatiquement après ajout
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                chargerVehicules(); // Recharger la liste quand on passe à l'onglet liste
            }
        });
        
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createAjoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de saisie
        JPanel panelSaisie = new JPanel(new GridBagLayout());
        panelSaisie.setBorder(BorderFactory.createTitledBorder("📝 Informations du véhicule"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Matricule (obligatoire)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblMatricule = new JLabel(" Matricule :", IconUtils.createCarIcon(new Color(52, 73, 94), 16), JLabel.LEFT);
        lblMatricule.setFont(lblMatricule.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblMatricule, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMatricule = new JTextField(20);
        txtMatricule.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtMatricule, gbc);
        row++;
        
        // Marque (obligatoire)
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblMarque = new JLabel("🏭 Marque :");
        lblMarque.setFont(lblMarque.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblMarque, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMarque = new JTextField(20);
        txtMarque.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtMarque, gbc);
        row++;
        
        // Type
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelSaisie.add(new JLabel("🚛 Type :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtType = new JTextField(20);
        txtType.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtType, gbc);
        row++;
        
        // Année
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelSaisie.add(new JLabel("📅 Année :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtAnnee = new JTextField(20);
        txtAnnee.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtAnnee, gbc);
        row++;
        
        // Disponible
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelSaisie.add(new JLabel("✅ Disponible :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        chkDisponible = new JCheckBox("Véhicule disponible pour affectation");
        chkDisponible.setSelected(true);
        panelSaisie.add(chkDisponible, gbc);
        row++;
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelSaisie.add(new JSeparator(), gbc);
        row++;
        
        // Section maintenance
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblMaintenance = new JLabel("🔧 Informations Maintenance");
        lblMaintenance.setFont(lblMaintenance.getFont().deriveFont(Font.BOLD, 14f));
        panelSaisie.add(lblMaintenance, gbc);
        row++;
        
        // Date assurance
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelSaisie.add(new JLabel(" Assurance :", IconUtils.createCalendarIcon(new Color(231, 76, 60), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        // Créer JSpinner avec SpinnerDateModel
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnDateAssurance = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnDateAssurance, "dd/MM/yyyy");
        spnDateAssurance.setEditor(dateEditor);
        spnDateAssurance.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        panelSaisie.add(spnDateAssurance, gbc);
        row++;
        
        // Date vidange
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelSaisie.add(new JLabel(" Vidange :", IconUtils.createCalendarIcon(new Color(142, 68, 173), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        // Créer JSpinner avec SpinnerDateModel pour vidange
        SpinnerDateModel dateModelVidange = new SpinnerDateModel();
        spnDateVidange = new JSpinner(dateModelVidange);
        JSpinner.DateEditor dateEditorVidange = new JSpinner.DateEditor(spnDateVidange, "dd/MM/yyyy");
        spnDateVidange.setEditor(dateEditorVidange);
        spnDateVidange.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        panelSaisie.add(spnDateVidange, gbc);
        row++;
        
        // Date visite technique
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelSaisie.add(new JLabel(" Visite Technique :", IconUtils.createCalendarIcon(new Color(52, 152, 219), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        // Créer JSpinner avec SpinnerDateModel pour visite technique
        SpinnerDateModel dateModelVisite = new SpinnerDateModel();
        spnDateVisite = new JSpinner(dateModelVisite);
        JSpinner.DateEditor dateEditorVisite = new JSpinner.DateEditor(spnDateVisite, "dd/MM/yyyy");
        spnDateVisite.setEditor(dateEditorVisite);
        spnDateVisite.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        panelSaisie.add(spnDateVisite, gbc);
        row++;
        
        // Panel boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBoutons.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnReinitialiser = new JButton(" Réinitialiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnReinitialiser.setPreferredSize(new Dimension(150, 35));
        btnReinitialiser.setBackground(new Color(255, 165, 0));
        btnReinitialiser.setForeground(Color.WHITE);
        btnReinitialiser.setFont(btnReinitialiser.getFont().deriveFont(Font.BOLD));
        btnReinitialiser.addActionListener(e -> reinitialiserFormulaire());
        
        btnSauvegarder = new JButton(" Sauvegarder", IconUtils.createSaveIcon(Color.WHITE, 16));
        btnSauvegarder.setPreferredSize(new Dimension(150, 35));
        btnSauvegarder.setBackground(new Color(34, 139, 34));
        btnSauvegarder.setForeground(Color.WHITE);
        btnSauvegarder.setFont(btnSauvegarder.getFont().deriveFont(Font.BOLD));
        btnSauvegarder.addActionListener(e -> sauvegarderVehicule());
        
        panelBoutons.add(btnReinitialiser);
        panelBoutons.add(btnSauvegarder);
        
        panel.add(panelSaisie, BorderLayout.CENTER);
        panel.add(panelBoutons, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createListePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de recherche
        JPanel panelRecherche = createRecherchePanel();
        
        // Panel tableau
        JPanel panelTableau = createTableauPanel();
        
        // Panel actions
        JPanel panelActions = createActionsPanel();
        
        panel.add(panelRecherche, BorderLayout.NORTH);
        panel.add(panelTableau, BorderLayout.CENTER);
        panel.add(panelActions, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRecherchePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Recherche et Filtres"));
        
        // Filtre par statut
        panel.add(new JLabel("Filtre :"));
        cmbFiltre = new JComboBox<>(new String[]{
            "Tous", "Disponibles", "Non Disponibles", 
            "Assurance Expirée", "Maintenance Due"
        });
        cmbFiltre.addActionListener(e -> appliquerFiltre());
        panel.add(cmbFiltre);
        
        // Recherche
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("🔍 Recherche :"));
        txtRecherche = new JTextField(20);
        txtRecherche.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        txtRecherche.addActionListener(e -> appliquerFiltre());
        
        // Recherche en temps réel
        txtRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
        });
        panel.add(txtRecherche);
        
        return panel;
    }
    
    private JPanel createTableauPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📋 Liste des Véhicules"));
        
        // Modèle de table
        modelTable = new DefaultTableModel(COLONNES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) return String.class; // Colonne Disponible (emoji)
                return String.class;
            }
        };
        
        // Table
        tableVehicules = new JTable(modelTable);
        tableVehicules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableVehicules.setRowHeight(30);
        tableVehicules.setFont(tableVehicules.getFont().deriveFont(12f));
        
        // Configuration des colonnes
        configureColumns();
        
        // Double-clic pour modifier
        tableVehicules.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modifierVehiculeSelectionne();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableVehicules);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Statistiques (gauche)
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotal = new JLabel("📊 Total : 0 véhicule(s)");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        panelStats.add(lblTotal);
        
        // Boutons (droite)
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        btnActualiser = new JButton("🔄 Actualiser");
        btnActualiser.setPreferredSize(new Dimension(120, 30));
        btnActualiser.setBackground(new Color(70, 130, 180));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.addActionListener(e -> chargerVehicules());
        
        btnModifier = new JButton("✏️ Modifier");
        btnModifier.setPreferredSize(new Dimension(120, 30));
        btnModifier.setBackground(new Color(255, 165, 0));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.addActionListener(e -> modifierVehiculeSelectionne());
        btnModifier.setEnabled(false);
        
        btnSupprimer = new JButton("🗑️ Supprimer");
        btnSupprimer.setPreferredSize(new Dimension(120, 30));
        btnSupprimer.setBackground(new Color(220, 20, 60));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.addActionListener(e -> supprimerVehiculeSelectionne());
        btnSupprimer.setEnabled(false);
        
        // Activer/désactiver les boutons selon la sélection
        tableVehicules.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = tableVehicules.getSelectedRow() != -1;
            btnModifier.setEnabled(hasSelection);
            btnSupprimer.setEnabled(hasSelection);
        });
        
        panelBoutons.add(btnActualiser);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        
        panel.add(panelStats, BorderLayout.WEST);
        panel.add(panelBoutons, BorderLayout.EAST);
        
        return panel;
    }
    
    private void configureColumns() {
        TableColumnModel columnModel = tableVehicules.getColumnModel();
        
        // ID (caché)
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setWidth(0);
        
        // Autres colonnes avec largeurs optimisées
        columnModel.getColumn(1).setPreferredWidth(100); // Matricule
        columnModel.getColumn(2).setPreferredWidth(120); // Marque
        columnModel.getColumn(3).setPreferredWidth(100); // Type
        columnModel.getColumn(4).setPreferredWidth(80);  // Année
        columnModel.getColumn(5).setPreferredWidth(80);  // Disponible
        columnModel.getColumn(6).setPreferredWidth(100); // Assurance
        columnModel.getColumn(7).setPreferredWidth(100); // Vidange
        columnModel.getColumn(8).setPreferredWidth(120); // Visite
    }
    
    private void chargerVehicules() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
                afficherVehicules(vehicules);
                mettreAJourStatistiques(vehicules.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors du chargement : " + e.getMessage(),
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void afficherVehicules(List<Vehicule> vehicules) {
        modelTable.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Vehicule v : vehicules) {
            Object[] row = {
                v.getId(),
                v.getMatricule(),
                v.getMarque(),
                v.getType() != null ? v.getType() : "",
                v.getAnnee() != null ? v.getAnnee().toString() : "",
                v.isDisponible() ? "✅ Oui" : "❌ Non",
                v.getDateAssurance() != null ? v.getDateAssurance().format(dateFormatter) : "",
                v.getDateVidange() != null ? v.getDateVidange().format(dateFormatter) : "",
                v.getDateVisiteTechnique() != null ? v.getDateVisiteTechnique().format(dateFormatter) : ""
            };
            modelTable.addRow(row);
        }
    }
    
    private void mettreAJourStatistiques(int total) {
        lblTotal.setText("📊 Total : " + total + " véhicule(s)");
    }
    
    private void appliquerFiltre() {
        String recherche = txtRecherche.getText().toLowerCase().trim();
        String filtre = (String) cmbFiltre.getSelectedItem();
        
        try {
            List<Vehicule> tousVehicules = vehiculeDAO.obtenirTousVehicules();
            List<Vehicule> vehiculesFiltres = tousVehicules.stream()
                .filter(v -> {
                    // Filtre par recherche textuelle
                    if (!recherche.isEmpty()) {
                        return v.getMatricule().toLowerCase().contains(recherche) ||
                               v.getMarque().toLowerCase().contains(recherche) ||
                               (v.getType() != null && v.getType().toLowerCase().contains(recherche));
                    }
                    return true;
                })
                .filter(v -> {
                    // Filtre par statut
                    switch (filtre) {
                        case "Disponibles":
                            return v.isDisponible();
                        case "Non Disponibles":
                            return !v.isDisponible();
                        case "Assurance Expirée":
                            return v.getDateAssurance() != null && 
                                   v.getDateAssurance().isBefore(LocalDate.now());
                        case "Maintenance Due":
                            return v.getDateVidange() != null && 
                                   v.getDateVidange().isBefore(LocalDate.now());
                        default:
                            return true; // "Tous"
                    }
                })
                .collect(java.util.stream.Collectors.toList());
                
            afficherVehicules(vehiculesFiltres);
            mettreAJourStatistiques(vehiculesFiltres.size());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Erreur lors du filtrage : " + e.getMessage(),
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sauvegarderVehicule() {
        if (!validerSaisie()) {
            return;
        }
        
        try {
            // Créer l'objet véhicule
            Vehicule vehicule = new Vehicule(
                0, // ID sera généré automatiquement
                txtMatricule.getText().trim().toUpperCase(),
                txtMarque.getText().trim(),
                txtType.getText().trim().isEmpty() ? null : txtType.getText().trim(),
                parseAnnee(),
                chkDisponible.isSelected(),
                convertSpinnerToLocalDate(spnDateAssurance),
                convertSpinnerToLocalDate(spnDateVidange),
                convertSpinnerToLocalDate(spnDateVisite)
            );
            
            // Sauvegarder en base
            if (vehiculeDAO.ajouterVehicule(vehicule)) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Véhicule '" + vehicule.getMatricule() + "' ajouté avec succès !",
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                reinitialiserFormulaire();
                chargerVehicules(); // Recharger la liste
                
                // Passer à l'onglet liste pour voir le résultat
                tabbedPane.setSelectedIndex(1);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de l'ajout du véhicule.\nVérifiez que le matricule n'existe pas déjà.",
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Erreur inattendue : " + e.getMessage(),
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validerSaisie() {
        // Vérifier matricule
        if (txtMatricule.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Le matricule est obligatoire !",
                "Validation", 
                JOptionPane.WARNING_MESSAGE);
            txtMatricule.requestFocus();
            return false;
        }
        
        // Vérifier marque
        if (txtMarque.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ La marque est obligatoire !",
                "Validation", 
                JOptionPane.WARNING_MESSAGE);
            txtMarque.requestFocus();
            return false;
        }
        
        // Vérifier l'année si saisie
        if (!txtAnnee.getText().trim().isEmpty()) {
            try {
                int annee = Integer.parseInt(txtAnnee.getText().trim());
                int anneeActuelle = LocalDate.now().getYear();
                if (annee < 1900 || annee > anneeActuelle + 1) {
                    JOptionPane.showMessageDialog(this, 
                        "⚠️ Année invalide ! (entre 1900 et " + (anneeActuelle + 1) + ")",
                        "Validation", 
                        JOptionPane.WARNING_MESSAGE);
                    txtAnnee.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ L'année doit être un nombre !",
                    "Validation", 
                    JOptionPane.WARNING_MESSAGE);
                txtAnnee.requestFocus();
                return false;
            }
        }
        
        // Note: JSpinner garantit des dates valides, pas besoin de validation spécifique
        
        return true;
    }
    
    private Integer parseAnnee() {
        if (txtAnnee.getText().trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(txtAnnee.getText().trim());
    }
    
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equals("jj/mm/aaaa")) {
            return null;
        }
        
        // Format attendu: jj/mm/aaaa
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateStr.trim(), formatter);
    }
    
    // Méthode pour convertir la valeur du JSpinner en LocalDate
    private LocalDate convertSpinnerToLocalDate(JSpinner spinner) {
        Date spinnerDate = (Date) spinner.getValue();
        if (spinnerDate == null) {
            return null;
        }
        // Convertir java.util.Date vers LocalDate
        Calendar cal = Calendar.getInstance();
        cal.setTime(spinnerDate);
        return LocalDate.of(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1, // Calendar.MONTH commence à 0
            cal.get(Calendar.DAY_OF_MONTH)
        );
    }
    
    private void reinitialiserFormulaire() {
        txtMatricule.setText("");
        txtMarque.setText("");
        txtType.setText("");
        txtAnnee.setText("");
        chkDisponible.setSelected(true);
        
        // Réinitialiser les spinners avec la date actuelle
        spnDateAssurance.setValue(new Date());
        spnDateVidange.setValue(new Date());
        spnDateVisite.setValue(new Date());
        
        txtMatricule.requestFocus();
    }
    
    private void modifierVehiculeSelectionne() {
        int selectedRow = tableVehicules.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Veuillez sélectionner un véhicule à modifier.",
                "Sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vehiculeId = (Integer) modelTable.getValueAt(selectedRow, 0);
        String matricule = (String) modelTable.getValueAt(selectedRow, 1);
        
        // Pour l'instant, afficher un message
        JOptionPane.showMessageDialog(this, 
            "✏️ Modification du véhicule '" + matricule + "' (ID " + vehiculeId + ")\n(Fonctionnalité à implémenter)",
            "Modification", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void supprimerVehiculeSelectionne() {
        int selectedRow = tableVehicules.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Veuillez sélectionner un véhicule à supprimer.",
                "Sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vehiculeId = (Integer) modelTable.getValueAt(selectedRow, 0);
        String matricule = (String) modelTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "❓ Êtes-vous sûr de vouloir supprimer le véhicule '" + matricule + "' ?\n" +
            "Cette action est irréversible !",
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (vehiculeDAO.supprimerVehicule(vehiculeId)) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Véhicule '" + matricule + "' supprimé avec succès !",
                        "Suppression réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerVehicules(); // Recharger la liste
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Impossible de supprimer le véhicule.\n" +
                        "Il pourrait être utilisé dans des affectations.",
                        "Erreur de suppression", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de la suppression : " + e.getMessage(),
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}