package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Vehicule;
import nexus_bmb_soft.application.Application;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulaire pour afficher et gérer la liste des véhicules
 * 
 * @author BlaiseMUBADI
 */
public class FormListeVehicules extends JPanel {
    
    private JTable tableVehicules;
    private DefaultTableModel modelTable;
    private JTextField txtRecherche;
    private JComboBox<String> cmbFiltre;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnActualiser;
    private JLabel lblTotal;
    
    private VehiculeDAO vehiculeDAO;
    
    // Colonnes du tableau
    private final String[] COLONNES = {
        "ID", "Matricule", "Marque", "Type", "Année", 
        "Disponible", "Assurance", "Vidange", "Visite Technique"
    };
    
    public FormListeVehicules() {
        vehiculeDAO = new VehiculeDAO();
        init();
        chargerVehicules();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Panel titre et recherche
        JPanel topPanel = createTopPanel();
        
        // Panel tableau
        JPanel centerPanel = createTablePanel();
        
        // Panel boutons et statistiques
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        // Titre
        JLabel lblTitre = new JLabel("🚗 Liste des Véhicules");
        lblTitre.setFont(lblTitre.getFont().deriveFont(Font.BOLD, 18f));
        
        // Panel de recherche et filtres
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Filtre par statut
        panelRecherche.add(new JLabel("Filtre :"));
        cmbFiltre = new JComboBox<>(new String[]{
            "Tous", "Disponibles", "Non Disponibles", 
            "Assurance Expirée", "Maintenance Due"
        });
        cmbFiltre.addActionListener(e -> appliquerFiltre());
        panelRecherche.add(cmbFiltre);
        
        // Recherche
        panelRecherche.add(Box.createHorizontalStrut(15));
        panelRecherche.add(new JLabel("🔍 Recherche :"));
        txtRecherche = new JTextField(15);
        txtRecherche.addActionListener(e -> appliquerFiltre());
        // Recherche en temps réel
        txtRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltre(); }
        });
        panelRecherche.add(txtRecherche);
        
        panel.add(lblTitre, BorderLayout.WEST);
        panel.add(panelRecherche, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        // Modèle de table
        modelTable = new DefaultTableModel(COLONNES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table en lecture seule
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) return Boolean.class; // Colonne Disponible
                return String.class;
            }
        };
        
        // Table
        tableVehicules = new JTable(modelTable);
        tableVehicules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableVehicules.setRowHeight(25);
        
        // Configuration des colonnes
        configureColumns();
        
        // Double-clic pour modifier
        tableVehicules.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modifierVehiculeSelectionne();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableVehicules);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void configureColumns() {
        TableColumnModel columnModel = tableVehicules.getColumnModel();
        
        // ID (caché)
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setWidth(0);
        
        // Matricule
        columnModel.getColumn(1).setPreferredWidth(100);
        
        // Marque
        columnModel.getColumn(2).setPreferredWidth(120);
        
        // Type
        columnModel.getColumn(3).setPreferredWidth(100);
        
        // Année
        columnModel.getColumn(4).setPreferredWidth(80);
        
        // Disponible
        columnModel.getColumn(5).setPreferredWidth(80);
        
        // Dates
        columnModel.getColumn(6).setPreferredWidth(100); // Assurance
        columnModel.getColumn(7).setPreferredWidth(100); // Vidange
        columnModel.getColumn(8).setPreferredWidth(120); // Visite
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Panel statistiques (gauche)
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotal = new JLabel("Total : 0 véhicule(s)");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD));
        panelStats.add(lblTotal);
        
        // Panel boutons (droite)
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        btnActualiser = new JButton("🔄 Actualiser");
        btnActualiser.addActionListener(e -> chargerVehicules());
        
        btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.setBackground(Color.BLUE);
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.addActionListener(e -> ajouterVehicule());
        
        btnModifier = new JButton("✏️ Modifier");
        btnModifier.addActionListener(e -> modifierVehiculeSelectionne());
        btnModifier.setEnabled(false);
        
        btnSupprimer = new JButton("🗑️ Supprimer");
        btnSupprimer.setBackground(Color.RED);
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.addActionListener(e -> supprimerVehiculeSelectionne());
        btnSupprimer.setEnabled(false);
        
        // Activer/désactiver les boutons selon la sélection
        tableVehicules.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = tableVehicules.getSelectedRow() != -1;
            btnModifier.setEnabled(hasSelection);
            btnSupprimer.setEnabled(hasSelection);
        });
        
        panelBoutons.add(btnActualiser);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        
        panel.add(panelStats, BorderLayout.WEST);
        panel.add(panelBoutons, BorderLayout.EAST);
        
        return panel;
    }
    
    private void chargerVehicules() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
                afficherVehicules(vehicules);
                mettreAJourStatistiques(vehicules.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors du chargement : " + e.getMessage(),
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void afficherVehicules(List<Vehicule> vehicules) {
        modelTable.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Vehicule v : vehicules) {
            Object[] row = {
                v.getId(),
                v.getMatricule(),
                v.getMarque(),
                v.getType() != null ? v.getType() : "",
                v.getAnnee() != null ? v.getAnnee().toString() : "",
                v.isDisponible() ? "✅" : "❌",
                v.getDateAssurance() != null ? v.getDateAssurance().format(dateFormatter) : "",
                v.getDateVidange() != null ? v.getDateVidange().format(dateFormatter) : "",
                v.getDateVisiteTechnique() != null ? v.getDateVisiteTechnique().format(dateFormatter) : ""
            };
            modelTable.addRow(row);
        }
    }
    
    private void mettreAJourStatistiques(int total) {
        lblTotal.setText("Total : " + total + " véhicule(s)");
    }
    
    private void appliquerFiltre() {
        String recherche = txtRecherche.getText().toLowerCase().trim();
        String filtre = (String) cmbFiltre.getSelectedItem();
        
        try {
            List<Vehicule> tousVehicules = vehiculeDAO.obtenirTousVehicules();
            List<Vehicule> vehiculesFiltres = tousVehicules.stream()
                .filter(v -> {
                    // Filtre par recherche textuelle
                    if (!recherche.isEmpty()) {
                        return v.getMatricule().toLowerCase().contains(recherche) ||
                               v.getMarque().toLowerCase().contains(recherche) ||
                               (v.getType() != null && v.getType().toLowerCase().contains(recherche));
                    }
                    return true;
                })
                .filter(v -> {
                    // Filtre par statut
                    switch (filtre) {
                        case "Disponibles":
                            return v.isDisponible();
                        case "Non Disponibles":
                            return !v.isDisponible();
                        case "Assurance Expirée":
                            return v.getDateAssurance() != null && 
                                   v.getDateAssurance().isBefore(java.time.LocalDate.now());
                        case "Maintenance Due":
                            return v.getDateVidange() != null && 
                                   v.getDateVidange().isBefore(java.time.LocalDate.now());
                        default:
                            return true; // "Tous"
                    }
                })
                .collect(java.util.stream.Collectors.toList());
                
            afficherVehicules(vehiculesFiltres);
            mettreAJourStatistiques(vehiculesFiltres.size());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Erreur lors du filtrage : " + e.getMessage(),
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ajouterVehicule() {
        Application.showForm(new FormAjoutVehicule());
    }
    
    private void modifierVehiculeSelectionne() {
        int selectedRow = tableVehicules.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Veuillez sélectionner un véhicule à modifier.",
                "Sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vehiculeId = (Integer) modelTable.getValueAt(selectedRow, 0);
        
        // Pour l'instant, afficher un message (plus tard on créera FormModifierVehicule)
        JOptionPane.showMessageDialog(this, 
            "✏️ Modification du véhicule ID " + vehiculeId + "\n(Fonctionnalité à implémenter)",
            "Modification", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void supprimerVehiculeSelectionne() {
        int selectedRow = tableVehicules.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Veuillez sélectionner un véhicule à supprimer.",
                "Sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vehiculeId = (Integer) modelTable.getValueAt(selectedRow, 0);
        String matricule = (String) modelTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "❓ Êtes-vous sûr de vouloir supprimer le véhicule '" + matricule + "' ?\n" +
            "Cette action est irréversible !",
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (vehiculeDAO.supprimerVehicule(vehiculeId)) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Véhicule '" + matricule + "' supprimé avec succès !",
                        "Suppression réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerVehicules(); // Recharger la liste
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Impossible de supprimer le véhicule.\n" +
                        "Il pourrait être utilisé dans des affectations.",
                        "Erreur de suppression", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de la suppression : " + e.getMessage(),
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}