package nexus_bmb_soft.application.form.other;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Formulaire pour la gestion des affectations
 * 
 * @author BlaiseMUBADI
 */
public class FormAffectations extends JPanel {
    
    private JTable tableAffectations;
    private JButton btnNouvelleAffectation;
    private JButton btnTerminer;
    private JButton btnHistorique;
    private JComboBox<String> cbFiltreStatut;
    
    public FormAffectations() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel supérieur
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitre = new JLabel("Gestion des Affectations");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        btnNouvelleAffectation = new JButton("Nouvelle Affectation");
        btnTerminer = new JButton("Terminer Affectation");
        btnHistorique = new JButton("Historique Complet");
        
        panelTop.add(lblTitre);
        panelTop.add(Box.createHorizontalStrut(30));
        panelTop.add(btnNouvelleAffectation);
        panelTop.add(btnTerminer);
        panelTop.add(btnHistorique);
        
        // Panel de filtres
        JPanel panelFiltre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblFiltre = new JLabel("Filtrer par statut:");
        String[] statuts = {"Toutes", "En cours", "Programmées", "Terminées"};
        cbFiltreStatut = new JComboBox<>(statuts);
        panelFiltre.add(lblFiltre);
        panelFiltre.add(cbFiltreStatut);
        
        // Table des affectations
        String[] colonnes = {"Véhicule", "Conducteur", "Date Début", "Date Fin", "Motif", "Statut"};
        Object[][] donnees = {
            {"ABC123", "Major Kabila", "2025-09-20", "2025-09-22", "Transport matériel médical", "Terminée"},
            {"DEF456", "Major Kabila", "2025-08-10", "2025-08-12", "Mission reconnaissance", "Terminée"},
            {"GHI789", "Major Kabila", "2025-09-25", "", "Déplacement zone logistique", "En cours"}
        };
        
        tableAffectations = new JTable(donnees, colonnes);
        tableAffectations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableAffectations);
        
        // Panel principal
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelTop, BorderLayout.NORTH);
        panelMain.add(panelFiltre, BorderLayout.CENTER);
        
        add(panelMain, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel d'informations
        JPanel panelInfo = new JPanel(new FlowLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Résumé"));
        panelInfo.add(new JLabel("Affectations actives: 1"));
        panelInfo.add(new JLabel("Programmées: 0"));
        panelInfo.add(new JLabel("Terminées cette semaine: 2"));
        
        add(panelInfo, BorderLayout.SOUTH);
    }
    
    private void setupLayout() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Styling des boutons
        btnNouvelleAffectation.setBackground(new Color(76, 175, 80));
        btnNouvelleAffectation.setForeground(Color.WHITE);
        btnTerminer.setBackground(new Color(255, 152, 0));
        btnTerminer.setForeground(Color.WHITE);
        btnHistorique.setBackground(new Color(96, 125, 139));
        btnHistorique.setForeground(Color.WHITE);
    }
}