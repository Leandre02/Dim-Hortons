-- =====================================================
-- Dim Hortons – Toujours Frette
-- Schéma minimal pour borne + cuisine + admin (exercice)
-- Sans colonnes "snapshot", sans table Historique, sans index
-- =====================================================

CREATE DATABASE dimhortons;
Use dimhortons;

-- Réinitialisation contrôlée (respect FKs)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS CommandeItemIngredient;
DROP TABLE IF EXISTS CommandeItem;
DROP TABLE IF EXISTS Commande;
DROP TABLE IF EXISTS IngredientItem;
DROP TABLE IF EXISTS Item;
DROP TABLE IF EXISTS Ingredient;
DROP TABLE IF EXISTS CategorieItem;
DROP TABLE IF EXISTS Utilisateur;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Table: CategorieItem
-- Rôle : classer les items du menu (Cafés, Beignes, etc.)
-- =====================================================
CREATE TABLE CategorieItem (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table: Ingredient
-- Rôle : référentiel des ingrédients/options disponibles
-- =====================================================
CREATE TABLE Ingredient (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    categorie VARCHAR(50),                   -- ex. Lait, Glaçage, Légume
    prix_supplement DECIMAL(10,2) DEFAULT 0.00,
    allergene BOOLEAN DEFAULT FALSE,
    actif BOOLEAN DEFAULT TRUE
);

-- =====================================================
-- Table: Item
-- Rôle : produits vendus sur la borne (café, beigne, sandwich…)
-- =====================================================
CREATE TABLE Item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL,            -- prix de base courant
    categorie_id INT NOT NULL,
    image_url VARCHAR(255),
    calories INT,
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categorie_id) REFERENCES CategorieItem(id) ON DELETE RESTRICT
);

