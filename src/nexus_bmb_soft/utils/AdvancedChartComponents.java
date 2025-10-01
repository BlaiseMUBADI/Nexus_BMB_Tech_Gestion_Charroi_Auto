package nexus_bmb_soft.utils;

import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import nexus_bmb_soft.database.dao.DashboardAdvancedDAO.DataPoint;

/**
 * Graphiques temporels avancés pour les tendances et évolutions
 * 
 * @author BlaiseMUBADI
 */
public class AdvancedChartComponents {
    
    /**
     * Graphique en courbe pour les tendances temporelles
     */
    public static class TrendLineChart extends JPanel {
        private List<DataPoint> dataPoints = new ArrayList<>();
        private String title = "Tendance";
        private String yAxisLabel = "Valeur";
        private Color lineColor = new Color(52, 152, 219);
        private Color fillColor = new Color(52, 152, 219, 30);
        private boolean showArea = true;
        private boolean animated = true;
        private float animationProgress = 0f;
        private Timer animationTimer;
        
        public TrendLineChart(String title, String yAxisLabel) {
            this.title = title;
            this.yAxisLabel = yAxisLabel;
            setPreferredSize(new Dimension(400, 250));
            setOpaque(false);
            initAnimation();
            
            // Tooltip pour afficher les valeurs
            setToolTipText("");
        }
        
        private void initAnimation() {
            animationTimer = new Timer(30, e -> {
                if (animationProgress < 1.0f) {
                    animationProgress += 0.05f;
                    if (animationProgress > 1.0f) animationProgress = 1.0f;
                    repaint();
                } else {
                    animationTimer.stop();
                }
            });
        }
        
        public void setData(List<DataPoint> dataPoints) {
            this.dataPoints = new ArrayList<>(dataPoints);
            if (animated && animationTimer != null) {
                animationProgress = 0f;
                animationTimer.restart();
            } else {
                repaint();
            }
        }
        
        public void setColors(Color lineColor, Color fillColor) {
            this.lineColor = lineColor;
            this.fillColor = fillColor;
            repaint();
        }
        
        @Override
        public String getToolTipText(MouseEvent event) {
            // Calculer le point de données le plus proche
            if (dataPoints.isEmpty()) return null;
            
            int chartX = 60;
            int chartWidth = getWidth() - 100;
            int mouseX = event.getX();
            
            if (mouseX < chartX || mouseX > chartX + chartWidth) return null;
            
            float relativeX = (float)(mouseX - chartX) / chartWidth;
            int index = Math.round(relativeX * (dataPoints.size() - 1));
            
            if (index >= 0 && index < dataPoints.size()) {
                DataPoint point = dataPoints.get(index);
                String dateStr = point.date != null ? 
                    point.date.format(DateTimeFormatter.ofPattern("dd/MM")) : 
                    point.label;
                return String.format("<html><b>%s</b><br/>%s: %d</html>", 
                    dateStr, yAxisLabel, point.value);
            }
            return null;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            int width = getWidth();
            int height = getHeight();
            int chartX = 60;
            int chartY = 40;
            int chartWidth = width - 100;
            int chartHeight = height - 80;
            
            // Titre
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2d.setColor(new Color(44, 62, 80));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, 20);
            
            if (dataPoints.isEmpty()) {
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                g2d.setColor(new Color(134, 142, 150));
                String noData = "Aucune donnée disponible";
                int noDataWidth = g2d.getFontMetrics().stringWidth(noData);
                g2d.drawString(noData, (width - noDataWidth) / 2, height / 2);
                g2d.dispose();
                return;
            }
            
            // Trouver min/max
            int maxValue = dataPoints.stream().mapToInt(p -> p.value).max().orElse(100);
            int minValue = dataPoints.stream().mapToInt(p -> p.value).min().orElse(0);
            int range = Math.max(1, maxValue - minValue);
            
            // Dessiner les axes
            g2d.setColor(new Color(223, 230, 233));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(chartX, chartY, chartX, chartY + chartHeight); // Axe Y
            g2d.drawLine(chartX, chartY + chartHeight, chartX + chartWidth, chartY + chartHeight); // Axe X
            
            // Lignes de grille horizontales
            g2d.setColor(new Color(236, 240, 241));
            for (int i = 1; i <= 4; i++) {
                int y = chartY + (chartHeight * i / 5);
                g2d.drawLine(chartX, y, chartX + chartWidth, y);
                
                // Labels Y
                int value = maxValue - (range * i / 5);
                g2d.setColor(new Color(134, 142, 150));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                String label = String.valueOf(value);
                int labelWidth = g2d.getFontMetrics().stringWidth(label);
                g2d.drawString(label, chartX - labelWidth - 5, y + 3);
                g2d.setColor(new Color(236, 240, 241));
            }
            
