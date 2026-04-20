package cours.biblio;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration API : Spring Boot démarré avec un vrai port + PostgreSQL Testcontainers.
 * REST Assured pour la lisibilité BDD (given/when/then) — plus agréable en cours que MockMvc.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresContainers.class)
@DisplayName("API /api/livres — intégration complète")
class LivreControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    LivreRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/livres crée un livre et renvoie 201 + Location")
    void postCreeUnLivre() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"isbn":"9782070360024","titre":"L'Étranger","auteur":"Camus"}
                        """)
                .when()
                .post("/api/livres")
                .then()
                .statusCode(201)
                .header("Location", matchesPattern("/api/livres/\\d+"))
                .body("isbn", equalTo("9782070360024"))
                .body("titre", equalTo("L'Étranger"))
                .body("disponible", equalTo(true));
    }

    @Test
    @DisplayName("GET /api/livres retourne la liste complète")
    void getListeLesLivres() {
        repository.save(new Livre("9782070360024", "L'Étranger", "Camus"));
        repository.save(new Livre("9782070411191", "Voyage au bout de la nuit", "Céline"));

        given()
                .when()
                .get("/api/livres")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("isbn", hasItems("9782070360024", "9782070411191"));
    }

    @Test
    @DisplayName("POST emprunter sur un livre disponible → 200 + disponible=false")
    void empruntNominal() {
        Livre livre = repository.save(new Livre("9782070360024", "L'Étranger", "Camus"));

        given()
                .when()
                .post("/api/livres/{id}/emprunter", livre.getId())
                .then()
                .statusCode(200)
                .body("disponible", equalTo(false))
                .body("isbn", equalTo("9782070360024"));
    }

    @Test
    @DisplayName("POST emprunter deux fois → 409 avec message contenant l'ISBN")
    void empruntDoubleRenvoie409() {
        Livre livre = repository.save(new Livre("9782070360024", "L'Étranger", "Camus"));

        // Premier emprunt : OK
        given().when().post("/api/livres/{id}/emprunter", livre.getId()).then().statusCode(200);

        // Second emprunt : 409
        given()
                .when()
                .post("/api/livres/{id}/emprunter", livre.getId())
                .then()
                .statusCode(409)
                .body(containsString("9782070360024"));
    }

    @Test
    @DisplayName("POST emprunter sur id inexistant → 404")
    void empruntIdInconnu() {
        given()
                .when()
                .post("/api/livres/{id}/emprunter", 999999L)
                .then()
                .statusCode(404)
                .body(containsString("999999"));
    }

    @Test
    @DisplayName("POST /api/livres avec ISBN invalide → 400")
    void postIsbnInvalide() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"isbn":"abc","titre":"X","auteur":"Y"}
                        """)
                .when()
                .post("/api/livres")
                .then()
                .statusCode(400);
    }
}
