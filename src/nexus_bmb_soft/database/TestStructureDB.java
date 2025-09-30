package nexus_bmb_soft.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Test pour vérifier la structure de la base de données
 * et diagnostiquer les problèmes de compatibilité
 */
public class TestStructureDB {
    
    public static void main(String[] args) {
        System.out.println("=== DIAGNOSTIC STRUCTURE BASE DE DONNÉES ===\n");
        
        try {
            verifierStructureVehicule();
            listerTables();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du diagnostic : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Vérifie la structure de la table vehicule
     */
    private static void verifierStructureVehicule() throws SQLException {
        System.out.println("🔍 Structure de la table 'vehicule' :");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "vehicule", null);
            
            System.out.println("Colonnes trouvées :");
            boolean hasDisponible = false;
            boolean hasStatut = false;
            
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String isNullable = columns.getString("IS_NULLABLE");
                
                System.out.println("   • " + columnName + " (" + columnType + ") - Nullable: " + isNullable);
                
                if ("disponible".equals(columnName)) hasDisponible = true;
                if ("statut".equals(columnName)) hasStatut = true;
            }
            
            System.out.println("\n📊 Analyse de compatibilité :");
            System.out.println("   • Champ 'disponible' (boolean) : " + (hasDisponible ? "✅ Présent" : "❌ Absent"));
            System.out.println("   • Champ 'statut' (enum) : " + (hasStatut ? "✅ Présent" : "❌ Absent"));
            
            if (!hasDisponible) {
                System.out.println("\n⚠️  SOLUTION : Exécutez le script 'migration_compatibilite.sql'");
            }
        }
    }
    
    /**
     * Liste toutes les tables de la base
     */
    private static void listerTables() throws SQLException {
        System.out.println("\n📋 Tables disponibles dans la base :");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   • " + tableName);
            }
        }
    }
}