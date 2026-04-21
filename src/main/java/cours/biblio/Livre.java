package cours.biblio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Entité Livre. Une règle métier non triviale est portée ici : {@link #emprunter()}
 * change l'état {@code disponible} et lève une exception si le livre est déjà emprunté.
 * Cette règle sert d'exemple de test unitaire dans le module 1.
 */
@Entity
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "\\d{10}|\\d{13}", message = "L'ISBN doit contenir 10 ou 13 chiffres")
    private String isbn;

    @NotBlank
    private String titre;

    @NotBlank
    private String auteur;

    private boolean disponible = true;

    protected Livre() {
        // pour JPA
    }

    public Livre(String isbn, String titre, String auteur) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.disponible = false;
    }

    /**
     * Emprunte le livre. Lève {@link LivreDejaEmprunteException} s'il est déjà emprunté.
     */
    public void emprunter() {
        if (!disponible) {
            throw new LivreDejaEmprunteException(isbn);
        }
        this.disponible = false;
    }

    public void rendre() {
        this.disponible = true;
    }

    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitre() { return titre; }
    public String getAuteur() { return auteur; }
    public boolean isDisponible() { return disponible; }
}
