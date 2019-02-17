CREATE TABLE `user_account` (
  `id`          bigint NOT NULL ,
  `status`      enum('active', 'suspended', 'deleted') NOT NULL default 'active',
  `email`       varchar(255) NOT NULL,
  `password`    varchar(255) NOT NULL,
  `first_name`  varchar(255) NOT NULL,
  `last_name`   varchar(255) NOT NULL,
  `created_at`  datetime(6) NOT NULL,
  `updated_at`  datetime(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user_account_id_sequence_number`(id bigint unsigned NOT NULL) ENGINE=MyISAM;
INSERT INTO `user_account_id_sequence_number` VALUES (100);

CREATE TABLE `user_message` (
  `user_id`     bigint NOT NULL,
  `message_id`  bigint NOT NULL,
  `status`      enum('active', 'suspended', 'deleted') NOT NULL default 'active',
  `message`   varchar(255) NOT NULL,
  `created_at`  datetime(6) NOT NULL,
  `updated_at`  datetime(6),
  PRIMARY KEY (`user_id`, `message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `message_id_sequence_number`(id bigint unsigned NOT NULL) ENGINE=MyISAM;
INSERT INTO `message_id_sequence_number` VALUES (100);

