package nexus_bmb_soft.application.form.other;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import nexus_bmb_soft.database.dao.EntretienDAO;
import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Entretien;
import nexus_bmb_soft.models.Vehicule;

/**
 * Formulaire pour la gestion de l'entretien et maintenance
 * Interface complète avec CRUD et gestion des alertes
 * 
 * @author BlaiseMUBADI
 */
public class FormEntretien extends JPanel {
    
    private static final Logger LOGGER = Logger.getLogger(FormEntretien.class.getName());
    
    // DAO
    private final EntretienDAO entretienDAO;
    private final VehiculeDAO vehiculeDAO;
    
    // Interface components
    private JTable tableEntretien;
    private DefaultTableModel modelEntretien;
    private JButton btnProgrammer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnAlertes;
    private JButton btnHistorique;
    private JButton btnRafraichir;
    private JComboBox<String> cbTypeEntretien;
    private JComboBox<String> cbStatutEntretien;
    private JComboBox<String> cbVehiculeFiltre;
    private JTextArea txtAlertes;
    
    // Cache pour les véhicules
    private Map<Integer, Vehicule> cacheVehicules;
    
    public FormEntretien() {
        this.entretienDAO = new EntretienDAO();
        this.vehiculeDAO = new VehiculeDAO();
        this.cacheVehicules = new HashMap<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        chargerDonnees();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel supérieur avec titre et boutons d'action
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitre = new JLabel("🔧 Entretien & Maintenance");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        btnProgrammer = new JButton("➕ Programmer");
        btnModifier = new JButton("✏️ Modifier");
        btnSupprimer = new JButton("🗑️ Supprimer");
        btnRafraichir = new JButton("🔄 Actualiser");
        btnAlertes = new JButton("⚠️ Alertes");
        btnHistorique = new JButton("📋 Historique");
        
        panelTop.add(lblTitre);
        panelTop.add(Box.createHorizontalStrut(30));
        panelTop.add(btnProgrammer);
        panelTop.add(btnModifier);
        panelTop.add(btnSupprimer);
        panelTop.add(Box.createHorizontalStrut(20));
        panelTop.add(btnRafraichir);
        panelTop.add(btnAlertes);
        panelTop.add(btnHistorique);
        
        // Panel de filtres
        JPanel panelFiltre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lblVehicule = new JLabel("Véhicule:");
        cbVehiculeFiltre = new JComboBox<>();
        cbVehiculeFiltre.addItem("Tous les véhicules");
        
        JLabel lblType = new JLabel("Type:");
        String[] types = {"Tous", "Vidange", "Freins", "Pneus", "Révision", "Réparation", "Assurance", "Visite technique"};
        cbTypeEntretien = new JComboBox<>(types);
        
        JLabel lblStatut = new JLabel("Statut:");
        String[] statuts = {"Tous", "Programmé", "En cours", "Terminé"};
        cbStatutEntretien = new JComboBox<>(statuts);
        
        panelFiltre.add(lblVehicule);
        panelFiltre.add(cbVehiculeFiltre);
        panelFiltre.add(Box.createHorizontalStrut(15));
        panelFiltre.add(lblType);
        panelFiltre.add(cbTypeEntretien);
        panelFiltre.add(Box.createHorizontalStrut(15));
        panelFiltre.add(lblStatut);
        panelFiltre.add(cbStatutEntretien);
        
        // Table d'entretien avec modèle dynamique
        String[] colonnes = {"ID", "Véhicule", "Date", "Type", "Coût", "Km", "Statut", "Commentaire"};
        modelEntretien = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Lecture seule
            }
        };
        
        tableEntretien = new JTable(modelEntretien);
        tableEntretien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEntretien.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Masquer la colonne ID
        tableEntretien.getColumnModel().getColumn(0).setMinWidth(0);
        tableEntretien.getColumnModel().getColumn(0).setMaxWidth(0);
        tableEntretien.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(tableEntretien);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Panel principal
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelTop, BorderLayout.NORTH);
        panelMain.add(panelFiltre, BorderLayout.CENTER);
        
        add(panelMain, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel d'alertes importantes
        JPanel panelAlertes = new JPanel(new BorderLayout());
        panelAlertes.setBorder(BorderFactory.createTitledBorder("⚠️ Alertes & Échéances Importantes"));
        panelAlertes.setPreferredSize(new Dimension(800, 120));
        
        txtAlertes = new JTextArea(3, 50);
        txtAlertes.setEditable(false);
        txtAlertes.setBackground(new Color(255, 249, 196));
        txtAlertes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panelAlertes.add(new JScrollPane(txtAlertes), BorderLayout.CENTER);
        
        add(panelAlertes, BorderLayout.SOUTH);
    }
    
    private void setupLayout() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Styling des boutons
        btnProgrammer.setBackground(new Color(76, 175, 80));
        btnProgrammer.setForeground(Color.WHITE);
        btnProgrammer.setFocusPainted(false);
        
        btnModifier.setBackground(new Color(33, 150, 243));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.setFocusPainted(false);
        
        btnSupprimer.setBackground(new Color(244, 67, 54));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setFocusPainted(false);
        
        btnRafraichir.setBackground(new Color(96, 125, 139));
        btnRafraichir.setForeground(Color.WHITE);
        btnRafraichir.setFocusPainted(false);
        
        btnAlertes.setBackground(new Color(255, 193, 7));
        btnAlertes.setForeground(Color.BLACK);
        btnAlertes.setFocusPainted(false);
        
        btnHistorique.setBackground(new Color(96, 125, 139));
        btnHistorique.setForeground(Color.WHITE);
        btnHistorique.setFocusPainted(false);
    }
    
    /**
     * Configure les écouteurs d'événements
     */
    private void setupEventListeners() {
        // Boutons d'action
        btnProgrammer.addActionListener(e -> ouvrirDialogueNouvelEntretien());
        btnModifier.addActionListener(e -> modifierEntretienSelectionne());
        btnSupprimer.addActionListener(e -> supprimerEntretienSelectionne());
        btnRafraichir.addActionListener(e -> chargerDonnees());
        btnAlertes.addActionListener(e -> afficherAlertes());
        btnHistorique.addActionListener(e -> afficherHistoriqueComplet());
        
        // Filtres
        cbVehiculeFiltre.addActionListener(e -> appliquerFiltres());
        cbTypeEntretien.addActionListener(e -> appliquerFiltres());
        cbStatutEntretien.addActionListener(e -> appliquerFiltres());
        
        // Double-clic sur la table pour modifier
        tableEntretien.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    modifierEntretienSelectionne();
                }
            }
        });
    }
    
    /**
     * Charge toutes les données (entretiens et véhicules)
     */
    private void chargerDonnees() {
        try {
            // Charger la liste des véhicules pour le cache et les filtres
            chargerVehicules();
            
            // Charger les entretiens
            chargerEntretiens();
            
            // Mettre à jour les alertes
            mettreAJourAlertes();
            
        } catch (Exception e) {
            LOGGER.severe("❌ Erreur lors du chargement des données: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des données: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Charge les véhicules dans le cache et les combobox
     */
    private void chargerVehicules() {
        List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
        cacheVehicules.clear();
        
        // Vider et remplir le combobox des véhicules
        cbVehiculeFiltre.removeAllItems();
        cbVehiculeFiltre.addItem("Tous les véhicules");
        
        for (Vehicule vehicule : vehicules) {
            cacheVehicules.put(vehicule.getId(), vehicule);
            cbVehiculeFiltre.addItem(vehicule.getMatricule() + " - " + vehicule.getMarque());
        }
    }
    
    /**
     * Charge les entretiens dans la table
     */
    private void chargerEntretiens() {
        modelEntretien.setRowCount(0);
        
        List<Entretien> entretiens = entretienDAO.listerTousEntretiens();
        
        for (Entretien entretien : entretiens) {
            Object[] ligne = new Object[8];
            ligne[0] = entretien.getId(); // ID caché
            
            // Véhicule
            if (entretien.getVehicule() != null) {
                ligne[1] = entretien.getVehicule().getMatricule();
            } else {
                Vehicule vehicule = cacheVehicules.get(entretien.getVehiculeId());
                ligne[1] = vehicule != null ? vehicule.getMatricule() : "Véhicule #" + entretien.getVehiculeId();
            }
            
            // Date
            ligne[2] = entretien.getDateEntretien() != null ? 
                entretien.getDateEntretien().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non définie";
            
            // Type
            ligne[3] = entretien.getTypeEntretien() != null ? entretien.getTypeEntretien() : "Non spécifié";
            
            // Coût
            ligne[4] = entretien.getCoutFormate();
            
            // Kilométrage
            ligne[5] = entretien.getKilometrage() > 0 ? 
                String.format("%,d km", entretien.getKilometrage()) : "Non défini";
            
            // Statut
            ligne[6] = entretien.getStatutAffichage();
            
            // Commentaire (tronqué si trop long)
            String commentaire = entretien.getCommentaire();
            if (commentaire != null && commentaire.length() > 50) {
                commentaire = commentaire.substring(0, 47) + "...";
            }
            ligne[7] = commentaire != null ? commentaire : "";
            
            modelEntretien.addRow(ligne);
        }
        
        LOGGER.info("✅ " + entretiens.size() + " entretiens chargés");
    }
    
    /**
     * Met à jour les alertes importantes
     */
    private void mettreAJourAlertes() {
        StringBuilder alertes = new StringBuilder();
        LocalDate aujourd_hui = LocalDate.now();
        
        // Entretiens programmés bientôt (dans les 7 jours)
        List<Entretien> entretiensProgrammes = entretienDAO.listerEntretiensParStatut("programme");
        for (Entretien entretien : entretiensProgrammes) {
            if (entretien.getDateEntretien() != null) {
                long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(aujourd_hui, entretien.getDateEntretien());
                if (joursRestants >= 0 && joursRestants <= 7) {
                    Vehicule vehicule = cacheVehicules.get(entretien.getVehiculeId());
                    String matricule = vehicule != null ? vehicule.getMatricule() : "Véhicule #" + entretien.getVehiculeId();
                    
                    if (joursRestants == 0) {
                        alertes.append("🔴 AUJOURD'HUI - ").append(matricule)
                               .append(" - ").append(entretien.getTypeEntretien()).append("\n");
                    } else if (joursRestants == 1) {
                        alertes.append("🟠 DEMAIN - ").append(matricule)
                               .append(" - ").append(entretien.getTypeEntretien()).append("\n");
                    } else {
                        alertes.append("🟡 Dans ").append(joursRestants).append(" jours - ")
                               .append(matricule).append(" - ").append(entretien.getTypeEntretien()).append("\n");
                    }
                }
            }
        }
        
        // Entretiens en cours depuis longtemps (plus de 7 jours)
        List<Entretien> entretiensEnCours = entretienDAO.listerEntretiensParStatut("en_cours");
        for (Entretien entretien : entretiensEnCours) {
            if (entretien.getJoursDepuis() > 7) {
                Vehicule vehicule = cacheVehicules.get(entretien.getVehiculeId());
                String matricule = vehicule != null ? vehicule.getMatricule() : "Véhicule #" + entretien.getVehiculeId();
                alertes.append("⚠️ En cours depuis ").append(entretien.getJoursDepuis())
                       .append(" jours - ").append(matricule).append(" - ")
                       .append(entretien.getTypeEntretien()).append("\n");
            }
        }
        
        if (alertes.length() == 0) {
            alertes.append("✅ Aucune alerte importante pour le moment");
        }
        
        txtAlertes.setText(alertes.toString());
    }
    
    /**
     * Applique les filtres sélectionnés
     */
    private void appliquerFiltres() {
        // Implementation des filtres sera ajoutée si nécessaire
        // Pour l'instant, on recharge simplement les données
        chargerEntretiens();
    }
    
    /**
     * Ouvre le dialogue pour programmer un nouvel entretien
     */
    private void ouvrirDialogueNouvelEntretien() {
        JOptionPane.showMessageDialog(this, 
            "Fonction 'Programmer Entretien' en cours de développement", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Modifie l'entretien sélectionné
     */
    private void modifierEntretienSelectionne() {
        int selectedRow = tableEntretien.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un entretien à modifier", 
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, 
            "Fonction 'Modifier Entretien' en cours de développement", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Supprime l'entretien sélectionné
     */
    private void supprimerEntretienSelectionne() {
        int selectedRow = tableEntretien.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un entretien à supprimer", 
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int entretienId = (Integer) modelEntretien.getValueAt(selectedRow, 0);
        String vehicule = (String) modelEntretien.getValueAt(selectedRow, 1);
        String type = (String) modelEntretien.getValueAt(selectedRow, 3);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cet entretien ?\n" +
            "Véhicule: " + vehicule + "\n" +
            "Type: " + type,
            "Confirmer la suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            if (entretienDAO.supprimerEntretien(entretienId)) {
                JOptionPane.showMessageDialog(this, 
                    "Entretien supprimé avec succès", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerEntretiens(); // Recharger les données
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression de l'entretien", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Affiche une fenêtre détaillée des alertes
     */
    private void afficherAlertes() {
        JOptionPane.showMessageDialog(this, 
            "Fonction 'Alertes détaillées' en cours de développement", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Affiche l'historique complet des entretiens
     */
    private void afficherHistoriqueComplet() {
        JOptionPane.showMessageDialog(this, 
            "Fonction 'Historique complet' en cours de développement", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}