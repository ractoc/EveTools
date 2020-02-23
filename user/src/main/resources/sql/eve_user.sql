DROP DATABASE IF EXISTS `eve_user`;

CREATE DATABASE `eve_user` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `eve_user`.`user`
(
    `eve_state`     char(36)    NOT NULL,
    `ip_address`    varchar(15) NOT NULL,
    `characterId`   int(11)          DEFAULT NULL,
    `name`          varchar(100)     DEFAULT NULL,
    `refresh_token` varchar(100)     DEFAULT NULL,
    `last_refresh`  timestamp   NULL DEFAULT NULL,
    `expires_in`    int(11)          DEFAULT NULL,
    `access_token`  text,
    PRIMARY KEY (`eve_state`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

