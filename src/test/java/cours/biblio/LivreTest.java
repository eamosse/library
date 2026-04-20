package cours.biblio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaires sur l'entité Livre — sans Spring, sans base, sans mocks.
 * Illustrent les bases JUnit 5, AssertJ, @ParameterizedTest et @Nested.
 */
@DisplayName("Un Livre")
class LivreTest {

    @Nested
    @DisplayName("à la création")
    class ALaCreation {

        @Test
        @DisplayName("est disponible")
        void estDisponibleParDefaut() {
            Livre livre = new Livre("9782070360024", "L'Étranger", "Camus");

            assertThat(livre.isDisponible()).isTrue();
        }

        @Test
        @DisplayName("expose son ISBN, son titre et son auteur")
        void exposeSesDonnees() {
            Livre livre = new Livre("9782070360024", "L'Étranger", "Camus");

            assertThat(livre.getIsbn()).isEqualTo("9782070360024");
            assertThat(livre.getTitre()).isEqualTo("L'Étranger");
            assertThat(livre.getAuteur()).isEqualTo("Camus");
        }
    }

    @Nested
    @DisplayName("quand on l'emprunte")
    class QuandOnLEmprunte {

        @Test
        @DisplayName("devient indisponible")
        void devientIndisponible() {
            Livre livre = new Livre("9782070360024", "L'Étranger", "Camus");

            livre.emprunter();

            assertThat(livre.isDisponible()).isFalse();
        }

        @Test
        @DisplayName("refuse un second emprunt avec LivreDejaEmprunteException")
        void refuseUnSecondEmprunt() {
            Livre livre = new Livre("9782070360024", "L'Étranger", "Camus");
            livre.emprunter();

            assertThatThrownBy(livre::emprunter)
                    .isInstanceOf(LivreDejaEmprunteException.class)
                    .hasMessageContaining("9782070360024");
        }
    }

    @Nested
    @DisplayName("accepte un ISBN")
    class AccepteUnIsbn {

        @ParameterizedTest(name = "valide : {0}")
        @ValueSource(strings = {"1234567890", "9782070360024", "0000000000", "9999999999999"})
        void accepteUnIsbnValide(String isbn) {
            Livre livre = new Livre(isbn, "Titre", "Auteur");

            assertThat(livre.getIsbn()).isEqualTo(isbn);
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
            "1234567890,     10 chiffres",
            "9782070360024, 13 chiffres"
        })
        void accepteLesDeuxLongueursAvecDescription(String isbn, String description) {
            Livre livre = new Livre(isbn, "Titre", "Auteur");

            assertThat(livre.getIsbn())
                    .as("ISBN %s (%s)", isbn, description)
                    .hasSize(isbn.length());
        }
    }
}
