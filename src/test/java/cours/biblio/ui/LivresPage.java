package cours.biblio.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Page Object pour la page /livres.
 *
 * <p>Aucune méthode n'expose de {@code SelenideElement} ou de {@code WebElement} — l'API est
 * exclusivement métier. Les sélecteurs CSS sont centralisés ici, ce qui permet de changer
 * le template HTML sans toucher aux tests.
 *
 * <p>Attentes : toutes les assertions passent par {@code shouldHave}/{@code shouldBe} de
 * Selenide. Aucun {@code Thread.sleep}.
 */
public class LivresPage {

    public LivresPage ouvrir() {
        open("/livres");
        return this;
    }

    public boolean estVide() {
        return $("[data-testid=empty-message]").exists();
    }

    public String messageVide() {
        return $("[data-testid=empty-message]")
                .shouldBe(Condition.visible)
                .getText();
    }

    public List<String> titresAffiches() {
        ElementsCollection lignes = $$("[data-testid=livres-table] tbody tr");
        lignes.shouldHave(com.codeborne.selenide.CollectionCondition.sizeGreaterThan(0));
        return lignes.asDynamicIterable()
                .stream()
                .map(tr -> tr.$$("td").get(1).getText())
                .toList();
    }

    public LivresPage emprunter(String isbn) {
        ligne(isbn)
                .$("[data-testid=bouton-emprunter]")
                .shouldBe(Condition.visible)
                .click();
        return this;
    }

    public String statut(String isbn) {
        return ligne(isbn)
                .$("[data-testid=statut]")
                .shouldBe(Condition.visible)
                .getText();
    }

    public boolean aUnBoutonEmprunter(String isbn) {
        return ligne(isbn).$("[data-testid=bouton-emprunter]").exists();
    }

    private SelenideElement ligne(String isbn) {
        return $("tr[data-isbn='" + isbn + "']").shouldBe(Condition.visible);
    }
}
