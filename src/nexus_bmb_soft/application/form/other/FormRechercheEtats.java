package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Vehicule;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface unifiée pour la recherche/filtres et les états/disponibilité des véhicules
 * 
 * @author BlaiseMUBADI
 */
public class FormRechercheEtats extends JPanel {
    
    private VehiculeDAO vehiculeDAO;
    private JTabbedPane tabbedPane;
    
    // Composants onglet Recherche
    private JTextField txtRechercheMatricule;
    private JComboBox<String> cmbMarque;
    private JComboBox<String> cmbTypeCarburant;
    private JComboBox<String> cmbStatut;
    private JTable tableResultats;
    private DefaultTableModel modelResultats;
    private JButton btnRechercher;
    private JButton btnReinitialiser;
    
    // Composants onglet États
    private JTable tableEtats;
    private DefaultTableModel modelEtats;
    private JLabel lblTotalVehicules;
    private JLabel lblDisponibles;
    private JLabel lblEnService;
    private JLabel lblEnMaintenance;
    private JButton btnActualiser;
    
    public FormRechercheEtats() {
        vehiculeDAO = new VehiculeDAO();
        initComponents();
        chargerDonnees();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec bordure
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal
        JLabel lblTitrePrincipal = new JLabel("🔍 Recherche & États des Véhicules", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180));
        lblTitrePrincipal.setForeground(Color.WHITE);
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        
        // Panel principal avec onglets
        tabbedPane = new JTabbedPane();
        
        // Onglet Recherche/Filtres
        JPanel panelRecherche = createPanelRecherche();
        tabbedPane.addTab(" Recherche & Filtres", IconUtils.createSearchIcon(new Color(52, 152, 219), 16), panelRecherche);
        
