package cours.biblio.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * Tests d'interface : Spring Boot + Thymeleaf + Selenide headless Chrome.
 *
 * <p>Pas de Testcontainers ici — H2 en mémoire suffit, l'objectif pédagogique porte sur
 * l'UI et Selenide, pas sur la persistance.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Page /livres — tests UI Selenide")
class LivresPageIT {

    @LocalServerPort
    int port;

    LivresPage page;

    @BeforeEach
    void setUp() {
        Configuration.baseUrl = "http://localhost:" + port;
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.timeout = 4000;
        Configuration.browserSize = "1280x800";
        page = new LivresPage();
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    @Test
    @DisplayName("affiche le message \"aucun livre\" quand la base est vide")
    void afficheMessageVideQuandBaseVide() {
        page.ouvrir();

        assertThat(page.estVide()).isTrue();
        assertThat(page.messageVide()).contains("Aucun livre");
    }

    @Test
    @DisplayName("liste les 2 livres de la fixture avec leurs titres")
    @Sql(scripts = "/fixtures/livres.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "/fixtures/clean.sql", executionPhase = AFTER_TEST_METHOD)
    void listeLesDeuxLivres() {
        page.ouvrir();

        assertThat(page.titresAffiches())
                .containsExactly("L'Étranger", "Voyage au bout de la nuit");
    }

    @Test
    @DisplayName("emprunter un livre fait passer son statut à \"Emprunté\" et retire le bouton")
    @Sql(scripts = "/fixtures/livres.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "/fixtures/clean.sql", executionPhase = AFTER_TEST_METHOD)
    void emprunterBasculeLeStatutVisuel() {
        page.ouvrir();

        page.emprunter("9782070360024");

        assertThat(page.statut("9782070360024")).isEqualTo("Emprunté");
        assertThat(page.aUnBoutonEmprunter("9782070360024")).isFalse();
    }
}
