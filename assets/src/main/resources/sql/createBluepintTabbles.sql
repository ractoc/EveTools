CREATE DATABASE IF NOT EXISTS eve_assets /*!40100 DEFAULT CHARACTER SET utf8 */;

/*
YAML for a blueprint
684:
    activities:
        copying:
            time: 4800
        invention:
            materials:
            -   quantity: 2
                typeID: 20411
            -   quantity: 2
                typeID: 25887
            products:
            -   probability: 0.3
                quantity: 1
                typeID: 11177
            -   probability: 0.3
                quantity: 1
                typeID: 11179
            skills:
            -   level: 1
                typeID: 11433
            -   level: 1
                typeID: 21790
            -   level: 1
                typeID: 11454
            time: 63900
        manufacturing:
            materials:
            -   quantity: 20000
                typeID: 34
            -   quantity: 4444
                typeID: 35
            -   quantity: 2111
                typeID: 36
            -   quantity: 556
                typeID: 37
            -   quantity: 11
                typeID: 38
            -   quantity: 2
                typeID: 39
            products:
            -   quantity: 1
                typeID: 583
            skills:
            -   level: 1
                typeID: 3380
            time: 6000
        research_material:
            time: 2100
        research_time:
            time: 2100
    blueprintTypeID: 684
    maxProductionLimit: 30
*/

-- blueprint table:
CREATE TABLE IF NOT EXISTS eve_assets.blueprint
(
    id                     int(11) NOT NULL,
    max_production_limit   int(11) NOT NULL,
    copying_time           int(11) NOT NULL,
    invention_time         int(11) NOT NULL,
    manufacturing_time     int(11) NOT NULL,
    research_material_time int(11) NOT NULL,
    research_time_time     int(11) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- blueprint_invention_materials table:
CREATE TABLE IF NOT EXISTS eve_assets.blueprint_invention_materials
(
    blueprint_id int(11) NOT NULL,
    type_id      int(11) NOT NULL,
    quantity     int(11) NOT NULL,
    PRIMARY KEY (blueprint_id, type_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- blueprint_invention_products table:
CREATE TABLE IF NOT EXISTS eve_assets.blueprint_invention_products
(
    blueprint_id int(11) NOT NULL,
    type_id      int(11) NOT NULL,
    probability  int(11) NOT NULL, -- TODO: convert probability to float type
    quantity     int(11) NOT NULL,
    PRIMARY KEY (blueprint_id, type_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- blueprint_invention_skills table:
CREATE TABLE IF NOT EXISTS eve_assets.blueprint_invention_skills
(
    blueprint_id int(11) NOT NULL,
    type_id      int(11) NOT NULL,
    level        int(11) NOT NULL,
    PRIMARY KEY (blueprint_id, type_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- blueprint_manufacturing_materials table:
CREATE TABLE IF NOT EXISTS eve_assets.blueprint_manufacturing_materials
(
    blueprint_id int(11) NOT NULL,
    type_id      int(11) NOT NULL,
    quantity     int(11) NOT NULL,
    PRIMARY KEY (blueprint_id, type_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- manufacturing_products table:
CREATE TABLE IF NOT EXISTS eve_assets.blueprint_manufacturing_products
(
    blueprint_id int(11) NOT NULL,
    type_id      int(11) NOT NULL,
    quantity     int(11) NOT NULL,
    PRIMARY KEY (blueprint_id, type_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- blueprint_manufacturing_skills table:
CREATE TABLE IF NOT EXISTS eve_assets.blueprint_manufacturing_skills
(
    blueprint_id int(11) NOT NULL,
    type_id      int(11) NOT NULL,
    level        int(11) NOT NULL,
    PRIMARY KEY (blueprint_id, type_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
