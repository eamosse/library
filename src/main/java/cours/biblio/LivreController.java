package cours.biblio;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.List;

/**
 * API REST pour les tests d'intégration (module 2)
 * et page HTML pour les tests d'interface (module 3).
 */
@Controller
public class LivreController {

    private final LivreService service;

    public LivreController(LivreService service) {
        this.service = service;
    }

    // ===== Page HTML (Thymeleaf) =====

    @GetMapping("/livres")
    public String page(Model model) {
        model.addAttribute("livres", service.lister());
        return "livres";
    }

    @PostMapping("/livres/{id}/emprunter-ui")
    public String emprunterDepuisUi(@PathVariable Long id) {
        try {
            service.emprunter(id);
        } catch (LivreDejaEmprunteException ignored) {
            // la page réaffichera l'état courant avec un bandeau
        }
        return "redirect:/livres";
    }

    // ===== API REST =====

    @RestController
    @RequestMapping("/api/livres")
    public static class Api {

        private final LivreService service;

        public Api(LivreService service) {
            this.service = service;
        }

        @GetMapping
        public List<Livre> lister() {
            return service.lister();
        }

        @PostMapping
        public ResponseEntity<Livre> creer(@Valid @RequestBody CreationLivre body) {
            Livre livre = service.creer(body.isbn(), body.titre(), body.auteur());
            return ResponseEntity.created(URI.create("/api/livres/" + livre.getId())).body(livre);
        }

        @PostMapping("/{id}/emprunter")
        public Livre emprunter(@PathVariable Long id) {
            return service.emprunter(id);
        }

        @ExceptionHandler(LivreIntrouvableException.class)
        public ResponseEntity<String> introuvable(LivreIntrouvableException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }

        @ExceptionHandler(LivreDejaEmprunteException.class)
        public ResponseEntity<String> dejaEmprunte(LivreDejaEmprunteException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }

        public record CreationLivre(
                @NotBlank @Pattern(regexp = "\\d{10}|\\d{13}") String isbn,
                @NotBlank String titre,
                @NotBlank String auteur) {
        }
    }
}
