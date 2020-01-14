CREATE DATABASE IF NOT EXISTS eve_user /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE IF NOT EXISTS  eve_user.user (
  eve_state char(36) NOT NULL,
  ip_address varchar(15) NOT NULL,
  characterId int(11) NULL,
  name varchar(100) NULL,
  refresh_token varchar(100) NULL,
  last_refresh TIMESTAMP NULL,
  expires_in int(11) NULL,
  access_token tinytext NULL,
  PRIMARY KEY (eve_state),
  UNIQUE KEY name_UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
