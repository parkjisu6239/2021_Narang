-- MySQL dump 10.13  Distrib 5.7.35, for Linux (x86_64)
--
-- Host: localhost    Database: narang_web_db
-- ------------------------------------------------------
-- Server version	5.7.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room` (
  `room_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `game` varchar(255) DEFAULT NULL,
  `is_activate` bit(1) DEFAULT b'1',
  `max_player` int(11) NOT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `password` int(11) NOT NULL,
  `thumbnail_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (8,'2021-08-19 22:29:43.557000',NULL,_binary '',6,51,0,NULL,'술기로운 싸피생활'),(17,'2021-08-20 01:34:06.610000',NULL,_binary '',6,47,0,NULL,'aaa');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `thumbnail_url` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `room_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FKr7pfjaqkuj79ip1vhtfu0ypa5` (`room_id`),
  CONSTRAINT `FKr7pfjaqkuj79ip1vhtfu0ypa5` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (42,'tlsdh1111@naver.com','$2a$10$1cxlivuA11yKoK84G/pplODBQNhXMNjg.Gg88VJ1JlBoRaI23j5QG','https://i.ibb.co/1LngynC/image.png','신동동윤',NULL),(43,'yeeybook@naver.com','$2a$10$m1EGo6Jg.UHgwG/tQHq28ulHN6b7JNxijv4Xk2lF.o4md6pCV35vq',NULL,'모닝수박',NULL),(44,'a@aa.aa','$2a$10$fTHN3Mwkly9bwk9.QMCcJulZk9tRrMTV6Jy1H.fj/awSJmMWp/mey',NULL,'모닝수박',NULL),(45,'tester@ssafy.com','$2a$10$C5/LBHK1hbXUCTC46Kex4ectKzJAIMPdywhTv62Ky79Xd8ZYvBMIi',NULL,'testssafy',NULL),(47,'damyoung1225@gmail.com','$2a$10$5yBU6jdm0nKMxZ1Uv6uW/O1xltMmQE5G1RhC8JGBrglhM6d7IEUiy',NULL,'이게~~나야',17),(48,'jjun940417@gmail.com','$2a$10$9ASSS0qSJy0n5GEwTtYWa.xs6Gtkl0GlOOovt2z2GEwADokd4lgQO',NULL,'시쿠아아',NULL),(50,'jisu@naver.com','$2a$10$ux9HwFnZR4FKYAbJCzPle.5F9PJXKVABUmaHIguDgXKJXBT3SAyGG',NULL,'지수박박쥐',NULL),(51,'ssafy@ssafy.com','$2a$10$1A7yrllCNnN4hGjBBpjRI.X590zlmAlDS4sY1uaBPx0TG.KA4nDCe',NULL,'ssafy',8),(52,'ssafytest@ssafy.com','$2a$10$GPbV58fa1.6Jr2xWo2DDCe3BHQJVO7hcnj2xEkw6kZ/BM0ZN.p5BG',NULL,'st',NULL),(53,'loginwhy@test.test','$2a$10$AXAwrhdRm8/K8Ci9k.4rp.KKUZ4UDpUqfsMzFmV4BvpHboF4k1gVG',NULL,'logind',NULL),(54,'ssafy123@ssafy.com','$2a$10$freX4Pc6vQ0oHJvmiucBreFN6XwdM8X6dzpSzhL91Pvu10EJexuvO',NULL,'ssafy123',NULL),(55,'hi@naver.com','$2a$10$NZdH62f71YjdSjBUEPmCQOwRk4Wn8c1hjjQbtwLZ0NyEJMCLwQpj2',NULL,'qwer',NULL),(56,'q@naver.com','$2a$10$j.jIN8GSEZS5oWKok3bZSeW8TFekEk57QOu7wI2.L8aEL/yy049nK',NULL,'asdf',NULL),(57,'asd@asd.com','$2a$10$KEVnXAtse/0rn6gEOmeBv.lc89OUnf8YTSc1dl8AvZ7RAlQWNufyC',NULL,'asd',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-08-20  2:00:48
