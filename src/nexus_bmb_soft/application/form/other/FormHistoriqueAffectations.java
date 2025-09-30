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
import java.util.Date;
import java.util.Calendar;

/**
 * Interface harmonisée pour consulter l'historique des affectations terminées
 * Permet de visualiser, filtrer et exporter l'historique avec pagination
 * Style harmonisé avec FormGestionVehicules (template design)
 * 
 * @author BlaiseMUBADI
 */
public class FormHistoriqueAffectations extends JPanel {
    
    private AffectationDAO affectationDAO;
    private VehiculeDAO vehiculeDAO;
    private UtilisateurDAO utilisateurDAO;
    
    // Table principale de l'historique
    private JTable tableHistorique;
    private DefaultTableModel modelHistorique;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Filtres
    private JComboBox<VehiculeItem> cmbFiltreVehicule;
    private JComboBox<ConducteurItem> cmbFiltreConducteur;
    private JSpinner spnDateDebut;
    private JSpinner spnDateFin;
    private JTextField txtFiltreMotif;
    
    // Pagination
    private JLabel lblPagination;
    private JButton btnPrecedent;
    private JButton btnSuivant;
    private JSpinner spnTaillePage;
    private int pageActuelle = 0;
    private int taillePageDefaut = 50;
    
    // Statistiques
    private JLabel lblNombreTotal;
    private JLabel lblStatistiques;
    
    // Boutons d'actions
    private JButton btnRafraichir;
    private JButton btnVoirDetails;
    private JButton btnExporter;
    private JButton btnStatistiques;
    
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
    
