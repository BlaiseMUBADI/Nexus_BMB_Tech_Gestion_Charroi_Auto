package nexus_bmb_soft.application.form.other;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import nexus_bmb_soft.database.dao.DashboardDAO;
import nexus_bmb_soft.database.dao.DashboardAdvancedDAO;
import nexus_bmb_soft.database.dao.DashboardAdvancedDAO.DataPoint;
import nexus_bmb_soft.utils.ModernChartComponents.*;
import nexus_bmb_soft.utils.AdvancedChartComponents.*;
import nexus_bmb_soft.utils.DashboardFilters;

/**
 * Tableau de bord moderne avec graphiques interactifs
 * 
 * @author BlaiseMUBADI
 */
public class FormDashboard extends javax.swing.JPanel {
    
    private final DashboardDAO dashboardDAO;
    private final DashboardAdvancedDAO advancedDAO;
    private Timer refreshTimer;
    
    // Composants graphiques modernes
    private ModernPieChart vehicleStatusChart;
    private ModernBarChart maintenanceChart;
    private CircularProgressBar availabilityProgress;
    private CircularProgressBar maintenanceProgress;
    
    // Nouveaux composants graphiques avancés
    private TrendLineChart affectationsTrendChart;
    private TrendLineChart entretiensTrendChart;
    private ModernBarChart topVehiculesChart;
    private RadarChart performanceRadar;
    
    // Cartes KPI dynamiques (références pour mise à jour)
    private ModernKPICard[] kpiCards;
    
    // Composant de filtres
    private DashboardFilters filtersPanel;
    
    public FormDashboard() {
        this.dashboardDAO = new DashboardDAO();
        this.advancedDAO = new DashboardAdvancedDAO();
        initComponents();
        setupModernDashboard();
        startAutoRefresh();
    }
    
    private void setupModernDashboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        
        // Header avec titre stylisé
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel de filtres
        filtersPanel = new DashboardFilters();
        filtersPanel.addFilterChangeListener(criteria -> {
            System.out.println("🔧 Filtres appliqués: " + criteria.toString());
            refreshDashboardWithFilters(criteria);
        });
        
        // Panel central combinant filtres et contenu
        JPanel centralPanel = new JPanel(new BorderLayout());
        centralPanel.add(filtersPanel, BorderLayout.NORTH);
        
