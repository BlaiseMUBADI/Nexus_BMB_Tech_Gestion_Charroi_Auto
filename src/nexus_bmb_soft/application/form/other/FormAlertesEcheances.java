package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.EntretienDAO;
import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Entretien;
import nexus_bmb_soft.models.Vehicule;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire pour les alertes d'échéances d'entretien
 * Style basé sur FormRechercheEtats - onglet "États & Disponibilité"
 * 
 * @author BlaiseMUBADI
 */
public class FormAlertesEcheances extends JPanel {
    
    private EntretienDAO entretienDAO;
    private VehiculeDAO vehiculeDAO;
    
    // Composants de l'interface
    private JTable tableAlertes;
    private DefaultTableModel modelAlertes;
    private JLabel lblTotalAlertes;
    private JLabel lblAlertesUrgentes;
    private JLabel lblAlertesImportantes;
    private JLabel lblAlertesInfo;
    private JComboBox<String> cmbNiveauUrgence;
    private JButton btnActualiser;
    private JButton btnProgrammerEntretien;
    
    public FormAlertesEcheances() {
        entretienDAO = new EntretienDAO();
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
        JLabel lblTitrePrincipal = new JLabel("⚠️ Alertes d'Échéances", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(231, 76, 60));
        lblTitrePrincipal.setForeground(Color.WHITE);
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        
        // Panel principal du contenu
        JPanel panelContenu = createPanelAlertes();
        mainPanel.add(panelContenu, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPanelAlertes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel des statistiques (comme dans FormRechercheEtats)
        JPanel panelStats = new JPanel(new GridLayout(1, 4, 10, 10));
        panelStats.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        // Cartes statistiques d'alertes
        lblTotalAlertes = createStatCard("Total Alertes", "0", new Color(52, 152, 219));
        lblAlertesUrgentes = createStatCard("Urgentes", "0", new Color(231, 76, 60));
        lblAlertesImportantes = createStatCard("Importantes", "0", new Color(230, 126, 34));
        lblAlertesInfo = createStatCard("Informatives", "0", new Color(46, 204, 113));
        
        panelStats.add(lblTotalAlertes);
        panelStats.add(lblAlertesUrgentes);
        panelStats.add(lblAlertesImportantes);
        panelStats.add(lblAlertesInfo);
        
        // Panel de filtrage et actions
        JPanel panelFiltres = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFiltres.setBorder(BorderFactory.createTitledBorder("🔍 Filtres"));
        
        panelFiltres.add(new JLabel(" Niveau :", IconUtils.createCalendarIcon(new Color(231, 76, 60), 16), JLabel.LEFT));
        cmbNiveauUrgence = new JComboBox<>(new String[]{"Toutes", "Urgente", "Importante", "Informative"});
        cmbNiveauUrgence.setFont(cmbNiveauUrgence.getFont().deriveFont(12f));
        cmbNiveauUrgence.addActionListener(e -> appliquerFiltres());
        panelFiltres.add(cmbNiveauUrgence);
        
        // Boutons d'action
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelActions.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnActualiser = new JButton(" Actualiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnActualiser.setFont(btnActualiser.getFont().deriveFont(Font.BOLD, 12f));
        btnActualiser.setBackground(new Color(52, 152, 219));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.setFocusPainted(false);
        btnActualiser.addActionListener(e -> actualiserAlertes());
        
        btnProgrammerEntretien = new JButton(" Programmer", IconUtils.createCalendarIcon(Color.WHITE, 16));
        btnProgrammerEntretien.setFont(btnProgrammerEntretien.getFont().deriveFont(Font.BOLD, 12f));
        btnProgrammerEntretien.setBackground(new Color(46, 204, 113));
        btnProgrammerEntretien.setForeground(Color.WHITE);
        btnProgrammerEntretien.setFocusPainted(false);
        btnProgrammerEntretien.addActionListener(e -> programmerEntretien());
        
        panelActions.add(btnActualiser);
        panelActions.add(btnProgrammerEntretien);
        
        // Table des alertes détaillées
        String[] colonnesAlertes = {"Véhicule", "Type Alerte", "Échéance", "Jours Restants", "Niveau", "Action Recommandée"};
        modelAlertes = new DefaultTableModel(colonnesAlertes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableAlertes = new JTable(modelAlertes);
        tableAlertes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAlertes.setRowHeight(30);
        tableAlertes.setFont(tableAlertes.getFont().deriveFont(12f));
        tableAlertes.getTableHeader().setBackground(new Color(231, 76, 60));
        tableAlertes.getTableHeader().setForeground(Color.WHITE);
        tableAlertes.getTableHeader().setFont(tableAlertes.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        
        JScrollPane scrollAlertes = new JScrollPane(tableAlertes);
        scrollAlertes.setBorder(BorderFactory.createTitledBorder("⚠️ Alertes Détaillées"));
        
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelStats, BorderLayout.CENTER);
        
        JPanel panelMiddle = new JPanel(new BorderLayout());
        panelMiddle.add(panelFiltres, BorderLayout.WEST);
        panelMiddle.add(panelActions, BorderLayout.EAST);
        
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelMiddle, BorderLayout.CENTER);
        panel.add(scrollAlertes, BorderLayout.SOUTH);
        
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
        // Charger toutes les alertes au démarrage
        actualiserAlertes();
    }
    
    /**
     * Classe interne pour représenter une alerte
     */
    private static class Alerte {
        String vehicule;
        String typeAlerte;
        LocalDate echeance;
        long joursRestants;
        String niveau;
        String actionRecommandee;
        
        public Alerte(String vehicule, String typeAlerte, LocalDate echeance, 
                     long joursRestants, String niveau, String actionRecommandee) {
            this.vehicule = vehicule;
            this.typeAlerte = typeAlerte;
            this.echeance = echeance;
            this.joursRestants = joursRestants;
            this.niveau = niveau;
            this.actionRecommandee = actionRecommandee;
        }
    }
    
    private void actualiserAlertes() {
        try {
            modelAlertes.setRowCount(0);
            List<Alerte> alertes = genererAlertes();
            
            // Calculer les statistiques
            int total = alertes.size();
            int urgentes = (int) alertes.stream().filter(a -> "Urgente".equals(a.niveau)).count();
            int importantes = (int) alertes.stream().filter(a -> "Importante".equals(a.niveau)).count();
            int informatives = (int) alertes.stream().filter(a -> "Informative".equals(a.niveau)).count();
            
            // Mettre à jour les cartes
            updateStatCard(lblTotalAlertes, "Total Alertes", String.valueOf(total), new Color(52, 152, 219));
            updateStatCard(lblAlertesUrgentes, "Urgentes", String.valueOf(urgentes), new Color(231, 76, 60));
            updateStatCard(lblAlertesImportantes, "Importantes", String.valueOf(importantes), new Color(230, 126, 34));
            updateStatCard(lblAlertesInfo, "Informatives", String.valueOf(informatives), new Color(46, 204, 113));
            
            // Remplir la table des alertes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Alerte alerte : alertes) {
                modelAlertes.addRow(new Object[]{
                    alerte.vehicule,
                    alerte.typeAlerte,
                    alerte.echeance != null ? alerte.echeance.format(formatter) : "N/A",
                    alerte.joursRestants + " jours",
                    alerte.niveau,
                    alerte.actionRecommandee
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'actualisation : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<Alerte> genererAlertes() {
        List<Alerte> alertes = new ArrayList<>();
        LocalDate aujourdhui = LocalDate.now();
        
        try {
            // Obtenir tous les véhicules
            List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
            
            for (Vehicule vehicule : vehicules) {
                String vehiculeInfo = vehicule.getMatricule() != null ? vehicule.getMatricule() : "ID: " + vehicule.getId();
                
                // Vérifier l'assurance
                if (vehicule.getDateAssurance() != null) {
                    long joursAssurance = ChronoUnit.DAYS.between(aujourdhui, vehicule.getDateAssurance());
                    if (joursAssurance <= 30) {
                        String niveau = joursAssurance <= 7 ? "Urgente" : 
                                       joursAssurance <= 15 ? "Importante" : "Informative";
                        alertes.add(new Alerte(vehiculeInfo, "Assurance", vehicule.getDateAssurance(), 
                                             joursAssurance, niveau, "Renouveler l'assurance"));
                    }
                }
                
                // Vérifier la visite technique
                if (vehicule.getDateVisiteTechnique() != null) {
                    long joursVisite = ChronoUnit.DAYS.between(aujourdhui, vehicule.getDateVisiteTechnique());
                    if (joursVisite <= 60) {
                        String niveau = joursVisite <= 14 ? "Urgente" : 
                                       joursVisite <= 30 ? "Importante" : "Informative";
                        alertes.add(new Alerte(vehiculeInfo, "Visite Technique", vehicule.getDateVisiteTechnique(), 
                                             joursVisite, niveau, "Programmer visite technique"));
                    }
                }
                
                // Vérifier la vidange
                if (vehicule.getDateVidange() != null) {
                    long joursVidange = ChronoUnit.DAYS.between(aujourdhui, vehicule.getDateVidange());
                    if (joursVidange <= 45) {
                        String niveau = joursVidange <= 10 ? "Urgente" : 
                                       joursVidange <= 20 ? "Importante" : "Informative";
                        alertes.add(new Alerte(vehiculeInfo, "Vidange", vehicule.getDateVidange(), 
                                             joursVidange, niveau, "Programmer vidange"));
                    }
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la génération des alertes : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        return alertes;
    }
    
    private void appliquerFiltres() {
        try {
            modelAlertes.setRowCount(0);
            List<Alerte> alertes = genererAlertes();
            
            // Appliquer le filtre
            String niveauSelectionne = (String) cmbNiveauUrgence.getSelectedItem();
            
            List<Alerte> alertesFiltrees = alertes.stream()
                .filter(a -> "Toutes".equals(niveauSelectionne) || a.niveau.equals(niveauSelectionne))
                .collect(java.util.stream.Collectors.toList());
            
            // Remplir la table avec les résultats filtrés
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Alerte alerte : alertesFiltrees) {
                modelAlertes.addRow(new Object[]{
                    alerte.vehicule,
                    alerte.typeAlerte,
                    alerte.echeance != null ? alerte.echeance.format(formatter) : "N/A",
                    alerte.joursRestants + " jours",
                    alerte.niveau,
                    alerte.actionRecommandee
                });
            }
            
            JOptionPane.showMessageDialog(this, 
                alertesFiltrees.size() + " alerte(s) trouvée(s)", 
                "Filtrage", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du filtrage : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void programmerEntretien() {
        int selectedRow = tableAlertes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une alerte pour programmer un entretien.", 
                "Aucune sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String vehicule = (String) tableAlertes.getValueAt(selectedRow, 0);
        String typeAlerte = (String) tableAlertes.getValueAt(selectedRow, 1);
        
        JOptionPane.showMessageDialog(this, 
            "Fonctionnalité à implémenter :\nProgrammer entretien pour " + vehicule + 
            "\nType : " + typeAlerte, 
            "Programmer Entretien", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatCard(JLabel card, String titre, String valeur, Color couleur) {
        card.setText("<html><center><b style='font-size: 18px; color: " + 
            String.format("#%06x", couleur.getRGB() & 0xFFFFFF) + ";'>" + valeur + 
            "</b><br><span style='font-size: 11px; color: #7f8c8d;'>" + titre + "</span></center></html>");
    }
}