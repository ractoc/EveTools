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
        'This fleet will primarily mine asteroids and moons. It can contain security forces for mining in low or null sec space.'),
       ('2', 'PvE', 'This fleet wil participate in PvE.'),
       ('3', 'PvP', 'This fleet will participate in PvP.');



ALTER TABLE `eve_fleetmanager`.`fleet`
    ADD COLUMN `type_id` INT(11) NULL;

update `eve_fleetmanager`.`fleet`
set `type_id`= 1;

ALTER TABLE `eve_fleetmanager`.`fleet`
    CHANGE COLUMN `type_id` `type_id` INT(11) NOT NULL;



CREATE TABLE `role`
(
    `id`          int(11)       NOT NULL AUTO_INCREMENT,
    `name`        varchar(45)   NOT NULL,
    `description` varchar(1000) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `role_types`
(
    `role_id` int(11) NOT NULL,
    `type_id` int(11) NOT NULL,
    PRIMARY KEY (`role_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
ALTER TABLE `eve_fleetmanager`.`role_types`
    ADD CONSTRAINT `fk_role_types_role`
        FOREIGN KEY (`role_id`)
            REFERENCES `eve_fleetmanager`.`role` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT `fk_role_types_types`
        FOREIGN KEY (`type_id`)
            REFERENCES `eve_fleetmanager`.`types` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

CREATE TABLE `role_fleet`
(
    `role_id`  int(11) NOT NULL,
    `fleet_id` int(11) NOT NULL,
    `number`   int(11) NOT NULL DEFAULT '1',
    PRIMARY KEY (`role_id`, `fleet_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `eve_fleetmanager`.`registrations`
    ADD COLUMN `role_id` INT NULL;

INSERT INTO `eve_fleetmanager`.`role` (`id`, `name`, `description`)
VALUES (1, 'Miner', 'Mining all the minerals');
INSERT INTO `eve_fleetmanager`.`role` (`id`, `name`, `description`)
VALUES (2, 'Booster', 'Boosting the miners');
INSERT INTO `eve_fleetmanager`.`role` (`id`, `name`, `description`)
VALUES (3, 'Hauler', 'Hauling the mined goods back to the station');

INSERT INTO `eve_fleetmanager`.`role_types` (`role_id`, `type_id`)
VALUES ('1', '1');
INSERT INTO `eve_fleetmanager`.`role_types` (`role_id`, `type_id`)
VALUES ('2', '1');
INSERT INTO `eve_fleetmanager`.`role_types` (`role_id`, `type_id`)
VALUES ('3', '1');
