package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.AffectationDAO;
import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.database.dao.UtilisateurDAO;
import nexus_bmb_soft.models.Affectation;
import nexus_bmb_soft.models.Vehicule;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Interface harmonisée pour gérer les affectations actives (en cours)
 * Permet de visualiser, modifier et terminer les affectations actives
 * Style harmonisé avec FormGestionVehicules (template design)
 * 
 * @author BlaiseMUBADI
 */
public class FormAffectationsActives extends JPanel {
    
    private AffectationDAO affectationDAO;
    private VehiculeDAO vehiculeDAO;
    private UtilisateurDAO utilisateurDAO;
    
    // Table principale des affectations actives
    private JTable tableAffectations;
    private DefaultTableModel modelAffectations;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Filtres
    private JComboBox<VehiculeItem> cmbFiltreVehicule;
    private JComboBox<ConducteurItem> cmbFiltreConducteur;
    private JTextField txtFiltreMotif;
    private JLabel lblNombreAffectations;
    private JLabel lblStatistiques;
    
    // Boutons d'actions
    private JButton btnRafraichir;
    private JButton btnTerminer;
    private JButton btnVoirDetails;
    private JButton btnExporter;
    
    // Cache pour les véhicules et utilisateurs
    private Map<Integer, Vehicule> cacheVehicules;
    private Map<Integer, Utilisateur> cacheUtilisateurs;
    
    // Classes helper pour les ComboBox de filtres
    private static class VehiculeItem {
        private final Vehicule vehicule;
        
        public VehiculeItem(Vehicule vehicule) {
            this.vehicule = vehicule;
        }
        
        public Vehicule getVehicule() { return vehicule; }
        
        @Override
        public String toString() {
            if (vehicule == null) return "Tous les véhicules";
            return String.format("%s - %s %s", 
                vehicule.getMatricule(),
                vehicule.getMarque(),
                vehicule.getType());
        }
    }
    
    private static class ConducteurItem {
        private final Utilisateur conducteur;
        
        public ConducteurItem(Utilisateur conducteur) {
            this.conducteur = conducteur;
        }
        
        public Utilisateur getConducteur() { return conducteur; }
        
        @Override
        public String toString() {
            if (conducteur == null) return "Tous les conducteurs";
            return String.format("%s %s", 
                conducteur.getNom(),
                conducteur.getPrenom() != null ? conducteur.getPrenom() : "");
        }
    }
    
    public FormAffectationsActives() {
        affectationDAO = new AffectationDAO();
        vehiculeDAO = new VehiculeDAO();
        utilisateurDAO = new UtilisateurDAO();
        cacheVehicules = new HashMap<>();
        cacheUtilisateurs = new HashMap<>();
        init();
        chargerDonnees();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        // Panel principal avec titre
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal harmonisé
        JLabel lblTitrePrincipal = new JLabel("🟢 Gestion des Affectations Actives", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180)); // Style harmonisé
        lblTitrePrincipal.setForeground(Color.WHITE);
        
        // Panel de filtres
        JPanel filtresPanel = createFiltresPanel();
        
        // Panel central avec tableau
        JPanel centralPanel = createTablePanel();
        
        // Panel de statistiques et actions
        JPanel actionsPanel = createActionsPanel();
        
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        mainPanel.add(filtresPanel, BorderLayout.WEST);
        mainPanel.add(centralPanel, BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFiltresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("🔍 Filtres de Recherche"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(280, 0));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Filtre par véhicule
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        JLabel lblFiltreVehicule = new JLabel("Véhicule:");
        lblFiltreVehicule.setFont(lblFiltreVehicule.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblFiltreVehicule, gbc);
        
        gbc.gridy = 1;
        cmbFiltreVehicule = new JComboBox<>();
        cmbFiltreVehicule.addActionListener(e -> appliquerFiltres());
        formPanel.add(cmbFiltreVehicule, gbc);
        
        // Filtre par conducteur
        gbc.gridy = 2;
        JLabel lblFiltreConducteur = new JLabel("Conducteur:");
        lblFiltreConducteur.setFont(lblFiltreConducteur.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblFiltreConducteur, gbc);
        
        gbc.gridy = 3;
        cmbFiltreConducteur = new JComboBox<>();
        cmbFiltreConducteur.addActionListener(e -> appliquerFiltres());
        formPanel.add(cmbFiltreConducteur, gbc);
        
        // Filtre par motif
        gbc.gridy = 4;
        JLabel lblFiltreMotif = new JLabel("Motif (contient):");
        lblFiltreMotif.setFont(lblFiltreMotif.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblFiltreMotif, gbc);
        
        gbc.gridy = 5;
        txtFiltreMotif = new JTextField();
        txtFiltreMotif.addActionListener(e -> appliquerFiltres());
        txtFiltreMotif.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltres(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltres(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltres(); }
        });
        formPanel.add(txtFiltreMotif, gbc);
        
