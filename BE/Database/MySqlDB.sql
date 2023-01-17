-- MySQL Workbench Synchronization
-- Generated: 2023-01-16 17:20
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: SSAFY

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE TABLE IF NOT EXISTS `hanjan`.`user` (
  `uid` BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(30) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  `nickname` VARCHAR(20) NOT NULL,
  `image` VARCHAR(100) NULL DEFAULT NULL,
  `birth` VARCHAR(15) NULL DEFAULT NULL,
  `gender` TINYINT(4) NULL DEFAULT NULL,
  `interest` VARCHAR(40) NULL DEFAULT NULL,
  `reportcnt` INT(11) NULL DEFAULT 0,
  PRIMARY KEY (`uid`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `nickname_UNIQUE` (`nickname` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hanjan`.`friendrequest` (
  `rid` BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sender` BIGINT(19) UNSIGNED NOT NULL,
  `receiver` BIGINT(19) UNSIGNED NOT NULL,
  `time` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`rid`),
  INDEX `request_user_fk_idx` (`sender` ASC) VISIBLE,
  INDEX `request_user2_fk_idx` (`receiver` ASC) VISIBLE,
  CONSTRAINT `request_user1_fk`
    FOREIGN KEY (`sender`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `request_user2_fk`
    FOREIGN KEY (`receiver`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hanjan`.`friends` (
  `user1` BIGINT(19) UNSIGNED NOT NULL,
  `user2` BIGINT(19) UNSIGNED NOT NULL,
  PRIMARY KEY (`user1`, `user2`),
  INDEX `friends_user2_fk_idx` (`user2` ASC) VISIBLE,
  CONSTRAINT `friends_user1_fk`
    FOREIGN KEY (`user1`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `friends_user2_fk`
    FOREIGN KEY (`user2`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hanjan`.`assessment` (
  `aid` BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `score` DOUBLE NULL DEFAULT NULL,
  `uid` BIGINT(19) UNSIGNED NOT NULL,
  PRIMARY KEY (`aid`),
  INDEX `assessment_user_fk_idx` (`uid` ASC) VISIBLE,
  CONSTRAINT `assessment_user_fk`
    FOREIGN KEY (`uid`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hanjan`.`assignment` (
  `uid` BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`uid`),
  CONSTRAINT `assignment_user_fk`
    FOREIGN KEY (`uid`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hanjan`.`report` (
  `rid` BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `content` VARCHAR(255) NULL DEFAULT NULL,
  `from` BIGINT(19) UNSIGNED NULL DEFAULT NULL,
  `to` BIGINT(19) UNSIGNED NOT NULL,
  PRIMARY KEY (`rid`),
  INDEX `report_user_fk_idx` (`from` ASC) VISIBLE,
  INDEX `report_user2_fk_idx` (`to` ASC) VISIBLE,
  CONSTRAINT `report_user1_fk`
    FOREIGN KEY (`from`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `report_user2_fk`
    FOREIGN KEY (`to`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hanjan`.`message` (
  `mid` BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sender` BIGINT(19) UNSIGNED NOT NULL,
  `receiver` BIGINT(19) UNSIGNED NOT NULL,
  `content` MEDIUMTEXT NULL DEFAULT NULL,
  `time` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`mid`),
  INDEX `message_user_fk_idx` (`sender` ASC) VISIBLE,
  INDEX `message_user2_fk_idx` (`receiver` ASC) VISIBLE,
  CONSTRAINT `message_user1_fk`
    FOREIGN KEY (`sender`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `message_user2_fk`
    FOREIGN KEY (`receiver`)
    REFERENCES `hanjan`.`user` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
