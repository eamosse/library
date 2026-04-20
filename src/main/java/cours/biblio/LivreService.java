package cours.biblio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LivreService {

    private final LivreRepository repository;

    public LivreService(LivreRepository repository) {
        this.repository = repository;
    }

    public List<Livre> lister() {
        return repository.findAll();
    }

    public Livre creer(String isbn, String titre, String auteur) {
        return repository.save(new Livre(isbn, titre, auteur));
    }

    /**
     * Tente d'emprunter le livre {@code id}.
     *
     * @throws LivreIntrouvableException     si aucun livre ne correspond
     * @throws LivreDejaEmprunteException    si le livre est déjà emprunté
     */
    public Livre emprunter(Long id) {
        Livre livre = repository.findById(id)
                .orElseThrow(() -> new LivreIntrouvableException(id));
        livre.emprunter();
        return repository.save(livre);
    }

    public Livre rendre(Long id) {
        Livre livre = repository.findById(id)
                .orElseThrow(() -> new LivreIntrouvableException(id));
        livre.rendre();
        return repository.save(livre);
    }
}
