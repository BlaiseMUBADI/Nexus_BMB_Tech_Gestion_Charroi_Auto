package nexus_bmb_soft.application.form.other;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import nexus_bmb_soft.database.dao.EntretienDAO;
import nexus_bmb_soft.models.Entretien;
import nexus_bmb_soft.utils.IconUtils;

/**
 * Formulaire pour l'historique détaillé de la maintenance
 * Style basé sur FormRechercheEtats - onglet "États & Disponibilité"
 * 
 * @author BlaiseMUBADI
 */
public class FormHistoriqueMaintenance extends JPanel {
    
    private EntretienDAO entretienDAO;
    
    // Composants de l'interface
    private JTable tableHistorique;
    private DefaultTableModel modelHistorique;
    private JLabel lblTotalEntretiens;
    private JLabel lblEntretiensTermines;
    private JLabel lblEntretiensEnCours;
    private JLabel lblCoutTotal;
    private JComboBox<String> cmbFiltreStatut;
    private JComboBox<String> cmbFiltreType;
    private JButton btnActualiser;
    
    public FormHistoriqueMaintenance() {
        entretienDAO = new EntretienDAO();
        initComponents();
        chargerDonnees();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec bordure
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal
        JLabel lblTitrePrincipal = new JLabel("📋 Historique de la Maintenance", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180));
        lblTitrePrincipal.setForeground(Color.WHITE);
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        
        // Panel principal du contenu
        JPanel panelContenu = createPanelHistorique();
        mainPanel.add(panelContenu, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPanelHistorique() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel des statistiques (comme dans FormRechercheEtats)
        JPanel panelStats = new JPanel(new GridLayout(1, 4, 10, 10));
        panelStats.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        // Cartes statistiques
        lblTotalEntretiens = createStatCard("Total Entretiens", "0", new Color(52, 152, 219));
        lblEntretiensTermines = createStatCard("Terminés", "0", new Color(46, 204, 113));
        lblEntretiensEnCours = createStatCard("En Cours", "0", new Color(230, 126, 34));
        lblCoutTotal = createStatCard("Coût Total", "0 €", new Color(231, 76, 60));
        
        panelStats.add(lblTotalEntretiens);
        panelStats.add(lblEntretiensTermines);
        panelStats.add(lblEntretiensEnCours);
        panelStats.add(lblCoutTotal);
        
        // Panel de filtrage et actions
        JPanel panelFiltres = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFiltres.setBorder(BorderFactory.createTitledBorder("🔍 Filtres"));
        
        panelFiltres.add(new JLabel(" Statut :", IconUtils.createListIcon(new Color(155, 89, 182), 16), JLabel.LEFT));
        cmbFiltreStatut = new JComboBox<>(new String[]{"Tous", "Terminé", "En cours", "Planifié"});
        cmbFiltreStatut.setFont(cmbFiltreStatut.getFont().deriveFont(12f));
        cmbFiltreStatut.addActionListener(e -> appliquerFiltres());
        panelFiltres.add(cmbFiltreStatut);
        
        panelFiltres.add(Box.createHorizontalStrut(20));
        
        panelFiltres.add(new JLabel(" Type :", IconUtils.createRefreshIcon(new Color(230, 126, 34), 16), JLabel.LEFT));
        cmbFiltreType = new JComboBox<>(new String[]{"Tous", "Vidange", "Révision", "Pneus", "Freins", "Autre"});
        cmbFiltreType.setFont(cmbFiltreType.getFont().deriveFont(12f));
        cmbFiltreType.addActionListener(e -> appliquerFiltres());
        panelFiltres.add(cmbFiltreType);
        
        // Bouton actualiser
        JPanel panelActualiser = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelActualiser.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnActualiser = new JButton(" Actualiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnActualiser.setFont(btnActualiser.getFont().deriveFont(Font.BOLD, 12f));
        btnActualiser.setBackground(new Color(52, 152, 219));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.setFocusPainted(false);
        btnActualiser.addActionListener(e -> actualiserHistorique());
        panelActualiser.add(btnActualiser);
        
        // Table de l'historique détaillé
        String[] colonnesHistorique = {"Date", "Véhicule", "Type", "Description", "Statut", "Coût", "Mécanicien"};
        modelHistorique = new DefaultTableModel(colonnesHistorique, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableHistorique = new JTable(modelHistorique);
        tableHistorique.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHistorique.setRowHeight(30);
        tableHistorique.setFont(tableHistorique.getFont().deriveFont(12f));
        tableHistorique.getTableHeader().setBackground(new Color(46, 204, 113));
        tableHistorique.getTableHeader().setForeground(Color.WHITE);
        tableHistorique.getTableHeader().setFont(tableHistorique.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        
        JScrollPane scrollHistorique = new JScrollPane(tableHistorique);
        scrollHistorique.setBorder(BorderFactory.createTitledBorder("📊 Historique Détaillé des Entretiens"));
        
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelStats, BorderLayout.CENTER);
        
        JPanel panelMiddle = new JPanel(new BorderLayout());
        panelMiddle.add(panelFiltres, BorderLayout.WEST);
        panelMiddle.add(panelActualiser, BorderLayout.EAST);
        
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelMiddle, BorderLayout.CENTER);
        panel.add(scrollHistorique, BorderLayout.SOUTH);
        
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
        // Charger tous les entretiens au démarrage
        actualiserHistorique();
    }
    
    private void actualiserHistorique() {
        try {
            modelHistorique.setRowCount(0);
            List<Entretien> entretiens = entretienDAO.listerTousEntretiens();
            
            // Calculer les statistiques
            int total = entretiens.size();
            int termines = (int) entretiens.stream().filter(e -> "termine".equals(e.getStatut())).count();
            int enCours = (int) entretiens.stream().filter(e -> "en_cours".equals(e.getStatut())).count();
            double coutTotal = entretiens.stream().mapToDouble(Entretien::getCout).sum();
            
            // Mettre à jour les cartes
            updateStatCard(lblTotalEntretiens, "Total Entretiens", String.valueOf(total), new Color(52, 152, 219));
            updateStatCard(lblEntretiensTermines, "Terminés", String.valueOf(termines), new Color(46, 204, 113));
            updateStatCard(lblEntretiensEnCours, "En Cours", String.valueOf(enCours), new Color(230, 126, 34));
            updateStatCard(lblCoutTotal, "Coût Total", String.format("%.2f €", coutTotal), new Color(231, 76, 60));
            
            // Remplir la table de l'historique
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Entretien entretien : entretiens) {
                String vehiculeInfo = "ID: " + entretien.getVehiculeId();
                if (entretien.getVehicule() != null) {
                    vehiculeInfo = entretien.getVehicule().getMatricule();
                }
                
                modelHistorique.addRow(new Object[]{
                    entretien.getDateEntretien() != null ? entretien.getDateEntretien().format(formatter) : "N/A",
                    vehiculeInfo,
                    entretien.getTypeEntretien() != null ? entretien.getTypeEntretien() : "N/A",
                    entretien.getCommentaire() != null ? entretien.getCommentaire() : "N/A",
                    entretien.getStatut() != null ? entretien.getStatut() : "N/A",
                    String.format("%.2f €", entretien.getCout()),
                    "N/A" // Mécanicien pas encore dans le modèle
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'actualisation : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void appliquerFiltres() {
        try {
            modelHistorique.setRowCount(0);
            List<Entretien> entretiens = entretienDAO.listerTousEntretiens();
            
            // Appliquer les filtres
            String statutSelectionne = (String) cmbFiltreStatut.getSelectedItem();
            String typeSelectionne = (String) cmbFiltreType.getSelectedItem();
            
            List<Entretien> entretiensFiltres = entretiens.stream()
                .filter(e -> "Tous".equals(statutSelectionne) || e.getStatut().equals(statutSelectionne))
                .filter(e -> "Tous".equals(typeSelectionne) || e.getTypeEntretien().equals(typeSelectionne))
                .collect(Collectors.toList());
            
            // Remplir la table avec les résultats filtrés
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Entretien entretien : entretiensFiltres) {
                String vehiculeInfo = "ID: " + entretien.getVehiculeId();
                if (entretien.getVehicule() != null) {
                    vehiculeInfo = entretien.getVehicule().getMatricule();
                }
                
                modelHistorique.addRow(new Object[]{
                    entretien.getDateEntretien() != null ? entretien.getDateEntretien().format(formatter) : "N/A",
                    vehiculeInfo,
                    entretien.getTypeEntretien() != null ? entretien.getTypeEntretien() : "N/A",
                    entretien.getCommentaire() != null ? entretien.getCommentaire() : "N/A",
                    entretien.getStatut() != null ? entretien.getStatut() : "N/A",
                    String.format("%.2f €", entretien.getCout()),
                    "N/A" // Mécanicien pas encore dans le modèle
                });
            }
            
            JOptionPane.showMessageDialog(this, 
                entretiensFiltres.size() + " entretien(s) trouvé(s)", 
                "Filtrage", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du filtrage : " + e.getMessage(), 
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