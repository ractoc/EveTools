DROP DATABASE IF EXISTS `eve_fleetmanager`;

CREATE DATABASE `eve_fleetmanager` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `eve_fleetmanager`.`fleet`
(
    `id`         int(11)     NOT NULL,
    `name`       varchar(45) NOT NULL,
    `owner`      int(11)     NOT NULL,
    `fleet_type` varchar(45) NOT NULL,
    `start`      datetime    NOT NULL,
    `end`        datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
