SHOW TABLES;

CREATE TABLE `dumb_table` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `password` VARCHAR(10) NOT NULL
);

CREATE TABLE `dict_unit` (
  `id` INT PRIMARY KEY,
  `name` VARCHAR(2) NOT NULL
);

create table `dict_structure`(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL
);

CREATE TABLE `biz_opcode` (
  `id` INT PRIMARY KEY,
  `name` VARCHAR(20) NOT NULL
);


