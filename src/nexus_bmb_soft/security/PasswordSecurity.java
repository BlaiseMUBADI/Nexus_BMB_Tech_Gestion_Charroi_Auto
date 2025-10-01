package nexus_bmb_soft.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitaire pour le cryptage sécurisé des mots de passe
 * Utilise SHA-256 avec salt aléatoire pour une sécurité renforcée
 * 
 * @author BlaiseMUBADI
 */
public class PasswordSecurity {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 32; // 32 bytes = 256 bits
    private static final int ITERATIONS = 10000; // Nombre d'itérations pour renforcer la sécurité
    
    /**
     * Génère un salt aléatoire
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hache un mot de passe avec un salt donné
     */
    private static byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());
        
        // Itérations multiples pour renforcer la sécurité
        for (int i = 0; i < ITERATIONS; i++) {
            md.reset();
            hashedPassword = md.digest(hashedPassword);
        }
        
        return hashedPassword;
    }
    
    /**
     * Crypte un mot de passe avec un salt aléatoire
     * @param password Le mot de passe en clair
     * @return Le mot de passe crypté avec le salt encodé en Base64
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();
            byte[] hashedPassword = hashPassword(password, salt);
            
            // Combiner salt + hash et encoder en Base64
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du cryptage du mot de passe", e);
        }
    }
    
    /**
     * Vérifie si un mot de passe correspond au hash stocké
     * @param password Le mot de passe en clair à vérifier
     * @param storedHash Le hash stocké en base de données
     * @return true si le mot de passe correspond
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Extraire le salt et le hash
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hashedPassword = new byte[combined.length - SALT_LENGTH];
            
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combined, SALT_LENGTH, hashedPassword, 0, hashedPassword.length);
            
            // Hasher le mot de passe fourni avec le même salt
            byte[] testHash = hashPassword(password, salt);
            
            // Comparer les hashes
            return MessageDigest.isEqual(hashedPassword, testHash);
            
        } catch (Exception e) {
            return false; // En cas d'erreur, accès refusé
        }
    }
    
    /**
     * Génère un mot de passe temporaire aléatoire
     * @param length Longueur du mot de passe
     * @return Mot de passe temporaire
     */
    public static String generateTemporaryPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return password.toString();
    }
    
    /**
     * Valide la complexité d'un mot de passe
     * @param password Le mot de passe à valider
     * @return true si le mot de passe respecte les critères de sécurité
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    
    /**
     * Retourne les critères de mot de passe pour l'interface utilisateur
     */
    public static String getPasswordRequirements() {
        return "Le mot de passe doit contenir :\n" +
               "• Au moins 8 caractères\n" +
               "• Au moins une majuscule\n" +
               "• Au moins une minuscule\n" +
               "• Au moins un chiffre\n" +
               "• Au moins un caractère spécial (!@#$%^&*()_+-=[]{}|;:,.<>?)";
    }
}