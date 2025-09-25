import javax.swing.*;
import nexus_bmb_soft.application.form.other.FormNouvelleAffectation;

public class TestFormNouvelleAffectation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Test - Nouvelle Affectation");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                FormNouvelleAffectation form = new FormNouvelleAffectation();
                frame.add(form);
                
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                System.out.println("✅ FormNouvelleAffectation créé avec succès - pas de NullPointerException");
                
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la création du formulaire:");
                e.printStackTrace();
            }
        });
    }
}