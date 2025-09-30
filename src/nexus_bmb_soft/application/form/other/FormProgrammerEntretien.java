package nexus_bmb_soft.application.form.other;

import nexus_bmb_soft.database.dao.EntretienDAO;
import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Entretien;
import nexus_bmb_soft.models.Vehicule;
import nexus_bmb_soft.utils.IconUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Formulaire pour programmer un nouvel entretien
 * Style basé sur FormRechercheEtats - design cohérent
 * 
 * @author BlaiseMUBADI
 */
public class FormProgrammerEntretien extends JPanel {
    
    private EntretienDAO entretienDAO;
    private VehiculeDAO vehiculeDAO;
    
    // Composants de l'interface
    private JComboBox<String> cmbVehicule;
    private JComboBox<String> cmbTypeEntretien;
    private JSpinner spnDateEntretien;
    private JTextArea txtCommentaire;
    private JTextField txtCout;
    private JTextField txtKilometrage;
    private JComboBox<String> cmbStatut;
    private JButton btnProgrammer;
    private JButton btnReinitialiser;
    
    // Cache des véhicules
    private List<Vehicule> vehicules;
    
    public FormProgrammerEntretien() {
        entretienDAO = new EntretienDAO();
        vehiculeDAO = new VehiculeDAO();
        initComponents();
        chargerVehicules();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec bordure
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Titre principal
        JLabel lblTitrePrincipal = new JLabel("📅 Programmer un Entretien", JLabel.CENTER);
        lblTitrePrincipal.setFont(lblTitrePrincipal.getFont().deriveFont(Font.BOLD, 20f));
        lblTitrePrincipal.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblTitrePrincipal.setOpaque(true);
        lblTitrePrincipal.setBackground(new Color(46, 204, 113));
        lblTitrePrincipal.setForeground(Color.WHITE);
        mainPanel.add(lblTitrePrincipal, BorderLayout.NORTH);
        
        // Panel principal du contenu
        JPanel panelContenu = createPanelFormulaire();
        mainPanel.add(panelContenu, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPanelFormulaire() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel du formulaire avec GridBagLayout
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("📝 Informations de l'Entretien"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Véhicule
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel(" Véhicule :", IconUtils.createCarIcon(new Color(52, 73, 94), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbVehicule = new JComboBox<>();
        cmbVehicule.setFont(cmbVehicule.getFont().deriveFont(12f));
        cmbVehicule.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelForm.add(cmbVehicule, gbc);
        
        // Type d'entretien
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel(" Type :", IconUtils.createRefreshIcon(new Color(230, 126, 34), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbTypeEntretien = new JComboBox<>(new String[]{"Vidange", "Révision", "Pneus", "Freins", "Autre"});
        cmbTypeEntretien.setFont(cmbTypeEntretien.getFont().deriveFont(12f));
        cmbTypeEntretien.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelForm.add(cmbTypeEntretien, gbc);
        
        // Date d'entretien
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel(" Date :", IconUtils.createCalendarIcon(new Color(52, 152, 219), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        spnDateEntretien = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnDateEntretien, "dd/MM/yyyy");
        spnDateEntretien.setEditor(dateEditor);
        spnDateEntretien.setFont(spnDateEntretien.getFont().deriveFont(12f));
        panelForm.add(spnDateEntretien, gbc);
        
        // Kilométrage
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel(" Kilométrage :", IconUtils.createListIcon(new Color(155, 89, 182), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtKilometrage = new JTextField(15);
        txtKilometrage.setFont(txtKilometrage.getFont().deriveFont(12f));
        txtKilometrage.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelForm.add(txtKilometrage, gbc);
        
        // Coût estimé
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel(" Coût (€) :", IconUtils.createRefreshIcon(new Color(231, 76, 60), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCout = new JTextField(15);
        txtCout.setFont(txtCout.getFont().deriveFont(12f));
        txtCout.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelForm.add(txtCout, gbc);
        
        // Statut
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel(" Statut :", IconUtils.createListIcon(new Color(46, 204, 113), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbStatut = new JComboBox<>(new String[]{"programme", "en_cours", "termine"});
        cmbStatut.setFont(cmbStatut.getFont().deriveFont(12f));
        cmbStatut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelForm.add(cmbStatut, gbc);
        
        // Commentaire
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel(" Commentaire :", IconUtils.createListIcon(new Color(52, 73, 94), 16), JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtCommentaire = new JTextArea(4, 20);
        txtCommentaire.setFont(txtCommentaire.getFont().deriveFont(12f));
        txtCommentaire.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtCommentaire.setLineWrap(true);
        txtCommentaire.setWrapStyleWord(true);
        JScrollPane scrollCommentaire = new JScrollPane(txtCommentaire);
        scrollCommentaire.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panelForm.add(scrollCommentaire, gbc);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBoutons.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnProgrammer = new JButton(" Programmer", IconUtils.createCalendarIcon(Color.WHITE, 16));
        btnProgrammer.setFont(btnProgrammer.getFont().deriveFont(Font.BOLD, 14f));
        btnProgrammer.setBackground(new Color(46, 204, 113));
        btnProgrammer.setForeground(Color.WHITE);
        btnProgrammer.setFocusPainted(false);
        btnProgrammer.setPreferredSize(new Dimension(150, 40));
        btnProgrammer.addActionListener(e -> programmerEntretien());
        
        btnReinitialiser = new JButton(" Réinitialiser", IconUtils.createRefreshIcon(Color.WHITE, 16));
        btnReinitialiser.setFont(btnReinitialiser.getFont().deriveFont(Font.BOLD, 14f));
        btnReinitialiser.setBackground(new Color(255, 165, 0));
        btnReinitialiser.setForeground(Color.WHITE);
        btnReinitialiser.setFocusPainted(false);
        btnReinitialiser.setPreferredSize(new Dimension(150, 40));
        btnReinitialiser.addActionListener(e -> reinitialiserFormulaire());
        
        panelBoutons.add(btnProgrammer);
        panelBoutons.add(btnReinitialiser);
        
        panel.add(panelForm, BorderLayout.CENTER);
        panel.add(panelBoutons, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void chargerVehicules() {
        try {
            vehicules = vehiculeDAO.obtenirTousVehicules();
            cmbVehicule.removeAllItems();
            
            for (Vehicule vehicule : vehicules) {
                String affichage = vehicule.getMatricule() + " - " + vehicule.getMarque() + " (" + vehicule.getType() + ")";
                cmbVehicule.addItem(affichage);
            }
            
            if (vehicules.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun véhicule disponible. Veuillez d'abord ajouter des véhicules.", 
                    "Aucun véhicule", 
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des véhicules : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void programmerEntretien() {
        try {
            // Validation des champs
            if (cmbVehicule.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner un véhicule.", 
                    "Validation", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String coutText = txtCout.getText().trim();
            if (coutText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez saisir le coût estimé.", 
                    "Validation", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String kilometrageText = txtKilometrage.getText().trim();
            if (kilometrageText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez saisir le kilométrage.", 
                    "Validation", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Conversion des données
            Vehicule vehiculeSelectionne = vehicules.get(cmbVehicule.getSelectedIndex());
            String typeEntretien = (String) cmbTypeEntretien.getSelectedItem();
            Date dateSelectionne = (Date) spnDateEntretien.getValue();
            LocalDate dateEntretien = dateSelectionne.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String commentaire = txtCommentaire.getText().trim();
            double cout = Double.parseDouble(coutText);
            int kilometrage = Integer.parseInt(kilometrageText);
            String statut = (String) cmbStatut.getSelectedItem();
            
            // Création de l'entretien
            Entretien entretien = new Entretien();
            entretien.setVehiculeId(vehiculeSelectionne.getId());
            entretien.setTypeEntretien(typeEntretien);
            entretien.setDateEntretien(dateEntretien);
            entretien.setCommentaire(commentaire.isEmpty() ? null : commentaire);
            entretien.setCout(cout);
            entretien.setKilometrage(kilometrage);
            entretien.setStatut(statut);
            
            // Sauvegarde
            boolean succes = entretienDAO.ajouterEntretien(entretien);
            
            if (succes) {
                JOptionPane.showMessageDialog(this, 
                    "Entretien programmé avec succès !", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                reinitialiserFormulaire();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la programmation de l'entretien.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir des valeurs numériques valides pour le coût et le kilométrage.", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la programmation : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reinitialiserFormulaire() {
        cmbVehicule.setSelectedIndex(0);
        cmbTypeEntretien.setSelectedIndex(0);
        spnDateEntretien.setValue(new Date());
        txtCommentaire.setText("");
        txtCout.setText("");
        txtKilometrage.setText("");
        cmbStatut.setSelectedIndex(0);
    }
}