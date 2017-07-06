CREATE TABLE `users` (
  `id`   BIGINT AUTO_INCREMENT,
  `name` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = ${engineName}
  DEFAULT CHARSET = utf8;