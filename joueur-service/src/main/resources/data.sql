INSERT INTO joueur (pseudo, email, mot_de_passe, date_inscription, role)
SELECT 'admin', 'admin@motus.fr', 'admin123', CURRENT_TIMESTAMP, 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM joueur WHERE pseudo = 'admin');
