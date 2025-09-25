package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.UtilisateurDAO;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Interface unifiée pour la gestion des utilisateurs (Conducteurs + Gestionnaires + Rôles)
 * 
 * @author BlaiseMUBADI
 */
public class FormGestionUtilisateurs extends JPanel {
    
    private UtilisateurDAO utilisateurDAO;
    
    // Components pour l'ajout
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtMatricule;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    private JPasswordField txtMotDePasse;
    private JPasswordField txtConfirmation;
    private JComboBox<String> cmbStatut;
    private JButton btnSauvegarder;
    private JButton btnReinitialiser;
    
    // Components pour la liste
    private JTable tableUtilisateurs;
    private DefaultTableModel modelTable;
    private JTextField txtRechercheNom;
    private JComboBox<String> cmbFiltreRole;
    private JComboBox<String> cmbFiltreStatut;
    private JButton btnActualiser;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JLabel lblTotal;
    
    // Components pour les rôles
    private JTable tableRoles;
    private DefaultTableModel modelRoles;
    private JList<String> listPermissions;
    private DefaultListModel<String> modelPermissions;
    private JButton btnAjouterRole;
    private JButton btnSupprimerRole;
    
    // Onglets
    private JTabbedPane tabbedPane;
    
    // Colonnes du tableau utilisateurs
    private final String[] COLONNES_USERS = {
        "ID", "Nom", "Prénom", "Matricule", "Email", "Rôle", "Statut", "Date Création"
    };
    
    // Colonnes du tableau rôles
    private final String[] COLONNES_ROLES = {
        "ID", "Nom Rôle", "Description", "Permissions", "Utilisateurs"
    };
    
