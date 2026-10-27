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

-- Du lieu mau cho local/demo; thong tin khach hang va lien he deu la gia lap.
-- Tai khoan demo: admin / 123456 (mat khau duoc ma hoa BCrypt trong bang user).

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
INSERT INTO `assignmentbuilding` (`id`,`staffid`,`buildingid`,`createddate`,`createdby`) VALUES
(1,2,1,'2026-10-01 09:00:00','seed'),
(2,2,2,'2026-10-01 09:00:00','seed'),
(3,2,3,'2026-10-01 09:00:00','seed'),
(4,3,4,'2026-10-01 09:00:00','seed'),
(5,3,5,'2026-10-01 09:00:00','seed'),
(6,3,6,'2026-10-01 09:00:00','seed'),
(7,4,7,'2026-10-01 09:00:00','seed'),
(8,4,8,'2026-10-01 09:00:00','seed'),
(9,4,9,'2026-10-01 09:00:00','seed'),
(10,2,10,'2026-10-01 09:00:00','seed'),
(11,3,11,'2026-10-01 09:00:00','seed'),
(12,4,12,'2026-10-01 09:00:00','seed');
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
INSERT INTO `assignmentcustomer` (`id`,`staffid`,`customerid`,`createddate`,`createdby`) VALUES
(1,2,1,'2026-10-01 09:00:00','seed'),
(2,2,2,'2026-10-01 09:00:00','seed'),
(3,2,3,'2026-10-01 09:00:00','seed'),
(4,3,4,'2026-10-01 09:00:00','seed'),
(5,3,5,'2026-10-01 09:00:00','seed'),
(6,3,6,'2026-10-01 09:00:00','seed'),
(7,4,7,'2026-10-01 09:00:00','seed'),
(8,4,8,'2026-10-01 09:00:00','seed'),
(9,4,9,'2026-10-01 09:00:00','seed'),
(10,2,10,'2026-10-01 09:00:00','seed'),
(11,3,11,'2026-10-01 09:00:00','seed'),
(12,4,12,'2026-10-01 09:00:00','seed');
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
INSERT INTO `building` (`id`,`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,`rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,`deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`linkofbuilding`,`map`,`createddate`,`createdby`,`managername`,`managerphone`) VALUES
(1,'Saigon Pearl Office','Nguyen Huu Canh','Phuong 22','Quận 1','1 ham, 12 tang',1,180,'Dong Nam','Van phong',42000000,'Gia thue theo thang','15000/m2/thang','2000000/thang','200000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','12 thang','15 ngay',1.00,'NOI_THAT','Van phong view song, ban giao noi that co ban',NULL,NULL,'2026-10-01 08:00:00','seed','Nguyen Minh Quan','0901000001'),
(2,'Ben Thanh Business Center','Le Loi','Ben Nghe','Quận 1','2 ham, 10 tang',2,220,'Dong','Van phong',55000000,'Gia thue theo thang','18000/m2/thang','2500000/thang','250000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','24 thang','30 ngay',1.50,'NOI_THAT','Gan ga metro va cho Ben Thanh',NULL,NULL,'2026-10-01 08:00:00','seed','Tran Thu Ha','0901000002'),
(3,'Nguyen Hue Retail House','Nguyen Hue','Ben Nghe','Quận 1','1 ham, 5 tang',1,95,'Tay Nam','Thuong mai',38000000,'Gia thue theo thang','20000/m2/thang','1800000/thang','180000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','12 thang','15 ngay',1.00,'TANG_TRET','Mat tien phu hop kinh doanh va showroom',NULL,NULL,'2026-10-01 08:00:00','seed','Le Thanh Nam','0901000003'),
(4,'Thao Dien Riverside Villa','Quoc Huong','Thao Dien','Quận 2','1 tret, 2 lau',0,260,'Dong Nam','Nha pho',48000000,'Gia thue theo thang','Thoa thuan','1500000/thang','150000/thang','Thoa thuan','Theo dong ho','Theo dong ho','2 thang','Hang thang','12 thang','30 ngay',1.00,'NGUYEN_CAN','Nha nguyen can co san vuon, khu dan tri cao',NULL,NULL,'2026-10-01 08:00:00','seed','Pham Hoang Long','0901000004'),
(5,'An Phu Garden Offices','Song Hanh','An Phu','Quận 2','1 ham, 8 tang',1,145,'Bac','Van phong',32000000,'Gia thue theo thang','16000/m2/thang','1800000/thang','180000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','12 thang','15 ngay',1.00,'NOI_THAT','Van phong co khu tiep khach rieng',NULL,NULL,'2026-10-01 08:00:00','seed','Nguyen Minh Quan','0901000001'),
(6,'Thao Dien Boutique Space','Xuan Thuy','Thao Dien','Quận 2','1 tret, 3 lau',0,110,'Dong','Thuong mai',28000000,'Gia thue theo thang','Thoa thuan','1200000/thang','120000/thang','Thoa thuan','Theo dong ho','Theo dong ho','2 thang','Hang thang','12 thang','15 ngay',1.00,'TANG_TRET','Khong gian tang tret phu hop cafe va ban le',NULL,NULL,'2026-10-01 08:00:00','seed','Tran Thu Ha','0901000002'),
(7,'Riverside Tower Office','Ben Van Don','Phuong 1','Quận 4','2 ham, 18 tang',2,200,'Dong Nam','Van phong',36000000,'Gia thue theo thang','17000/m2/thang','2000000/thang','200000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','24 thang','30 ngay',1.50,'NOI_THAT','Mat bang thoang, co phong hop chung',NULL,NULL,'2026-10-01 08:00:00','seed','Le Thanh Nam','0901000003'),
(8,'Khanh Hoi Townhouse','Ton That Thuyet','Phuong 16','Quận 4','1 tret, 3 lau',0,150,'Nam','Nha pho',26000000,'Gia thue theo thang','Thoa thuan','1200000/thang','120000/thang','Thoa thuan','Theo dong ho','Theo dong ho','2 thang','Hang thang','12 thang','30 ngay',1.00,'NGUYEN_CAN','Nha nguyen can gan trung tam va truong hoc',NULL,NULL,'2026-10-01 08:00:00','seed','Pham Hoang Long','0901000004'),
(9,'District 4 Creative Hub','Hoang Dieu','Phuong 6','Quận 4','1 ham, 7 tang',1,125,'Tay','Van phong',24000000,'Gia thue theo thang','15000/m2/thang','1500000/thang','150000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','12 thang','15 ngay',1.00,'NOI_THAT','Van phong nho, hop voi nhom khoi nghiep',NULL,NULL,'2026-10-01 08:00:00','seed','Nguyen Minh Quan','0901000001'),
(10,'Le Thanh Ton Shopfront','Le Thanh Ton','Ben Nghe','Quận 1','1 tret, 2 lau',0,80,'Dong','Thuong mai',45000000,'Gia thue theo thang','Thoa thuan','2000000/thang','200000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','24 thang','30 ngay',1.50,'TANG_TRET','Mat bang kinh doanh khu trung tam',NULL,NULL,'2026-10-01 08:00:00','seed','Tran Thu Ha','0901000002'),
(11,'An Khanh Family Home','Mai Chi Tho','An Phu','Quận 2','1 tret, 2 lau',0,190,'Dong Nam','Nha pho',34000000,'Gia thue theo thang','Thoa thuan','1500000/thang','150000/thang','Thoa thuan','Theo dong ho','Theo dong ho','2 thang','Hang thang','12 thang','30 ngay',1.00,'NGUYEN_CAN','Nha rong, co cho de xe hoi',NULL,NULL,'2026-10-01 08:00:00','seed','Le Thanh Nam','0901000003'),
(12,'Calmette Business Suites','Calmette','Nguyen Thai Binh','Quận 1','1 ham, 9 tang',1,135,'Bac','Van phong',30000000,'Gia thue theo thang','16000/m2/thang','1700000/thang','170000/thang','Thoa thuan','Theo dong ho','Theo dong ho','3 thang','Hang thang','12 thang','15 ngay',1.00,'NOI_THAT','Co san noi that va le tan chung',NULL,NULL,'2026-10-01 08:00:00','seed','Pham Hoang Long','0901000004');
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
INSERT INTO `customer` (`id`,`fullname`,`phone`,`email`,`companyname`,`demand`,`status`,`is_active`,`createddate`,`createdby`) VALUES
(1,'Nguyen An Khang','0912340001','khang.nguyen@example.test','Khang Consulting','Tim van phong 150-200 m2 tai Quan 1','DANG_XU_LY',1,'2026-10-02 09:15:00','seed'),
(2,'Tran Bao Chau','0912340002','chau.tran@example.test','Chau Design Studio','Mat bang kinh doanh tang tret','CHUA_XU_LY',1,'2026-10-02 10:00:00','seed'),
(3,'Le Minh Duc','0912340003','duc.le@example.test','Minh Duc Trading','Thue nha nguyen can khu Thao Dien','DANG_XU_LY',1,'2026-10-03 08:30:00','seed'),
(4,'Pham Gia Han','0912340004','han.pham@example.test','Han Digital','Van phong 100-150 m2, co noi that','DA_XU_LY',1,'2026-10-03 11:20:00','seed'),
(5,'Vo Thanh Tung','0912340005','tung.vo@example.test','Tung Logistics','Van phong gan trung tam, hop dong 2 nam','DANG_XU_LY',1,'2026-10-04 13:10:00','seed'),
(6,'Dang Ngoc Mai','0912340006','mai.dang@example.test','Mai Foods','Cua hang khu Quan 1 hoac Quan 4','CHUA_XU_LY',1,'2026-10-04 14:40:00','seed'),
(7,'Bui Quoc Bao','0912340007','bao.bui@example.test','Bao Architecture','Nha nguyen can 3 phong ngu','DANG_XU_LY',1,'2026-10-05 09:00:00','seed'),
(8,'Hoang Yen Nhi','0912340008','nhi.hoang@example.test','Nhi Wellness','Mat bang tang tret lam studio','DA_XU_LY',1,'2026-10-05 15:30:00','seed'),
(9,'Do Trung Hieu','0912340009','hieu.do@example.test','Hieu Software','Van phong cho nhom 20 nguoi','CHUA_XU_LY',1,'2026-10-06 08:45:00','seed'),
(10,'Nguyen Khanh Linh','0912340010','linh.nguyen@example.test','Linh Education','Nha pho gan truong hoc','DANG_XU_LY',1,'2026-10-06 10:15:00','seed'),
(11,'Truong Minh Tam','0912340011','tam.truong@example.test','Tam Media','Van phong nho tai Quan 4','DA_XU_LY',1,'2026-10-07 16:00:00','seed'),
(12,'Ly Thu Phuong','0912340012','phuong.ly@example.test','Phuong Retail','Mat bang ban le gan ga metro','CHUA_XU_LY',1,'2026-10-08 09:45:00','seed');
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
INSERT INTO `order_details` (`ID`,`AMOUNT`,`PRICE`,`QUANITY`,`ORDER_ID`,`PRODUCT_ID`) VALUES
('20000000-0000-4000-8000-000000000001',42000000,42000000,1,'10000000-0000-4000-8000-000000000001',1),
('20000000-0000-4000-8000-000000000002',28000000,28000000,1,'10000000-0000-4000-8000-000000000002',6);
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
INSERT INTO `orders` (`ID`,`AMOUNT`,`CUSTOMER_ADDRESS`,`CUSTOMER_EMAIL`,`CUSTOMER_NAME`,`CUSTOMER_PHONE`,`ORDER_DATE`,`ORDER_NUM`) VALUES
('10000000-0000-4000-8000-000000000001',42000000,'12 Le Loi, Ben Nghe, District 1','khang.nguyen@example.test','Nguyen An Khang','0912340001','2026-10-10 10:30:00',1001),
('10000000-0000-4000-8000-000000000002',28000000,'25 Xuan Thuy, Thao Dien, District 2','mai.dang@example.test','Dang Ngoc Mai','0912340006','2026-10-12 14:00:00',1002);
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
INSERT INTO `rentarea` (`id`,`value`,`buildingid`,`createddate`,`createdby`) VALUES
(1,180,1,'2026-10-01 08:00:00','seed'),(2,95,3,'2026-10-01 08:00:00','seed'),
(3,120,1,'2026-10-01 08:00:00','seed'),(4,220,2,'2026-10-01 08:00:00','seed'),
(5,110,3,'2026-10-01 08:00:00','seed'),(6,260,4,'2026-10-01 08:00:00','seed'),
(7,145,5,'2026-10-01 08:00:00','seed'),(8,110,6,'2026-10-01 08:00:00','seed'),
(9,200,7,'2026-10-01 08:00:00','seed'),(10,150,8,'2026-10-01 08:00:00','seed'),
(11,125,9,'2026-10-01 08:00:00','seed'),(12,80,10,'2026-10-01 08:00:00','seed'),
(13,190,11,'2026-10-01 08:00:00','seed'),(14,135,12,'2026-10-01 08:00:00','seed');
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
INSERT INTO `transaction` (`id`,`code`,`note`,`customerid`,`createddate`,`createdby`,`staffid`,`is_active`) VALUES
(1,'CSKH','Goi dien xac nhan nhu cau va ngan sach.',1,'2026-10-02 10:00:00','seed',2,1),
(2,'DDX','Hen xem mat bang vao chieu thu Sau.',1,'2026-10-04 14:00:00','seed',2,1),
(3,'CSKH','Gui danh sach van phong Quan 1 qua email.',2,'2026-10-03 09:30:00','seed',2,1),
(4,'DDX','Khach da xem nha tai Thao Dien.',3,'2026-10-05 15:00:00','seed',2,1),
(5,'CSKH','Tu van hop dong thue 24 thang.',4,'2026-10-04 11:00:00','seed',3,1),
(6,'DDX','Hen xem Riverside Tower sang thu Hai.',5,'2026-10-06 09:00:00','seed',3,1),
(7,'CSKH','Dang cho khach bo sung thong tin cong ty.',6,'2026-10-05 10:30:00','seed',3,1),
(8,'DDX','Da xem can nha Nguyen can tai Quan 4.',7,'2026-10-07 16:00:00','seed',4,1),
(9,'CSKH','Khach xac nhan da nhan bao gia.',8,'2026-10-06 13:00:00','seed',4,1),
(10,'DDX','Hen xem Creative Hub vao ngay 15/10.',9,'2026-10-08 08:45:00','seed',4,1),
(11,'CSKH','Tu van khu vuc gan truong hoc.',10,'2026-10-08 11:30:00','seed',2,1),
(12,'DDX','Da xem van phong va dang xem xet.',11,'2026-10-09 14:30:00','seed',3,1);
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
INSERT INTO `user` (`id`,`username`,`password`,`fullname`,`phone`,`email`,`active`,`userrole`,`createddate`,`createdby`) VALUES
(1,'admin','$2a$10$wKoUMS0YvFmy1Hrs6qvocufeA4DRmT1UxqdN4mbUTC5rSPnRQJxPq','Quan tri vien','0909000001','admin@example.test',1,'ROLE_MANAGER','2026-10-01 08:00:00','seed'),
(2,'staff01','$2a$10$wKoUMS0YvFmy1Hrs6qvocufeA4DRmT1UxqdN4mbUTC5rSPnRQJxPq','Nguyen Minh Quan','0909000002','quan.nguyen@example.test',1,'ROLE_STAFF','2026-10-01 08:00:00','seed'),
(3,'staff02','$2a$10$wKoUMS0YvFmy1Hrs6qvocufeA4DRmT1UxqdN4mbUTC5rSPnRQJxPq','Tran Thu Ha','0909000003','ha.tran@example.test',1,'ROLE_STAFF','2026-10-01 08:00:00','seed'),
(4,'staff03','$2a$10$wKoUMS0YvFmy1Hrs6qvocufeA4DRmT1UxqdN4mbUTC5rSPnRQJxPq','Le Thanh Nam','0909000004','nam.le@example.test',1,'ROLE_STAFF','2026-10-01 08:00:00','seed');
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
