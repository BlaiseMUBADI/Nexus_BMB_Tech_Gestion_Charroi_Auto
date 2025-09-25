package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.UtilisateurDAO;
import nexus_bmb_soft.models.RoleUtilisateur;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface dédiée à la gestion des gestionnaires
 * Style harmonisé avec FormGestionVehicules
 * 
 * @author BlaiseMUBADI
 */
public class FormGestionGestionnaires extends JPanel {
    
    private UtilisateurDAO utilisateurDAO;
    
    // Composants du formulaire
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtMatricule;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    private JComboBox<String> cmbStatut;
    private JPasswordField txtMotDePasse;
    private JPasswordField txtConfirmation;
    
    // Composants de recherche
    private JTextField txtRechercheNom;
    private JComboBox<String> cmbFiltreRole;
    private JComboBox<String> cmbFiltreStatut;
    
    // Tableau et modèle
    private JTable tableGestionnaires;
    private DefaultTableModel modelTable;
    private JLabel lblNombreTotal;
    
    // Onglets
    private JTabbedPane tabbedPane;
    
    // Colonnes du tableau
    private final String[] COLONNES = {
        "ID", "Nom", "Prénom", "Matricule", "Email", "Rôle", "Statut", "Date Création"
    };
    
    public FormGestionGestionnaires() {
        utilisateurDAO = new UtilisateurDAO();
        init();
        chargerGestionnaires();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        // Panel principal avec titre
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal (style FormGestionVehicules)
        JLabel lblTitrePrincipal = new JLabel("👨‍💼 Gestion des Gestionnaires", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180));
        lblTitrePrincipal.setForeground(Color.WHITE);
        
        // Créer les onglets
        tabbedPane = new JTabbedPane();
        
        // Onglet 1: Ajouter gestionnaire
        JPanel panelAjout = createAjoutPanel();
        tabbedPane.addTab(" Ajouter Gestionnaire", IconUtils.createAddIcon(new Color(52, 152, 219), 16), panelAjout);
        
        // Onglet 2: Liste gestionnaires
        JPanel panelListe = createListePanel();
        tabbedPane.addTab(" Liste Gestionnaires", IconUtils.createListIcon(new Color(46, 204, 113), 16), panelListe);
        
