package nexus_bmb_soft.models;

/**
 * Énumération des rôles utilisateurs
 * 
 * @author BlaiseMUBADI
 */
public enum RoleUtilisateur {
    ADMIN("admin"),
    GESTIONNAIRE("gestionnaire"), 
    CONDUCTEUR("conducteur"),
    CONDUCTEUR_SENIOR("conducteur_senior"),
    SUPER_ADMIN("super_admin");
    
    private final String valeur;
    
    RoleUtilisateur(String valeur) {
        this.valeur = valeur;
    }
    
    public String getValeur() {
        return valeur;
    }
    
    public static RoleUtilisateur fromString(String role) {
        for (RoleUtilisateur r : RoleUtilisateur.values()) {
            if (r.valeur.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rôle inconnu: " + role);
    }
    
    @Override
    public String toString() {
        return valeur;
    }
}