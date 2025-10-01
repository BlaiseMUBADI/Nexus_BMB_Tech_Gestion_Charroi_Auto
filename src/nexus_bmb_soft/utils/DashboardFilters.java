package nexus_bmb_soft.utils;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Composant de filtrage pour le tableau de bord
 * Permet de filtrer les données par période, type de véhicule, statut, etc.
 */
public class DashboardFilters extends JPanel {
    
    // Interface pour les écouteurs de changement de filtre
    public interface FilterChangeListener {
        void onFilterChanged(FilterCriteria criteria);
    }
    
    // Classe pour les critères de filtrage
    public static class FilterCriteria {
        public LocalDate dateDebut = LocalDate.now().minusDays(30);
        public LocalDate dateFin = LocalDate.now();
        public String typeVehicule = "Tous";
        public String statutVehicule = "Tous";  
        public String typeEntretien = "Tous";
        public String priorite = "Toutes";
        public boolean affectationsUniquement = false;
        public boolean entretiensDus = false;
        
        @Override
        public String toString() {
            return String.format("Période: %s - %s, Type: %s, Statut: %s", 
                dateDebut, dateFin, typeVehicule, statutVehicule);
        }
    }
    
    // Options de période prédéfinies
    public enum PeriodOption {
        AUJOURD_HUI("Aujourd'hui"),
        HIER("Hier"),
        CETTE_SEMAINE("Cette semaine"),
        SEMAINE_DERNIERE("Semaine dernière"),
        CE_MOIS("Ce mois"),
        MOIS_DERNIER("Mois dernier"),
        TROIS_MOIS("3 derniers mois"),
        CETTE_ANNEE("Cette année"),
        PERSONNALISE("Personnalisé");
        
        private final String label;
        
        PeriodOption(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    // Variables d'instance
    private final List<FilterChangeListener> listeners = new ArrayList<>();
    private FilterCriteria currentCriteria = new FilterCriteria();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Composants UI
    private JComboBox<PeriodOption> periodCombo;
    private JTextField dateDebutField;
    private JTextField dateFinField;
    private JComboBox<String> typeVehiculeCombo;
    private JComboBox<String> statutVehiculeCombo;
    private JComboBox<String> typeEntretienCombo;
    private JComboBox<String> prioriteCombo;
    private JCheckBox affectationsCheck;
    private JCheckBox entretiensDusCheck;
    private JButton resetButton;
    private JButton applyButton;
    
    public DashboardFilters() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        applyCriteria(currentCriteria); // Appliquer les critères par défaut
    }
    
    private void initializeComponents() {
        // Sélecteur de période
        periodCombo = new JComboBox<>(PeriodOption.values());
        periodCombo.setSelectedItem(PeriodOption.CE_MOIS);
        
        // Champs de date
        dateDebutField = new JTextField(10);
        dateFinField = new JTextField(10);
        dateDebutField.setPreferredSize(new Dimension(120, 30));
        dateFinField.setPreferredSize(new Dimension(120, 30));
        
        // Configuration des champs de date
        LocalDate today = LocalDate.now();
        dateDebutField.setText(today.minusDays(30).format(dateFormatter));
        dateFinField.setText(today.format(dateFormatter));
        
        // Tooltip d'aide
        dateDebutField.setToolTipText("Format: JJ/MM/AAAA");
        dateFinField.setToolTipText("Format: JJ/MM/AAAA");
        
        // Type de véhicule
        typeVehiculeCombo = new JComboBox<>(new String[]{
            "Tous", "Voiture", "Camionnette", "Camion", "Bus", "Moto", "Autre"
        });
        
        // Statut véhicule
        statutVehiculeCombo = new JComboBox<>(new String[]{
            "Tous", "Disponible", "En service", "En panne", "En entretien", "Hors service"
        });
        
        // Type d'entretien
        typeEntretienCombo = new JComboBox<>(new String[]{
            "Tous", "Préventif", "Correctif", "Révision", "Contrôle technique", "Autre"
        });
        
        // Priorité
        prioriteCombo = new JComboBox<>(new String[]{
            "Toutes", "Basse", "Normale", "Élevée", "Critique"
        });
        
        // Cases à cocher
        affectationsCheck = new JCheckBox("Affectations uniquement");
        entretiensDusCheck = new JCheckBox("Entretiens dus");
        
        // Boutons
        resetButton = new JButton("Réinitialiser");
        applyButton = new JButton("Appliquer");
        
        // Style des boutons
        styleButton(resetButton, new Color(52, 152, 219), Color.WHITE);
        styleButton(applyButton, new Color(46, 204, 113), Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Filtres du Dashboard"));
        setBackground(Color.WHITE);
        
        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Première ligne - Période
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Période:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(periodCombo, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Date début:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(dateDebutField, gbc);
        
        gbc.gridx = 4;
        mainPanel.add(new JLabel("Date fin:"), gbc);
        gbc.gridx = 5;
        mainPanel.add(dateFinField, gbc);
        
        // Deuxième ligne - Types et statuts
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Type véhicule:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(typeVehiculeCombo, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(statutVehiculeCombo, gbc);
        
        gbc.gridx = 4;
        mainPanel.add(new JLabel("Type entretien:"), gbc);
        gbc.gridx = 5;
        mainPanel.add(typeEntretienCombo, gbc);
        
        // Troisième ligne - Priorité et options
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Priorité:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(prioriteCombo, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(affectationsCheck, gbc);
        gbc.gridx = 3;
        mainPanel.add(entretiensDusCheck, gbc);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(resetButton);
        buttonPanel.add(applyButton);
        
        gbc.gridx = 4; gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setPreferredSize(new Dimension(100, 30));
        
        // Effet de survol
        Color originalBg = bgColor;
        Color darkColor = bgColor.darker();
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(darkColor);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
    }
    
    private void setupEventListeners() {
        // Écouteur pour le changement de période
        periodCombo.addActionListener(e -> updateDatesForPeriod());
        
        // Écouteurs pour les champs de date
        dateDebutField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateCriteria();
            }
        });
        dateFinField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateCriteria();
            }
        });
        
