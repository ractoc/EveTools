DROP DATABASE IF EXISTS `eve_fleetmanager`;

CREATE DATABASE `eve_fleetmanager` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `eve_fleetmanager`.`fleet`
(
    `id`                     int(11)      NOT NULL AUTO_INCREMENT,
    `name`                   varchar(45)  NOT NULL,
    `owner`                  int(11)      NOT NULL,
    `start`                  varchar(256) NOT NULL,
    `duration`               int(2)                DEFAULT NULL,
    `password_restricted`    tinyint(4)   NOT NULL DEFAULT 0,
    `corporation_restricted` tinyint(4)   NOT NULL DEFAULT 0,
    `password`               varchar(45)           DEFAULT NULL,
    `corporation_id`         int(11)               DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  DEFAULT CHARSET = utf8;