            // Construire le chemin de la courbe avec animation
            if (dataPoints.size() > 1) {
                Path2D.Double linePath = new Path2D.Double();
                Path2D.Double areaPath = new Path2D.Double();
                
                boolean first = true;
                int animatedPoints = Math.max(1, (int)(dataPoints.size() * animationProgress));
                
                for (int i = 0; i < animatedPoints; i++) {
                    DataPoint point = dataPoints.get(i);
                    float x = chartX + (float)i / (dataPoints.size() - 1) * chartWidth;
                    float y = chartY + chartHeight - (float)(point.value - minValue) / range * chartHeight;
                    
                    if (first) {
                        linePath.moveTo(x, y);
                        if (showArea) {
                            areaPath.moveTo(x, chartY + chartHeight);
                            areaPath.lineTo(x, y);
                        }
                        first = false;
                    } else {
                        linePath.lineTo(x, y);
                        if (showArea) {
                            areaPath.lineTo(x, y);
                        }
                    }
                }
                
                // Fermer la zone pour le remplissage
                if (showArea && animatedPoints > 1) {
                    DataPoint lastPoint = dataPoints.get(animatedPoints - 1);
                    float lastX = chartX + (float)(animatedPoints - 1) / (dataPoints.size() - 1) * chartWidth;
                    areaPath.lineTo(lastX, chartY + chartHeight);
                    areaPath.closePath();
                    
                    // Dessiner la zone remplie
                    g2d.setColor(fillColor);
                    g2d.fill(areaPath);
                }
                
                // Dessiner la ligne
                g2d.setColor(lineColor);
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(linePath);
                
                // Dessiner les points
                g2d.setColor(lineColor);
                for (int i = 0; i < animatedPoints; i++) {
                    DataPoint point = dataPoints.get(i);
                    float x = chartX + (float)i / (dataPoints.size() - 1) * chartWidth;
                    float y = chartY + chartHeight - (float)(point.value - minValue) / range * chartHeight;
                    
                    g2d.fillOval((int)x - 3, (int)y - 3, 6, 6);
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval((int)x - 2, (int)y - 2, 4, 4);
                    g2d.setColor(lineColor);
                }
            }
            
            // Labels de l'axe X (dates)
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2d.setColor(new Color(134, 142, 150));
            int step = Math.max(1, dataPoints.size() / 6); // Afficher max 6 labels
            for (int i = 0; i < dataPoints.size(); i += step) {
                DataPoint point = dataPoints.get(i);
                float x = chartX + (float)i / (dataPoints.size() - 1) * chartWidth;
                
                String dateLabel = point.date != null ? 
                    point.date.format(DateTimeFormatter.ofPattern("dd/MM")) : 
                    point.label.substring(0, Math.min(6, point.label.length()));
                
                int labelWidth = g2d.getFontMetrics().stringWidth(dateLabel);
                g2d.drawString(dateLabel, x - labelWidth / 2, chartY + chartHeight + 15);
            }
            