-- =====================================================
-- Table: IngredientItem
-- Rôle : composition "standard" d'un item (recette de base)
--        Sert aussi de borne pour valider les modifs côté app
-- =====================================================
CREATE TABLE IngredientItem (
    id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantite DECIMAL(10,2) DEFAULT 1.00,    -- quantité standard
    unite VARCHAR(20),                       -- g, ml, pièce, etc.
    obligatoire BOOLEAN DEFAULT FALSE,       -- non supprimable si TRUE
    FOREIGN KEY (item_id) REFERENCES Item(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES Ingredient(id) ON DELETE RESTRICT,
    UNIQUE KEY unique_item_ingredient (item_id, ingredient_id)
);

-- =====================================================
-- Table: Utilisateur
-- Rôle : comptes admin ou non
-- =====================================================
CREATE TABLE Utilisateur (
id INT PRIMARY KEY AUTO_INCREMENT,
mot_de_passe_hash VARCHAR(255) NOT NULL,
nom VARCHAR(100) NOT NULL,
prenom VARCHAR(100) NOT NULL,
email VARCHAR(150) UNIQUE,
admin BOOLEAN DEFAULT FALSE
);

-- =====================================================
-- Table: Commande
-- Rôle : en-tête de commande (statut, totaux, timestamps)
-- =====================================================
CREATE TABLE Commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    numero_commande VARCHAR(50) UNIQUE NOT NULL, -- ex: CMD-20251110-001
    utilisateur_id INT NOT NULL,                 -- qui a pris la commande
    statut ENUM('EN_ATTENTE','EN_PREPARATION','PRETE','SERVIE','PAYEE','ANNULEE')
           DEFAULT 'EN_ATTENTE',
    prix_total DECIMAL(10,2) DEFAULT 0.00,      -- subtotal hors taxes
    taxe DECIMAL(10,2) DEFAULT 0.00,            -- taxes calculées
    montant_final DECIMAL(10,2) DEFAULT 0.00,   -- total TTC
    remarques TEXT,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_preparation TIMESTAMP NULL,
    date_completion TIMESTAMP NULL,
    date_paiement TIMESTAMP NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id) ON DELETE RESTRICT
);
--
-- -- =====================================================
-- Table: CommandeItem
-- Rôle : lignes d'une commande (quantité, prix au moment T)
--        Utilise directement Item.prix (pas de snapshot)
-- =====================================================
CREATE TABLE CommandeItem (
    id INT PRIMARY KEY AUTO_INCREMENT,
    commande_id INT NOT NULL,
    item_id INT NOT NULL,
    quantite INT DEFAULT 1,
    prix_unitaire DECIMAL(10,2) NOT NULL,  -- copié de Item.prix au moment de l'ajout
    prix_total DECIMAL(10,2) NOT NULL,     -- (prix_unitaire + Σ delta) * quantite
    remarques TEXT,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (commande_id) REFERENCES Commande(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES Item(id) ON DELETE RESTRICT
);

-- =====================================================
-- Table: CommandeItemIngredient
-- Rôle : modifs/ingrédients choisis pour une ligne (ajout/retrait)
--        Permet d'implémenter "Modifier ingrédients d'un item"
-- =====================================================
CREATE TABLE CommandeItemIngredient (
    id INT PRIMARY KEY AUTO_INCREMENT,
    commande_item_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantite DECIMAL(10,2) DEFAULT 1.00,
    unite VARCHAR(20),
    prix_delta DECIMAL(10,2) DEFAULT 0.00,      -- supplément (ou 0)
    action ENUM('AJOUT','RETRAIT','STANDARD') DEFAULT 'STANDARD',
    FOREIGN KEY (commande_item_id) REFERENCES CommandeItem(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES Ingredient(id) ON DELETE RESTRICT
);

-- =====================================================
-- ==============  DONNÉES D'EXEMPLE  =================
-- =====================================================

-- Catégories d'items
INSERT INTO CategorieItem (nom, description) VALUES
('Cafés',     'Boissons chaudes à base de café'),
('Beignes',   'Beignes frais du jour'),
('Sandwichs', 'Sandwichs chauds et froids');

-- Ingrédients
INSERT INTO Ingredient (nom, description, categorie, prix_supplement, allergene, actif) VALUES
-- Cafés
('Lait 2%',          'Lait de vache standard', 'Cafés',     0.00, TRUE,  TRUE),
('Lait d''avoine',   'Alternative végétale',   'Cafés',     0.50, FALSE, TRUE),
('Sucre blanc',      'Sucre granulé',          'Cafés',     0.00, FALSE, TRUE),
('Crème',            'Crème pour café',        'Cafés',     0.25, TRUE,  TRUE),
('Glace',            'Glaçons',                'Cafés',     0.00, FALSE, TRUE),
('Espresso',         'Shot d''espresso',       'Cafés',     0.00, FALSE, TRUE),

-- Beignes
('Glaçage chocolat', 'Glaçage chocolat',       'Beignes',   0.00, FALSE, TRUE),
('Glaçage vanille',  'Glaçage vanille',        'Beignes',   0.25, FALSE, TRUE),

-- Sandwichs
('Poulet',           'Poulet grillé',          'Sandwichs', 0.00, FALSE, TRUE),
('Laitue',           'Feuilles croquantes',    'Sandwichs', 0.20, FALSE, TRUE),
('Tomate',           'Tranches fraîches',      'Sandwichs', 0.20, FALSE, TRUE),
('Mayonnaise',       'Sauce mayo',             'Sandwichs', 0.15, TRUE,  TRUE);

-- Items
INSERT INTO Item (nom, description, prix, categorie_id, image_url, calories, actif) VALUES
('Café régulier',       'Café noir chaud',                           1.99, 1, '/edu/cegepvicto/dimhortons/images/cafe.jpg',              5,   TRUE),
('Cappuccino glacé',    'Espresso, lait et glace',                   3.49, 1, '/edu/cegepvicto/dimhortons/images/cappucinoglace.png',  120, TRUE),
('Dim glacé',           'Café glacé signature Dim Hortons',          2.49, 1, '/edu/cegepvicto/dimhortons/images/timglace.png',         80,  TRUE),
('Beigne au chocolat',  'Beigne moelleux glaçage chocolat',          1.49, 2, '/edu/cegepvicto/dimhortons/images/beigne_choco.jpg',      250, TRUE),
('Sandwich au poulet',  'Poulet + laitue + tomate + mayo',           4.99, 3, '/edu/cegepvicto/dimhortons/images/sandwich_poulet.jpg',   350, TRUE);

-- Recette standard (composition de base)
INSERT INTO IngredientItem (item_id, ingredient_id, quantite, unite, obligatoire) VALUES
-- Café régulier
(1, 3,   1.00, 'portion', FALSE), -- Sucre blanc optionnel

-- Cappuccino glacé (item_id = 2)
(2, 6,   2.00, 'shot',    TRUE),  -- Espresso
(2, 1,   1.00, 'portion', TRUE),  -- Lait 2%
(2, 5,   1.00, 'portion', TRUE),  -- Glace

-- Dim glacé (item_id = 3)
(3, 5,   1.00, 'portion', TRUE),  -- Glace
(3, 1,   1.00, 'portion', TRUE),  -- Lait 2%
(3, 3,   1.00, 'portion', FALSE), -- Sucre blanc optionnel

-- Beigne
(4, 7,   1.00, 'portion', TRUE),  -- Glaçage chocolat

-- Sandwich au poulet
(5, 9,   1.00, 'portion', TRUE),  -- Poulet
(5, 10,  2.00, 'feuille', TRUE),  -- Laitue
(5, 11,  2.00, 'tranche', TRUE),  -- Tomate
(5, 12,  1.00, 'portion', TRUE);  -- Mayonnaise

-- Utilisateurs
INSERT INTO Utilisateur (mot_de_passe_hash, nom, prenom, email, admin) VALUES
('1234',  'Touil',  'Keven',  'keven@dimhortons.ca', FALSE),
('1234', 'Horton', 'Dimitri', 'dimitri@dimhortons.ca', TRUE),
('1234',  'Gagner', 'Nicolas',  'nicolas@dimhortons.ca', FALSE);

-- Commandes de démo
INSERT INTO Commande (numero_commande, utilisateur_id, statut, prix_total, taxe, montant_final, remarques)
VALUES
('CMD-20251110-001', 2, 'EN_PREPARATION',  3.48, 0.35, 3.83, 'Café sans sucre'),
('CMD-20251110-002', 3, 'EN_ATTENTE',      4.99, 0.50, 5.49, 'Ajouter lait d''avoine');

INSERT INTO CommandeItem (commande_id, item_id, quantite, prix_unitaire, prix_total, remarques)
VALUES
(1, 1, 1, 1.99, 1.99, 'No sugar'),
(1, 4, 1, 1.49, 1.49, NULL),
(2, 5, 1, 4.99, 4.99, 'Sans tomate');

INSERT INTO CommandeItemIngredient (commande_item_id, ingredient_id, quantite, unite, prix_delta, action)
VALUES
(1, 3, 0.00, 'portion', 0.00, 'RETRAIT'),  -- retrait sucre
(3, 2, 1.00, 'portion', 0.50, 'AJOUT');    -- ajout lait d'avoine comme extra

-- =====================================================
-- Notes d'usage (côté application) :
-- - Recalculer prix_total d'une ligne = (prix_unitaire + Σ prix_delta) * quantite
-- - Mettre à jour prix_total / taxe / montant_final de la commande après chaque modif
-- - Bloquer la modification des lignes si statut >= 'EN_PREPARATION' (selon politique)
-- =====================================================