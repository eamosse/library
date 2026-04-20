package cours.biblio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivreRepository extends JpaRepository<Livre, Long> {

    Optional<Livre> findByIsbn(String isbn);
}
