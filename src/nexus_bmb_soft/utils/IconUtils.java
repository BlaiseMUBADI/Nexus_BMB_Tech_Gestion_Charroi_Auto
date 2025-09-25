package nexus_bmb_soft.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Classe utilitaire pour créer des icônes personnalisées sans dépendances externes
 * Compatible avec NetBeans et sans FlatLaf
 * 
 * @author BlaiseMUBADI
 */
public class IconUtils {
    
    /**
     * Crée une icône de voiture
     */
    public static Icon createCarIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                // Corps de la voiture
                g2.fillRoundRect(x + 2, y + size/2, size - 4, size/3, 4, 4);
                // Toit
                g2.fillRoundRect(x + size/4, y + size/3, size/2, size/4, 4, 4);
                // Roues
                g2.fillOval(x + size/6, y + size*2/3, size/6, size/6);
                g2.fillOval(x + size*2/3, y + size*2/3, size/6, size/6);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône d'ajout (+)
     */
    public static Icon createAddIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                
                // Ligne horizontale
                g2.drawLine(x + size/4, y + size/2, x + size*3/4, y + size/2);
                // Ligne verticale
                g2.drawLine(x + size/2, y + size/4, x + size/2, y + size*3/4);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône de liste
     */
    public static Icon createListIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                
                // Trois lignes pour représenter une liste
                int lineY1 = y + size/4;
                int lineY2 = y + size/2;
                int lineY3 = y + size*3/4;
                
                g2.drawLine(x + size/6, lineY1, x + size*5/6, lineY1);
                g2.drawLine(x + size/6, lineY2, x + size*5/6, lineY2);
                g2.drawLine(x + size/6, lineY3, x + size*5/6, lineY3);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône de calendrier
     */
    public static Icon createCalendarIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                // Cadre du calendrier
                g2.drawRoundRect(x + 2, y + 4, size - 4, size - 6, 2, 2);
                // En-tête
                g2.fillRoundRect(x + 2, y + 4, size - 4, size/4, 2, 2);
                // Points pour les jours
                g2.fillOval(x + size/4, y + size/2, 2, 2);
                g2.fillOval(x + size/2, y + size/2, 2, 2);
                g2.fillOval(x + size*3/4, y + size/2, 2, 2);
                g2.fillOval(x + size/4, y + size*2/3, 2, 2);
                g2.fillOval(x + size/2, y + size*2/3, 2, 2);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône de sauvegarde
     */
    public static Icon createSaveIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                // Carré avec coin coupé (disquette)
                int[] xPoints = {x + 2, x + size - 6, x + size - 2, x + size - 2, x + 2};
                int[] yPoints = {y + 2, y + 2, y + 6, y + size - 2, y + size - 2};
                g2.fillPolygon(xPoints, yPoints, 5);
                
                // Petit carré au centre
                g2.setColor(Color.WHITE);
                g2.fillRect(x + size/3, y + size/3, size/3, size/4);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône de réinitialisation (flèche circulaire)
     */
    public static Icon createRefreshIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                
                // Arc circulaire
                g2.drawArc(x + 3, y + 3, size - 6, size - 6, 30, 300);
                
                // Flèche
                int[] xArrow = {x + size - 4, x + size - 8, x + size - 6};
                int[] yArrow = {y + size/3, y + size/4, y + size/2};
                g2.fillPolygon(xArrow, yArrow, 3);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône d'affectation (clé + voiture)
     */
    public static Icon createAssignmentIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                // Clé
                g2.fillOval(x + 2, y + 2, size/3, size/3);
                g2.drawLine(x + size/6, y + size/3, x + size/6, y + size*2/3);
                g2.drawLine(x + size/6, y + size/2, x + size/4, y + size/2);
                
                // Flèche d'assignation
                g2.drawLine(x + size/3, y + size/2, x + size*2/3, y + size/2);
                int[] xArrow = {x + size*2/3, x + size*2/3 - 3, x + size*2/3 - 3};
                int[] yArrow = {y + size/2, y + size/2 - 2, y + size/2 + 2};
                g2.fillPolygon(xArrow, yArrow, 3);
                
                // Voiture simplifiée
                g2.fillRoundRect(x + size*3/4, y + size*2/5, size/5, size/6, 2, 2);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône d'historique (horloge)
     */
    public static Icon createHistoryIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                
                // Cercle de l'horloge
                g2.drawOval(x + 2, y + 2, size - 4, size - 4);
                
                // Centre
                g2.fillOval(x + size/2 - 1, y + size/2 - 1, 2, 2);
                
                // Aiguille des heures
                g2.drawLine(x + size/2, y + size/2, x + size/2 + 2, y + size/2 - 3);
                
                // Aiguille des minutes
                g2.drawLine(x + size/2, y + size/2, x + size/2, y + size/2 - 5);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône de recherche (loupe)
     */
    public static Icon createSearchIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                
                // Cercle de la loupe
                g2.drawOval(x + 2, y + 2, size*2/3, size*2/3);
                
                // Manche de la loupe
                g2.drawLine(x + size*2/3, y + size*2/3, x + size - 2, y + size - 2);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    /**
     * Crée une icône d'utilisateur (personne)
     */
    public static Icon createUserIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                // Tête
                g2.fillOval(x + size/3, y + 2, size/3, size/3);
                
                // Corps (arc)
                g2.fillArc(x + size/6, y + size/2, size*2/3, size/2, 0, 180);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }
}