            g2d.dispose();
        }
    }
    
    /**
     * Heatmap pour visualiser l'activité par jour/heure
     */
    public static class ActivityHeatmap extends JPanel {
        private int[][] data = new int[7][24]; // 7 jours x 24 heures
        private String title = "Activité";
        private String[] dayLabels = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        private Color lowColor = new Color(233, 236, 239);
        private Color highColor = new Color(52, 152, 219);
        
        public ActivityHeatmap(String title) {
            this.title = title;
            setPreferredSize(new Dimension(600, 200));
            setOpaque(false);
            setToolTipText("");
        }
        
        public void setData(int[][] activityData) {
            this.data = activityData.clone();
            repaint();
        }
        
        @Override
        public String getToolTipText(MouseEvent event) {
            int cellWidth = (getWidth() - 100) / 24;
            int cellHeight = (getHeight() - 60) / 7;
            int startX = 60;
            int startY = 40;
            
            int col = (event.getX() - startX) / cellWidth;
            int row = (event.getY() - startY) / cellHeight;
            
            if (col >= 0 && col < 24 && row >= 0 && row < 7) {
                return String.format("<html><b>%s %dh</b><br/>Activité: %d</html>", 
                    dayLabels[row], col, data[row][col]);
            }
            return null;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int startX = 60;
            int startY = 40;
            int cellWidth = (width - 100) / 24;
            int cellHeight = (height - 60) / 7;
            
            // Titre
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.setColor(new Color(44, 62, 80));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, 20);
            
            // Trouver la valeur max pour la normalisation
            int maxValue = 0;
            for (int[] row : data) {
                for (int value : row) {
                    maxValue = Math.max(maxValue, value);
                }
            }
            
            if (maxValue == 0) maxValue = 1; // Éviter division par zéro
            
            // Dessiner les cellules
            for (int day = 0; day < 7; day++) {
                for (int hour = 0; hour < 24; hour++) {
                    int x = startX + hour * cellWidth;
                    int y = startY + day * cellHeight;
                    int value = data[day][hour];
                    
                    // Calculer la couleur basée sur l'intensité
                    float intensity = (float) value / maxValue;
                    Color cellColor = interpolateColor(lowColor, highColor, intensity);
                    
                    g2d.setColor(cellColor);
                    g2d.fillRect(x, y, cellWidth - 1, cellHeight - 1);
                    
                    // Bordure légère
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawRect(x, y, cellWidth - 1, cellHeight - 1);
                }
            }
            
            // Labels des jours
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.setColor(new Color(134, 142, 150));
            for (int day = 0; day < 7; day++) {
                int y = startY + day * cellHeight + cellHeight / 2 + 3;
                g2d.drawString(dayLabels[day], 10, y);
            }
            
            // Labels des heures (quelques-unes)
            for (int hour = 0; hour < 24; hour += 4) {
                int x = startX + hour * cellWidth + cellWidth / 2;
                g2d.drawString(hour + "h", x - 8, startY - 5);
            }
            
            g2d.dispose();
        }
        
        private Color interpolateColor(Color color1, Color color2, float factor) {
            factor = Math.max(0, Math.min(1, factor));
            int red = (int) (color1.getRed() + factor * (color2.getRed() - color1.getRed()));
            int green = (int) (color1.getGreen() + factor * (color2.getGreen() - color1.getGreen()));
            int blue = (int) (color1.getBlue() + factor * (color2.getBlue() - color1.getBlue()));
            return new Color(red, green, blue);
        }
    }
    
    /**
     * Graphique radar pour les performances multidimensionnelles
     */
    public static class RadarChart extends JPanel {
        private List<DataPoint> metrics = new ArrayList<>();
        private String title = "Performance";
        private Color fillColor = new Color(52, 152, 219, 50);
        private Color lineColor = new Color(52, 152, 219);
        
        public RadarChart(String title) {
            this.title = title;
            setPreferredSize(new Dimension(300, 300));
            setOpaque(false);
        }
        
        public void setMetrics(List<DataPoint> metrics) {
            this.metrics = new ArrayList<>(metrics);
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2 + 10;
            int radius = Math.min(width, height - 60) / 3;
            
            // Titre
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.setColor(new Color(44, 62, 80));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, 20);
            
            if (metrics.isEmpty()) return;
            
            int numMetrics = metrics.size();
            double angleStep = 2 * Math.PI / numMetrics;
            
            // Dessiner les cercles concentriques (grille)
            g2d.setColor(new Color(236, 240, 241));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 1; i <= 4; i++) {
                int r = radius * i / 4;
                g2d.drawOval(centerX - r, centerY - r, 2 * r, 2 * r);
            }
            
            // Dessiner les axes
            for (int i = 0; i < numMetrics; i++) {
                double angle = i * angleStep - Math.PI / 2;
                int x = centerX + (int) (radius * Math.cos(angle));
                int y = centerY + (int) (radius * Math.sin(angle));
                g2d.drawLine(centerX, centerY, x, y);
            }
            
            // Dessiner le polygone des données
            if (numMetrics >= 3) {
                Path2D.Double polygon = new Path2D.Double();
                boolean first = true;
                
                for (int i = 0; i < numMetrics; i++) {
                    DataPoint metric = metrics.get(i);
                    double angle = i * angleStep - Math.PI / 2;
                    double normalizedValue = Math.min(1.0, metric.percentage / 100.0);
                    int x = centerX + (int) (radius * normalizedValue * Math.cos(angle));
                    int y = centerY + (int) (radius * normalizedValue * Math.sin(angle));
                    
                    if (first) {
                        polygon.moveTo(x, y);
                        first = false;
                    } else {
                        polygon.lineTo(x, y);
                    }
                }
                polygon.closePath();
                
                // Remplir le polygone
                g2d.setColor(fillColor);
                g2d.fill(polygon);
                
                // Dessiner le contour
                g2d.setColor(lineColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(polygon);
                
                // Dessiner les points
                for (int i = 0; i < numMetrics; i++) {
                    DataPoint metric = metrics.get(i);
                    double angle = i * angleStep - Math.PI / 2;
                    double normalizedValue = Math.min(1.0, metric.percentage / 100.0);
                    int x = centerX + (int) (radius * normalizedValue * Math.cos(angle));
                    int y = centerY + (int) (radius * normalizedValue * Math.sin(angle));
                    
                    g2d.setColor(lineColor);
                    g2d.fillOval(x - 3, y - 3, 6, 6);
                }
            }
            
            // Labels des métriques
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.setColor(new Color(44, 62, 80));
            for (int i = 0; i < numMetrics; i++) {
                DataPoint metric = metrics.get(i);
                double angle = i * angleStep - Math.PI / 2;
                int labelRadius = radius + 20;
                int x = centerX + (int) (labelRadius * Math.cos(angle));
                int y = centerY + (int) (labelRadius * Math.sin(angle));
                
                String label = metric.label;
                int labelWidth = g2d.getFontMetrics().stringWidth(label);
                g2d.drawString(label, x - labelWidth / 2, y + 3);
            }
            
            g2d.dispose();
        }
    }
}