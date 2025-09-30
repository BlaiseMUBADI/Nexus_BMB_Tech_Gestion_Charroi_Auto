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
import java.math.BigDecimal;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Interface complète pour la gestion des véhicules - Version mise à jour
 * Adaptée à la nouvelle architecture de base de données
 * 
 * @author BlaiseMUBADI
 */
public class FormGestionVehiculesNew extends JPanel {
    
    private VehiculeDAO vehiculeDAO;
    
    // Components pour l'ajout - Nouveaux champs BDD
    private JTextField txtMatricule;
    private JTextField txtImmatriculation;
    private JTextField txtMarque;
    private JTextField txtModele;
    private JTextField txtType;
    private JComboBox<String> cmbCategorie;
    private JTextField txtAnnee;
    private JTextField txtCouleur;
    private JTextField txtNumeroChasssis;
    private JTextField txtNumeroMoteur;
    private JComboBox<String> cmbCarburant;
    private JTextField txtConsommation;
    private JTextField txtCapaciteReservoir;
    private JTextField txtKilometrageInitial;
    private JTextField txtKilometrageActuel;
    private JComboBox<String> cmbStatut;
    private JComboBox<String> cmbEtat;
    private JSpinner spnDateAcquisition;
    private JTextField txtPrixAcquisition;
    private JSpinner spnDateMiseService;
    private JSpinner spnDateAssurance;
    private JTextField txtCompagnieAssurance;
    private JTextField txtPoliceAssurance;
    private JSpinner spnDateVisiteTechnique;
    private JTextField txtLieuVisiteTechnique;
    private JSpinner spnDateDerniereVidange;
    private JTextField txtKmDerniereVidange;
    private JTextField txtLocalisation;
    private JTextArea txtNotes;
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
    
    // Colonnes du tableau - Nouvelle architecture BDD
    private final String[] COLONNES = {
        "ID", "Matricule", "Immatriculation", "Marque", "Modèle", "Type", 
        "Catégorie", "Année", "Couleur", "Carburant", "Kilométrage", 
        "Statut", "État", "Assurance", "Visite Technique", "Localisation"
    };
    
    public FormGestionVehiculesNew() {
        vehiculeDAO = new VehiculeDAO();
        init();
        chargerVehicules();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Création des onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Onglet ajout
        JPanel panelAjout = creerPanelAjout();
        tabbedPane.addTab("✚ Ajouter Véhicule", panelAjout);
        
        // Onglet liste  
        JPanel panelListe = creerPanelListe();
        tabbedPane.addTab("📋 Liste Véhicules", panelListe);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel creerPanelAjout() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titre
        JLabel titre = new JLabel("🚗 Ajouter un Nouveau Véhicule");
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        titre.setForeground(new Color(52, 73, 94));
        panel.add(titre, BorderLayout.NORTH);
        
        // Formulaire avec onglets pour organiser les champs
        JTabbedPane sousOnglets = new JTabbedPane();
        
        // Onglet Informations de base
        sousOnglets.addTab("📝 Informations de base", creerPanelInfosDeBase());
        
        // Onglet Caractéristiques techniques
        sousOnglets.addTab("⚙️ Caractéristiques", creerPanelCaracteristiques());
        
        // Onglet Documents et dates
        sousOnglets.addTab("📄 Documents", creerPanelDocuments());
        
        // Onglet Localisation et notes
        sousOnglets.addTab("📍 Autres", creerPanelAutres());
        
        panel.add(sousOnglets, BorderLayout.CENTER);
        
        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        btnReinitialiser = new JButton("🔄 Réinitialiser");
        btnSauvegarder = new JButton("💾 Sauvegarder");
        
        btnReinitialiser.addActionListener(e -> reinitialiserFormulaire());
        btnSauvegarder.addActionListener(e -> sauvegarderVehicule());
        
        panelBoutons.add(btnReinitialiser);
        panelBoutons.add(btnSauvegarder);
        panel.add(panelBoutons, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel creerPanelInfosDeBase() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Matricule (obligatoire)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📋 Matricule *:"), gbc);
        gbc.gridx = 1;
        txtMatricule = new JTextField(15);
        panel.add(txtMatricule, gbc);
        
        // Immatriculation
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("🔖 Immatriculation:"), gbc);
        gbc.gridx = 1;
        txtImmatriculation = new JTextField(15);
        panel.add(txtImmatriculation, gbc);
        
        // Marque (obligatoire)
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("🏭 Marque *:"), gbc);
        gbc.gridx = 1;
        txtMarque = new JTextField(15);
        panel.add(txtMarque, gbc);
        
