package nexus_bmb_soft.application.form.other;

import javax.swing.*;
import java.awt.*;

/**
 * Formulaire pour la gestion de l'entretien et maintenance
 * 
 * @author BlaiseMUBADI
 */
public class FormEntretien extends JPanel {
    
    private JTable tableEntretien;
    private JButton btnProgrammer;
    private JButton btnAlertes;
    private JButton btnHistorique;
    private JComboBox<String> cbTypeEntretien;
    
    public FormEntretien() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel supérieur
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitre = new JLabel("Entretien & Maintenance");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        btnProgrammer = new JButton("Programmer Entretien");
        btnAlertes = new JButton("Voir Alertes");
        btnHistorique = new JButton("Historique Complet");
        
        panelTop.add(lblTitre);
        panelTop.add(Box.createHorizontalStrut(30));
        panelTop.add(btnProgrammer);
        panelTop.add(btnAlertes);
        panelTop.add(btnHistorique);
        
        // Panel de filtres
        JPanel panelFiltre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblType = new JLabel("Type d'entretien:");
        String[] types = {"Tous", "Vidange", "Freins", "Pneus", "Révision", "Réparation"};
        cbTypeEntretien = new JComboBox<>(types);
        panelFiltre.add(lblType);
        panelFiltre.add(cbTypeEntretien);
        
        // Table d'entretien
        String[] colonnes = {"Véhicule", "Date", "Type", "Commentaire", "Priorité", "Statut"};
        Object[][] donnees = {
            {"ABC123", "2025-09-10", "Vidange", "Vidange moteur complète", "Normale", "Terminé"},
            {"DEF456", "2025-07-15", "Pneus", "Remplacement pneus arrière", "Normale", "Terminé"},
            {"GHI789", "2025-10-01", "Freins", "Révision plaquettes", "Haute", "Programmé"}
        };
        
        tableEntretien = new JTable(donnees, colonnes);
        tableEntretien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableEntretien);
        
        // Panel principal
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelTop, BorderLayout.NORTH);
        panelMain.add(panelFiltre, BorderLayout.CENTER);
        
        add(panelMain, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel d'alertes importantes
        JPanel panelAlertes = new JPanel(new BorderLayout());
        panelAlertes.setBorder(BorderFactory.createTitledBorder("Alertes & Échéances"));
        
        JTextArea txtAlertes = new JTextArea(3, 50);
        txtAlertes.setEditable(false);
        txtAlertes.setBackground(new Color(255, 249, 196));
        txtAlertes.setText("⚠️ ABC123 - Assurance expire dans 21 jours (15/10/2025)\n" +
                          "⚠️ DEF456 - Vidange nécessaire dans 15 jours\n" +
                          "⚠️ GHI789 - Révision freins programmée demain");
        
        panelAlertes.add(new JScrollPane(txtAlertes), BorderLayout.CENTER);
        
        add(panelAlertes, BorderLayout.SOUTH);
    }
    
    private void setupLayout() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Styling des boutons
        btnProgrammer.setBackground(new Color(76, 175, 80));
        btnProgrammer.setForeground(Color.WHITE);
        btnAlertes.setBackground(new Color(255, 193, 7));
        btnAlertes.setForeground(Color.BLACK);
        btnHistorique.setBackground(new Color(96, 125, 139));
        btnHistorique.setForeground(Color.WHITE);
    }
}