CREATE DATABASE IF NOT EXISTS eve_universe /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE IF NOT EXISTS  eve_universe.region (
  id int(11) NOT NULL,
  name varchar(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name_UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS  eve_universe.constellation (
  id int(11) NOT NULL,
  name varchar(45) NOT NULL,
  region_id int(11) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name_UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS  eve_universe.solarsystem (
  id int(11) NOT NULL,
  name varchar(45) NOT NULL,
  constellation_id int(11) NOT NULL,
  security_class varchar(45) NULL,
  security_rating FLOAT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name_UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;