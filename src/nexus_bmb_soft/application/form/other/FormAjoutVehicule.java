package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Vehicule;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Formulaire pour ajouter un nouveau véhicule
 * 
 * @author BlaiseMUBADI
 */
public class FormAjoutVehicule extends JPanel {
    
    private JTextField txtMatricule;
    private JTextField txtMarque;
    private JTextField txtType;
    private JTextField txtAnnee;
    private JCheckBox chkDisponible;
    private JTextField txtDateAssurance;
    private JTextField txtDateVidange;
    private JTextField txtDateVisite;
    private JButton btnSauvegarder;
    private JButton btnAnnuler;
    private JButton btnReinitialiser;
    
    private VehiculeDAO vehiculeDAO;
    
    public FormAjoutVehicule() {
        vehiculeDAO = new VehiculeDAO();
        init();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        // Panel principal avec titre
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre
        JLabel lblTitre = new JLabel("➕ Ajouter un Véhicule");
        lblTitre.setFont(lblTitre.getFont().deriveFont(Font.BOLD, 18f));
        lblTitre.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel de saisie
        JPanel panelSaisie = createSaisiePanel();
        
        // Panel des boutons
        JPanel panelBoutons = createButtonPanel();
        
        mainPanel.add(lblTitre, BorderLayout.NORTH);
        mainPanel.add(panelSaisie, BorderLayout.CENTER);
        mainPanel.add(panelBoutons, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSaisiePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Matricule (obligatoire)
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("🚗 Matricule :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMatricule = new JTextField(20);
        panel.add(txtMatricule, gbc);
        row++;
        
        // Marque (obligatoire)
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🏭 Marque :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMarque = new JTextField(20);
        panel.add(txtMarque, gbc);
        row++;
        
        // Type
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🚛 Type :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtType = new JTextField(20);
        panel.add(txtType, gbc);
        row++;
        
        // Année
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("📅 Année :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtAnnee = new JTextField(20);
        panel.add(txtAnnee, gbc);
        row++;
        
        // Disponible
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("✅ Disponible :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        chkDisponible = new JCheckBox("Véhicule disponible pour affectation");
        chkDisponible.setSelected(true); // Par défaut disponible
        panel.add(chkDisponible, gbc);
        row++;
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSeparator(), gbc);
        row++;
        
        // Section dates
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblDates = new JLabel("📋 Informations Maintenance");
        lblDates.setFont(lblDates.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblDates, gbc);
        row++;
        
        // Date assurance
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🛡️ Date Assurance :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDateAssurance = new JTextField(20);
        panel.add(txtDateAssurance, gbc);
        row++;
        
        // Date vidange
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🔧 Date Vidange :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDateVidange = new JTextField(20);
        panel.add(txtDateVidange, gbc);
        row++;
        
        // Date visite technique
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🔍 Visite Technique :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDateVisite = new JTextField(20);
        panel.add(txtDateVisite, gbc);
        row++;
        
        // Note d'aide
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblAide = new JLabel("💡 Les champs Matricule et Marque sont obligatoires");
        lblAide.setFont(lblAide.getFont().deriveFont(Font.ITALIC, 12f));
        lblAide.setForeground(Color.BLUE);
        panel.add(lblAide, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        
        // Bouton Réinitialiser
        btnReinitialiser = new JButton("🔄 Réinitialiser");
        btnReinitialiser.addActionListener(e -> reinitialiserFormulaire());
        
        // Bouton Annuler
        btnAnnuler = new JButton("❌ Annuler");
        btnAnnuler.addActionListener(e -> annulerSaisie());
        
        // Bouton Sauvegarder
        btnSauvegarder = new JButton("💾 Sauvegarder");
        btnSauvegarder.setBackground(Color.BLUE);
        btnSauvegarder.setForeground(Color.WHITE);
        btnSauvegarder.addActionListener(e -> sauvegarderVehicule());
        
        panel.add(btnReinitialiser);
        panel.add(btnAnnuler);
        panel.add(btnSauvegarder);
        
        return panel;
    }
    
    private void sauvegarderVehicule() {
        if (!validerSaisie()) {
            return;
        }
        
        try {
            // Créer l'objet véhicule
            Vehicule vehicule = new Vehicule(
                0, // ID sera généré automatiquement
                txtMatricule.getText().trim().toUpperCase(),
                txtMarque.getText().trim(),
                txtType.getText().trim().isEmpty() ? null : txtType.getText().trim(),
                parseAnnee(),
                chkDisponible.isSelected(),
                parseDate(txtDateAssurance.getText()),
                parseDate(txtDateVidange.getText()),
                parseDate(txtDateVisite.getText())
            );
            
            // Sauvegarder en base
            if (vehiculeDAO.ajouterVehicule(vehicule)) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Véhicule '" + vehicule.getMatricule() + "' ajouté avec succès !",
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                reinitialiserFormulaire();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de l'ajout du véhicule.\nVérifiez que le matricule n'existe pas déjà.",
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Erreur inattendue : " + e.getMessage(),
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validerSaisie() {
        // Vérifier matricule
        if (txtMatricule.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Le matricule est obligatoire !",
                "Validation", 
                JOptionPane.WARNING_MESSAGE);
            txtMatricule.requestFocus();
            return false;
        }
        
        // Vérifier marque
        if (txtMarque.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ La marque est obligatoire !",
                "Validation", 
                JOptionPane.WARNING_MESSAGE);
            txtMarque.requestFocus();
            return false;
        }
        
        // Vérifier l'année si saisie
        if (!txtAnnee.getText().trim().isEmpty()) {
            try {
                int annee = Integer.parseInt(txtAnnee.getText().trim());
                int anneeActuelle = LocalDate.now().getYear();
                if (annee < 1900 || annee > anneeActuelle + 1) {
                    JOptionPane.showMessageDialog(this, 
                        "⚠️ Année invalide ! (entre 1900 et " + (anneeActuelle + 1) + ")",
                        "Validation", 
                        JOptionPane.WARNING_MESSAGE);
                    txtAnnee.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ L'année doit être un nombre !",
                    "Validation", 
                    JOptionPane.WARNING_MESSAGE);
                txtAnnee.requestFocus();
                return false;
            }
        }
        
        // Vérifier les dates si saisies
        if (!validerDate(txtDateAssurance, "Date d'assurance")) return false;
        if (!validerDate(txtDateVidange, "Date de vidange")) return false;
        if (!validerDate(txtDateVisite, "Date de visite technique")) return false;
        
        return true;
    }
    
    private boolean validerDate(JTextField txtDate, String nomChamp) {
        if (!txtDate.getText().trim().isEmpty()) {
            try {
                parseDate(txtDate.getText());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ " + nomChamp + " invalide !\nFormat attendu: jj/mm/aaaa",
                    "Validation", 
                    JOptionPane.WARNING_MESSAGE);
                txtDate.requestFocus();
                return false;
            }
        }
        return true;
    }
    
    private Integer parseAnnee() {
        if (txtAnnee.getText().trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(txtAnnee.getText().trim());
    }
    
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        // Format attendu: jj/mm/aaaa
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateStr.trim(), formatter);
    }
    
    private void reinitialiserFormulaire() {
        txtMatricule.setText("");
        txtMarque.setText("");
        txtType.setText("");
        txtAnnee.setText("");
        chkDisponible.setSelected(true);
        txtDateAssurance.setText("");
        txtDateVidange.setText("");
        txtDateVisite.setText("");
        txtMatricule.requestFocus();
    }
    
    private void annulerSaisie() {
        reinitialiserFormulaire();
        // Ici on pourrait ajouter une navigation vers la liste des véhicules
        // ou fermer le formulaire selon les besoins de l'application
    }
}