        // Bouton réinitialiser filtres
        gbc.gridy = 6;
        JButton btnReinitFiltres = new JButton("🔄 Réinitialiser");
        btnReinitFiltres.setBackground(new Color(255, 165, 0));
        btnReinitFiltres.setForeground(Color.WHITE);
        btnReinitFiltres.addActionListener(e -> reinitialiserFiltres());
        formPanel.add(btnReinitFiltres, gbc);
        
        // Statistiques rapides
        gbc.gridy = 7;
        lblNombreAffectations = new JLabel("Affectations: 0");
        lblNombreAffectations.setFont(lblNombreAffectations.getFont().deriveFont(Font.BOLD));
        lblNombreAffectations.setForeground(new Color(52, 152, 219));
        formPanel.add(lblNombreAffectations, gbc);
        
        gbc.gridy = 8; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH;
        lblStatistiques = new JLabel("<html>Chargement...</html>");
        lblStatistiques.setFont(lblStatistiques.getFont().deriveFont(11f));
        formPanel.add(lblStatistiques, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("📋 Liste des Affectations Actives"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Configuration du modèle de table
        String[] colonnes = {
            "ID", "Véhicule", "Matricule", "Conducteur", 
            "Date Début", "Date Fin", "Durée", "Motif", "Statut"
        };
        
        modelAffectations = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table en lecture seule
            }
        };
        
        tableAffectations = new JTable(modelAffectations);
        tableAffectations.setRowHeight(28);
        tableAffectations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAffectations.setAutoCreateRowSorter(true);
        
        // Configurateur les colonnes
        tableAffectations.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tableAffectations.getColumnModel().getColumn(1).setPreferredWidth(120); // Véhicule
        tableAffectations.getColumnModel().getColumn(2).setPreferredWidth(80);  // Matricule
        tableAffectations.getColumnModel().getColumn(3).setPreferredWidth(150); // Conducteur
        tableAffectations.getColumnModel().getColumn(4).setPreferredWidth(90);  // Date Début
        tableAffectations.getColumnModel().getColumn(5).setPreferredWidth(90);  // Date Fin
        tableAffectations.getColumnModel().getColumn(6).setPreferredWidth(60);  // Durée
        tableAffectations.getColumnModel().getColumn(7).setPreferredWidth(200); // Motif
        tableAffectations.getColumnModel().getColumn(8).setPreferredWidth(80);  // Statut
        
        // Sorter pour filtrage
        sorter = new TableRowSorter<>(modelAffectations);
        tableAffectations.setRowSorter(sorter);
        
