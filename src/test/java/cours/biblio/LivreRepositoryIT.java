package cours.biblio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Tests d'intégration de la couche persistance : JPA réel, PostgreSQL éphémère via Testcontainers.
 * Prouve la pile Hibernate → JDBC → PostgreSQL de bout en bout, sans mocks.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(PostgresContainers.class)
@DisplayName("LivreRepository sur PostgreSQL")
class LivreRepositoryIT {

    @Autowired
    LivreRepository repository;

    @Test
    @DisplayName("persiste un livre et le retrouve par son id")
    void persisteEtRetrouveParId() {
        Livre livre = new Livre("9782070360024", "L'Étranger", "Camus");

        Livre sauvegarde = repository.save(livre);
        Optional<Livre> retrouve = repository.findById(sauvegarde.getId());

        assertThat(retrouve)
                .isPresent()
                .get()
                .extracting(Livre::getIsbn, Livre::getTitre, Livre::getAuteur, Livre::isDisponible)
                .containsExactly("9782070360024", "L'Étranger", "Camus", true);
    }

    @Test
    @DisplayName("retrouve un livre par son ISBN")
    void retrouveParIsbn() {
        repository.save(new Livre("9782070360024", "L'Étranger", "Camus"));

        Optional<Livre> trouve = repository.findByIsbn("9782070360024");

        assertThat(trouve).isPresent();
        assertThat(trouve.get().getTitre()).isEqualTo("L'Étranger");
    }

    @Test
    @DisplayName("renvoie un Optional vide pour un ISBN inconnu")
    void renvoieVidePourIsbnInconnu() {
        Optional<Livre> trouve = repository.findByIsbn("0000000000000");

        assertThat(trouve).isEmpty();
    }

    @Test
    @DisplayName("liste tous les livres persistés")
    void listeTousLesLivres() {
        repository.save(new Livre("9782070360024", "L'Étranger", "Camus"));
        repository.save(new Livre("9782070411191", "Voyage au bout de la nuit", "Céline"));

        assertThat(repository.findAll())
                .hasSize(2)
                .extracting(Livre::getIsbn)
                .containsExactlyInAnyOrder("9782070360024", "9782070411191");
    }

    @Test
    @DisplayName("un livre emprunté est persisté avec disponible=false")
    void livreEmpruntePersisteIndisponible() {
        Livre livre = repository.save(new Livre("9782070360024", "L'Étranger", "Camus"));
        livre.emprunter();
        repository.save(livre);

        Livre relu = repository.findById(livre.getId()).orElseThrow();
        assertThat(relu.isDisponible()).isFalse();
    }
}
