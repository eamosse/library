package cours.biblio;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Conteneur PostgreSQL partagé pour les tests d'intégration.
 *
 * <p>Pattern : le container est déclaré {@code static final} et {@code @Bean}, ce qui garantit
 * qu'il est démarré une seule fois pour toute la JVM de test. L'annotation
 * {@link ServiceConnection} (Spring Boot 3.1+) expose automatiquement les propriétés JDBC
 * à Spring — plus besoin de {@code @DynamicPropertySource}.
 *
 * <p>Image : {@code postgres:16-alpine} — multi-arch (x86 et ARM), ~80 MB, démarrage rapide.
 *
 * <p>Usage : {@code @Import(PostgresContainers.class)} dans la classe de test.
 */
@TestConfiguration(proxyBeanMethods = false)
public class PostgresContainers {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));
    }
}
