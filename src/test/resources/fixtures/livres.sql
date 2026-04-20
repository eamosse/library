-- Données de test partagées par les tests d'intégration et d'interface.
-- Chargé via @Sql(scripts="/fixtures/livres.sql") dans les tests qui en ont besoin.
INSERT INTO livre (id, isbn, titre, auteur, disponible)
VALUES (1001, '9782070360024', 'L''Étranger', 'Albert Camus', TRUE);
INSERT INTO livre (id, isbn, titre, auteur, disponible)
VALUES (1002, '9782070411191', 'Voyage au bout de la nuit', 'Louis-Ferdinand Céline', TRUE);
