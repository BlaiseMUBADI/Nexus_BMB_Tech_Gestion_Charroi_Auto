package nexus_bmb_soft.utils;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Composants graphiques modernes pour le tableau de bord
 * 
 * @author BlaiseMUBADI
 */
public class ModernChartComponents {
    
    /**
     * Graphique circulaire moderne avec animations
     */
    public static class ModernPieChart extends JPanel {
        private List<PieSlice> slices = new ArrayList<>();
        private String title = "Graphique";
        private boolean showLabels = true;
        private boolean animated = true;
        private float animationProgress = 0f;
        private Timer animationTimer;
        
        public ModernPieChart(String title) {
            this.title = title;
            setPreferredSize(new Dimension(300, 280));
            setOpaque(false);
            initAnimation();
        }
        
        private void initAnimation() {
            animationTimer = new Timer(20, e -> {
                if (animationProgress < 1.0f) {
                    animationProgress += 0.03f;
                    if (animationProgress > 1.0f) animationProgress = 1.0f;
                    repaint();
                } else {
                    animationTimer.stop();
                }
            });
        }
        
        public void addSlice(String label, double value, Color color) {
            slices.add(new PieSlice(label, value, color));
            if (animated && animationTimer != null) {
                animationProgress = 0f;
                animationTimer.restart();
            }
        }
        
        public void clearSlices() {
            slices.clear();
            animationProgress = 0f;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2 + 15;
            int radius = Math.min(width, height - 60) / 3;
            
            // Titre
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2d.setColor(new Color(44, 62, 80));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, 20);
            
            if (slices.isEmpty()) {
                g2d.dispose();
                return;
            }
            
            // Calculer le total
            double total = slices.stream().mapToDouble(s -> s.value).sum();
            if (total == 0) {
                g2d.dispose();
                return;
            }
            
            // Dessiner les tranches
            double currentAngle = -90; // Commencer en haut
            
            for (PieSlice slice : slices) {
                double sliceAngle = (slice.value / total) * 360 * animationProgress;
                
                // Dessiner la tranche
                Arc2D.Double arc = new Arc2D.Double(
                    centerX - radius, centerY - radius,
                    radius * 2, radius * 2,
                    currentAngle, sliceAngle, Arc2D.PIE
                );
                
                // Gradient pour effet moderne
                Point2D center = new Point2D.Float(centerX, centerY);
                Point2D edge = new Point2D.Float(centerX + radius, centerY);
                RadialGradientPaint gradient = new RadialGradientPaint(
                    center, radius,
                    new float[]{0f, 1f},
                    new Color[]{slice.color, slice.color.darker()}
                );
                
                g2d.setPaint(gradient);
                g2d.fill(arc);
                
                // Contour
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(arc);
                
                currentAngle += sliceAngle;
            }
            
            // Légendes
            if (showLabels) {
                drawLegend(g2d, width, height);
            }
            
            g2d.dispose();
        }
        
        private void drawLegend(Graphics2D g2d, int width, int height) {
            int legendY = height - 80;
            int legendX = 20;
            int itemHeight = 18;
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            for (int i = 0; i < slices.size(); i++) {
                PieSlice slice = slices.get(i);
                
                // Carré coloré
                g2d.setColor(slice.color);
                g2d.fillRect(legendX, legendY + i * itemHeight, 12, 12);
                
                // Texte
                g2d.setColor(new Color(44, 62, 80));
                String text = slice.label + " (" + (int)slice.value + ")";
                g2d.drawString(text, legendX + 18, legendY + i * itemHeight + 10);
            }
        }
        
        private static class PieSlice {
            String label;
            double value;
            Color color;
            
