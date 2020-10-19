DROP DATABASE IF EXISTS `eve_fleetmanager`;

CREATE DATABASE `eve_fleetmanager` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `eve_fleetmanager`.`fleet`
(
    `id`             int(11)      NOT NULL AUTO_INCREMENT,
    `name`           varchar(45)  NOT NULL,
    `owner`          int(11)      NOT NULL,
    `start`          varchar(256) NOT NULL,
    `duration`       int(2)  DEFAULT NULL,
    `corporation_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_fleetmanager`.`invites`
(
    `fleet_id`       int(11)     NOT NULL,
    `character_id`   int(11)     NOT NULL,
    `name`           varchar(45) NOT NULL,
    `corporation_id` int(11) DEFAULT NULL,
    `key`            varchar(45) NOT NULL,
    PRIMARY KEY (`key`),
    UNIQUE KEY `invite_key_UNIQUE` (`key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `eve_fleetmanager`.`registrations`
(
    `character_id` int(11)     NOT NULL,
    `fleet_id`     int(11)     NOT NULL,
    `name`         varchar(45) NOT NULL,
    `start`        timestamp   NULL DEFAULT NULL,
    `end`          timestamp   NULL DEFAULT NULL,
    PRIMARY KEY (`character_id`, `fleet_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `eve_fleetmanager`.`invites`
    ADD COLUMN `additional_info` VARCHAR(1000) NULL;


CREATE TABLE `eve_fleetmanager`.`types`
(
    `id`          int(11)       NOT NULL AUTO_INCREMENT,
    `name`        varchar(45)   NOT NULL,
    `description` varchar(1000) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8;

INSERT INTO `eve_fleetmanager`.`types`
(`id`,
 `name`,
 `description`)
VALUES ('1', 'Mining',
        'This fleet will primarily mine asteroids and moons. It can contain secutity forces for mining in low or null sec space.'),
       ('2', 'PvE', 'This fleet wil participate in PvE.'),
       ('3', 'PvP', 'This fleet will participate in PvP.');



ALTER TABLE `eve_fleetmanager`.`fleet`
    ADD COLUMN `type_id` INT(11) NULL;

update `eve_fleetmanager`.`fleet`
set `type_id`= 1;

ALTER TABLE `eve_fleetmanager`.`fleet`
    CHANGE COLUMN `type_id` `type_id` INT(11) NOT NULL;