    public FormHistoriqueAffectations() {
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
        JLabel lblTitrePrincipal = new JLabel("📚 Historique des Affectations", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(70, 130, 180)); // Style harmonisé
        lblTitrePrincipal.setForeground(Color.WHITE);
        
        // Panel de filtres
        JPanel filtresPanel = createFiltresPanel();
        
        // Panel central avec tableau
        JPanel centralPanel = createTablePanel();
        
        // Panel de pagination et actions
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        mainPanel.add(filtresPanel, BorderLayout.WEST);
        mainPanel.add(centralPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFiltresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("🔍 Filtres de Recherche"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(300, 0));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Période de recherche
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        JLabel lblPeriode = new JLabel("Période:");
        lblPeriode.setFont(lblPeriode.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblPeriode, gbc);
        
        gbc.gridy = 1;
        JLabel lblDateDebut = new JLabel("Du:");
        formPanel.add(lblDateDebut, gbc);
        
        gbc.gridy = 2;
        spnDateDebut = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorDebut = new JSpinner.DateEditor(spnDateDebut, "dd/MM/yyyy");
        spnDateDebut.setEditor(editorDebut);
        // Par défaut: il y a 30 jours
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        spnDateDebut.setValue(cal.getTime());
        spnDateDebut.addChangeListener(e -> appliquerFiltres());
        formPanel.add(spnDateDebut, gbc);
        
        gbc.gridy = 3;
        JLabel lblDateFin = new JLabel("Au:");
        formPanel.add(lblDateFin, gbc);
        
        gbc.gridy = 4;
        spnDateFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spnDateFin, "dd/MM/yyyy");
        spnDateFin.setEditor(editorFin);
        spnDateFin.setValue(new Date()); // Aujourd'hui
        spnDateFin.addChangeListener(e -> appliquerFiltres());
        formPanel.add(spnDateFin, gbc);
        
        // Filtre par véhicule
        gbc.gridy = 5;
        JLabel lblFiltreVehicule = new JLabel("Véhicule:");
        lblFiltreVehicule.setFont(lblFiltreVehicule.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblFiltreVehicule, gbc);
        
        gbc.gridy = 6;
        cmbFiltreVehicule = new JComboBox<>();
        cmbFiltreVehicule.addActionListener(e -> appliquerFiltres());
        formPanel.add(cmbFiltreVehicule, gbc);
        
        // Filtre par conducteur
        gbc.gridy = 7;
        JLabel lblFiltreConducteur = new JLabel("Conducteur:");
        lblFiltreConducteur.setFont(lblFiltreConducteur.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblFiltreConducteur, gbc);
        
        gbc.gridy = 8;
        cmbFiltreConducteur = new JComboBox<>();
        cmbFiltreConducteur.addActionListener(e -> appliquerFiltres());
        formPanel.add(cmbFiltreConducteur, gbc);
        
        // Filtre par motif
        gbc.gridy = 9;
        JLabel lblFiltreMotif = new JLabel("Motif (contient):");
        lblFiltreMotif.setFont(lblFiltreMotif.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblFiltreMotif, gbc);
        
        gbc.gridy = 10;
        txtFiltreMotif = new JTextField();
        txtFiltreMotif.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltres(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltres(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { appliquerFiltres(); }
        });
        formPanel.add(txtFiltreMotif, gbc);
        
        // Boutons de raccourcis période
        gbc.gridy = 11;
        JPanel raccourcisPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        JButton btn7Jours = new JButton("7 derniers jours");
        btn7Jours.setFont(btn7Jours.getFont().deriveFont(10f));
        btn7Jours.addActionListener(e -> definirPeriode(7));
        
        JButton btn30Jours = new JButton("30 derniers jours");
        btn30Jours.setFont(btn30Jours.getFont().deriveFont(10f));
        btn30Jours.addActionListener(e -> definirPeriode(30));
        
        JButton btnAnnee = new JButton("Cette année");
        btnAnnee.setFont(btnAnnee.getFont().deriveFont(10f));
        btnAnnee.addActionListener(e -> definirPeriodeAnnee());
        
        raccourcisPanel.add(btn7Jours);
        raccourcisPanel.add(btn30Jours);
        raccourcisPanel.add(btnAnnee);
        formPanel.add(raccourcisPanel, gbc);
        
        // Bouton réinitialiser filtres
        gbc.gridy = 12;
        JButton btnReinitFiltres = new JButton("🔄 Réinitialiser");
        btnReinitFiltres.setBackground(new Color(255, 165, 0));
        btnReinitFiltres.setForeground(Color.WHITE);
        btnReinitFiltres.addActionListener(e -> reinitialiserFiltres());
        formPanel.add(btnReinitFiltres, gbc);
        
        // Statistiques rapides
        gbc.gridy = 13;
        lblNombreTotal = new JLabel("Total: 0");
        lblNombreTotal.setFont(lblNombreTotal.getFont().deriveFont(Font.BOLD));
        lblNombreTotal.setForeground(new Color(52, 152, 219));
        formPanel.add(lblNombreTotal, gbc);
        
        gbc.gridy = 14; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH;
        lblStatistiques = new JLabel("<html>Chargement...</html>");
        lblStatistiques.setFont(lblStatistiques.getFont().deriveFont(10f));
        formPanel.add(lblStatistiques, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder("📋 Historique des Affectations"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Configuration du modèle de table
        String[] colonnes = {
            "ID", "Véhicule", "Matricule", "Conducteur", 
            "Date Début", "Date Fin", "Durée", "Motif", "Terminée le"
        };
        
        modelHistorique = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table en lecture seule
            }
        };
        
        tableHistorique = new JTable(modelHistorique);
        tableHistorique.setRowHeight(28);
        tableHistorique.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHistorique.setAutoCreateRowSorter(true);
        
        // Configurateur les colonnes
        tableHistorique.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tableHistorique.getColumnModel().getColumn(1).setPreferredWidth(120); // Véhicule
        tableHistorique.getColumnModel().getColumn(2).setPreferredWidth(80);  // Matricule
        tableHistorique.getColumnModel().getColumn(3).setPreferredWidth(150); // Conducteur
        tableHistorique.getColumnModel().getColumn(4).setPreferredWidth(90);  // Date Début
        tableHistorique.getColumnModel().getColumn(5).setPreferredWidth(90);  // Date Fin
        tableHistorique.getColumnModel().getColumn(6).setPreferredWidth(60);  // Durée
        tableHistorique.getColumnModel().getColumn(7).setPreferredWidth(200); // Motif
        tableHistorique.getColumnModel().getColumn(8).setPreferredWidth(90);  // Terminée le
        
        // Sorter pour filtrage
        sorter = new TableRowSorter<>(modelHistorique);
        tableHistorique.setRowSorter(sorter);
        
        // Listener de sélection
        tableHistorique.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mettreAJourBoutons();
            }
        });
        
        // Double-click pour voir détails
        tableHistorique.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    voirDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableHistorique);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de pagination
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paginationPanel.setBorder(BorderFactory.createTitledBorder("📄 Pagination"));
        
        lblPagination = new JLabel("Page 1 sur 1");
        btnPrecedent = new JButton("◀ Précédent");
        btnPrecedent.setEnabled(false);
        btnPrecedent.addActionListener(e -> changerPage(-1));
        
        btnSuivant = new JButton("Suivant ▶");
        btnSuivant.setEnabled(false);
        btnSuivant.addActionListener(e -> changerPage(1));
        
        paginationPanel.add(new JLabel("Éléments par page:"));
        spnTaillePage = new JSpinner(new SpinnerNumberModel(taillePageDefaut, 10, 500, 10));
        spnTaillePage.addChangeListener(e -> {
            pageActuelle = 0;
            chargerHistorique();
        });
        paginationPanel.add(spnTaillePage);
        
        paginationPanel.add(Box.createHorizontalStrut(20));
        paginationPanel.add(btnPrecedent);
        paginationPanel.add(lblPagination);
        paginationPanel.add(btnSuivant);
        
        // Panel des boutons d'actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("⚡ Actions"));
        
        btnRafraichir = new JButton("🔄 Rafraîchir");
        btnRafraichir.setBackground(new Color(52, 152, 219));
        btnRafraichir.setForeground(Color.WHITE);
        btnRafraichir.setPreferredSize(new Dimension(120, 35));
        btnRafraichir.addActionListener(e -> chargerHistorique());
        
        btnVoirDetails = new JButton("👁️ Détails");
        btnVoirDetails.setBackground(new Color(155, 89, 182));
        btnVoirDetails.setForeground(Color.WHITE);
        btnVoirDetails.setPreferredSize(new Dimension(120, 35));
        btnVoirDetails.setEnabled(false);
        btnVoirDetails.addActionListener(e -> voirDetails());
        
        btnStatistiques = new JButton("📊 Statistiques");
        btnStatistiques.setBackground(new Color(230, 126, 34));
        btnStatistiques.setForeground(Color.WHITE);
        btnStatistiques.setPreferredSize(new Dimension(130, 35));
        btnStatistiques.addActionListener(e -> afficherStatistiques());
        
        btnExporter = new JButton("📄 Exporter");
        btnExporter.setBackground(new Color(34, 139, 34));
        btnExporter.setForeground(Color.WHITE);
        btnExporter.setPreferredSize(new Dimension(120, 35));
        btnExporter.addActionListener(e -> exporterDonnees());
        
        actionsPanel.add(btnRafraichir);
        actionsPanel.add(btnVoirDetails);
        actionsPanel.add(btnStatistiques);
        actionsPanel.add(btnExporter);
        
        panel.add(paginationPanel, BorderLayout.WEST);
        panel.add(actionsPanel, BorderLayout.EAST);
        
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
                
                // Charger UNIQUEMENT les conducteurs pour les filtres
                List<Utilisateur> conducteurs = utilisateurDAO.getTousConducteurs();
                cacheUtilisateurs.clear();
                cmbFiltreConducteur.removeAllItems();
                cmbFiltreConducteur.addItem(new ConducteurItem(null)); // "Tous les conducteurs"
                
                for (Utilisateur conducteur : conducteurs) {
                    cacheUtilisateurs.put(conducteur.getId(), conducteur);
                    cmbFiltreConducteur.addItem(new ConducteurItem(conducteur));
                }
                
                // Charger l'historique
                chargerHistorique();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement des données: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void chargerHistorique() {
        try {
            // Pour cette version simplifiée, on charge tout l'historique
            // Dans une version réelle, on utiliserait la pagination côté base de données
            int taillePage = (Integer) spnTaillePage.getValue();
            List<Affectation> historique = affectationDAO.listerHistorique(taillePage * 10); // Limite élargie
            
            modelHistorique.setRowCount(0);
            
            for (Affectation affectation : historique) {
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
                    formatDate(affectation.getDateFin()) : "Non définie";
                
                String duree = calculerDuree(affectation.getDateDebut(), affectation.getDateFin());
                String motif = affectation.getMotif() != null ? 
                    (affectation.getMotif().length() > 50 ? 
                        affectation.getMotif().substring(0, 47) + "..." : 
                        affectation.getMotif()) : "";
                
                String dateTerminaison = formatDate(LocalDate.now()); // Simplification
                
                modelHistorique.addRow(new Object[]{
                    affectation.getId(),
                    vehiculeInfo,
                    matricule,
                    conducteurInfo,
                    dateDebut,
                    dateFin,
                    duree,
                    motif,
                    dateTerminaison
                });
            }
            
            // Mettre à jour les statistiques
            lblNombreTotal.setText(String.format("Total: %d", historique.size()));
            mettreAJourStatistiques(historique);
            mettreAJourPagination(historique.size());
            
            appliquerFiltres();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement de l'historique: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mettreAJourStatistiques(List<Affectation> historique) {
        if (historique.isEmpty()) {
            lblStatistiques.setText("<html><div style='font-size:10px;'>Aucune donnée</div></html>");
            return;
        }
        
        long affectationsLongues = historique.stream()
            .filter(a -> a.getDateFin() != null && 
                        a.getDateDebut().until(a.getDateFin()).getDays() > 30)
            .count();
        
        String stats = String.format(
            "<html><div style='font-size:10px;'>" +
            "🔸 Total: %d<br>" +
            "📅 Longues (>30j): %d<br>" +
            "⏱️ Période sélectionnée" +
            "</div></html>",
            historique.size(),
            affectationsLongues
        );
        
        lblStatistiques.setText(stats);
    }
    
    private void mettreAJourPagination(int totalItems) {
        int taillePage = (Integer) spnTaillePage.getValue();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / taillePage));
        
        lblPagination.setText(String.format("Page %d sur %d", pageActuelle + 1, totalPages));
        btnPrecedent.setEnabled(pageActuelle > 0);
        btnSuivant.setEnabled(pageActuelle < totalPages - 1);
    }
    
    private void changerPage(int direction) {
        pageActuelle += direction;
        chargerHistorique();
    }
    
    private void definirPeriode(int jours) {
        Calendar cal = Calendar.getInstance();
        spnDateFin.setValue(cal.getTime());
        
        cal.add(Calendar.DAY_OF_MONTH, -jours);
        spnDateDebut.setValue(cal.getTime());
        
        appliquerFiltres();
    }
    
    private void definirPeriodeAnnee() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        spnDateDebut.setValue(cal.getTime());
        
        cal = Calendar.getInstance();
        spnDateFin.setValue(cal.getTime());
        
        appliquerFiltres();
    }
    
    private void appliquerFiltres() {
        if (sorter == null) return;
        
        java.util.List<RowFilter<Object, Object>> filtres = new java.util.ArrayList<>();
        
        // Filtre par période (simplifié pour l'exemple)
        
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
        int lignesVisibles = tableHistorique.getRowCount();
        lblNombreTotal.setText(String.format("Affiché: %d", lignesVisibles));
    }
    
    private void reinitialiserFiltres() {
        // Période par défaut: 30 derniers jours
        definirPeriode(30);
        
        cmbFiltreVehicule.setSelectedIndex(0);
        cmbFiltreConducteur.setSelectedIndex(0);
        txtFiltreMotif.setText("");
        pageActuelle = 0;
        
        chargerHistorique();
    }
    
    private void mettreAJourBoutons() {
        boolean selectionValide = tableHistorique.getSelectedRow() != -1;
        btnVoirDetails.setEnabled(selectionValide);
    }
    
    private void voirDetails() {
        int selectedRow = tableHistorique.getSelectedRow();
        if (selectedRow == -1) return;
        
        int modelRow = tableHistorique.convertRowIndexToModel(selectedRow);
        int affectationId = (Integer) modelHistorique.getValueAt(modelRow, 0);
        
        // Créer une fenêtre de dialogue avec les détails
        StringBuilder details = new StringBuilder();
        details.append("=== DÉTAILS DE L'AFFECTATION HISTORIQUE ===\n\n");
        details.append(String.format("ID: %s\n", modelHistorique.getValueAt(modelRow, 0)));
        details.append(String.format("Véhicule: %s\n", modelHistorique.getValueAt(modelRow, 1)));
        details.append(String.format("Matricule: %s\n", modelHistorique.getValueAt(modelRow, 2)));
        details.append(String.format("Conducteur: %s\n", modelHistorique.getValueAt(modelRow, 3)));
        details.append(String.format("Date début: %s\n", modelHistorique.getValueAt(modelRow, 4)));
        details.append(String.format("Date fin: %s\n", modelHistorique.getValueAt(modelRow, 5)));
        details.append(String.format("Durée totale: %s\n", modelHistorique.getValueAt(modelRow, 6)));
        details.append(String.format("Terminée le: %s\n\n", modelHistorique.getValueAt(modelRow, 8)));
        details.append("Motif complet:\n");
        details.append(String.format("%s", modelHistorique.getValueAt(modelRow, 7)));
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Historique affectation #" + affectationId, 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void afficherStatistiques() {
        try {
            // Utiliser la méthode de statistiques de l'AffectationDAO
            Map<String, Object> stats = affectationDAO.obtenirStatistiques();
            
            StringBuilder rapport = new StringBuilder();
            rapport.append("=== STATISTIQUES DES AFFECTATIONS ===\n\n");
            
            if (stats != null && !stats.isEmpty()) {
                for (Map.Entry<String, Object> entry : stats.entrySet()) {
                    rapport.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
                }
            } else {
                rapport.append("Aucune statistique disponible pour le moment.\n");
                rapport.append("Cela peut être dû à l'absence de données ou à un problème de connexion.");
            }
            
            JTextArea textArea = new JTextArea(rapport.toString());
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            textArea.setEditable(false);
            textArea.setCaretPosition(0);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "📊 Statistiques des Affectations", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du calcul des statistiques: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exporterDonnees() {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("ID,Vehicule,Matricule,Conducteur,DateDebut,DateFin,Duree,Motif,TermineeLe\n");
            
            // Exporter seulement les lignes visibles (après filtres)
            for (int i = 0; i < tableHistorique.getRowCount(); i++) {
                for (int j = 0; j < tableHistorique.getColumnCount(); j++) {
                    Object value = tableHistorique.getValueAt(i, j);
                    String strValue = value != null ? value.toString().replace(",", ";") : "";
                    csv.append(strValue);
                    if (j < tableHistorique.getColumnCount() - 1) {
                        csv.append(",");
                    }
                }
                csv.append("\n");
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("historique_affectations_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.nio.file.Files.write(fileChooser.getSelectedFile().toPath(), 
                    csv.toString().getBytes());
                
                JOptionPane.showMessageDialog(this,
                    "✅ Export réussi!\n\nFichier: " + fileChooser.getSelectedFile().getName() +
                    "\nLignes exportées: " + tableHistorique.getRowCount(),
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
        
        return jours + " jour(s)";
    }
    
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
}