        // Contenu principal avec scroll
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centralPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centralPanel, BorderLayout.CENTER);
        
        // Charger les données initiales
        refreshDashboard();
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Titre principal
        JLabel titleLabel = new JLabel("🚗 Tableau de Bord - Gestion Charroi Automobile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // Sous-titre avec date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE dd MMMM yyyy", java.util.Locale.FRENCH);
        JLabel dateLabel = new JLabel("Dernière mise à jour : " + sdf.format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(189, 195, 199));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(dateLabel, BorderLayout.SOUTH);
        
        // Bouton de rafraîchissement
        JButton refreshBtn = new JButton("🔄 Actualiser");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        refreshBtn.setBackground(new Color(41, 128, 185));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        header.add(titlePanel, BorderLayout.CENTER);
        header.add(refreshBtn, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Section KPI moderne
        JPanel kpiSection = createKPISection();
        mainPanel.add(kpiSection, BorderLayout.NORTH);
        
        // Section graphiques
        JPanel chartsSection = createChartsSection();
        mainPanel.add(chartsSection, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createKPISection() {
        JPanel kpiPanel = new JPanel();
        kpiPanel.setLayout(new GridLayout(2, 3, 20, 20));
        kpiPanel.setOpaque(false);
        kpiPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(223, 230, 233), 1),
            "📊 Indicateurs Clés de Performance",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(44, 62, 80)
        ));
        
        // Créer et stocker les références des cartes KPI pour mise à jour dynamique
        kpiCards = new ModernKPICard[6];
        kpiCards[0] = new ModernKPICard("Véhicules", "0", "Total dans le parc", 
                                       new Color(52, 152, 219), "🚗");
        kpiCards[1] = new ModernKPICard("Disponibles", "0", "Prêts à utiliser", 
                                       new Color(46, 204, 113), "✅");
        kpiCards[2] = new ModernKPICard("Maintenance", "0", "En réparation", 
                                       new Color(231, 76, 60), "🔧");
        kpiCards[3] = new ModernKPICard("Affectations", "0", "Actives aujourd'hui", 
                                       new Color(155, 89, 182), "📋");
        kpiCards[4] = new ModernKPICard("Entretiens", "0", "Planifiés ce mois", 
                                       new Color(243, 156, 18), "📅");
        kpiCards[5] = new ModernKPICard("Alertes", "0", "Nécessitent attention", 
                                       new Color(230, 126, 34), "⚠️");
        
        // Ajouter toutes les cartes au panel
        for (ModernKPICard card : kpiCards) {
            kpiPanel.add(card);
        }
        
        return kpiPanel;
    }
    
    private JPanel createChartsSection() {
        JPanel chartsPanel = new JPanel(new GridBagLayout());
        chartsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // LIGNE 1 : Graphiques de base
        // Graphique circulaire - État des véhicules
        vehicleStatusChart = new ModernPieChart("État du Parc Automobile");
        JPanel pieChartPanel = createChartPanel(vehicleStatusChart, "Répartition des véhicules par statut");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.33; gbc.weighty = 0.4;
        chartsPanel.add(pieChartPanel, gbc);
        
        // Barres de progression circulaires
        JPanel progressPanel = createProgressPanel();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.33; gbc.weighty = 0.4;
        chartsPanel.add(progressPanel, gbc);
        
        // Radar de performance
        performanceRadar = new RadarChart("Performance Globale");
        JPanel radarPanel = createChartPanel(performanceRadar, "Indicateurs de performance");
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.33; gbc.weighty = 0.4;
        chartsPanel.add(radarPanel, gbc);
        
        // LIGNE 2 : Graphiques temporels
        // Tendance des affectations (7 derniers jours)
        affectationsTrendChart = new TrendLineChart("Tendance Affectations", "Nombre");
        affectationsTrendChart.setColors(new Color(155, 89, 182), new Color(155, 89, 182, 30));
        JPanel affectationsTrendPanel = createChartPanel(affectationsTrendChart, "Évolution sur 7 jours");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5; gbc.weighty = 0.3;
        chartsPanel.add(affectationsTrendPanel, gbc);
        
        // Tendance des entretiens (30 derniers jours)
        entretiensTrendChart = new TrendLineChart("Tendance Entretiens", "Nombre");
        entretiensTrendChart.setColors(new Color(243, 156, 18), new Color(243, 156, 18, 30));
        JPanel entretiensTrendPanel = createChartPanel(entretiensTrendChart, "Évolution sur 30 jours");
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5; gbc.weighty = 0.3;
        chartsPanel.add(entretiensTrendPanel, gbc);
        
        // LIGNE 3 : Analyses détaillées
        // Top 5 véhicules les plus utilisés
        topVehiculesChart = new ModernBarChart("Top 5 Véhicules");
        JPanel topVehiculesPanel = createChartPanel(topVehiculesChart, "Véhicules les plus sollicités");
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5; gbc.weighty = 0.3;
        chartsPanel.add(topVehiculesPanel, gbc);
        
        // Graphique en barres - Maintenance par type (amélioré)
        maintenanceChart = new ModernBarChart("Répartition Entretiens");
        JPanel barChartPanel = createChartPanel(maintenanceChart, "Types d'interventions");
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.5; gbc.weighty = 0.3;
        chartsPanel.add(barChartPanel, gbc);
        
        return chartsPanel;
    }
    
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(223, 230, 233), 1),
            "📈 Taux d'Utilisation",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(44, 62, 80)
        ));
        
        availabilityProgress = new CircularProgressBar("Disponibilité", new Color(46, 204, 113));
        maintenanceProgress = new CircularProgressBar("Maintenance", new Color(231, 76, 60));
        
        panel.add(availabilityProgress);
        panel.add(maintenanceProgress);
        
        return panel;
    }
    
    private JPanel createChartPanel(JComponent chart, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 230, 233), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        panel.add(chart, BorderLayout.CENTER);
        
        if (description != null) {
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            descLabel.setForeground(new Color(134, 142, 150));
            descLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(descLabel, BorderLayout.SOUTH);
        }
        
        return panel;
    }
    
    private void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🔄 Actualisation du dashboard...");
                
                // === 1. DONNÉES DE BASE ===
                int totalVehicules = dashboardDAO.getTotalVehicules();
                int disponibles = dashboardDAO.getVehiculesDisponibles();
                int maintenance = dashboardDAO.getVehiculesMaintenance();
                int affectations = dashboardDAO.getAffectationsActives();
                int entretiens = dashboardDAO.getEntretiensPlanifies();
                int alertes = dashboardDAO.getAlertesActives();
                
                System.out.println("📊 KPI: " + totalVehicules + " véhicules | " + disponibles + " dispo | " + 
                                 maintenance + " maintenance | " + affectations + " affectations | " + 
                                 entretiens + " entretiens | " + alertes + " alertes");
                
                // === 2. MISE À JOUR DES CARTES KPI ===
                if (kpiCards != null) {
                    kpiCards[0].updateValue(String.valueOf(totalVehicules));
                    kpiCards[1].updateValue(String.valueOf(disponibles));
                    kpiCards[2].updateValue(String.valueOf(maintenance));
                    kpiCards[3].updateValue(String.valueOf(affectations));
                    kpiCards[4].updateValue(String.valueOf(entretiens));
                    kpiCards[5].updateValue(String.valueOf(alertes));
                }
                
                // === 3. GRAPHIQUE CIRCULAIRE - ÉTAT VÉHICULES ===
                if (vehicleStatusChart != null) {
                    vehicleStatusChart.clearSlices();
                    if (disponibles > 0) {
                        vehicleStatusChart.addSlice("Disponibles", disponibles, new Color(46, 204, 113));
                    }
                    if (maintenance > 0) {
                        vehicleStatusChart.addSlice("En maintenance", maintenance, new Color(231, 76, 60));
                    }
                    int enMission = dashboardDAO.getVehiculesEnMission();
                    if (enMission > 0) {
                        vehicleStatusChart.addSlice("En mission", enMission, new Color(52, 152, 219));
                    }
                    System.out.println("🥧 Graphique circulaire mis à jour");
                }
                
                // === 4. BARRES DE PROGRESSION CIRCULAIRES ===
                if (availabilityProgress != null && maintenanceProgress != null && totalVehicules > 0) {
                    double tauxDisponibilite = dashboardDAO.getTauxDisponibilite();
                    double tauxMaintenance = dashboardDAO.getTauxMaintenance();
                    
                    availabilityProgress.setProgress(tauxDisponibilite);
                    maintenanceProgress.setProgress(tauxMaintenance);
                    System.out.println("📊 Taux: " + String.format("%.1f", tauxDisponibilite) + "% dispo, " + 
                                     String.format("%.1f", tauxMaintenance) + "% maintenance");
                }
                
                // === 5. RADAR DE PERFORMANCE ===
                if (performanceRadar != null) {
                    List<DataPoint> metrics = new java.util.ArrayList<>();
                    metrics.add(new DataPoint("Disponibilité", 0, dashboardDAO.getTauxDisponibilite()));
                    metrics.add(new DataPoint("Utilisation", 0, dashboardDAO.getTauxUtilisation()));
                    metrics.add(new DataPoint("Maintenance", 0, 100 - dashboardDAO.getTauxMaintenance()));
                    metrics.add(new DataPoint("Affectations", 0, Math.min(100, affectations * 10)));
                    metrics.add(new DataPoint("Entretiens", 0, Math.min(100, entretiens * 5)));
                    performanceRadar.setMetrics(metrics);
                    System.out.println("🎯 Radar de performance mis à jour");
                }
                
                // === 6. TENDANCES TEMPORELLES ===
                // Tendance affectations (7 jours)
                if (affectationsTrendChart != null) {
                    List<DataPoint> affectationsTrend = advancedDAO.getAffectationsTrend7Days();
                    affectationsTrendChart.setData(affectationsTrend);
                    System.out.println("📈 Tendance affectations: " + affectationsTrend.size() + " points");
                }
                
                // Tendance entretiens (30 jours)
                if (entretiensTrendChart != null) {
                    List<DataPoint> entretiensTrend = advancedDAO.getEntretiensTrend30Days();
                    entretiensTrendChart.setData(entretiensTrend);
                    System.out.println("📈 Tendance entretiens: " + entretiensTrend.size() + " points");
                }
                
                // === 7. TOP 5 VÉHICULES ===
                if (topVehiculesChart != null) {
                    topVehiculesChart.clearBars();
                    List<DataPoint> top5 = advancedDAO.getTop5VehiculesUtilises();
                    Color[] colors = {
                        new Color(52, 152, 219), new Color(46, 204, 113), new Color(243, 156, 18),
                        new Color(231, 76, 60), new Color(155, 89, 182)
                    };
                    
                    for (int i = 0; i < Math.min(5, top5.size()); i++) {
                        DataPoint vehicle = top5.get(i);
                        topVehiculesChart.addBar(vehicle.label, vehicle.value, colors[i]);
                    }
                    System.out.println("🚗 Top 5 véhicules: " + top5.size() + " entrées");
                }
                
                // === 8. RÉPARTITION ENTRETIENS ===
                if (maintenanceChart != null) {
                    maintenanceChart.clearBars();
                    List<DataPoint> entretiensParType = advancedDAO.getEntretiensParType();
                    Color[] maintenanceColors = {
                        new Color(52, 152, 219), new Color(231, 76, 60), new Color(243, 156, 18),
                        new Color(46, 204, 113), new Color(155, 89, 182)
                    };
                    
                    for (int i = 0; i < Math.min(5, entretiensParType.size()); i++) {
                        DataPoint entretien = entretiensParType.get(i);
                        maintenanceChart.addBar(entretien.label, entretien.value, maintenanceColors[i]);
                    }
                    System.out.println("🔧 Types d'entretiens: " + entretiensParType.size() + " types");
                }
                
                System.out.println("✅ Dashboard actualisé avec succès !");
                
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'actualisation: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la mise à jour des données : " + e.getMessage(), 
                    "Erreur Dashboard", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Actualise le dashboard avec des critères de filtrage
     */
    private void refreshDashboardWithFilters(DashboardFilters.FilterCriteria criteria) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🔍 Actualisation avec filtres: " + criteria.toString());
                
                // Pour l'instant, appeler la méthode standard
                // TODO: Implémenter la logique de filtrage dans les DAOs
                refreshDashboard();
                
                // Afficher un résumé des filtres appliqués
                if (filtersPanel != null) {
                    String summary = filtersPanel.getFilterSummary();
                    System.out.println("📊 Filtres actifs: " + summary);
                }
                
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'actualisation avec filtres: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'application des filtres : " + e.getMessage(), 
                    "Erreur Filtres", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void startAutoRefresh() {
        refreshTimer = new Timer(30000, e -> refreshDashboard()); // Refresh toutes les 30 secondes
        refreshTimer.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        // Composants générés automatiquement remplacés par le système moderne
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Variables remplacées par les composants modernes
    // End of variables declaration//GEN-END:variables
}
