import javax.swing.*;
import java.awt.*;

public class TestSimpleInterface {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🧪 Test simple de l'interface sans base de données...");
                
                // Créer une interface de test qui simule notre problème
                JFrame frame = new JFrame("Test - Fonctionnalité des Panneaux");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                // Panel principal
                JPanel mainPanel = new JPanel(new BorderLayout());
                
                // Section Informations
                JPanel infoPanel = createInfoPanel();
                mainPanel.add(infoPanel, BorderLayout.NORTH);
                
                // Section Validation
                JPanel validationPanel = createValidationPanel();
                mainPanel.add(validationPanel, BorderLayout.CENTER);
                
                // Section boutons de test
                JPanel buttonsPanel = new JPanel();
                JButton btnTestUpdate = new JButton("🧪 Tester Mise à Jour");
                btnTestUpdate.addActionListener(e -> {
                    updateInfoLabels(infoPanel);
                    updateValidationStatus(validationPanel, true);
                });
                
                JButton btnTestError = new JButton("❌ Simuler Erreur");
                btnTestError.addActionListener(e -> {
                    updateValidationStatus(validationPanel, false);
                });
                
                buttonsPanel.add(btnTestUpdate);
                buttonsPanel.add(btnTestError);
                mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
                
                frame.add(mainPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                System.out.println("✅ Interface de test créée avec succès !");
                
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la création de l'interface de test:");
                e.printStackTrace();
            }
        });
    }
    
    private static JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("ℹ️ Informations"));
        
        JLabel lblVehicules = new JLabel("Véhicules disponibles: 0");
        lblVehicules.setName("lblVehicules"); // Pour identifier plus tard
        lblVehicules.setForeground(new Color(46, 204, 113));
        panel.add(lblVehicules);
        
        JLabel lblConducteurs = new JLabel("Conducteurs actifs: 0");
        lblConducteurs.setName("lblConducteurs");
        lblConducteurs.setForeground(new Color(52, 152, 219));
        panel.add(lblConducteurs);
        
        return panel;
    }
    
    private static JPanel createValidationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("✅ Validation"));
        
        JLabel lblStatus = new JLabel("⏳ En attente de validation...", JLabel.CENTER);
        lblStatus.setName("lblStatus");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblStatus, BorderLayout.CENTER);
        
        JButton btnAction = new JButton("💾 Action Désactivée");
        btnAction.setName("btnAction");
        btnAction.setEnabled(false);
        btnAction.setBackground(new Color(127, 127, 127));
        btnAction.setForeground(Color.WHITE);
        panel.add(btnAction, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private static void updateInfoLabels(JPanel infoPanel) {
        // Simuler des données
        int vehiculesDispos = 2;
        int conducteursActifs = 3;
        
        for (Component comp : infoPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if ("lblVehicules".equals(label.getName())) {
                    label.setText("Véhicules disponibles: " + vehiculesDispos);
                    label.setForeground(vehiculesDispos > 0 ? new Color(46, 204, 113) : new Color(231, 76, 60));
                }
                if ("lblConducteurs".equals(label.getName())) {
                    label.setText("Conducteurs actifs: " + conducteursActifs);
                    label.setForeground(conducteursActifs > 0 ? new Color(52, 152, 219) : new Color(231, 76, 60));
                }
            }
        }
        System.out.println("✅ Compteurs mis à jour: " + vehiculesDispos + " véhicules, " + conducteursActifs + " conducteurs");
    }
    
    private static void updateValidationStatus(JPanel validationPanel, boolean isValid) {
        for (Component comp : validationPanel.getComponents()) {
            if (comp instanceof JLabel && "lblStatus".equals(comp.getName())) {
                JLabel label = (JLabel) comp;
                if (isValid) {
                    label.setText("✅ Prêt pour sauvegarde");
                    label.setForeground(new Color(34, 139, 34));
                } else {
                    label.setText("❌ Informations manquantes");
                    label.setForeground(Color.RED);
                }
            }
            if (comp instanceof JButton && "btnAction".equals(comp.getName())) {
                JButton button = (JButton) comp;
                button.setEnabled(isValid);
                if (isValid) {
                    button.setText("💾 Créer Affectation");
                    button.setBackground(new Color(46, 204, 113));
                } else {
                    button.setText("💾 Action Désactivée");
                    button.setBackground(new Color(127, 127, 127));
                }
            }
        }
        System.out.println("✅ Statut de validation mis à jour: " + (isValid ? "Valide" : "Invalide"));
    }
}