    public FormGestionUtilisateurs() {
        utilisateurDAO = new UtilisateurDAO();
        init();
        chargerUtilisateurs();
        chargerRoles();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        // Panel principal avec titre
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal
        JLabel lblTitrePrincipal = new JLabel("👥 Gestion des Utilisateurs", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(155, 89, 182));
        lblTitrePrincipal.setForeground(Color.WHITE);
        
        // Créer les onglets
        tabbedPane = new JTabbedPane();
        
        // Onglet 1: Gestion Conducteurs
        JPanel panelConducteurs = createConducteursPanel();
        tabbedPane.addTab(" Gestion Conducteurs", IconUtils.createUserIcon(new Color(52, 152, 219), 16), panelConducteurs);
        
        // Onglet 2: Gestion Gestionnaires  
        JPanel panelGestionnaires = createGestionnairesPanel();
        tabbedPane.addTab(" Gestion Gestionnaires", IconUtils.createUserIcon(new Color(230, 126, 34), 16), panelGestionnaires);
        
        // Onglet 3: Rôles & Permissions
        JPanel panelRoles = createRolesPanel();
        tabbedPane.addTab(" Rôles & Permissions", IconUtils.createListIcon(new Color(46, 204, 113), 16), panelRoles);
        
        // Changer d'onglet automatiquement après ajout
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0 || selectedIndex == 1) {
                chargerUtilisateurs(); // Recharger la liste pour conducteurs/gestionnaires
            } else if (selectedIndex == 2) {
                chargerRoles(); // Recharger les rôles
            }
        });
        
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createConducteursPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel du formulaire d'ajout
        JPanel panelFormulaire = createFormulairePanel("CONDUCTEUR");
        
        // Panel du tableau avec recherche
        JPanel panelTableau = createTableauPanel();
        
        // Diviser verticalement
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(panelFormulaire);
        splitPane.setBottomComponent(panelTableau);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0.4);
        
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createGestionnairesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel du formulaire d'ajout  
        JPanel panelFormulaire = createFormulairePanel("GESTIONNAIRE");
        
        // Panel du tableau avec recherche
        JPanel panelTableau = createTableauPanel();
        
        // Diviser verticalement
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(panelFormulaire);
        splitPane.setBottomComponent(panelTableau);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0.4);
        
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createFormulairePanel(String typeUtilisateur) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("➕ Ajouter " + typeUtilisateur));
        
        // Panel des champs
        JPanel panelChamps = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Ligne 1: Nom + Prénom
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        panelChamps.add(new JLabel(" Nom :", IconUtils.createUserIcon(new Color(52, 152, 219), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1;
        txtNom = new JTextField(15);
        txtNom.setFont(txtNom.getFont().deriveFont(12f));
        panelChamps.add(txtNom, gbc);
        
        gbc.gridx = 2;
        panelChamps.add(new JLabel(" Prénom :", IconUtils.createUserIcon(new Color(46, 204, 113), 16), JLabel.LEFT), gbc);
        gbc.gridx = 3;
        txtPrenom = new JTextField(15);
        txtPrenom.setFont(txtPrenom.getFont().deriveFont(12f));
        panelChamps.add(txtPrenom, gbc);
        
        // Ligne 2: Matricule + Email
        gbc.gridx = 0; gbc.gridy = 1;
        panelChamps.add(new JLabel(" Matricule :", IconUtils.createCarIcon(new Color(155, 89, 182), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1;
        txtMatricule = new JTextField(15);
        txtMatricule.setFont(txtMatricule.getFont().deriveFont(12f));
        panelChamps.add(txtMatricule, gbc);
        
        gbc.gridx = 2;
        panelChamps.add(new JLabel(" Email :", IconUtils.createListIcon(new Color(230, 126, 34), 16), JLabel.LEFT), gbc);
        gbc.gridx = 3;
        txtEmail = new JTextField(15);
        txtEmail.setFont(txtEmail.getFont().deriveFont(12f));
        panelChamps.add(txtEmail, gbc);
        
        // Ligne 3: Rôle + Statut
        gbc.gridx = 0; gbc.gridy = 2;
        panelChamps.add(new JLabel(" Rôle :", IconUtils.createCalendarIcon(new Color(155, 89, 182), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1;
        if (typeUtilisateur.equals("CONDUCTEUR")) {
            cmbRole = new JComboBox<>(new String[]{"CONDUCTEUR", "CONDUCTEUR_SENIOR"});
        } else {
            cmbRole = new JComboBox<>(new String[]{"GESTIONNAIRE", "ADMIN", "SUPER_ADMIN"});
        }
        cmbRole.setFont(cmbRole.getFont().deriveFont(12f));
        panelChamps.add(cmbRole, gbc);
        
        gbc.gridx = 2;
        panelChamps.add(new JLabel(" Statut :", IconUtils.createRefreshIcon(new Color(52, 152, 219), 16), JLabel.LEFT), gbc);
        gbc.gridx = 3;
        cmbStatut = new JComboBox<>(new String[]{"ACTIF", "INACTIF", "SUSPENDU"});
        cmbStatut.setFont(cmbStatut.getFont().deriveFont(12f));
        panelChamps.add(cmbStatut, gbc);
        
        // Ligne 3: Mot de passe + Confirmation
        gbc.gridx = 0; gbc.gridy = 3;
        panelChamps.add(new JLabel(" Mot de passe :", IconUtils.createSaveIcon(new Color(231, 76, 60), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1;
        txtMotDePasse = new JPasswordField(15);
        txtMotDePasse.setFont(txtMotDePasse.getFont().deriveFont(12f));
        panelChamps.add(txtMotDePasse, gbc);
        
        gbc.gridx = 2;
        panelChamps.add(new JLabel(" Confirmation :", IconUtils.createSaveIcon(new Color(231, 76, 60), 16), JLabel.LEFT), gbc);
        gbc.gridx = 3;
        txtConfirmation = new JPasswordField(15);
        txtConfirmation.setFont(txtConfirmation.getFont().deriveFont(12f));
        panelChamps.add(txtConfirmation, gbc);
        
        panel.add(panelChamps, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnSauvegarder = new JButton(" Sauvegarder", IconUtils.createSaveIcon(Color.WHITE, 16));
        btnSauvegarder.setFont(btnSauvegarder.getFont().deriveFont(Font.BOLD, 12f));
        btnSauvegarder.setBackground(new Color(46, 204, 113));
        btnSauvegarder.setForeground(Color.WHITE);
        btnSauvegarder.setFocusPainted(false);
        btnSauvegarder.setPreferredSize(new Dimension(140, 35));
        btnSauvegarder.addActionListener(e -> sauvegarderUtilisateur());
        
        btnReinitialiser = new JButton(" Réinitialiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnReinitialiser.setFont(btnReinitialiser.getFont().deriveFont(Font.BOLD, 12f));
        btnReinitialiser.setBackground(new Color(255, 165, 0));
        btnReinitialiser.setForeground(Color.WHITE);
        btnReinitialiser.setFocusPainted(false);
        btnReinitialiser.setPreferredSize(new Dimension(140, 35));
        btnReinitialiser.addActionListener(e -> reinitialiserFormulaire());
        
        panelBoutons.add(btnSauvegarder);
        panelBoutons.add(btnReinitialiser);
        
        panel.add(panelBoutons, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTableauPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de recherche
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelRecherche.setBorder(BorderFactory.createTitledBorder("🔍 Recherche & Filtres"));
        
        panelRecherche.add(new JLabel(" Nom/Prénom :", IconUtils.createSearchIcon(new Color(52, 152, 219), 16), JLabel.LEFT));
        txtRechercheNom = new JTextField(15);
        txtRechercheNom.setFont(txtRechercheNom.getFont().deriveFont(12f));
        panelRecherche.add(txtRechercheNom);
        
        panelRecherche.add(new JLabel(" Rôle :", IconUtils.createListIcon(new Color(155, 89, 182), 16), JLabel.LEFT));
        cmbFiltreRole = new JComboBox<>(new String[]{"Tous", "CONDUCTEUR", "CONDUCTEUR_SENIOR", "GESTIONNAIRE", "ADMIN", "SUPER_ADMIN"});
        cmbFiltreRole.setFont(cmbFiltreRole.getFont().deriveFont(12f));
        panelRecherche.add(cmbFiltreRole);
        
        panelRecherche.add(new JLabel(" Statut :", IconUtils.createCalendarIcon(new Color(231, 76, 60), 16), JLabel.LEFT));
        cmbFiltreStatut = new JComboBox<>(new String[]{"Tous", "ACTIF", "INACTIF", "SUSPENDU"});
        cmbFiltreStatut.setFont(cmbFiltreStatut.getFont().deriveFont(12f));
        panelRecherche.add(cmbFiltreStatut);
        
        JButton btnRechercher = new JButton(" Rechercher", IconUtils.createSearchIcon(Color.WHITE, 16));
        btnRechercher.setFont(btnRechercher.getFont().deriveFont(Font.BOLD, 12f));
        btnRechercher.setBackground(new Color(52, 152, 219));
        btnRechercher.setForeground(Color.WHITE);
        btnRechercher.setFocusPainted(false);
        btnRechercher.addActionListener(e -> effectuerRechercheUtilisateurs());
        panelRecherche.add(btnRechercher);
        
        // Modèle de tableau
        modelTable = new DefaultTableModel(COLONNES_USERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Table
        tableUtilisateurs = new JTable(modelTable);
        tableUtilisateurs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUtilisateurs.setRowHeight(30);
        tableUtilisateurs.setFont(tableUtilisateurs.getFont().deriveFont(12f));
        tableUtilisateurs.getTableHeader().setBackground(new Color(155, 89, 182));
        tableUtilisateurs.getTableHeader().setForeground(Color.WHITE);
        tableUtilisateurs.getTableHeader().setFont(tableUtilisateurs.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        
        JScrollPane scrollPane = new JScrollPane(tableUtilisateurs);
        scrollPane.setBorder(BorderFactory.createTitledBorder("👥 Liste des Utilisateurs"));
        
        panel.add(panelRecherche, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(createActionsUtilisateursPanel(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActionsUtilisateursPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Statistiques (gauche)
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotal = new JLabel("📊 Total : 0 utilisateur(s)");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        panelStats.add(lblTotal);
        
        // Boutons (droite)
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        btnActualiser = new JButton(" Actualiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnActualiser.setFont(btnActualiser.getFont().deriveFont(Font.BOLD, 12f));
        btnActualiser.setPreferredSize(new Dimension(120, 30));
        btnActualiser.setBackground(new Color(70, 130, 180));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.setFocusPainted(false);
        btnActualiser.addActionListener(e -> chargerUtilisateurs());
        
        btnModifier = new JButton(" Modifier", IconUtils.createListIcon(Color.WHITE, 16));
        btnModifier.setFont(btnModifier.getFont().deriveFont(Font.BOLD, 12f));
        btnModifier.setPreferredSize(new Dimension(120, 30));
        btnModifier.setBackground(new Color(255, 165, 0));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.setFocusPainted(false);
        btnModifier.addActionListener(e -> modifierUtilisateurSelectionne());
        
        btnSupprimer = new JButton(" Supprimer", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnSupprimer.setFont(btnSupprimer.getFont().deriveFont(Font.BOLD, 12f));
        btnSupprimer.setPreferredSize(new Dimension(120, 30));
        btnSupprimer.setBackground(new Color(231, 76, 60));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.addActionListener(e -> supprimerUtilisateurSelectionne());
        
        panelBoutons.add(btnActualiser);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        
        panel.add(panelStats, BorderLayout.WEST);
        panel.add(panelBoutons, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createRolesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel des rôles et permissions
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Panel gauche: Rôles
        JPanel panelRoles = new JPanel(new BorderLayout());
        panelRoles.setBorder(BorderFactory.createTitledBorder("🔑 Rôles Système"));
        
        modelRoles = new DefaultTableModel(COLONNES_ROLES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableRoles = new JTable(modelRoles);
        tableRoles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableRoles.setRowHeight(30);
        tableRoles.setFont(tableRoles.getFont().deriveFont(12f));
        tableRoles.getTableHeader().setBackground(new Color(46, 204, 113));
        tableRoles.getTableHeader().setForeground(Color.WHITE);
        tableRoles.getTableHeader().setFont(tableRoles.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        
        JScrollPane scrollRoles = new JScrollPane(tableRoles);
        panelRoles.add(scrollRoles, BorderLayout.CENTER);
        
        // Panel droite: Permissions
        JPanel panelPermissions = new JPanel(new BorderLayout());
        panelPermissions.setBorder(BorderFactory.createTitledBorder("⚙️ Permissions Disponibles"));
        
        modelPermissions = new DefaultListModel<>();
        modelPermissions.addElement("VOIR_VEHICULES");
        modelPermissions.addElement("MODIFIER_VEHICULES");
        modelPermissions.addElement("SUPPRIMER_VEHICULES");
        modelPermissions.addElement("VOIR_UTILISATEURS");
        modelPermissions.addElement("MODIFIER_UTILISATEURS");
        modelPermissions.addElement("SUPPRIMER_UTILISATEURS");
        modelPermissions.addElement("VOIR_AFFECTATIONS");
        modelPermissions.addElement("MODIFIER_AFFECTATIONS");
        modelPermissions.addElement("VOIR_MAINTENANCE");
        modelPermissions.addElement("PROGRAMMER_MAINTENANCE");
        modelPermissions.addElement("EXPORT_RAPPORTS");
        modelPermissions.addElement("ADMIN_SYSTEME");
        
        listPermissions = new JList<>(modelPermissions);
        listPermissions.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listPermissions.setFont(listPermissions.getFont().deriveFont(12f));
        
        JScrollPane scrollPermissions = new JScrollPane(listPermissions);
        panelPermissions.add(scrollPermissions, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(panelRoles);
        splitPane.setRightComponent(panelPermissions);
        splitPane.setDividerLocation(400);
        
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(createActionsRolesPanel(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActionsRolesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnAjouterRole = new JButton(" Nouveau Rôle", IconUtils.createAddIcon(Color.WHITE, 16));
        btnAjouterRole.setFont(btnAjouterRole.getFont().deriveFont(Font.BOLD, 12f));
        btnAjouterRole.setBackground(new Color(46, 204, 113));
        btnAjouterRole.setForeground(Color.WHITE);
        btnAjouterRole.setFocusPainted(false);
        btnAjouterRole.setPreferredSize(new Dimension(150, 35));
        btnAjouterRole.addActionListener(e -> ajouterNouveauRole());
        
        btnSupprimerRole = new JButton(" Supprimer Rôle", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnSupprimerRole.setFont(btnSupprimerRole.getFont().deriveFont(Font.BOLD, 12f));
        btnSupprimerRole.setBackground(new Color(231, 76, 60));
        btnSupprimerRole.setForeground(Color.WHITE);
        btnSupprimerRole.setFocusPainted(false);
        btnSupprimerRole.setPreferredSize(new Dimension(150, 35));
        btnSupprimerRole.addActionListener(e -> supprimerRoleSelectionne());
        
        panel.add(btnAjouterRole);
        panel.add(btnSupprimerRole);
        
        return panel;
    }
    
    // Méthodes d'action
    private void sauvegarderUtilisateur() {
        try {
            // Validation des champs
            if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() || 
                txtEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires !", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validation mot de passe
            String motDePasse = new String(txtMotDePasse.getPassword());
            String confirmation = new String(txtConfirmation.getPassword());
            
            if (motDePasse.length() < 6) {
                JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 6 caractères !", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!motDePasse.equals(confirmation)) {
                JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas !", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer le nouvel utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(txtNom.getText().trim());
            utilisateur.setPrenom(txtPrenom.getText().trim());
            utilisateur.setMatricule(txtMatricule.getText().trim());
            utilisateur.setEmail(txtEmail.getText().trim().toLowerCase());
            
            // Convertir le rôle String en RoleUtilisateur
            String roleString = (String) cmbRole.getSelectedItem();
            try {
                utilisateur.setRole(nexus_bmb_soft.models.RoleUtilisateur.valueOf(roleString));
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Rôle invalide sélectionné !", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            utilisateur.setStatut((String) cmbStatut.getSelectedItem());
            utilisateur.setMotDePasse(motDePasse); // À hasher en production
            utilisateur.setDateCreation(LocalDateTime.now());
            
            // Sauvegarder en base
            boolean success = utilisateurDAO.creer(utilisateur);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Utilisateur créé avec succès !", 
                                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                reinitialiserFormulaire();
                chargerUtilisateurs();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création de l'utilisateur !", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur inattendue : " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reinitialiserFormulaire() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtMatricule.setText("");
        txtEmail.setText("");
        txtMotDePasse.setText("");
        txtConfirmation.setText("");
        cmbRole.setSelectedIndex(0);
        cmbStatut.setSelectedItem("ACTIF");
    }
    
    private void chargerUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
            
            // Vider le modèle
            modelTable.setRowCount(0);
            
            // Ajouter les utilisateurs
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Utilisateur user : utilisateurs) {
                modelTable.addRow(new Object[]{
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getMatricule() != null ? user.getMatricule() : "N/A",
                    user.getEmail(),
                    user.getRole().name(), // Utiliser .name() pour obtenir la String
                    user.getStatut(),
                    user.getDateCreation() != null ? user.getDateCreation().format(formatter) : "N/A"
                });
            }
            
            // Mettre à jour le compteur
            lblTotal.setText("📊 Total : " + utilisateurs.size() + " utilisateur(s)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement : " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void chargerRoles() {
        // Pour le moment, données simulées - à connecter à la base plus tard
        modelRoles.setRowCount(0);
        modelRoles.addRow(new Object[]{"1", "CONDUCTEUR", "Conducteur standard", "VOIR_VEHICULES", "15"});
        modelRoles.addRow(new Object[]{"2", "CONDUCTEUR_SENIOR", "Conducteur expérimenté", "VOIR_VEHICULES, MODIFIER_VEHICULES", "8"});
        modelRoles.addRow(new Object[]{"3", "GESTIONNAIRE", "Gestionnaire de flotte", "Toutes permissions véhicules", "5"});
        modelRoles.addRow(new Object[]{"4", "ADMIN", "Administrateur", "Toutes permissions", "2"});
        modelRoles.addRow(new Object[]{"5", "SUPER_ADMIN", "Super Administrateur", "Contrôle total", "1"});
    }
    
    private void effectuerRechercheUtilisateurs() {
        // TODO: Implémenter la logique de recherche avec filtres
        chargerUtilisateurs();
    }
    
    private void modifierUtilisateurSelectionne() {
        int selectedRow = tableUtilisateurs.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à modifier !", 
                                        "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: Implémenter la modification
        JOptionPane.showMessageDialog(this, "Fonctionnalité de modification en cours de développement !", 
                                    "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void supprimerUtilisateurSelectionne() {
        int selectedRow = tableUtilisateurs.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer !", 
                                        "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer cet utilisateur ?\nCette action est irréversible !", 
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Implémenter la suppression
            JOptionPane.showMessageDialog(this, "Fonctionnalité de suppression en cours de développement !", 
                                        "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void ajouterNouveauRole() {
        // TODO: Implémenter l'ajout de rôle
        JOptionPane.showMessageDialog(this, "Fonctionnalité d'ajout de rôle en cours de développement !", 
                                    "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void supprimerRoleSelectionne() {
        int selectedRow = tableRoles.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rôle à supprimer !", 
                                        "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: Implémenter la suppression de rôle
        JOptionPane.showMessageDialog(this, "Fonctionnalité de suppression de rôle en cours de développement !", 
                                    "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}