        // Changer d'onglet automatiquement après ajout
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                chargerGestionnaires(); // Recharger la liste quand on passe à l'onglet liste
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
        panelSaisie.setBorder(BorderFactory.createTitledBorder("📝 Informations du gestionnaire"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Nom (obligatoire)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblNom = new JLabel(" Nom :", IconUtils.createUserIcon(new Color(52, 73, 94), 16), JLabel.LEFT);
        lblNom.setFont(lblNom.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblNom, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNom = new JTextField(20);
        txtNom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtNom, gbc);
        row++;
        
        // Prénom (obligatoire)
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblPrenom = new JLabel(" Prénom :", IconUtils.createUserIcon(new Color(52, 73, 94), 16), JLabel.LEFT);
        lblPrenom.setFont(lblPrenom.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblPrenom, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrenom = new JTextField(20);
        txtPrenom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtPrenom, gbc);
        row++;
        
        // Matricule
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
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
        
        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblEmail = new JLabel("📧 Email :");
        lblEmail.setFont(lblEmail.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblEmail, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtEmail, gbc);
        row++;
        
        // Rôle (spécifique gestionnaires)
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblRole = new JLabel("👔 Rôle :");
        lblRole.setFont(lblRole.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblRole, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbRole = new JComboBox<>(new String[]{"GESTIONNAIRE", "ADMIN", "SUPER_ADMIN"});
        cmbRole.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(cmbRole, gbc);
        row++;
        
        // Statut
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblStatut = new JLabel("✅ Statut :");
        lblStatut.setFont(lblStatut.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblStatut, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbStatut = new JComboBox<>(new String[]{"ACTIF", "INACTIF", "SUSPENDU"});
        cmbStatut.setSelectedItem("ACTIF");
        cmbStatut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(cmbStatut, gbc);
        row++;
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelSaisie.add(new JSeparator(), gbc);
        row++;
        
        // Section mot de passe
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblMotDePasse = new JLabel("🔐 Informations de Connexion");
        lblMotDePasse.setFont(lblMotDePasse.getFont().deriveFont(Font.BOLD, 14f));
        panelSaisie.add(lblMotDePasse, gbc);
        row++;
        
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblMdp = new JLabel(" Mot de passe :", IconUtils.createSaveIcon(new Color(231, 76, 60), 16), JLabel.LEFT);
        lblMdp.setFont(lblMdp.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblMdp, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMotDePasse = new JPasswordField(20);
        txtMotDePasse.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtMotDePasse, gbc);
        row++;
        
        // Confirmation mot de passe
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JLabel lblConfirm = new JLabel(" Confirmation :", IconUtils.createSaveIcon(new Color(231, 76, 60), 16), JLabel.LEFT);
        lblConfirm.setFont(lblConfirm.getFont().deriveFont(Font.BOLD));
        panelSaisie.add(lblConfirm, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtConfirmation = new JPasswordField(20);
        txtConfirmation.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelSaisie.add(txtConfirmation, gbc);
        row++;
        
        // Panel boutons (style FormGestionVehicules)
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBoutons.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        JButton btnReinitialiser = new JButton(" Réinitialiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnReinitialiser.setPreferredSize(new Dimension(150, 35));
        btnReinitialiser.setBackground(new Color(255, 165, 0));
        btnReinitialiser.setForeground(Color.WHITE);
        btnReinitialiser.setFont(btnReinitialiser.getFont().deriveFont(Font.BOLD));
        btnReinitialiser.addActionListener(e -> reinitialiserFormulaire());
        
        JButton btnSauvegarder = new JButton(" Sauvegarder", IconUtils.createSaveIcon(Color.WHITE, 16));
        btnSauvegarder.setPreferredSize(new Dimension(150, 35));
        btnSauvegarder.setBackground(new Color(34, 139, 34));
        btnSauvegarder.setForeground(Color.WHITE);
        btnSauvegarder.setFont(btnSauvegarder.getFont().deriveFont(Font.BOLD));
        btnSauvegarder.addActionListener(e -> ajouterGestionnaire());
        
        panelBoutons.add(btnReinitialiser);
        panelBoutons.add(btnSauvegarder);
        
        panel.add(panelSaisie, BorderLayout.CENTER);
        panel.add(panelBoutons, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createListePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Recherche et Filtres"));
        
        searchPanel.add(new JLabel("Rechercher :"));
        txtRechercheNom = new JTextField(15);
        txtRechercheNom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        searchPanel.add(txtRechercheNom);
        
        searchPanel.add(new JLabel("Rôle :"));
        cmbFiltreRole = new JComboBox<>(new String[]{"Tous", "GESTIONNAIRE", "ADMIN", "SUPER_ADMIN"});
        searchPanel.add(cmbFiltreRole);
        
        searchPanel.add(new JLabel("Statut :"));
        cmbFiltreStatut = new JComboBox<>(new String[]{"Tous", "ACTIF", "INACTIF", "SUSPENDU"});
        searchPanel.add(cmbFiltreStatut);
        
        JButton btnRechercher = new JButton("Rechercher", IconUtils.createSearchIcon(Color.WHITE, 16));
        btnRechercher.setBackground(new Color(52, 152, 219));
        btnRechercher.setForeground(Color.WHITE);
        btnRechercher.setFont(btnRechercher.getFont().deriveFont(Font.BOLD, 12f));
        btnRechercher.addActionListener(e -> filtrerGestionnaires());
        searchPanel.add(btnRechercher);
        
        // Panel du tableau
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("📋 Liste des Gestionnaires"));
        
        // Modèle de tableau
        modelTable = new DefaultTableModel(COLONNES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tableau en lecture seule
            }
        };
        
        tableGestionnaires = new JTable(modelTable);
        tableGestionnaires.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        tableGestionnaires.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tableGestionnaires.setRowHeight(25);
        tableGestionnaires.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableGestionnaires.setGridColor(new Color(220, 220, 220));
        
        // Couleurs de sélection
        tableGestionnaires.setSelectionBackground(new Color(255, 235, 59, 100));
        tableGestionnaires.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tableGestionnaires);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblNombreTotal = new JLabel("Total: 0 gestionnaire(s)");
        lblNombreTotal.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblNombreTotal.setForeground(new Color(44, 62, 80));
        infoPanel.add(lblNombreTotal);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void ajouterGestionnaire() {
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
            
            // Créer le nouveau gestionnaire
            Utilisateur gestionnaire = new Utilisateur();
            gestionnaire.setNom(txtNom.getText().trim());
            gestionnaire.setPrenom(txtPrenom.getText().trim());
            gestionnaire.setMatricule(txtMatricule.getText().trim());
            gestionnaire.setEmail(txtEmail.getText().trim().toLowerCase());
            
            // Rôle gestionnaire/admin uniquement
            String roleString = (String) cmbRole.getSelectedItem();
            gestionnaire.setRole(RoleUtilisateur.valueOf(roleString));
            
            gestionnaire.setStatut((String) cmbStatut.getSelectedItem());
            gestionnaire.setMotDePasse(motDePasse); // À hasher en production
            gestionnaire.setDateCreation(LocalDateTime.now());
            
            // Sauvegarder en base
            if (utilisateurDAO.creer(gestionnaire)) {
                JOptionPane.showMessageDialog(this, "Gestionnaire ajouté avec succès !", 
                                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                reinitialiserFormulaire();
                tabbedPane.setSelectedIndex(1); // Basculer vers l'onglet liste
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du gestionnaire !", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
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
    
    private void chargerGestionnaires() {
        try {
            List<Utilisateur> tousUtilisateurs = utilisateurDAO.lireTous();
            
            // Filtrer seulement les gestionnaires
            List<Utilisateur> gestionnaires = tousUtilisateurs.stream()
                .filter(user -> user.getRole() == RoleUtilisateur.GESTIONNAIRE || 
                               user.getRole() == RoleUtilisateur.ADMIN ||
                               user.getRole() == RoleUtilisateur.SUPER_ADMIN)
                .collect(Collectors.toList());
            
            // Vider le modèle
            modelTable.setRowCount(0);
            
            // Ajouter les gestionnaires
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Utilisateur gestionnaire : gestionnaires) {
                modelTable.addRow(new Object[]{
                    gestionnaire.getId(),
                    gestionnaire.getNom(),
                    gestionnaire.getPrenom(),
                    gestionnaire.getMatricule() != null ? gestionnaire.getMatricule() : "N/A",
                    gestionnaire.getEmail(),
                    gestionnaire.getRole().name(),
                    gestionnaire.getStatut(),
                    gestionnaire.getDateCreation() != null ? gestionnaire.getDateCreation().format(formatter) : "N/A"
                });
            }
            
            // Mettre à jour le compteur
            lblNombreTotal.setText("Total: " + gestionnaires.size() + " gestionnaire(s)");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des gestionnaires: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filtrerGestionnaires() {
        try {
            List<Utilisateur> tousUtilisateurs = utilisateurDAO.lireTous();
            
            // Filtrer les gestionnaires selon les critères
            String rechercheNom = txtRechercheNom.getText().trim().toLowerCase();
            String filtreRole = (String) cmbFiltreRole.getSelectedItem();
            String filtreStatut = (String) cmbFiltreStatut.getSelectedItem();
            
            List<Utilisateur> gestionnairesFiltres = tousUtilisateurs.stream()
                .filter(user -> user.getRole() == RoleUtilisateur.GESTIONNAIRE || 
                               user.getRole() == RoleUtilisateur.ADMIN ||
                               user.getRole() == RoleUtilisateur.SUPER_ADMIN)
                .filter(user -> rechercheNom.isEmpty() || 
                               user.getNom().toLowerCase().contains(rechercheNom) ||
                               user.getPrenom().toLowerCase().contains(rechercheNom))
                .filter(user -> "Tous".equals(filtreRole) || user.getRole().name().equals(filtreRole))
                .filter(user -> "Tous".equals(filtreStatut) || user.getStatut().equals(filtreStatut))
                .collect(Collectors.toList());
            
            // Vider le modèle et ajouter les résultats filtrés
            modelTable.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Utilisateur gestionnaire : gestionnairesFiltres) {
                modelTable.addRow(new Object[]{
                    gestionnaire.getId(),
                    gestionnaire.getNom(),
                    gestionnaire.getPrenom(),
                    gestionnaire.getMatricule() != null ? gestionnaire.getMatricule() : "N/A",
                    gestionnaire.getEmail(),
                    gestionnaire.getRole().name(),
                    gestionnaire.getStatut(),
                    gestionnaire.getDateCreation() != null ? gestionnaire.getDateCreation().format(formatter) : "N/A"
                });
            }
            
            // Mettre à jour le compteur
            lblNombreTotal.setText("Total: " + gestionnairesFiltres.size() + " gestionnaire(s) trouvé(s)");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}