        // Écouteurs pour les autres composants
        typeVehiculeCombo.addActionListener(e -> updateCriteria());
        statutVehiculeCombo.addActionListener(e -> updateCriteria());
        typeEntretienCombo.addActionListener(e -> updateCriteria());
        prioriteCombo.addActionListener(e -> updateCriteria());
        affectationsCheck.addActionListener(e -> updateCriteria());
        entretiensDusCheck.addActionListener(e -> updateCriteria());
        
        // Boutons
        resetButton.addActionListener(e -> resetFilters());
        applyButton.addActionListener(e -> applyFilters());
    }
    
    private void updateDatesForPeriod() {
        PeriodOption selected = (PeriodOption) periodCombo.getSelectedItem();
        LocalDate today = LocalDate.now();
        LocalDate debut, fin;
        
        switch (selected) {
            case AUJOURD_HUI:
                debut = fin = today;
                break;
            case HIER:
                debut = fin = today.minusDays(1);
                break;
            case CETTE_SEMAINE:
                debut = today.minusDays(today.getDayOfWeek().getValue() - 1);
                fin = today;
                break;
            case SEMAINE_DERNIERE:
                LocalDate lundiDernier = today.minusDays(today.getDayOfWeek().getValue() + 6);
                debut = lundiDernier;
                fin = lundiDernier.plusDays(6);
                break;
            case CE_MOIS:
                debut = today.withDayOfMonth(1);
                fin = today;
                break;
            case MOIS_DERNIER:
                LocalDate premierJourMoisDernier = today.minusMonths(1).withDayOfMonth(1);
                debut = premierJourMoisDernier;
                fin = premierJourMoisDernier.plusMonths(1).minusDays(1);
                break;
            case TROIS_MOIS:
                debut = today.minusMonths(3);
                fin = today;
                break;
            case CETTE_ANNEE:
                debut = today.withDayOfYear(1);
                fin = today;
                break;
            default: // PERSONNALISE
                return; // Ne pas modifier les dates
        }
        
        if (selected != PeriodOption.PERSONNALISE) {
            dateDebutField.setText(debut.format(dateFormatter));
            dateFinField.setText(fin.format(dateFormatter));
            
            // Désactiver les champs de date pour les périodes prédéfinies
            dateDebutField.setEnabled(false);
            dateFinField.setEnabled(false);
        } else {
            // Période personnalisée - activer les champs
            dateDebutField.setEnabled(true);
            dateFinField.setEnabled(true);
        }
        
        updateCriteria();
    }
    
    private void updateCriteria() {
        // Mise à jour des dates
        try {
            String dateDebutText = dateDebutField.getText().trim();
            if (!dateDebutText.isEmpty()) {
                currentCriteria.dateDebut = LocalDate.parse(dateDebutText, dateFormatter);
            }
        } catch (Exception e) {
            // Ignorer les erreurs de formatage
        }
        
        try {
            String dateFinText = dateFinField.getText().trim();
            if (!dateFinText.isEmpty()) {
                currentCriteria.dateFin = LocalDate.parse(dateFinText, dateFormatter);
            }
        } catch (Exception e) {
            // Ignorer les erreurs de formatage
        }
        
        // Mise à jour des autres critères
        currentCriteria.typeVehicule = (String) typeVehiculeCombo.getSelectedItem();
        currentCriteria.statutVehicule = (String) statutVehiculeCombo.getSelectedItem();
        currentCriteria.typeEntretien = (String) typeEntretienCombo.getSelectedItem();
        currentCriteria.priorite = (String) prioriteCombo.getSelectedItem();
        currentCriteria.affectationsUniquement = affectationsCheck.isSelected();
        currentCriteria.entretiensDus = entretiensDusCheck.isSelected();
        
        // Notification des écouteurs (mode automatique)
        notifyListeners();
    }
    
    private void resetFilters() {
        // Réinitialiser aux valeurs par défaut
        FilterCriteria defaultCriteria = new FilterCriteria();
        applyCriteria(defaultCriteria);
        periodCombo.setSelectedItem(PeriodOption.CE_MOIS);
        updateDatesForPeriod();
    }
    
    private void applyFilters() {
        // Forcer la mise à jour et notification
        updateCriteria();
        // Double notification pour s'assurer que les changements sont pris en compte
        notifyListeners();
    }
    
    private void applyCriteria(FilterCriteria criteria) {
        currentCriteria = criteria;
        
        // Appliquer aux composants UI
        dateDebutField.setText(criteria.dateDebut.format(dateFormatter));
        dateFinField.setText(criteria.dateFin.format(dateFormatter));
        typeVehiculeCombo.setSelectedItem(criteria.typeVehicule);
        statutVehiculeCombo.setSelectedItem(criteria.statutVehicule);
        typeEntretienCombo.setSelectedItem(criteria.typeEntretien);
        prioriteCombo.setSelectedItem(criteria.priorite);
        affectationsCheck.setSelected(criteria.affectationsUniquement);
        entretiensDusCheck.setSelected(criteria.entretiensDus);
    }
    
    private void notifyListeners() {
        for (FilterChangeListener listener : listeners) {
            listener.onFilterChanged(currentCriteria);
        }
    }
    
    // Méthodes publiques
    public void addFilterChangeListener(FilterChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeFilterChangeListener(FilterChangeListener listener) {
        listeners.remove(listener);
    }
    
    public FilterCriteria getCurrentCriteria() {
        return currentCriteria;
    }
    
    public void setCriteria(FilterCriteria criteria) {
        applyCriteria(criteria);
        notifyListeners();
    }
    
    /**
     * Méthode pour définir les options disponibles pour un type de filtre
     */
    public void setTypeVehiculeOptions(String[] options) {
        typeVehiculeCombo.setModel(new DefaultComboBoxModel<>(options));
    }
    
    public void setStatutOptions(String[] options) {
        statutVehiculeCombo.setModel(new DefaultComboBoxModel<>(options));
    }
    
    public void setTypeEntretienOptions(String[] options) {
        typeEntretienCombo.setModel(new DefaultComboBoxModel<>(options));
    }
    
    /**
     * Méthode pour obtenir un résumé textuel des filtres actifs
     */
    public String getFilterSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (!currentCriteria.typeVehicule.equals("Tous")) {
            summary.append("Type: ").append(currentCriteria.typeVehicule).append(" | ");
        }
        
        if (!currentCriteria.statutVehicule.equals("Tous")) {
            summary.append("Statut: ").append(currentCriteria.statutVehicule).append(" | ");
        }
        
        if (currentCriteria.affectationsUniquement) {
            summary.append("Affectations uniquement | ");
        }
        
        if (currentCriteria.entretiensDus) {
            summary.append("Entretiens dus | ");
        }
        
        summary.append("Période: ").append(currentCriteria.dateDebut.format(dateFormatter))
               .append(" - ").append(currentCriteria.dateFin.format(dateFormatter));
        
        return summary.toString();
    }
}