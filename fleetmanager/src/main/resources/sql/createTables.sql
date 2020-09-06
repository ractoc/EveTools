DROP DATABASE IF EXISTS `eve_fleetmanager`;

CREATE DATABASE `eve_fleetmanager` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `eve_fleetmanager`.`fleet`
(
    `id`       int(11)      NOT NULL AUTO_INCREMENT,
    `name`     varchar(45)  NOT NULL,
    `owner`    int(11)      NOT NULL,
    `start`    varchar(256) NOT NULL,
    `duration` int(2) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 17
  DEFAULT CHARSET = utf8;
