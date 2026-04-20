package cours.biblio;

public class LivreIntrouvableException extends RuntimeException {

    public LivreIntrouvableException(Long id) {
        super("Livre introuvable : id=" + id);
    }
}
