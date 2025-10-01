package nexus_bmb_soft.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import nexus_bmb_soft.application.Application;
import nexus_bmb_soft.security.AuthenticationDAO;
import nexus_bmb_soft.models.Utilisateur;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Formulaire de login
 * 
 * @author BlaiseMUBADI
 */
public class LoginForm extends javax.swing.JPanel {

    private AuthenticationDAO authDAO;
    private Utilisateur currentUser;

    public LoginForm() {
        initComponents();
        init();
        authDAO = new AuthenticationDAO();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));

        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$h1.font");
        
        txtPass.putClientProperty(FlatClientProperties.STYLE, ""
                + "showRevealButton:true;"
                + "showCapsLock:true");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, ""
                + "focusWidth:0");
        txtUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Matricule ou Email");
        txtPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mot de passe");
        
        // Permettre la connexion avec la touche Entrée
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        };
        
        txtUser.addKeyListener(enterKeyListener);
        txtPass.addKeyListener(enterKeyListener);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelLogin1 = new nexus_bmb_soft.application.form.PanelLogin();
        lbTitle = new javax.swing.JLabel();
        lbUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lbPass = new javax.swing.JLabel();
        txtPass = new javax.swing.JPasswordField();
        cmdLogin = new javax.swing.JButton();

        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitle.setText("Login");
        panelLogin1.add(lbTitle);

        lbUser.setText("User Name");
        panelLogin1.add(lbUser);
        panelLogin1.add(txtUser);

        lbPass.setText("Password");
        panelLogin1.add(lbPass);
        panelLogin1.add(txtPass);

        cmdLogin.setText("Login");
        cmdLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLoginActionPerformed(evt);
            }
        });
        panelLogin1.add(cmdLogin);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(218, Short.MAX_VALUE)
                .addComponent(panelLogin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(197, 197, 197))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(panelLogin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(96, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLoginActionPerformed
        performLogin();
    }//GEN-LAST:event_cmdLoginActionPerformed
    
    /**
     * Effectue l'authentification avec validation des identifiants
     */
    private void performLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        
        // Validation des champs
        if (username.isEmpty()) {
            showError("Veuillez saisir votre nom d'utilisateur ou email.");
            txtUser.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Veuillez saisir votre mot de passe.");
            txtPass.requestFocus();
            return;
        }
        
        // Désactiver le bouton pendant l'authentification
        cmdLogin.setEnabled(false);
        cmdLogin.setText("Connexion...");
        
        // Authentification en arrière-plan pour ne pas bloquer l'UI
        SwingWorker<AuthenticationDAO.AuthResult, Void> worker = new SwingWorker<AuthenticationDAO.AuthResult, Void>() {
            @Override
            protected AuthenticationDAO.AuthResult doInBackground() throws Exception {
                return authDAO.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    AuthenticationDAO.AuthResult result = get();
                    
                    if (result.isSuccess()) {
                        currentUser = result.getUser();
                        showSuccess("Connexion réussie ! Bienvenue " + currentUser.getNomComplet());
                        
                        // Créer une session
                        String sessionToken = authDAO.createSession(
                            currentUser.getId(), 
                            "localhost", 
                            "Desktop Application"
                        );
                        
                        if (sessionToken != null) {
                            System.out.println("🔐 Session créée: " + sessionToken.substring(0, 8) + "...");
                            System.out.println("👤 Utilisateur connecté: " + currentUser.getNomComplet() + 
                                             " (" + currentUser.getRole().toString() + ")");
                        }
                        
                        // Passer à l'interface principale
                        Application.login();
                        
                    } else {
                        showError(result.getMessage());
                        txtPass.setText(""); // Effacer le mot de passe
                        txtUser.requestFocus();
                    }
                    
                } catch (Exception e) {
                    showError("Erreur lors de la connexion: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // Réactiver le bouton
                    cmdLogin.setEnabled(true);
                    cmdLogin.setText("Login");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Affiche un message de succès
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Connexion réussie", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Retourne l'utilisateur actuellement connecté
     */
    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdLogin;
    private javax.swing.JLabel lbPass;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbUser;
    private nexus_bmb_soft.application.form.PanelLogin panelLogin1;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
