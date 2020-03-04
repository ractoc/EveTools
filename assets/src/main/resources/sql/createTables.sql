DROP DATABASE IF EXISTS `eve_assets`;

CREATE DATABASE `eve_assets` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `eve_assets`.`blueprint`
(
    `id`                     int(11) NOT NULL,
    `max_production_limit`   int(11) NOT NULL,
    `copying_time`           int(11) NOT NULL,
    `invention_time`         int(11) NOT NULL,
    `manufacturing_time`     int(11) NOT NULL,
    `research_material_time` int(11) NOT NULL,
    `research_time_time`     int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_assets`.`blueprint_invention_materials`
(
    `blueprint_id` int(11) NOT NULL,
    `type_id`      int(11) NOT NULL,
    `quantity`     int(11) NOT NULL,
    PRIMARY KEY (`blueprint_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_assets`.`blueprint_invention_products`
(
    `blueprint_id` int(11) NOT NULL,
    `type_id`      int(11) NOT NULL,
    `probability`  int(11) NOT NULL,
    `quantity`     int(11) NOT NULL,
    PRIMARY KEY (`blueprint_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_assets`.`blueprint_invention_skills`
(
    `blueprint_id` int(11) NOT NULL,
    `type_id`      int(11) NOT NULL,
    `level`        int(11) NOT NULL,
    PRIMARY KEY (`blueprint_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_assets`.`blueprint_manufacturing_materials`
(
    `blueprint_id` int(11) NOT NULL,
    `type_id`      int(11) NOT NULL,
    `quantity`     int(11) NOT NULL,
    PRIMARY KEY (`blueprint_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_assets`.`blueprint_manufacturing_products`
(
    `blueprint_id` int(11) NOT NULL,
    `type_id`      int(11) NOT NULL,
    `quantity`     int(11) NOT NULL,
    PRIMARY KEY (`blueprint_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_assets`.`blueprint_manufacturing_skills`
(
    `blueprint_id` int(11) NOT NULL,
    `type_id`      int(11) NOT NULL,
    `level`        int(11) NOT NULL,
    PRIMARY KEY (`blueprint_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_assets`.`type`
(
    `id`       int(11) NOT NULL,
    `name`     varchar(100)   DEFAULT NULL,
    `group_id` int(11)        DEFAULT NULL,
    `volume`   decimal(11, 2) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
