package cours.biblio;

public class LivreDejaEmprunteException extends RuntimeException {

    private final String isbn;

    public LivreDejaEmprunteException(String isbn) {
        super("Le livre " + isbn + " est déjà emprunté");
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}