        // Listener de sélection
        tableAffectations.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mettreAJourBoutons();
            }
        });
        
        // Double-click pour voir détails
        tableAffectations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    voirDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableAffectations);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("⚡ Actions"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Boutons principaux
        JPanel boutonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnRafraichir = new JButton("🔄 Rafraîchir");
        btnRafraichir.setBackground(new Color(52, 152, 219));
        btnRafraichir.setForeground(Color.WHITE);
        btnRafraichir.setPreferredSize(new Dimension(120, 35));
        btnRafraichir.addActionListener(e -> chargerAffectationsActives());
        
        btnVoirDetails = new JButton("👁️ Détails");
        btnVoirDetails.setBackground(new Color(155, 89, 182));
        btnVoirDetails.setForeground(Color.WHITE);
        btnVoirDetails.setPreferredSize(new Dimension(120, 35));
        btnVoirDetails.setEnabled(false);
        btnVoirDetails.addActionListener(e -> voirDetails());
        
        btnTerminer = new JButton("🏁 Terminer");
        btnTerminer.setBackground(new Color(230, 126, 34));
        btnTerminer.setForeground(Color.WHITE);
        btnTerminer.setPreferredSize(new Dimension(120, 35));
        btnTerminer.setEnabled(false);
        btnTerminer.addActionListener(e -> terminerAffectation());
        
        btnExporter = new JButton("📊 Exporter");
        btnExporter.setBackground(new Color(34, 139, 34));
        btnExporter.setForeground(Color.WHITE);
        btnExporter.setPreferredSize(new Dimension(120, 35));
        btnExporter.addActionListener(e -> exporterDonnees());
        
        boutonsPanel.add(btnRafraichir);
        boutonsPanel.add(btnVoirDetails);
        boutonsPanel.add(btnTerminer);
        boutonsPanel.add(btnExporter);
        
        panel.add(boutonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void chargerDonnees() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Charger les véhicules pour les filtres
                List<Vehicule> vehicules = vehiculeDAO.getTousVehicules();
                cacheVehicules.clear();
                cmbFiltreVehicule.removeAllItems();
                cmbFiltreVehicule.addItem(new VehiculeItem(null)); // "Tous"
                
                for (Vehicule vehicule : vehicules) {
                    cacheVehicules.put(vehicule.getId(), vehicule);
                    cmbFiltreVehicule.addItem(new VehiculeItem(vehicule));
                }
                
                // Charger les utilisateurs pour les filtres
                List<Utilisateur> utilisateurs = utilisateurDAO.lireTous();
                cacheUtilisateurs.clear();
                cmbFiltreConducteur.removeAllItems();
                cmbFiltreConducteur.addItem(new ConducteurItem(null)); // "Tous"
                
                for (Utilisateur utilisateur : utilisateurs) {
                    cacheUtilisateurs.put(utilisateur.getId(), utilisateur);
                    cmbFiltreConducteur.addItem(new ConducteurItem(utilisateur));
                }
                
                // Charger les affectations actives
                chargerAffectationsActives();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement des données: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void chargerAffectationsActives() {
        try {
            List<Affectation> affectations = affectationDAO.listerActives();
            
            modelAffectations.setRowCount(0);
            
            for (Affectation affectation : affectations) {
                // Récupérer les détails du véhicule et conducteur
                Vehicule vehicule = cacheVehicules.get(affectation.getVehiculeId());
                Utilisateur conducteur = cacheUtilisateurs.get(affectation.getConducteurId());
                
                String vehiculeInfo = vehicule != null ? 
                    String.format("%s %s", vehicule.getMarque(), vehicule.getType()) : "Inconnu";
                String matricule = vehicule != null ? vehicule.getMatricule() : "N/A";
                String conducteurInfo = conducteur != null ?
                    String.format("%s %s", conducteur.getNom(), 
                        conducteur.getPrenom() != null ? conducteur.getPrenom() : "") : "Inconnu";
                
                String dateDebut = formatDate(affectation.getDateDebut());
                String dateFin = affectation.getDateFin() != null ? 
                    formatDate(affectation.getDateFin()) : "Ouverte";
                
                String duree = calculerDuree(affectation.getDateDebut(), affectation.getDateFin());
                String motif = affectation.getMotif() != null ? 
                    (affectation.getMotif().length() > 50 ? 
                        affectation.getMotif().substring(0, 47) + "..." : 
                        affectation.getMotif()) : "";
                
                modelAffectations.addRow(new Object[]{
                    affectation.getId(),
                    vehiculeInfo,
                    matricule,
                    conducteurInfo,
                    dateDebut,
                    dateFin,
                    duree,
                    motif,
                    "🟢 Active"
                });
            }
            
            // Mettre à jour les statistiques
            lblNombreAffectations.setText(String.format("Affectations: %d", affectations.size()));
            mettreAJourStatistiques(affectations);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des affectations: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mettreAJourStatistiques(List<Affectation> affectations) {
        long affectationsOuvertes = affectations.stream()
            .filter(a -> a.getDateFin() == null)
            .count();
        
        long affectationsTemporaires = affectations.size() - affectationsOuvertes;
        
        String stats = String.format(
            "<html><div style='font-size:10px;'>" +
            "🔓 Ouvertes: %d<br>" +
            "⏰ Temporaires: %d<br>" +
            "📅 Aujourd'hui: %s" +
            "</div></html>",
            affectationsOuvertes,
            affectationsTemporaires,
            formatDate(LocalDate.now())
        );
        
        lblStatistiques.setText(stats);
    }
    
    private void appliquerFiltres() {
        if (sorter == null) return;
        
        java.util.List<RowFilter<Object, Object>> filtres = new java.util.ArrayList<>();
        
        // Filtre véhicule
        VehiculeItem vehiculeSelectionne = (VehiculeItem) cmbFiltreVehicule.getSelectedItem();
        if (vehiculeSelectionne != null && vehiculeSelectionne.getVehicule() != null) {
            String matricule = vehiculeSelectionne.getVehicule().getMatricule();
            filtres.add(RowFilter.regexFilter("(?i)" + matricule, 2)); // Colonne Matricule
        }
        
        // Filtre conducteur
        ConducteurItem conducteurSelectionne = (ConducteurItem) cmbFiltreConducteur.getSelectedItem();
        if (conducteurSelectionne != null && conducteurSelectionne.getConducteur() != null) {
            String nom = conducteurSelectionne.getConducteur().getNom();
            filtres.add(RowFilter.regexFilter("(?i).*" + nom + ".*", 3)); // Colonne Conducteur
        }
        
        // Filtre motif
        String motif = txtFiltreMotif.getText().trim();
        if (!motif.isEmpty()) {
            filtres.add(RowFilter.regexFilter("(?i).*" + motif + ".*", 7)); // Colonne Motif
        }
        
        // Appliquer les filtres
        if (filtres.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filtres));
        }
        
        // Mettre à jour le compteur
        int lignesVisibles = tableAffectations.getRowCount();
        lblNombreAffectations.setText(String.format("Affectations: %d", lignesVisibles));
    }
    
    private void reinitialiserFiltres() {
        cmbFiltreVehicule.setSelectedIndex(0);
        cmbFiltreConducteur.setSelectedIndex(0);
        txtFiltreMotif.setText("");
        appliquerFiltres();
    }
    
    private void mettreAJourBoutons() {
        boolean selectionValide = tableAffectations.getSelectedRow() != -1;
        btnVoirDetails.setEnabled(selectionValide);
        btnTerminer.setEnabled(selectionValide);
    }
    
    private void voirDetails() {
        int selectedRow = tableAffectations.getSelectedRow();
        if (selectedRow == -1) return;
        
        int modelRow = tableAffectations.convertRowIndexToModel(selectedRow);
        int affectationId = (Integer) modelAffectations.getValueAt(modelRow, 0);
        
        // Créer une fenêtre de dialogue avec les détails
        StringBuilder details = new StringBuilder();
        details.append("=== DÉTAILS DE L'AFFECTATION ===\n\n");
        details.append(String.format("ID: %s\n", modelAffectations.getValueAt(modelRow, 0)));
        details.append(String.format("Véhicule: %s\n", modelAffectations.getValueAt(modelRow, 1)));
        details.append(String.format("Matricule: %s\n", modelAffectations.getValueAt(modelRow, 2)));
        details.append(String.format("Conducteur: %s\n", modelAffectations.getValueAt(modelRow, 3)));
        details.append(String.format("Date début: %s\n", modelAffectations.getValueAt(modelRow, 4)));
        details.append(String.format("Date fin: %s\n", modelAffectations.getValueAt(modelRow, 5)));
        details.append(String.format("Durée: %s\n", modelAffectations.getValueAt(modelRow, 6)));
        details.append(String.format("Statut: %s\n\n", modelAffectations.getValueAt(modelRow, 8)));
        details.append("Motif complet:\n");
        details.append(String.format("%s", modelAffectations.getValueAt(modelRow, 7)));
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Détails de l'affectation #" + affectationId, 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void terminerAffectation() {
        int selectedRow = tableAffectations.getSelectedRow();
        if (selectedRow == -1) return;
        
        int modelRow = tableAffectations.convertRowIndexToModel(selectedRow);
        int affectationId = (Integer) modelAffectations.getValueAt(modelRow, 0);
        String vehicule = (String) modelAffectations.getValueAt(modelRow, 1);
        String conducteur = (String) modelAffectations.getValueAt(modelRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Voulez-vous terminer cette affectation ?\n\n" +
                "ID: %d\n" +
                "Véhicule: %s\n" +
                "Conducteur: %s\n\n" +
                "Cette action est irréversible.",
                affectationId, vehicule, conducteur),
            "Confirmer la fin d'affectation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = affectationDAO.terminer(affectationId, LocalDate.now());
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Affectation terminée avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    chargerAffectationsActives();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Erreur lors de la fin d'affectation",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exporterDonnees() {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("ID,Vehicule,Matricule,Conducteur,DateDebut,DateFin,Duree,Motif,Statut\n");
            
            for (int i = 0; i < modelAffectations.getRowCount(); i++) {
                for (int j = 0; j < modelAffectations.getColumnCount(); j++) {
                    Object value = modelAffectations.getValueAt(i, j);
                    csv.append(value != null ? value.toString().replace(",", ";") : "");
                    if (j < modelAffectations.getColumnCount() - 1) {
                        csv.append(",");
                    }
                }
                csv.append("\n");
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("affectations_actives_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.nio.file.Files.write(fileChooser.getSelectedFile().toPath(), 
                    csv.toString().getBytes());
                
                JOptionPane.showMessageDialog(this,
                    "✅ Export réussi!\n\nFichier: " + fileChooser.getSelectedFile().getName(),
                    "Export terminé",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'export: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Utilitaires
    private String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
    
    private String calculerDuree(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null) return "N/A";
        
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now();
        long jours = dateDebut.until(fin).getDays() + 1;
        
        if (dateFin == null) {
            return jours + "j (en cours)";
        } else {
            return jours + " jour(s)";
        }
    }
}