package cours.biblio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires sur LivreService, avec Mockito pour isoler le Repository.
 * Démontrent les patterns Mockito essentiels : @Mock, when/thenReturn, verify, ArgumentCaptor.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LivreService.emprunter(id)")
class LivreServiceTest {

    @Mock
    LivreRepository repository;

    @InjectMocks
    LivreService service;

    @Nested
    @DisplayName("cas nominal")
    class CasNominal {

        @Test
        @DisplayName("marque le livre indisponible et le sauvegarde")
        void marqueLeLivreIndisponible() {
            Livre livre = new Livre("9782070360024", "L'Étranger", "Camus");
            when(repository.findById(1L)).thenReturn(Optional.of(livre));
            when(repository.save(any(Livre.class))).thenReturn(livre);

            Livre resultat = service.emprunter(1L);

            assertThat(resultat.isDisponible()).isFalse();
        }

        @Test
        @DisplayName("sauvegarde un livre dont la disponibilité est bien false")
        void sauvegardeUnLivreIndisponible() {
            Livre livre = new Livre("9782070360024", "L'Étranger", "Camus");
            when(repository.findById(1L)).thenReturn(Optional.of(livre));
            when(repository.save(any(Livre.class))).thenReturn(livre);

            service.emprunter(1L);

            ArgumentCaptor<Livre> captor = ArgumentCaptor.forClass(Livre.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().isDisponible()).isFalse();
            assertThat(captor.getValue().getIsbn()).isEqualTo("9782070360024");
        }
    }

    @Nested
    @DisplayName("quand le livre n'existe pas")
    class QuandLeLivreNExistePas {

        @Test
        @DisplayName("lève LivreIntrouvableException")
        void leveIntrouvable() {
            when(repository.findById(42L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.emprunter(42L))
                    .isInstanceOf(LivreIntrouvableException.class)
                    .hasMessageContaining("42");
        }
    }
}
