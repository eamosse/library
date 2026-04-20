-- Nettoyage après les tests qui modifient l'état de la base (ex. tests UI qui empruntent).
DELETE FROM livre WHERE id IN (1001, 1002);
