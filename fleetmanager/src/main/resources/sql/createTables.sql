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

CREATE TABLE `registrations`
(
    `character_id` int(11)     NOT NULL,
    `fleet_id`     int(11)     NOT NULL,
    `name`         varchar(45) NOT NULL,
    `start`        timestamp   NULL DEFAULT NULL,
    `end`          timestamp   NULL DEFAULT NULL,
    PRIMARY KEY (`character_id`, `fleet_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
