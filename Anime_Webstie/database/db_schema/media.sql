CREATE TABLE IF NOT EXISTS user (
  usr_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  usr_first_name varchar(100) NOT NULL,
  usr_last_name varchar(100) NOT NULL,
  usr_username varchar(150) NOT NULL,
  usr_password varchar(255) NOT NULL,
  usr_salt varchar(100) NOT NULL,
  usr_avatar varchar(150) DEFAULT NULL,
  PRIMARY KEY (usr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELETE FROM `user`;
INSERT INTO `user` (`usr_id`, `usr_first_name`, `usr_last_name`, `usr_username`, `usr_password`, `usr_salt`, `usr_avatar`) VALUES
    (1, 'Vu', 'Otakhub', 'Vueey Gooey', '83d9bdb5e20f3571b087db9aabf190a296741c3e864d7742f35658cfccc1b79c4599aad25084aa9a28c649a50c92244227b3e53e197621301d619d1ea01873c4', '48c8947f69c054a5caa934674ce8881d02bb18fb59d5a63eeaddff735b0e9', 'https://robohash.org/veniamdoloresenim.png?size=64x64&set=set1%27'),
    (2, 'Tay Tay', 'Swift', 'TayTayTay', 'e289219c34f9a32ebc82393f09719b7f34872de95463242b5ffe8bb4b11a5fe7d454f9f5d082c8207c5d69b220ba06624b4bb15ffa05cc7d7d53c43f9e96da6a', '801e87294783281ae49fc8287a0fd86779b27d7972d3e84f0fa0d826d7cb67dfefc', 'https://robohash.org/nullaautemin.png?size=64x64&set=set1%27');

CREATE TABLE IF NOT EXISTS `favorite` (
  fav_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  fav_media_type varchar(15) NOT NULL,
  fav_media_id varchar(50) NOT NULL, 
  fav_media_title varchar(200) NOT NULL,
  fav_cover varchar(999) NOT NULL, 
  fav_holder int(11) NOT NULL, 
  usr_id int(11) unsigned NOT NULL, 
  PRIMARY KEY (fav_id),
  FOREIGN KEY(`usr_id`) REFERENCES `user` (`usr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELETE FROM `favorite`;

CREATE TABLE IF NOT EXISTS `history` (
  his_id int(11) unsigned NOT NULL AUTO_INCREMENT,
  his_media_type varchar(15) NOT NULL,
  his_media_id varchar(50) NOT NULL, 
  his_media_title varchar(200) NOT NULL,
  his_cover varchar(999) NOT NULL,
  usr_id int(11) unsigned NOT NULL,
  PRIMARY KEY (his_id),
  FOREIGN KEY(`usr_id`) REFERENCES `user` (`usr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELETE FROM `history`; 