        // Onglet États/Disponibilité  
        JPanel panelEtats = createPanelEtats();
        tabbedPane.addTab(" États & Disponibilité", IconUtils.createListIcon(new Color(46, 204, 113), 16), panelEtats);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPanelRecherche() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de critères de recherche
        JPanel panelCriteres = new JPanel(new GridBagLayout());
        panelCriteres.setBorder(BorderFactory.createTitledBorder("📝 Critères de Recherche"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Recherche par matricule
        gbc.gridx = 0; gbc.gridy = 0;
        panelCriteres.add(new JLabel(" Matricule :", IconUtils.createCarIcon(new Color(52, 73, 94), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1;
        txtRechercheMatricule = new JTextField(15);
        txtRechercheMatricule.setFont(new Font("Arial", Font.PLAIN, 12));
        txtRechercheMatricule.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelCriteres.add(txtRechercheMatricule, gbc);
        
        // Filtre par marque
        gbc.gridx = 0; gbc.gridy = 1;
        panelCriteres.add(new JLabel(" Marque :", IconUtils.createListIcon(new Color(155, 89, 182), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1;
        cmbMarque = new JComboBox<>(new String[]{"Toutes", "Toyota", "Nissan", "Mitsubishi", "Hyundai", "KIA", "Autre"});
        cmbMarque.setFont(cmbMarque.getFont().deriveFont(12f));
        panelCriteres.add(cmbMarque, gbc);
        
        // Filtre par carburant
        gbc.gridx = 2; gbc.gridy = 0;
        panelCriteres.add(new JLabel(" Type :", IconUtils.createRefreshIcon(new Color(230, 126, 34), 16), JLabel.LEFT), gbc);
        gbc.gridx = 3;
        cmbTypeCarburant = new JComboBox<>(new String[]{"Tous", "Berline", "SUV", "Pick-up", "Camion", "Autre"});
        cmbTypeCarburant.setFont(cmbTypeCarburant.getFont().deriveFont(12f));
        panelCriteres.add(cmbTypeCarburant, gbc);
        
        // Filtre par statut
        gbc.gridx = 2; gbc.gridy = 1;
        panelCriteres.add(new JLabel(" Disponibilité :", IconUtils.createCalendarIcon(new Color(231, 76, 60), 16), JLabel.LEFT), gbc);
        gbc.gridx = 3;
        cmbStatut = new JComboBox<>(new String[]{"Tous", "Disponible", "Non Disponible"});
        cmbStatut.setFont(cmbStatut.getFont().deriveFont(12f));
        panelCriteres.add(cmbStatut, gbc);
        
        // Boutons d'action
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnRechercher = new JButton(" Rechercher", IconUtils.createSearchIcon(Color.WHITE, 16));
        btnRechercher.setFont(btnRechercher.getFont().deriveFont(Font.BOLD, 12f));
        btnRechercher.setBackground(new Color(52, 152, 219));
        btnRechercher.setForeground(Color.WHITE);
        btnRechercher.setFocusPainted(false);
        btnRechercher.addActionListener(e -> effectuerRecherche());
        
        btnReinitialiser = new JButton(" Réinitialiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnReinitialiser.setFont(btnReinitialiser.getFont().deriveFont(Font.BOLD, 12f));
        btnReinitialiser.setBackground(new Color(255, 165, 0));
        btnReinitialiser.setForeground(Color.WHITE);
        btnReinitialiser.setFocusPainted(false);
        btnReinitialiser.addActionListener(e -> reinitialiserRecherche());
        
        panelBoutons.add(btnRechercher);
        panelBoutons.add(btnReinitialiser);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panelCriteres.add(panelBoutons, gbc);
        
        // Table des résultats
        String[] colonnesResultats = {"Matricule", "Marque", "Type", "Année", "Disponible", "Assurance"};
        modelResultats = new DefaultTableModel(colonnesResultats, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableResultats = new JTable(modelResultats);
        tableResultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableResultats.setRowHeight(30);
        tableResultats.setFont(tableResultats.getFont().deriveFont(12f));
        tableResultats.getTableHeader().setBackground(new Color(52, 152, 219));
        tableResultats.getTableHeader().setForeground(Color.WHITE);
        tableResultats.getTableHeader().setFont(tableResultats.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        
        JScrollPane scrollResultats = new JScrollPane(tableResultats);
        scrollResultats.setBorder(BorderFactory.createTitledBorder("📋 Résultats de la Recherche"));
        
        panel.add(panelCriteres, BorderLayout.NORTH);
        panel.add(scrollResultats, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPanelEtats() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel des statistiques
        JPanel panelStats = new JPanel(new GridLayout(1, 4, 10, 10));
        panelStats.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        // Cartes statistiques
        lblTotalVehicules = createStatCard("Total Véhicules", "0", new Color(52, 152, 219));
        lblDisponibles = createStatCard("Disponibles", "0", new Color(46, 204, 113));
        lblEnService = createStatCard("Non Disponibles", "0", new Color(230, 126, 34));
        lblEnMaintenance = createStatCard("Avec Assurance", "0", new Color(231, 76, 60));
        
        panelStats.add(lblTotalVehicules);
        panelStats.add(lblDisponibles);
        panelStats.add(lblEnService);
        panelStats.add(lblEnMaintenance);
        
        // Bouton actualiser
        JPanel panelActualiser = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelActualiser.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnActualiser = new JButton(" Actualiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnActualiser.setFont(btnActualiser.getFont().deriveFont(Font.BOLD, 12f));
        btnActualiser.setBackground(new Color(52, 152, 219));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.setFocusPainted(false);
        btnActualiser.addActionListener(e -> actualiserEtats());
        panelActualiser.add(btnActualiser);
        
        // Table des états détaillés
        String[] colonnesEtats = {"Matricule", "Marque", "Type", "Année", "Disponible", "Assurance", "Vidange", "Visite Technique"};
        modelEtats = new DefaultTableModel(colonnesEtats, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableEtats = new JTable(modelEtats);
        tableEtats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEtats.setRowHeight(30);
        tableEtats.setFont(tableEtats.getFont().deriveFont(12f));
        tableEtats.getTableHeader().setBackground(new Color(46, 204, 113));
        tableEtats.getTableHeader().setForeground(Color.WHITE);
        tableEtats.getTableHeader().setFont(tableEtats.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        
        JScrollPane scrollEtats = new JScrollPane(tableEtats);
        scrollEtats.setBorder(BorderFactory.createTitledBorder("📊 États Détaillés des Véhicules"));
        
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelStats, BorderLayout.CENTER);
        panelTop.add(panelActualiser, BorderLayout.EAST);
        
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(scrollEtats, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createStatCard(String titre, String valeur, Color couleur) {
        JLabel card = new JLabel("<html><center><b style='font-size: 18px; color: " + 
            String.format("#%06x", couleur.getRGB() & 0xFFFFFF) + ";'>" + valeur + 
            "</b><br><span style='font-size: 11px; color: #7f8c8d;'>" + titre + "</span></center></html>");
        card.setHorizontalAlignment(SwingConstants.CENTER);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleur, 2),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        card.setOpaque(false); // Pas de fond fixé pour s'adapter au thème
        return card;
    }
    
    private void chargerDonnees() {
        // Charger tous les véhicules au démarrage
        effectuerRecherche();
        actualiserEtats();
    }
    
    private void effectuerRecherche() {
        try {
            modelResultats.setRowCount(0);
            List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
            
            // Appliquer les filtres
            String matricule = txtRechercheMatricule.getText().trim().toLowerCase();
            String marque = (String) cmbMarque.getSelectedItem();
            String typeVehicule = (String) cmbTypeCarburant.getSelectedItem();
            String disponibilite = (String) cmbStatut.getSelectedItem();
            
            List<Vehicule> vehiculesFiltres = vehicules.stream()
                .filter(v -> matricule.isEmpty() || v.getMatricule().toLowerCase().contains(matricule))
                .filter(v -> "Toutes".equals(marque) || v.getMarque().equals(marque))
                .filter(v -> "Tous".equals(typeVehicule) || v.getType().equals(typeVehicule))
                .filter(v -> "Tous".equals(disponibilite) || 
                    ("Disponible".equals(disponibilite) && v.isDisponible()) ||
                    ("Non Disponible".equals(disponibilite) && !v.isDisponible()))
                .collect(Collectors.toList());
            
            // Remplir la table
            for (Vehicule v : vehiculesFiltres) {
                modelResultats.addRow(new Object[]{
                    v.getMatricule(),
                    v.getMarque(),
                    v.getType(),
                    v.getAnnee(),
                    v.isDisponible() ? "Oui" : "Non",
                    v.getDateAssurance() != null ? v.getDateAssurance().toString() : "N/A"
                });
            }
            
            JOptionPane.showMessageDialog(this, 
                vehiculesFiltres.size() + " véhicule(s) trouvé(s)", 
                "Recherche", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la recherche : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reinitialiserRecherche() {
        txtRechercheMatricule.setText("");
        cmbMarque.setSelectedIndex(0);
        cmbTypeCarburant.setSelectedIndex(0);
        cmbStatut.setSelectedIndex(0);
        effectuerRecherche();
    }
    
    private void actualiserEtats() {
        try {
            modelEtats.setRowCount(0);
            List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
            
            // Calculer les statistiques
            int total = vehicules.size();
            int disponibles = (int) vehicules.stream().filter(v -> v.isDisponible()).count();
            int nonDisponibles = total - disponibles;
            int avecAssurance = (int) vehicules.stream().filter(v -> v.getDateAssurance() != null).count();
            
            // Mettre à jour les cartes
            updateStatCard(lblTotalVehicules, "Total Véhicules", String.valueOf(total), new Color(52, 152, 219));
            updateStatCard(lblDisponibles, "Disponibles", String.valueOf(disponibles), new Color(46, 204, 113));
            updateStatCard(lblEnService, "Non Disponibles", String.valueOf(nonDisponibles), new Color(230, 126, 34));
            updateStatCard(lblEnMaintenance, "Avec Assurance", String.valueOf(avecAssurance), new Color(231, 76, 60));
            
            // Remplir la table des états
            for (Vehicule v : vehicules) {
                modelEtats.addRow(new Object[]{
                    v.getMatricule(),
                    v.getMarque(),
                    v.getType(),
                    v.getAnnee(),
                    v.isDisponible() ? "Oui" : "Non",
                    v.getDateAssurance() != null ? v.getDateAssurance().toString() : "N/A",
                    v.getDateVidange() != null ? v.getDateVidange().toString() : "N/A",
                    v.getDateVisiteTechnique() != null ? v.getDateVisiteTechnique().toString() : "N/A"
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'actualisation : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatCard(JLabel card, String titre, String valeur, Color couleur) {
        card.setText("<html><center><b style='font-size: 18px; color: " + 
            String.format("#%06x", couleur.getRGB() & 0xFFFFFF) + ";'>" + valeur + 
            "</b><br><span style='font-size: 11px; color: #7f8c8d;'>" + titre + "</span></center></html>");
    }
}