package nexus_bmb_soft.security;

/**
 * Énumération des rôles utilisateur dans le système de gestion de charroi
 * Chaque rôle a des permissions spécifiques définies
 * 
 * @author BlaiseMUBADI
 */
public enum UserRole {
    
    /**
     * Administrateur système
     * - Accès complet à toutes les fonctionnalités
     * - Gestion des utilisateurs et rôles
     * - Configuration système
     * - Rapports et statistiques avancées
     */
    ADMIN("Administrateur", 1, "Accès complet au système"),
    
    /**
     * Gestionnaire de flotte
     * - Gestion des véhicules et affectations
     * - Planification des entretiens
     * - Consultation des rapports
     * - Gestion des conducteurs
     */
    GESTIONNAIRE("Gestionnaire", 2, "Gestion de la flotte et planification"),
    
    /**
     * Mécanicien/Technicien
     * - Consultation des véhicules
     * - Gestion des entretiens (création, modification, clôture)
     * - Consultation des historiques d'entretien
     * - Saisie des coûts et pièces
     */
    MECANICIEN("Mécanicien", 3, "Gestion des entretiens et réparations"),
    
    /**
     * Chauffeur/Conducteur
     * - Consultation des véhicules qui lui sont affectés
     * - Signalement de problèmes
     * - Consultation de ses affectations
     * - Mise à jour du kilométrage
     */
    CHAUFFEUR("Chauffeur", 4, "Consultation et signalement");
    
    private final String displayName;
    private final int hierarchyLevel;
    private final String description;
    
    UserRole(String displayName, int hierarchyLevel, String description) {
        this.displayName = displayName;
        this.hierarchyLevel = hierarchyLevel;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Vérifie si ce rôle a un niveau hiérarchique supérieur ou égal à un autre
     */
    public boolean hasHigherOrEqualLevel(UserRole other) {
        return this.hierarchyLevel <= other.hierarchyLevel;
    }
    
    /**
     * Vérifie si ce rôle peut gérer un autre rôle
     */
    public boolean canManage(UserRole other) {
        return this.hierarchyLevel < other.hierarchyLevel;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}