package nexus_bmb_soft.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import nexus_bmb_soft.application.Application;
import nexus_bmb_soft.application.form.other.FormDashboard;
import nexus_bmb_soft.application.form.other.FormInbox;
import nexus_bmb_soft.application.form.other.FormRead;
import nexus_bmb_soft.application.form.other.FormParcAutomobile;
import nexus_bmb_soft.application.form.other.FormGestionVehicules;
import nexus_bmb_soft.application.form.other.FormRechercheEtats;
import nexus_bmb_soft.application.form.other.FormAffectations;
import nexus_bmb_soft.application.form.other.FormEntretien;
import nexus_bmb_soft.application.form.other.DefaultForm;
import nexus_bmb_soft.menu.Menu;
import nexus_bmb_soft.menu.MenuAction;

/**
 *
 * @author Raven
 */
public class MainForm extends JLayeredPane {

    public MainForm() {
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        menu = new Menu();
        panelBody = new JPanel(new BorderLayout());
        initMenuArrowIcon();
        menuButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.button.background;"
                + "arc:999;"
                + "focusWidth:0;"
                + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });
        initMenuEvent();
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(menu);
        add(panelBody);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        initMenuArrowIcon();
    }

    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
        menuButton.setIcon(new FlatSVGIcon("nexus_bmb_soft/icon/svg/" + icon, 0.8f));
    }

    private void initMenuEvent() {
        menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
            System.out.println("Menu cliqué - Index: " + index + ", SubIndex: " + subIndex);
            
            if (index == 0) { // TABLEAU DE BORD
                if (subIndex == 0) {
                    Application.showForm(new FormDashboard());
                } else {
                    action.cancel();
                }
            } else if (index == 1) { // GESTION VÉHICULES - Parc Automobile
                if (subIndex == 0) {
                    // Menu principal : Interface unifiée de gestion des véhicules
                    Application.showForm(new FormGestionVehicules());
                } else if (subIndex == 1) {
                    // Sous-menu "Gestion Véhicules" : Interface unifiée
                    Application.showForm(new FormGestionVehicules());
                } else if (subIndex == 2) {
                    // Sous-menu "Recherche & États" : Interface unifiée
                    Application.showForm(new FormRechercheEtats());
                } else {
                    // Autres sous-menus (à implémenter plus tard)
                    Application.showForm(new DefaultForm("Parc Auto - Sous-menu " + subIndex));
                }
            } else if (index == 2) { // OPÉRATIONS - Affectations
                if (subIndex == 0) {
                    Application.showForm(new FormAffectations());
                } else {
                    // Sous-menus des affectations (à implémenter plus tard)
                    Application.showForm(new DefaultForm("Affectations - Sous-menu " + subIndex));
                }
            } else if (index == 3) { // OPÉRATIONS - Entretien & Maintenance
                if (subIndex == 0) {
                    Application.showForm(new FormEntretien());
                } else {
                    // Sous-menus de l'entretien (à implémenter plus tard)
                    Application.showForm(new DefaultForm("Entretien - Sous-menu " + subIndex));
                }
            } else if (index == 4) { // ADMINISTRATION - Utilisateurs
                Application.showForm(new DefaultForm("Gestion Utilisateurs - En cours de développement"));
            } else if (index == 5) { // ADMINISTRATION - Journalisation
                Application.showForm(new DefaultForm("Journalisation - En cours de développement"));
            } else if (index == 6) { // RAPPORTS
                Application.showForm(new DefaultForm("Export & Rapports - En cours de développement"));
            } else if (index == 7) { // SYSTÈME - Déconnexion
                Application.logout();
            } else {
                action.cancel();
            }
        });
    }

    private void setMenuFull(boolean full) {
        String icon;
        if (getComponentOrientation().isLeftToRight()) {
            icon = (full) ? "menu_left.svg" : "menu_right.svg";
        } else {
            icon = (full) ? "menu_right.svg" : "menu_left.svg";
        }
        menuButton.setIcon(new FlatSVGIcon("nexus_bmb_soft/icon/svg/" + icon, 0.8f));
        menu.setMenuFull(full);
        revalidate();
    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    public void setSelectedMenu(int index, int subIndex) {
        menu.setSelectedMenu(index, subIndex);
    }

    private Menu menu;
    private JPanel panelBody;
    private JButton menuButton;

    private class MainFormLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                menu.setBounds(menuX, y, menuWidth, height);
                int menuButtonWidth = menuButton.getPreferredSize().width;
                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menubX;
                if (ltr) {
                    menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
                    menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
                menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }
}
