# Bibliothèque — application fil rouge

Application Spring Boot utilisée comme support du cours **Tests logiciels** (L3/M1).
Le domaine est volontairement minimal — un livre peut être créé, listé, emprunté, rendu — pour laisser toute la place aux techniques de test.

Le projet est organisé en **trois modules pédagogiques**, chacun illustré par un jeu de tests.

## Pré-requis

- Java 21
- Maven Wrapper (`./mvnw`) — pas besoin d'installer Maven
- Docker (uniquement pour le module 2 : Testcontainers démarre un PostgreSQL éphémère)
- Chrome installé (uniquement pour le module 3 : Selenide pilote un Chrome headless)

## Démarrer l'application

```bash
./mvnw spring-boot:run
```

L'application écoute sur `http://localhost:9090`.

- Page HTML : `http://localhost:9090/livres`
- API REST : `http://localhost:9090/api/livres`
- Base embarquée H2 en mémoire (aucun setup nécessaire)

## Lancer l'intégralité des tests

```bash
./mvnw verify
```

- `test` → tests unitaires (`*Test.java`)
- `integration-test` + `verify` → tests d'intégration et d'interface (`*IT.java`), via `maven-failsafe-plugin`
- Rapport de couverture JaCoCo généré dans `target/site/jacoco/index.html`

---

## Module 1 — Tests unitaires (JUnit 5, AssertJ, Mockito)

**Objectif :** tester des règles métier en isolation, sans Spring ni base de données.

| Fichier | Ce qu'il illustre |
|---|---|
| `src/test/java/cours/biblio/LivreTest.java` | JUnit 5 pur sur l'entité `Livre` : `@Nested`, `@DisplayName`, `@ParameterizedTest` (`@ValueSource`, `@CsvSource`), AssertJ (`assertThat`, `assertThatThrownBy`). |
| `src/test/java/cours/biblio/LivreServiceTest.java` | Mockito : `@Mock`, `@InjectMocks`, `when(...).thenReturn(...)`, `verify(...)`, `ArgumentCaptor`. Le `LivreRepository` est mocké pour isoler le service. |

**Lancer uniquement ce module :**

```bash
./mvnw test
```

Ou cibler une classe :

```bash
./mvnw test -Dtest=LivreTest
./mvnw test -Dtest=LivreServiceTest
```

---

## Module 2 — Tests d'intégration (Testcontainers, REST Assured)

**Objectif :** vérifier que les couches s'intègrent correctement — JPA ↔ PostgreSQL réel, contrôleur ↔ HTTP ↔ service ↔ base — sans mocks.

| Fichier | Ce qu'il illustre |
|---|---|
| `src/test/java/cours/biblio/LivreRepositoryIT.java` | `@DataJpaTest` avec un PostgreSQL démarré par Testcontainers (`PostgresContainers`). Prouve la pile Hibernate → JDBC → PostgreSQL de bout en bout. |
| `src/test/java/cours/biblio/LivreControllerIT.java` | `@SpringBootTest` sur un port aléatoire + REST Assured (`given / when / then`). Couvre les codes 201, 200, 400, 404, 409. |
| `src/test/java/cours/biblio/PostgresContainers.java` | Configuration `@TestConfiguration` qui expose un `PostgreSQLContainer` partagé aux tests d'intégration. |

**Pré-requis :** Docker en cours d'exécution.

**Lancer uniquement ce module :**

```bash
./mvnw verify -Dtest='!*' -Dit.test='LivreRepositoryIT,LivreControllerIT'
```

Ou cibler une classe :

```bash
./mvnw verify -Dit.test=LivreRepositoryIT
./mvnw verify -Dit.test=LivreControllerIT
```

---

## Module 3 — Tests d'interface (Selenide)

**Objectif :** piloter l'application dans un vrai navigateur (Chrome headless) et vérifier les comportements visibles par l'utilisateur.

| Fichier | Ce qu'il illustre |
|---|---|
| `src/test/java/cours/biblio/ui/LivresPage.java` | Page Object — encapsule les sélecteurs CSS et les interactions de la page `/livres`. |
| `src/test/java/cours/biblio/ui/LivresPageIT.java` | `@SpringBootTest` + Selenide. Démontre les attentes automatiques, `@Sql` pour préparer/nettoyer l'état, et la vérification de l'UI après un emprunt. |
| `src/test/resources/fixtures/livres.sql`, `clean.sql` | Jeux de données utilisés par `@Sql` avant/après chaque test. |

**Pré-requis :** Chrome installé localement. Selenide télécharge le WebDriver automatiquement.

Les tests UI utilisent H2 en mémoire (pas besoin de Docker pour ce module — l'intérêt pédagogique porte sur l'UI, pas la persistance).

**Lancer uniquement ce module :**

```bash
./mvnw verify -Dtest='!*' -Dit.test=LivresPageIT
```

Pour voir le navigateur pendant l'exécution, passer `Configuration.headless = false` dans `LivresPageIT#setUp`.

---

## Couverture de code

Après `./mvnw verify`, ouvrir :

```
target/site/jacoco/index.html
```

---

## Intégration continue

