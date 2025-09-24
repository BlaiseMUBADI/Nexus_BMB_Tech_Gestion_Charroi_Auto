package nexus_bmb_soft.application.form.other;

import javax.swing.*;
import java.awt.*;
import nexus_bmb_soft.models.Vehicule;

/**
 * Formulaire pour la gestion du parc automobile
 * 
 * @author BlaiseMUBADI
 */
public class FormParcAutomobile extends JPanel {
    
    private JTable tableVehicules;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnRafraichir;
    private JTextField txtRecherche;
    
    public FormParcAutomobile() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel supérieur avec titre et boutons d'action
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitre = new JLabel("Gestion du Parc Automobile");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        btnAjouter = new JButton("Ajouter Véhicule");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnRafraichir = new JButton("Actualiser");
        
        panelTop.add(lblTitre);
        panelTop.add(Box.createHorizontalStrut(50));
        panelTop.add(btnAjouter);
        panelTop.add(btnModifier);
        panelTop.add(btnSupprimer);
        panelTop.add(btnRafraichir);
        
        // Panel de recherche
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblRecherche = new JLabel("Rechercher:");
        txtRecherche = new JTextField(20);
        panelRecherche.add(lblRecherche);
        panelRecherche.add(txtRecherche);
        
        // Table des véhicules
        String[] colonnes = {"Matricule", "Marque", "Type", "Année", "Statut", "Assurance", "Vidange"};
        Object[][] donnees = {
            {"ABC123", "Toyota", "Pickup", "2018", "Disponible", "2025-10-15", "2025-09-10"},
            {"DEF456", "Hyundai", "SUV", "2020", "Affecté", "2025-08-20", "2025-07-15"},
            {"GHI789", "Ford", "Camionnette", "2016", "Disponible", "2025-12-01", "2025-10-01"}
        };
        
        tableVehicules = new JTable(donnees, colonnes);
        tableVehicules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableVehicules);
        
        // Panel principal
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelTop, BorderLayout.NORTH);
        panelMain.add(panelRecherche, BorderLayout.CENTER);
        
        add(panelMain, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de statistiques en bas
        JPanel panelStats = new JPanel(new FlowLayout());
        panelStats.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        panelStats.add(new JLabel("Total véhicules: 3"));
        panelStats.add(new JLabel("Disponibles: 2"));
        panelStats.add(new JLabel("Affectés: 1"));
        panelStats.add(new JLabel("En maintenance: 0"));
        
        add(panelStats, BorderLayout.SOUTH);
    }
    
    private void setupLayout() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Styling des boutons
        btnAjouter.setBackground(new Color(76, 175, 80));
        btnAjouter.setForeground(Color.WHITE);
        btnModifier.setBackground(new Color(33, 150, 243));
        btnModifier.setForeground(Color.WHITE);
        btnSupprimer.setBackground(new Color(244, 67, 54));
        btnSupprimer.setForeground(Color.WHITE);
        btnRafraichir.setBackground(new Color(156, 39, 176));
        btnRafraichir.setForeground(Color.WHITE);
    }
}