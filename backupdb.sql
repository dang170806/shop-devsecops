-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: estateadvance
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `assignmentbuilding`
--

DROP TABLE IF EXISTS `assignmentbuilding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignmentbuilding` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staffid` bigint NOT NULL,
  `buildingid` bigint NOT NULL,
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_building` (`staffid`),
  KEY `fk_building_user` (`buildingid`),
  CONSTRAINT `fk_building_user` FOREIGN KEY (`buildingid`) REFERENCES `building` (`id`),
  CONSTRAINT `fk_user_building` FOREIGN KEY (`staffid`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignmentbuilding`
--

LOCK TABLES `assignmentbuilding` WRITE;
/*!40000 ALTER TABLE `assignmentbuilding` DISABLE KEYS */;
INSERT INTO `assignmentbuilding` VALUES (2,2,3,NULL,NULL,NULL,NULL),(14,5,2,NULL,NULL,NULL,NULL),(15,6,3,NULL,NULL,NULL,NULL),(17,7,3,NULL,NULL,NULL,NULL),(18,7,2,NULL,NULL,NULL,NULL),(32,2,4,NULL,NULL,NULL,NULL),(33,5,4,NULL,NULL,NULL,NULL),(38,5,1,NULL,NULL,NULL,NULL),(39,6,1,NULL,NULL,NULL,NULL),(40,2,1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `assignmentbuilding` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignmentcustomer`
--

DROP TABLE IF EXISTS `assignmentcustomer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignmentcustomer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staffid` bigint NOT NULL,
  `customerid` bigint NOT NULL,
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_customer` (`staffid`),
  KEY `fk_customer_user` (`customerid`),
  CONSTRAINT `fk_customer_user` FOREIGN KEY (`customerid`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_user_customer` FOREIGN KEY (`staffid`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignmentcustomer`
--

LOCK TABLES `assignmentcustomer` WRITE;
/*!40000 ALTER TABLE `assignmentcustomer` DISABLE KEYS */;
INSERT INTO `assignmentcustomer` VALUES (1,2,1,NULL,NULL,NULL,NULL),(2,2,3,NULL,NULL,NULL,NULL),(3,3,1,NULL,NULL,NULL,NULL),(4,3,3,NULL,NULL,NULL,NULL),(13,9,15,NULL,NULL,NULL,NULL),(14,5,15,NULL,NULL,NULL,NULL),(15,6,15,NULL,NULL,NULL,NULL),(16,8,15,NULL,NULL,NULL,NULL),(17,6,13,NULL,NULL,NULL,NULL),(18,5,13,NULL,NULL,NULL,NULL),(19,8,13,NULL,NULL,NULL,NULL),(21,6,19,NULL,NULL,NULL,NULL),(22,8,19,NULL,NULL,NULL,NULL),(23,2,16,NULL,NULL,NULL,NULL),(24,5,16,NULL,NULL,NULL,NULL),(25,8,16,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `assignmentcustomer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building`
--

DROP TABLE IF EXISTS `building`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `building` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `street` varchar(255) DEFAULT NULL,
  `ward` varchar(255) DEFAULT NULL,
  `district` varchar(255) NOT NULL,
  `structure` varchar(255) DEFAULT NULL,
  `numberofbasement` int DEFAULT NULL,
  `floorarea` int DEFAULT NULL,
  `direction` varchar(255) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `rentprice` int NOT NULL,
  `rentpricedescription` text,
  `servicefee` varchar(255) DEFAULT NULL,
  `carfee` varchar(255) DEFAULT NULL,
  `motofee` varchar(255) DEFAULT NULL,
  `overtimefee` varchar(255) DEFAULT NULL,
  `waterfee` varchar(255) DEFAULT NULL,
  `electricityfee` varchar(255) DEFAULT NULL,
  `deposit` varchar(255) DEFAULT NULL,
  `payment` varchar(255) DEFAULT NULL,
  `renttime` varchar(255) DEFAULT NULL,
  `decorationtime` varchar(255) DEFAULT NULL,
  `brokeragefee` decimal(13,2) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `linkofbuilding` varchar(255) DEFAULT NULL,
  `map` varchar(255) DEFAULT NULL,
  `image` longblob,
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  `managername` varchar(255) DEFAULT NULL,
  `managerphone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building`
--

LOCK TABLES `building` WRITE;
/*!40000 ALTER TABLE `building` DISABLE KEYS */;
INSERT INTO `building` VALUES (1,'Nam Giao Building Tower','59 phan xích long','Phường 2','QUAN_1','đẹp',2,500,'','',15,'15 triệu/m2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'TANG_TRET,NGUYEN_CAN','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Anh Nam-Chị Linh','0915354727'),(2,'ACM Tower','96 cao thắng','Phường 4','QUAN_2',NULL,2,650,NULL,NULL,18,'18 triệu/m2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'NGUYEN_CAN',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Chú Thuận','0173546263'),(3,'Alpha 2 Building Tower','153 nguyễn đình chiểu','Phường 6','QUAN_1','',1,200,'','',20,'20 triệu/m2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'TANG_TRET','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cô Lý','0555532578'),(4,'IDD 1 Building','111 Lý Chính Thắng','Phường 7','QUAN_4',NULL,1,200,NULL,NULL,12,'12 triệu/m2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'TANG_TRET,NGUYEN_CAN,NOI_THAT',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Anh Long','017345253'),(59,'IDD','f','2','QUAN_2','đẹp',2,1234,'','',1000,'15 triệu/m2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'TANG_TRET,NOI_THAT','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'A Dang','0968178006'),(60,'Nguyen Hai Dang','59 phan xích long','Duong Noi','QUAN_1','đẹp',2,1234,'','',20,'15 triệu/m2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'TANG_TRET,NOI_THAT','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'A Dang','0968178006');
/*!40000 ALTER TABLE `building` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fullname` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `companyname` varchar(255) DEFAULT NULL,
  `demand` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Luc Van Hai','0905671231','hailv@gmail.com',NULL,NULL,NULL,1,NULL,NULL,NULL,NULL),(2,'Nguyen Xuan Hong','0205671231','hongxuanng@gmail.com',NULL,NULL,NULL,1,NULL,NULL,NULL,NULL),(3,'Ta Thi Cuc','0912121231','cucthita1@gmail.com',NULL,NULL,NULL,1,NULL,NULL,NULL,NULL),(4,'Nguyen Hai Dang','0869145425','',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL),(5,'Nguyen Hai Dang','0869145425','',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL),(9,'Nguyen Hai Dang','0869145425','',NULL,'','CHUA_XU_LY',0,'2026-10-06 10:49:36','2026-10-06 21:58:16',NULL,'manager1'),(10,'Nguyen Hai Dang','0869145425','',NULL,'','CHUA_XU_LY',0,'2026-10-06 11:46:30','2026-10-06 21:58:16','anonymous','manager1'),(13,'Nguyen Hai Dang','0869145426','',NULL,'','CHUA_XU_LY',1,'2026-10-06 11:53:02','2026-10-06 17:14:05','manager1','manager1'),(15,'Nguyen Van B','0869145427','',NULL,'','CHUA_XU_LY',1,'2026-10-06 12:09:49','2026-10-06 16:48:47','anonymous','manager1'),(16,'Nguyen Hai Dang','0968178006','dang170806@gmail.com','','','CHUA_XU_LY',0,'2026-10-06 20:07:23','2026-10-06 22:00:51','anonymous','manager1'),(17,'Nguyen Hai Dang','0869145421','','','','CHUA_XU_LY',1,'2026-10-06 20:46:03','2026-10-06 20:46:03','manager1','manager1'),(19,'Nguyen Van B','0869145421','','','','CHUA_XU_LY',1,'2026-10-06 21:56:12','2026-10-06 21:58:46','manager1','manager1'),(20,'Nguyen Van B','0968178007','oni34915@gmail.com','','','CHUA_XU_LY',NULL,'2026-10-06 22:01:17','2026-10-06 22:01:17','manager1','manager1'),(21,'Nguyen Van Baaaaa','0869145422','oni34915@gmail.com','','','CHUA_XU_LY',1,'2026-10-06 22:04:48','2026-10-06 22:04:48','manager1','manager1'),(22,'Nguyen Van Baaaaa','0869145411','',NULL,'','CHUA_XU_LY',1,'2026-10-06 22:08:36','2026-10-06 22:08:36','anonymous','anonymous'),(23,'Nguyen Hai Dang','0869145412','',NULL,'','CHUA_XU_LY',1,'2026-10-06 22:09:56','2026-10-06 22:09:56','manager2','manager2'),(24,'Nguyen Hai Dang','0869145409','','','','CHUA_XU_LY',1,'2026-10-07 17:06:02','2026-10-07 17:06:02','manager1','manager1'),(25,'Nguyen Hai Dang','0968178008','','','','CHUA_XU_LY',1,'2026-10-07 17:06:22','2026-10-07 17:06:22','manager1','manager1'),(26,'a','0156489873','','','','CHUA_XU_LY',0,'2026-10-09 23:10:10','2026-10-09 23:16:10','manager1','manager1');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `ID` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `AMOUNT` double NOT NULL,
  `PRICE` double NOT NULL,
  `QUANITY` int NOT NULL,
  `ORDER_ID` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `PRODUCT_ID` bigint NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `ORDER_DETAIL_ORD_FK` (`ORDER_ID`),
  KEY `ORDER_DETAIL_PROD_FK` (`PRODUCT_ID`),
  CONSTRAINT `ORDER_DETAIL_ORD_FK` FOREIGN KEY (`ORDER_ID`) REFERENCES `orders` (`ID`),
  CONSTRAINT `ORDER_DETAIL_PROD_FK` FOREIGN KEY (`PRODUCT_ID`) REFERENCES `building` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
INSERT INTO `order_details` VALUES ('29805425-7ab9-4665-8e67-212e75a9de29',12,12,1,'b5ba86bd-144d-4a85-9563-3c373b216efe',1),('aa832e86-b27a-4ac1-9ec1-777db8644388',12,12,1,'b5ba86bd-144d-4a85-9563-3c373b216efe',2);
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `ID` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `AMOUNT` double NOT NULL,
  `CUSTOMER_ADDRESS` varchar(255) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `CUSTOMER_EMAIL` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `CUSTOMER_NAME` varchar(255) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `CUSTOMER_PHONE` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `ORDER_DATE` datetime NOT NULL,
  `ORDER_NUM` int NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ORDER_UK` (`ORDER_NUM`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('b5ba86bd-144d-4a85-9563-3c373b216efe',24,'InnYa Street','overmidnight@gmail.com','susu','09772919500','2020-03-03 21:40:56',1);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rentarea`
--

DROP TABLE IF EXISTS `rentarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rentarea` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `value` int DEFAULT NULL,
  `buildingid` bigint DEFAULT NULL,
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `rentarea_building` (`buildingid`),
  CONSTRAINT `rentarea_building` FOREIGN KEY (`buildingid`) REFERENCES `building` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rentarea`
--

LOCK TABLES `rentarea` WRITE;
/*!40000 ALTER TABLE `rentarea` DISABLE KEYS */;
INSERT INTO `rentarea` VALUES (3,200,2,NULL,NULL,NULL,NULL),(4,300,2,NULL,NULL,NULL,NULL),(5,400,2,NULL,NULL,NULL,NULL),(9,100,4,NULL,NULL,NULL,NULL),(10,400,4,NULL,NULL,NULL,NULL),(11,250,4,NULL,NULL,NULL,NULL),(93,300,3,NULL,NULL,NULL,NULL),(94,400,3,NULL,NULL,NULL,NULL),(95,500,3,NULL,NULL,NULL,NULL),(96,100,1,NULL,NULL,NULL,NULL),(97,200,1,NULL,NULL,NULL,NULL),(98,12,59,NULL,NULL,NULL,NULL),(99,23,59,NULL,NULL,NULL,NULL),(100,300,60,NULL,NULL,NULL,NULL),(101,400,60,NULL,NULL,NULL,NULL),(102,500,60,NULL,NULL,NULL,NULL),(103,600,60,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `rentarea` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Quản lý','ROLE_MANAGER',NULL,NULL,NULL,NULL),(2,'Nhân viên','ROLE_STAFF',NULL,NULL,NULL,NULL),(3,'Người dùng','ROLE_USER',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `customerid` bigint NOT NULL,
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  `staffid` bigint DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_customer_transaction` (`customerid`),
  CONSTRAINT `fk_customer_transaction` FOREIGN KEY (`customerid`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,'CSKH','note cl t s',17,NULL,'2026-10-06 21:49:04',NULL,'manager1',5,1),(2,'CSKH','not cl',17,NULL,NULL,NULL,NULL,3,1),(3,'DDX','cho manh',17,NULL,NULL,NULL,NULL,NULL,1),(5,'DDX','di xem sẽ',17,'2026-10-06 21:49:35','2026-10-06 21:49:35','manager1','manager1',NULL,1),(6,'CSKH','s',17,'2026-10-06 21:57:46','2026-10-06 21:57:53','manager1','manager1',NULL,0),(7,'CSKH','abs',19,'2026-10-06 21:59:06','2026-10-06 21:59:06','manager1','manager1',NULL,1),(8,'DDX','cde',19,'2026-10-06 21:59:14','2026-10-06 21:59:14','manager1','manager1',NULL,1),(9,'CSKH','av',1,'2026-10-06 22:21:43','2026-10-06 22:21:43','admin1','admin1',NULL,1);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fullname` varchar(255) DEFAULT NULL,
  `phone` varchar(10) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `image` longblob,
  `userrole` varchar(20) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `createddate` datetime DEFAULT NULL,
  `modifieddate` datetime DEFAULT NULL,
  `createdby` varchar(255) DEFAULT NULL,
  `modifiedby` varchar(255) DEFAULT NULL,
  `google_account_id` varchar(255) DEFAULT NULL,
  `github_account_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'manager1','$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu','Devon Nguyen','0366688868',NULL,1,NULL,'ROLE_MANAGER',NULL,NULL,NULL,NULL,NULL,NULL),(2,'admin1','$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu','Nguyen Van A',NULL,NULL,1,NULL,'ROLE_STAFF',NULL,NULL,NULL,NULL,NULL,NULL),(3,'user1','$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu','Nguyen Van B',NULL,NULL,1,NULL,'ROLE_USER',NULL,NULL,NULL,NULL,NULL,NULL),(5,'admin2','12345678','Nguyen Hai Dang',NULL,NULL,1,NULL,'ROLE_STAFF',NULL,NULL,NULL,NULL,NULL,NULL),(6,'admin3','12345678','Nguyen Hai Dang 3',NULL,NULL,1,NULL,'ROLE_STAFF',NULL,NULL,NULL,NULL,NULL,NULL),(7,'admin4','12345678','Nguyen Hai Dang 4',NULL,NULL,1,NULL,'ROLE_STAFF',NULL,NULL,NULL,NULL,NULL,NULL),(8,'admin5','12345678','Nguyen Hai Dang 5',NULL,NULL,1,NULL,'ROLE_STAFF',NULL,NULL,NULL,NULL,NULL,NULL),(9,'admin6','12345678','Nguyen Hai Dang 6',NULL,NULL,1,NULL,'ROLE_STAFF',NULL,NULL,NULL,NULL,NULL,NULL),(10,'dang170806@gmail.com','$2a$10$so5.gwu5O1dmJaz4thbPCuUYrWdKyI1cogiJKiwCrblsrC1/NgAHC','Đăng Nguyễn Hải',NULL,NULL,1,NULL,'ROLE_MANAGER',NULL,NULL,NULL,NULL,'107112344425441284407',NULL),(11,'dang170806','$2a$10$pjNzyxGIuH./gHc3C/bDbeJodYaLxoNMonFH.gmLmkRPcHAlLuati','Nguyen Hai Dang',NULL,NULL,1,NULL,'ROLE_USER',NULL,NULL,NULL,NULL,NULL,NULL),(12,'manager2','$2a$10$apj8U.NM0Jk4D.F7g4CGQ.hIR/NjiJgCPflIHTD4XorHJAPPc20CG','Nguyen Hai Dang',NULL,NULL,1,NULL,'ROLE_USER',NULL,NULL,NULL,NULL,NULL,NULL);
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

-- Dump completed on 2026-10-09 23:21:10