        // Modèle
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("🚗 Modèle:"), gbc);
        gbc.gridx = 1;
        txtModele = new JTextField(15);
        panel.add(txtModele, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("🔧 Type:"), gbc);
        gbc.gridx = 1;
        txtType = new JTextField(15);
        panel.add(txtType, gbc);
        
        // Catégorie
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("📊 Catégorie:"), gbc);
        gbc.gridx = 1;
        cmbCategorie = new JComboBox<>(new String[]{"LEGER", "UTILITAIRE", "POIDS_LOURD", "SPECIAL"});
        panel.add(cmbCategorie, gbc);
        
        // Année
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("📅 Année:"), gbc);
        gbc.gridx = 1;
        txtAnnee = new JTextField(15);
        panel.add(txtAnnee, gbc);
        
        // Couleur
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("🎨 Couleur:"), gbc);
        gbc.gridx = 1;
        txtCouleur = new JTextField(15);
        panel.add(txtCouleur, gbc);
        
        return panel;
    }
    
    private JPanel creerPanelCaracteristiques() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Numéro de châssis
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("🔢 N° Châssis:"), gbc);
        gbc.gridx = 1;
        txtNumeroChasssis = new JTextField(15);
        panel.add(txtNumeroChasssis, gbc);
        
        // Numéro moteur
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("⚙️ N° Moteur:"), gbc);
        gbc.gridx = 1;
        txtNumeroMoteur = new JTextField(15);
        panel.add(txtNumeroMoteur, gbc);
        
        // Carburant
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("⛽ Carburant:"), gbc);
        gbc.gridx = 1;
        cmbCarburant = new JComboBox<>(new String[]{"ESSENCE", "DIESEL", "HYBRIDE", "ELECTRIQUE", "GAZ"});
        panel.add(cmbCarburant, gbc);
        
        // Consommation
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("📊 Consommation (L/100km):"), gbc);
        gbc.gridx = 1;
        txtConsommation = new JTextField(15);
        panel.add(txtConsommation, gbc);
        
        // Capacité réservoir
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("🛢️ Capacité réservoir (L):"), gbc);
        gbc.gridx = 1;
        txtCapaciteReservoir = new JTextField(15);
        panel.add(txtCapaciteReservoir, gbc);
        
        // Kilométrage initial
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("📏 Kilométrage initial:"), gbc);
        gbc.gridx = 1;
        txtKilometrageInitial = new JTextField(15);
        txtKilometrageInitial.setText("0");
        panel.add(txtKilometrageInitial, gbc);
        
        // Kilométrage actuel
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("📍 Kilométrage actuel:"), gbc);
        gbc.gridx = 1;
        txtKilometrageActuel = new JTextField(15);
        txtKilometrageActuel.setText("0");
        panel.add(txtKilometrageActuel, gbc);
        
        // Statut
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("🚦 Statut:"), gbc);
        gbc.gridx = 1;
        cmbStatut = new JComboBox<>(new String[]{"DISPONIBLE", "AFFECTE", "MAINTENANCE", "HORS_SERVICE", "VENDU"});
        panel.add(cmbStatut, gbc);
        
        // État
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("⭐ État:"), gbc);
        gbc.gridx = 1;
        cmbEtat = new JComboBox<>(new String[]{"EXCELLENT", "BON", "MOYEN", "MAUVAIS", "CRITIQUE"});
        cmbEtat.setSelectedItem("BON");
        panel.add(cmbEtat, gbc);
        
        return panel;
    }
    
    private JPanel creerPanelDocuments() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Date d'acquisition
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📅 Date acquisition:"), gbc);
        gbc.gridx = 1;
        spnDateAcquisition = new JSpinner(new SpinnerDateModel());
        spnDateAcquisition.setEditor(new JSpinner.DateEditor(spnDateAcquisition, "dd/MM/yyyy"));
        panel.add(spnDateAcquisition, gbc);
        
        // Prix d'acquisition
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("💰 Prix acquisition:"), gbc);
        gbc.gridx = 1;
        txtPrixAcquisition = new JTextField(15);
        panel.add(txtPrixAcquisition, gbc);
        
        // Date mise en service
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("🚀 Date mise service:"), gbc);
        gbc.gridx = 1;
        spnDateMiseService = new JSpinner(new SpinnerDateModel());
        spnDateMiseService.setEditor(new JSpinner.DateEditor(spnDateMiseService, "dd/MM/yyyy"));
        panel.add(spnDateMiseService, gbc);
        
        // Date assurance
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("🛡️ Date assurance:"), gbc);
        gbc.gridx = 1;
        spnDateAssurance = new JSpinner(new SpinnerDateModel());
        spnDateAssurance.setEditor(new JSpinner.DateEditor(spnDateAssurance, "dd/MM/yyyy"));
        panel.add(spnDateAssurance, gbc);
        
        // Compagnie assurance
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("🏢 Compagnie assurance:"), gbc);
        gbc.gridx = 1;
        txtCompagnieAssurance = new JTextField(15);
        panel.add(txtCompagnieAssurance, gbc);
        
        // Police assurance
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("📋 Police assurance:"), gbc);
        gbc.gridx = 1;
        txtPoliceAssurance = new JTextField(15);
        panel.add(txtPoliceAssurance, gbc);
        
        // Date visite technique
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("🔧 Date visite technique:"), gbc);
        gbc.gridx = 1;
        spnDateVisiteTechnique = new JSpinner(new SpinnerDateModel());
        spnDateVisiteTechnique.setEditor(new JSpinner.DateEditor(spnDateVisiteTechnique, "dd/MM/yyyy"));
        panel.add(spnDateVisiteTechnique, gbc);
        
        // Lieu visite technique
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("📍 Lieu visite technique:"), gbc);
        gbc.gridx = 1;
        txtLieuVisiteTechnique = new JTextField(15);
        panel.add(txtLieuVisiteTechnique, gbc);
        
        // Date dernière vidange
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("🛢️ Date dernière vidange:"), gbc);
        gbc.gridx = 1;
        spnDateDerniereVidange = new JSpinner(new SpinnerDateModel());
        spnDateDerniereVidange.setEditor(new JSpinner.DateEditor(spnDateDerniereVidange, "dd/MM/yyyy"));
        panel.add(spnDateDerniereVidange, gbc);
        
        // Km dernière vidange
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("📏 Km dernière vidange:"), gbc);
        gbc.gridx = 1;
        txtKmDerniereVidange = new JTextField(15);
        panel.add(txtKmDerniereVidange, gbc);
        
        return panel;
    }
    
    private JPanel creerPanelAutres() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Localisation
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📍 Localisation:"), gbc);
        gbc.gridx = 1;
        txtLocalisation = new JTextField(15);
        panel.add(txtLocalisation, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("📝 Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtNotes = new JTextArea(8, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollNotes = new JScrollPane(txtNotes);
        panel.add(scrollNotes, gbc);
        
        return panel;
    }
    
    private JPanel creerPanelListe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Barre de recherche et filtres
        JPanel panelRecherche = creerPanelRecherche();
        panel.add(panelRecherche, BorderLayout.NORTH);
        
        // Tableau
        creerTableau();
        JScrollPane scrollPane = new JScrollPane(tableVehicules);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Véhicules"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel du bas avec boutons et statistiques
        JPanel panelBas = new JPanel(new BorderLayout());
        
        // Boutons d'action
        JPanel panelBoutons = new JPanel(new FlowLayout());
        btnActualiser = new JButton("🔄 Actualiser");
        btnModifier = new JButton("✏️ Modifier");
        btnSupprimer = new JButton("🗑️ Supprimer");
        
        btnActualiser.addActionListener(e -> chargerVehicules());
        btnModifier.addActionListener(e -> modifierVehiculeSelectionne());
        btnSupprimer.addActionListener(e -> supprimerVehiculeSelectionne());
        
        panelBoutons.add(btnActualiser);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        
        panelBas.add(panelBoutons, BorderLayout.EAST);
        
        // Statistiques
        lblTotal = new JLabel("Total: 0 véhicules");
        panelBas.add(lblTotal, BorderLayout.WEST);
        
        panel.add(panelBas, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel creerPanelRecherche() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panel.add(new JLabel("🔍 Rechercher:"));
        txtRecherche = new JTextField(20);
        panel.add(txtRecherche);
        
        panel.add(new JLabel("📊 Filtrer par:"));
        cmbFiltre = new JComboBox<>(new String[]{
            "Tous", "Disponibles", "Affectés", "En maintenance", "Hors service"
        });
        panel.add(cmbFiltre);
        
        // Écouteurs pour la recherche en temps réel
        txtRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
        });
        
        cmbFiltre.addActionListener(e -> appliquerFiltre());
        
        return panel;
    }
    
    private void creerTableau() {
        modelTable = new DefaultTableModel(COLONNES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tableau en lecture seule
            }
        };
        
        tableVehicules = new JTable(modelTable);
        tableVehicules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableVehicules.setRowHeight(25);
        tableVehicules.getTableHeader().setReorderingAllowed(false);
        
        // Configuration des largeurs de colonnes
        TableColumnModel columnModel = tableVehicules.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // ID
        columnModel.getColumn(1).setPreferredWidth(80);   // Matricule
        columnModel.getColumn(2).setPreferredWidth(100);  // Immatriculation
        columnModel.getColumn(3).setPreferredWidth(80);   // Marque
        columnModel.getColumn(4).setPreferredWidth(80);   // Modèle
        columnModel.getColumn(5).setPreferredWidth(80);   // Type
        columnModel.getColumn(6).setPreferredWidth(80);   // Catégorie
        columnModel.getColumn(7).setPreferredWidth(60);   // Année
        columnModel.getColumn(8).setPreferredWidth(60);   // Couleur
        columnModel.getColumn(9).setPreferredWidth(80);   // Carburant
        columnModel.getColumn(10).setPreferredWidth(80);  // Kilométrage
        columnModel.getColumn(11).setPreferredWidth(80);  // Statut
        columnModel.getColumn(12).setPreferredWidth(60);  // État
        columnModel.getColumn(13).setPreferredWidth(100); // Assurance
        columnModel.getColumn(14).setPreferredWidth(100); // Visite Technique
        columnModel.getColumn(15).setPreferredWidth(100); // Localisation
        
        // Double-clic pour modifier
        tableVehicules.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modifierVehiculeSelectionne();
                }
            }
        });
    }
    
    private void chargerVehicules() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
                afficherVehicules(vehicules);
                lblTotal.setText("Total: " + vehicules.size() + " véhicules");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement des véhicules: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void afficherVehicules(List<Vehicule> vehicules) {
        modelTable.setRowCount(0);
        
        for (Vehicule v : vehicules) {
            Object[] row = {
                v.getId(),
                v.getMatricule(),
                v.getImmatriculation() != null ? v.getImmatriculation() : "*",
                v.getMarque(),
                v.getModele() != null ? v.getModele() : "*",
                v.getType() != null ? v.getType() : "*",
                v.getCategorie() != null ? v.getCategorie().toString() : "*",
                v.getAnnee() != null ? v.getAnnee() : "*",
                v.getCouleur() != null ? v.getCouleur() : "*",
                v.getCarburant() != null ? v.getCarburant().toString() : "*",
                v.getKilometrageActuel(),
                v.getStatutVehicule() != null ? v.getStatutVehicule().toString() : "*",
                v.getEtat() != null ? v.getEtat().toString() : "*",
                v.getDateAssurance() != null ? v.getDateAssurance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "*",
                v.getDateVisiteTechnique() != null ? v.getDateVisiteTechnique().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "*",
                v.getLocalisation() != null ? v.getLocalisation() : "*"
            };
            modelTable.addRow(row);
        }
    }
    
    private void appliquerFiltre() {
        // Implémentation du filtrage
        // * pour zones non critiques pour le moment
    }
    
    private void sauvegarderVehicule() {
        try {
            // Validation des champs obligatoires
            if (txtMatricule.getText().trim().isEmpty() || txtMarque.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Les champs Matricule et Marque sont obligatoires!",
                    "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Création du véhicule avec tous les nouveaux champs
            Vehicule vehicule = new Vehicule();
            
            // Champs de base
            vehicule.setMatricule(txtMatricule.getText().trim());
            vehicule.setImmatriculation(txtImmatriculation.getText().trim().isEmpty() ? null : txtImmatriculation.getText().trim());
            vehicule.setMarque(txtMarque.getText().trim());
            vehicule.setModele(txtModele.getText().trim().isEmpty() ? null : txtModele.getText().trim());
            vehicule.setType(txtType.getText().trim().isEmpty() ? null : txtType.getText().trim());
            
            // Énumérations
            if (cmbCategorie.getSelectedItem() != null) {
                vehicule.setCategorie(Vehicule.Categorie.valueOf(cmbCategorie.getSelectedItem().toString()));
            }
            
            if (cmbCarburant.getSelectedItem() != null) {
                vehicule.setCarburant(Vehicule.Carburant.valueOf(cmbCarburant.getSelectedItem().toString()));
            }
            
            if (cmbStatut.getSelectedItem() != null) {
                vehicule.setStatutVehicule(Vehicule.Statut.valueOf(cmbStatut.getSelectedItem().toString()));
            }
            
            if (cmbEtat.getSelectedItem() != null) {
                vehicule.setEtat(Vehicule.Etat.valueOf(cmbEtat.getSelectedItem().toString()));
            }
            
            // Champs numériques avec gestion des erreurs
            if (!txtAnnee.getText().trim().isEmpty()) {
                try {
                    vehicule.setAnnee(Integer.parseInt(txtAnnee.getText().trim()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Année invalide!", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Kilométrage
            if (!txtKilometrageInitial.getText().trim().isEmpty()) {
                vehicule.setKilometrageInitial(Integer.parseInt(txtKilometrageInitial.getText().trim()));
            }
            
            if (!txtKilometrageActuel.getText().trim().isEmpty()) {
                vehicule.setKilometrageActuel(Integer.parseInt(txtKilometrageActuel.getText().trim()));
            }
            
            // Autres champs texte
            vehicule.setCouleur(txtCouleur.getText().trim().isEmpty() ? null : txtCouleur.getText().trim());
            vehicule.setNumeroChasssis(txtNumeroChasssis.getText().trim().isEmpty() ? null : txtNumeroChasssis.getText().trim());
            vehicule.setNumeroMoteur(txtNumeroMoteur.getText().trim().isEmpty() ? null : txtNumeroMoteur.getText().trim());
            vehicule.setCompagnieAssurance(txtCompagnieAssurance.getText().trim().isEmpty() ? null : txtCompagnieAssurance.getText().trim());
            vehicule.setPoliceAssurance(txtPoliceAssurance.getText().trim().isEmpty() ? null : txtPoliceAssurance.getText().trim());
            vehicule.setLieuVisiteTechnique(txtLieuVisiteTechnique.getText().trim().isEmpty() ? null : txtLieuVisiteTechnique.getText().trim());
            vehicule.setLocalisation(txtLocalisation.getText().trim().isEmpty() ? null : txtLocalisation.getText().trim());
            vehicule.setNotes(txtNotes.getText().trim().isEmpty() ? null : txtNotes.getText().trim());
            
            // Dates - conversion des spinners en LocalDate
            vehicule.setDateAcquisition(convertirSpinnerEnLocalDate(spnDateAcquisition));
            vehicule.setDateMiseService(convertirSpinnerEnLocalDate(spnDateMiseService));
            vehicule.setDateAssurance(convertirSpinnerEnLocalDate(spnDateAssurance));
            vehicule.setDateVisiteTechnique(convertirSpinnerEnLocalDate(spnDateVisiteTechnique));
            vehicule.setDateVidange(convertirSpinnerEnLocalDate(spnDateDerniereVidange));
            
            // Champs décimaux
            if (!txtConsommation.getText().trim().isEmpty()) {
                vehicule.setConsommation100km(new BigDecimal(txtConsommation.getText().trim()));
            }
            
            if (!txtCapaciteReservoir.getText().trim().isEmpty()) {
                vehicule.setCapaciteReservoir(new BigDecimal(txtCapaciteReservoir.getText().trim()));
            }
            
            if (!txtPrixAcquisition.getText().trim().isEmpty()) {
                vehicule.setPrixAcquisition(new BigDecimal(txtPrixAcquisition.getText().trim()));
            }
            
            if (!txtKmDerniereVidange.getText().trim().isEmpty()) {
                vehicule.setKmDerniereVidange(Integer.parseInt(txtKmDerniereVidange.getText().trim()));
            }
            
            vehicule.setActif(true);
            
            // Sauvegarde
            if (vehiculeDAO.ajouterVehicule(vehicule)) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Véhicule ajouté avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                reinitialiserFormulaire();
                chargerVehicules();
                tabbedPane.setSelectedIndex(1); // Aller à l'onglet liste
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de l'ajout du véhicule.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private LocalDate convertirSpinnerEnLocalDate(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        if (date == null) return null;
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    private void reinitialiserFormulaire() {
        // Réinitialiser tous les champs
        txtMatricule.setText("");
        txtImmatriculation.setText("");
        txtMarque.setText("");
        txtModele.setText("");
        txtType.setText("");
        cmbCategorie.setSelectedIndex(0);
        txtAnnee.setText("");
        txtCouleur.setText("");
        txtNumeroChasssis.setText("");
        txtNumeroMoteur.setText("");
        cmbCarburant.setSelectedIndex(0);
        txtConsommation.setText("");
        txtCapaciteReservoir.setText("");
        txtKilometrageInitial.setText("0");
        txtKilometrageActuel.setText("0");
        cmbStatut.setSelectedIndex(0);
        cmbEtat.setSelectedItem("BON");
        
        // Réinitialiser les spinners à la date actuelle
        Date today = new Date();
        spnDateAcquisition.setValue(today);
        spnDateMiseService.setValue(today);
        spnDateAssurance.setValue(today);
        spnDateVisiteTechnique.setValue(today);
        spnDateDerniereVidange.setValue(today);
        
        txtPrixAcquisition.setText("");
        txtCompagnieAssurance.setText("");
        txtPoliceAssurance.setText("");
        txtLieuVisiteTechnique.setText("");
        txtKmDerniereVidange.setText("");
        txtLocalisation.setText("");
        txtNotes.setText("");
    }
    
    private void modifierVehiculeSelectionne() {
        // * Implémentation pour modification
        JOptionPane.showMessageDialog(this, 
            "Fonctionnalité de modification en cours de développement",
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void supprimerVehiculeSelectionne() {
        int selectedRow = tableVehicules.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un véhicule à supprimer.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vehiculeId = (Integer) modelTable.getValueAt(selectedRow, 0);
        String matricule = (String) modelTable.getValueAt(selectedRow, 1);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer le véhicule '" + matricule + "' ?",
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                if (vehiculeDAO.supprimerVehicule(vehiculeId)) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Véhicule supprimé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerVehicules();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Impossible de supprimer le véhicule.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}