Un workflow GitHub Actions (`.github/workflows/tests.yml`) exécute les **tests unitaires** à chaque `push` ou `pull_request` sur `main` ou `master`.

- Le job installe Java 21 (Temurin) et lance `./mvnw test`.
- Les rapports Surefire sont publiés en artefact de build.
- Les tests d'intégration et d'interface **ne sont pas** exécutés en CI (ils demandent Docker / Chrome) — c'est volontaire : l'un des exercices consiste à les faire tourner localement.

Pour vérifier que la CI passe sur votre fork, poussez sur une branche et consultez l'onglet **Actions** de votre dépôt GitHub.

---

## 🎯 Exercices — à compléter par les étudiants

L'état initial du projet est **volontairement incomplet** : certains tests manquent, ce qui fait chuter le taux de couverture mesuré par JaCoCo. Votre mission : **restaurer une couverture élevée** en écrivant les tests absents, dans le style des tests déjà présents (JUnit 5 + AssertJ + Mockito, `@Nested`, `@DisplayName`).

### Étape 0 — Mesurer la couverture actuelle

```bash
./mvnw verify
open target/site/jacoco/index.html     # macOS
xdg-open target/site/jacoco/index.html  # Linux
```

Notez la couverture **avant** d'écrire le moindre test, classe par classe. Vous devriez repérer au moins trois zones rouges évidentes.

### Étape 1 — Compléter le module 1 (tests unitaires)

Dans `LivreTest.java`, la méthode `Livre.rendre()` **n'est testée par aucun test unitaire**. Ajoutez un bloc `@Nested` `QuandOnLeRend` avec au minimum :

- [ ] un test vérifiant qu'un livre rendu redevient disponible (`isDisponible()` passe à `true`) ;
- [ ] un test vérifiant qu'un livre rendu peut être ré-emprunté ensuite sans lever d'exception.

Dans `LivreServiceTest.java`, le cas « livre déjà emprunté » n'est pas couvert au niveau service. Ajoutez un bloc `@Nested` `QuandLeLivreEstDejaEmprunte` avec :

- [ ] un test vérifiant que `service.emprunter(id)` lève `LivreDejaEmprunteException` avec un message contenant l'ISBN ;
- [ ] un test vérifiant qu'aucun `save()` n'est appelé dans ce cas (`verify(repository, never()).save(any())`).

Créez également un nouveau fichier de test unitaire pour la méthode `LivreService.rendre(Long id)` :

- [ ] cas nominal : un livre emprunté puis rendu via le service devient disponible ;
- [ ] cas d'erreur : `service.rendre(idInconnu)` lève `LivreIntrouvableException`.

### Étape 2 — Compléter le module 2 (tests d'intégration)

Dans `LivreControllerIT.java`, ajoutez les tests manquants sur l'API :

- [ ] `POST /api/livres` avec un corps sans `titre` → 400 ;
- [ ] un scénario end-to-end : créer un livre via `POST`, l'emprunter, vérifier qu'un `GET /api/livres` le renvoie avec `disponible=false` ;
- [ ] un endpoint `POST /api/livres/{id}/rendre` n'existe pas encore — **implémentez-le** (contrôleur + service) puis testez-le côté intégration.

### Étape 3 — Compléter le module 3 (tests UI)

Dans `LivresPageIT.java`, ajoutez :

- [ ] un test qui emprunte deux livres d'affilée et vérifie que les deux passent au statut « Emprunté » ;
- [ ] un test qui vérifie le comportement visuel après un rendu (une fois le bouton « Rendre » implémenté dans la page).

### Critère de validation

Votre rendu est considéré complet si :

1. `./mvnw verify` passe en local **sans erreur** ;
2. le workflow GitHub Actions est **vert** sur votre branche ;
3. la couverture JaCoCo dépasse **85 %** sur les packages `cours.biblio` (hors classe `Application`).

Bonus (facultatif) :

- étendre la CI pour lancer aussi `./mvnw verify` (nécessite d'activer Docker dans le workflow) ;
- ajouter un test paramétré qui vérifie le rejet d'ISBN invalides (longueur ≠ 10/13, caractères non numériques) — actuellement seule la validation côté API est indirectement couverte.

## Structure du projet

```
src/main/java/cours/biblio/
├── Application.java              # point d'entrée Spring Boot
├── Livre.java                    # entité + règle métier emprunter()
├── LivreRepository.java          # Spring Data JPA
├── LivreService.java             # orchestration transactionnelle
├── LivreController.java          # API REST + page HTML
├── LivreIntrouvableException.java
└── LivreDejaEmprunteException.java

src/main/resources/
├── application.yml               # port 9090, H2 en mémoire
└── templates/livres.html         # page Thymeleaf pour le module 3

src/test/java/cours/biblio/
├── LivreTest.java                # module 1
├── LivreServiceTest.java         # module 1
├── LivreRepositoryIT.java        # module 2
├── LivreControllerIT.java        # module 2
├── PostgresContainers.java       # module 2 (config Testcontainers)
└── ui/
    ├── LivresPage.java           # module 3 (Page Object)
    └── LivresPageIT.java         # module 3
```
