package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.UtilisateurDAO;
import nexus_bmb_soft.models.RoleUtilisateur;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interface dédiée à la gestion des rôles et permissions
 * 
 * @author BlaiseMUBADI
 */
public class FormGestionRoles extends JPanel {
    
    private UtilisateurDAO utilisateurDAO;
    
    // Composants de modification de rôle
    private JComboBox<String> cmbUtilisateurs;
    private JComboBox<String> cmbNouveauRole;
    private JTextArea txtDescriptionRole;
    
    // Statistiques des rôles
    private JTable tableStatistiques;
    private DefaultTableModel modelStatistiques;
    
    // Tableau des rôles et permissions
    private JTable tableRoles;
    private DefaultTableModel modelRoles;
    
    // Colonnes des tableaux
    private final String[] COLONNES_STATISTIQUES = {"Rôle", "Nombre d'utilisateurs", "Pourcentage"};
    private final String[] COLONNES_ROLES = {"Rôle", "Description", "Permissions"};
    
    public FormGestionRoles() {
        utilisateurDAO = new UtilisateurDAO();
        init();
        chargerDonnees();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Panel principal avec titre
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titre
        JLabel titleLabel = new JLabel("🔐 Gestion des Rôles et Permissions", IconUtils.createCalendarIcon(new Color(52, 152, 219), 24), JLabel.LEFT);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Panel de modification de rôle
        JPanel modificationPanel = createModificationPanel();
        
        // Panel des statistiques
        JPanel statistiquesPanel = createStatistiquesPanel();
        
        // Panel des définitions des rôles
        JPanel rolesPanel = createRolesPanel();
        
        // Organisation
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        topPanel.add(modificationPanel, BorderLayout.NORTH);
        topPanel.add(statistiquesPanel, BorderLayout.CENTER);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(rolesPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createModificationPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2), 
            " ⚙️ Modification de Rôle "
        ));
        
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Sélection utilisateur
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Utilisateur :", IconUtils.createUserIcon(new Color(52, 152, 219), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1;
        cmbUtilisateurs = new JComboBox<>();
        cmbUtilisateurs.setFont(cmbUtilisateurs.getFont().deriveFont(12f));
        cmbUtilisateurs.setPreferredSize(new Dimension(200, 25));
        cmbUtilisateurs.addActionListener(e -> afficherRoleActuel());
        panel.add(cmbUtilisateurs, gbc);
        
        // Nouveau rôle
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Nouveau rôle :", IconUtils.createCalendarIcon(new Color(52, 152, 219), 16), JLabel.LEFT), gbc);
        gbc.gridx = 3;
        cmbNouveauRole = new JComboBox<>(new String[]{"CONDUCTEUR", "CONDUCTEUR_SENIOR", "GESTIONNAIRE", "ADMIN", "SUPER_ADMIN"});
        cmbNouveauRole.setFont(cmbNouveauRole.getFont().deriveFont(12f));
        cmbNouveauRole.setPreferredSize(new Dimension(150, 25));
        cmbNouveauRole.addActionListener(e -> afficherDescriptionRole());
        panel.add(cmbNouveauRole, gbc);
        
        // Description du rôle
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(new JLabel("Description du rôle sélectionné :", IconUtils.createListIcon(new Color(52, 152, 219), 16), JLabel.LEFT), gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        txtDescriptionRole = new JTextArea(3, 50);
        txtDescriptionRole.setEditable(false);
        txtDescriptionRole.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        txtDescriptionRole.setBackground(new Color(248, 249, 250));
        txtDescriptionRole.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollDesc = new JScrollPane(txtDescriptionRole);
        panel.add(scrollDesc, gbc);
        
        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBoutons.setOpaque(false);
        
        JButton btnModifier = new JButton("Modifier Rôle", IconUtils.createSaveIcon(Color.WHITE, 16));
        btnModifier.setBackground(new Color(52, 152, 219));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.setFont(btnModifier.getFont().deriveFont(Font.BOLD, 12f));
        btnModifier.setFocusPainted(false);
        btnModifier.setBorderPainted(false);
        btnModifier.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModifier.addActionListener(e -> modifierRole());
        
        JButton btnRafraichir = new JButton("Rafraîchir", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnRafraichir.setBackground(new Color(52, 152, 219));
        btnRafraichir.setForeground(Color.WHITE);
        btnRafraichir.setFont(btnRafraichir.getFont().deriveFont(Font.BOLD, 12f));
        btnRafraichir.setFocusPainted(false);
        btnRafraichir.setBorderPainted(false);
        btnRafraichir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRafraichir.addActionListener(e -> chargerDonnees());
        
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnRafraichir);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        panel.add(panelBoutons, gbc);
        
        return panel;
    }
    
    private JPanel createStatistiquesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2), 
            " 📊 Statistiques des Rôles "
        ));
        
        modelStatistiques = new DefaultTableModel(COLONNES_STATISTIQUES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableStatistiques = new JTable(modelStatistiques);
        tableStatistiques.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        tableStatistiques.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tableStatistiques.setRowHeight(25);
        tableStatistiques.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableStatistiques.setGridColor(new Color(220, 220, 220));
        tableStatistiques.setSelectionBackground(new Color(255, 235, 59, 100));
        
        JScrollPane scrollStatistiques = new JScrollPane(tableStatistiques);
        scrollStatistiques.setPreferredSize(new Dimension(500, 150));
        panel.add(scrollStatistiques, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRolesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2), 
            " 🛡️ Définitions des Rôles et Permissions "
        ));
        
        modelRoles = new DefaultTableModel(COLONNES_ROLES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableRoles = new JTable(modelRoles);
        tableRoles.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        tableRoles.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tableRoles.setRowHeight(35);
        tableRoles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableRoles.setGridColor(new Color(220, 220, 220));
        tableRoles.setSelectionBackground(new Color(255, 235, 59, 100));
        
        // Ajustement des colonnes
        tableRoles.getColumnModel().getColumn(0).setPreferredWidth(120);
        tableRoles.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableRoles.getColumnModel().getColumn(2).setPreferredWidth(300);
        
        JScrollPane scrollRoles = new JScrollPane(tableRoles);
        scrollRoles.setPreferredSize(new Dimension(800, 200));
        panel.add(scrollRoles, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void chargerDonnees() {
        chargerUtilisateurs();
        chargerStatistiques();
        chargerDefinitionsRoles();
        afficherDescriptionRole();
    }
    
    private void chargerUtilisateurs() {
        try {
            cmbUtilisateurs.removeAllItems();
            List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
            
            for (Utilisateur utilisateur : utilisateurs) {
                String displayText = String.format("%s %s (%s) - %s", 
                    utilisateur.getNom(), 
                    utilisateur.getPrenom(), 
                    utilisateur.getEmail(),
                    utilisateur.getRole().name()
                );
                cmbUtilisateurs.addItem(displayText);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void chargerStatistiques() {
        try {
            List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
            Map<RoleUtilisateur, Long> statistiques = utilisateurs.stream()
                .collect(Collectors.groupingBy(Utilisateur::getRole, Collectors.counting()));
            
            modelStatistiques.setRowCount(0);
            long total = utilisateurs.size();
            
            for (RoleUtilisateur role : RoleUtilisateur.values()) {
                long count = statistiques.getOrDefault(role, 0L);
                double pourcentage = total > 0 ? (count * 100.0 / total) : 0.0;
                
                modelStatistiques.addRow(new Object[]{
                    role.name(),
                    count,
                    String.format("%.1f%%", pourcentage)
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du calcul des statistiques: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void chargerDefinitionsRoles() {
        modelRoles.setRowCount(0);
        
        // Définitions des rôles
        modelRoles.addRow(new Object[]{
            "CONDUCTEUR", 
            "Conducteur basique du parc automobile",
            "• Consultation véhicules\n• Réservation véhicules\n• Consultation planning personnel"
        });
        
        modelRoles.addRow(new Object[]{
            "CONDUCTEUR_SENIOR", 
            "Conducteur expérimenté avec privilèges étendus",
            "• Toutes permissions CONDUCTEUR\n• Validation réservations juniors\n• Formation nouveaux conducteurs"
        });
        
        modelRoles.addRow(new Object[]{
            "GESTIONNAIRE", 
            "Gestionnaire du parc automobile",
            "• Gestion véhicules et entretien\n• Planning global\n• Rapports et statistiques\n• Gestion conducteurs"
        });
        
        modelRoles.addRow(new Object[]{
            "ADMIN", 
            "Administrateur système",
            "• Toutes permissions GESTIONNAIRE\n• Gestion utilisateurs\n• Configuration système\n• Sauvegarde données"
        });
        
        modelRoles.addRow(new Object[]{
            "SUPER_ADMIN", 
            "Super administrateur",
            "• Toutes permissions\n• Gestion rôles\n• Configuration avancée\n• Accès complet système"
        });
    }
    
    private void afficherRoleActuel() {
        // Afficher le rôle actuel de l'utilisateur sélectionné
        int selectedIndex = cmbUtilisateurs.getSelectedIndex();
        if (selectedIndex >= 0) {
            try {
                List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
                if (selectedIndex < utilisateurs.size()) {
                    RoleUtilisateur roleActuel = utilisateurs.get(selectedIndex).getRole();
                    cmbNouveauRole.setSelectedItem(roleActuel.name());
                }
            } catch (Exception e) {
                // Ignore l'erreur silencieusement
            }
        }
    }
    
    private void afficherDescriptionRole() {
        String roleSelectionne = (String) cmbNouveauRole.getSelectedItem();
        if (roleSelectionne != null) {
            switch (roleSelectionne) {
                case "CONDUCTEUR":
                    txtDescriptionRole.setText("Rôle basique pour les conducteurs du parc automobile.\n" +
                                             "Permissions: Consultation véhicules, réservation véhicules, consultation planning personnel.");
                    break;
                case "CONDUCTEUR_SENIOR":
                    txtDescriptionRole.setText("Conducteur expérimenté avec des privilèges étendus.\n" +
                                             "Permissions: Toutes les permissions CONDUCTEUR + validation réservations + formation nouveaux conducteurs.");
                    break;
                case "GESTIONNAIRE":
                    txtDescriptionRole.setText("Gestionnaire responsable du parc automobile.\n" +
                                             "Permissions: Gestion véhicules, entretien, planning global, rapports et gestion conducteurs.");
                    break;
                case "ADMIN":
                    txtDescriptionRole.setText("Administrateur du système avec accès complet.\n" +
                                             "Permissions: Toutes permissions GESTIONNAIRE + gestion utilisateurs + configuration système.");
                    break;
                case "SUPER_ADMIN":
                    txtDescriptionRole.setText("Super administrateur avec tous les privilèges.\n" +
                                             "Permissions: Accès complet au système, gestion des rôles et configuration avancée.");
                    break;
            }
        }
    }
    
    private void modifierRole() {
        try {
            int selectedIndex = cmbUtilisateurs.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur !", 
                                            "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
            if (selectedIndex >= utilisateurs.size()) {
                JOptionPane.showMessageDialog(this, "Utilisateur non valide !", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Utilisateur utilisateur = utilisateurs.get(selectedIndex);
            String nouveauRoleString = (String) cmbNouveauRole.getSelectedItem();
            RoleUtilisateur nouveauRole = RoleUtilisateur.valueOf(nouveauRoleString);
            
            if (utilisateur.getRole() == nouveauRole) {
                JOptionPane.showMessageDialog(this, "L'utilisateur a déjà ce rôle !", 
                                            "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Confirmation
            int confirmation = JOptionPane.showConfirmDialog(this, 
                String.format("Êtes-vous sûr de vouloir changer le rôle de %s %s\nde %s vers %s ?",
                    utilisateur.getNom(), utilisateur.getPrenom(),
                    utilisateur.getRole().name(), nouveauRole.name()),
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                utilisateur.setRole(nouveauRole);
                
                if (utilisateurDAO.mettreAJour(utilisateur)) {
                    JOptionPane.showMessageDialog(this, "Rôle modifié avec succès !", 
                                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerDonnees(); // Recharger les données
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification du rôle !", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}