            PieSlice(String label, double value, Color color) {
                this.label = label;
                this.value = value;
                this.color = color;
            }
        }
    }
    
    /**
     * Carte KPI moderne avec icône et animation
     */
    public static class ModernKPICard extends JPanel {
        private String title;
        private String value;
        private String subtitle;
        private Color accentColor;
        private String icon;
        private boolean isHovered = false;
        
        public ModernKPICard(String title, String value, String subtitle, Color accentColor, String icon) {
            this.title = title;
            this.value = value;
            this.subtitle = subtitle;
            this.accentColor = accentColor;
            this.icon = icon;
            
            setPreferredSize(new Dimension(200, 120));
            setBorder(new EmptyBorder(15, 15, 15, 15));
            setBackground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        public void updateValue(String newValue) {
            this.value = newValue;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Fond avec effet hover
            Color bgColor = isHovered ? new Color(248, 249, 250) : Color.WHITE;
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, width, height, 12, 12);
            
            // Bordure colorée à gauche
            g2d.setColor(accentColor);
            g2d.fillRoundRect(0, 0, 4, height, 2, 2);
            
            // Icône
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            g2d.setColor(accentColor);
            g2d.drawString(icon, 15, 35);
            
            // Titre
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2d.setColor(new Color(108, 117, 125));
            g2d.drawString(title.toUpperCase(), 50, 20);
            
            // Valeur principale
            g2d.setFont(new Font("Segoe UI", Font.BOLD, isHovered ? 34 : 32));
            g2d.setColor(new Color(33, 37, 41));
            g2d.drawString(value, 50, 55);
            
            // Sous-titre
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.setColor(new Color(134, 142, 150));
            g2d.drawString(subtitle, 15, height - 15);
            
            // Ombre légère
            if (isHovered) {
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.drawRoundRect(1, 1, width - 2, height - 2, 12, 12);
            }
            
            g2d.dispose();
        }
    }
    
    /**
     * Barre de progression circulaire moderne
     */
    public static class CircularProgressBar extends JPanel {
        private double progress = 0.0;
        private String label = "";
        private Color progressColor = new Color(52, 152, 219);
        private Color backgroundColor = new Color(236, 240, 241);
        private boolean animated = true;
        private Timer animationTimer;
        private double targetProgress = 0.0;
        
        public CircularProgressBar(String label, Color color) {
            this.label = label;
            this.progressColor = color;
            setPreferredSize(new Dimension(120, 120));
            setOpaque(false);
        }
        
        public void setProgress(double progress) {
            this.targetProgress = Math.max(0, Math.min(100, progress));
            
            if (animated) {
                if (animationTimer != null) animationTimer.stop();
                
                animationTimer = new Timer(20, e -> {
                    double diff = targetProgress - this.progress;
                    if (Math.abs(diff) < 0.5) {
                        this.progress = targetProgress;
                        animationTimer.stop();
                    } else {
                        this.progress += diff * 0.1;
                    }
                    repaint();
                });
                animationTimer.start();
            } else {
                this.progress = targetProgress;
                repaint();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            int strokeWidth = 8;
            
            // Cercle de fond
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(backgroundColor);
            g2d.drawOval(x + strokeWidth/2, y + strokeWidth/2, size - strokeWidth, size - strokeWidth);
            
            // Arc de progression
            if (progress > 0) {
                g2d.setColor(progressColor);
                double angle = (progress / 100.0) * 360;
                g2d.drawArc(x + strokeWidth/2, y + strokeWidth/2, size - strokeWidth, size - strokeWidth, 
                           90, -(int)angle);
            }
            
            // Texte central
            g2d.setColor(new Color(44, 62, 80));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            String percentage = String.format("%.0f%%", progress);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(percentage);
            g2d.drawString(percentage, x + (size - textWidth) / 2, y + size / 2 + 5);
            
            // Label
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.setColor(new Color(108, 117, 125));
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, x + (size - labelWidth) / 2, y + size / 2 + 25);
            
            g2d.dispose();
        }
    }
    
    /**
     * Graphique en barres horizontal moderne
     */
    public static class ModernBarChart extends JPanel {
        private List<BarData> bars = new ArrayList<>();
        private String title = "Graphique";
        private int maxValue = 100;
        
        public ModernBarChart(String title) {
            this.title = title;
            setPreferredSize(new Dimension(350, 200));
            setOpaque(false);
        }
        
        public void addBar(String label, int value, Color color) {
            bars.add(new BarData(label, value, color));
            maxValue = Math.max(maxValue, value);
            repaint();
        }
        
        public void clearBars() {
            bars.clear();
            maxValue = 100;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Titre
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2d.setColor(new Color(44, 62, 80));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, 20);
            
            if (bars.isEmpty()) {
                g2d.dispose();
                return;
            }
            
            int barHeight = 25;
            int barSpacing = 35;
            int startY = 40;
            int barAreaWidth = width - 120;
            
            for (int i = 0; i < bars.size(); i++) {
                BarData bar = bars.get(i);
                int y = startY + i * barSpacing;
                
                // Label
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2d.setColor(new Color(73, 80, 87));
                g2d.drawString(bar.label, 10, y + 15);
                
                // Barre de fond
                g2d.setColor(new Color(233, 236, 239));
                g2d.fillRoundRect(100, y, barAreaWidth, barHeight, 12, 12);
                
                // Barre de valeur
                int barWidth = (int) ((double) bar.value / maxValue * barAreaWidth);
                GradientPaint gradient = new GradientPaint(
                    100, y, bar.color.brighter(),
                    100 + barWidth, y, bar.color
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(100, y, barWidth, barHeight, 12, 12);
                
                // Valeur
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2d.setColor(Color.WHITE);
                String valueStr = String.valueOf(bar.value);
                if (barWidth > 30) {
                    g2d.drawString(valueStr, 105, y + 15);
                } else {
                    g2d.setColor(new Color(73, 80, 87));
                    g2d.drawString(valueStr, 105 + barWidth, y + 15);
                }
            }
            
            g2d.dispose();
        }
        
        private static class BarData {
            String label;
            int value;
            Color color;
            
            BarData(String label, int value, Color color) {
                this.label = label;
                this.value = value;
                this.color = color;
            }
        }
    }
}