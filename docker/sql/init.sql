-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: flight
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `airlines`
--

DROP TABLE IF EXISTS `airlines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airlines` (
  `airline_id` int NOT NULL AUTO_INCREMENT,
  `airline_code` varchar(2) NOT NULL COMMENT '航空公司代码(如CA, MU)',
  `airline_name` varchar(50) NOT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`airline_id`),
  UNIQUE KEY `airline_code` (`airline_code`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airlines`
--

/*!40000 ALTER TABLE `airlines` DISABLE KEYS */;
INSERT INTO `airlines` VALUES (1,'CA','中国国际航空','95583','www.airchina.com.cn','2025-05-28 16:08:17'),(2,'MU','中国东方航空','95530','www.ceair.com','2025-05-28 16:08:17'),(3,'CZ','中国南方航空','95539','www.csair.com','2025-05-28 16:08:17'),(4,'HU','海南航空','95339','www.hnair.com','2025-05-28 16:08:17'),(6,'MZ','河南航空','95561','www.henanair.com','2025-06-23 18:20:20'),(7,'ZH','深圳航空公司','95566','www.shenzhenairport.com','2025-06-24 04:05:22'),(8,'SH','上海电力大学航空','95522','www.suep.com','2025-12-23 07:48:50'),(9,'SB','松江1航空','12345','','2025-12-24 01:28:36');
/*!40000 ALTER TABLE `airlines` ENABLE KEYS */;

--
-- Table structure for table `airports`
--

DROP TABLE IF EXISTS `airports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airports` (
  `airport_code` varchar(3) NOT NULL COMMENT '机场三字码',
  `airport_name` varchar(100) NOT NULL,
  `city` varchar(50) NOT NULL,
  `country` varchar(50) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`airport_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airports`
--

/*!40000 ALTER TABLE `airports` DISABLE KEYS */;
INSERT INTO `airports` VALUES ('CAN','广州白云国际机场','广州','中国','2025-05-28 16:08:21'),('CTU','成都双流国际机场','成都','中国','2025-05-28 16:08:21'),('HEN','河南首都国际机场','河南','中国','2025-06-23 18:20:53'),('HKG','香港国际机场','香港','中国','2025-05-28 16:08:21'),('PEK','北京首都国际机场','北京','中国','2025-05-28 16:08:21'),('PVG','上海浦东国际机场','上海','中国','2025-05-28 16:08:21'),('SHA','上海虹桥国际机场','上海','中国','2025-05-28 16:08:21'),('SZX','深圳宝安国际机场','深圳','中国','2025-05-28 16:08:21'),('TPE','台湾桃园国际机场','台北','中国','2025-05-28 16:08:21');
/*!40000 ALTER TABLE `airports` ENABLE KEYS */;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` tinytext NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
INSERT INTO `announcement` VALUES (3,'暴雪','2025-12-25 16:14:40.922000',NULL,NULL,'冰雹'),(4,'航班延误，飞机停飞','2025-12-28 10:09:47.497000',NULL,NULL,'冰雹'),(5,'因为雷暴雨，航班延误','2025-12-28 10:19:53.461000',NULL,NULL,'雷暴雨'),(6,'由于雅利洛6号的极端天气，我们无奈只能把14号的航班取消','2026-01-09 19:19:38.394000',NULL,NULL,'暴雪天，14号的航班取消'),(7,'由于雅利洛6号的极端天气，会有大暴雨，飞机会有安全隐患，所以选择取消16号航班。','2026-01-09 19:25:34.491000',NULL,NULL,'大暴雨，16号航班取消'),(8,'因为雅利洛6号的极端天气影响，会有超强雷阵雨，对飞行受阻，所以选择取消该航班。','2026-01-09 19:35:58.717000',NULL,NULL,'18号的MU5101航班取消，因为大暴雨'),(9,'由于雅利洛6号强雷阵雨，飞行受阻，所以取消该航班。','2026-01-09 19:38:42.964000',NULL,NULL,'12号的MU5101航班取消。'),(10,'1','2026-01-09 19:47:44.268000',NULL,NULL,'1'),(11,'2','2026-01-11 08:51:26.375000',NULL,NULL,'1'),(12,'333','2026-01-11 09:20:05.810000',NULL,NULL,'222');
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;

--
-- Table structure for table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` text,
  `announcement_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK12w0loi0ekay1jfqjjsw3362s` (`announcement_id`),
  CONSTRAINT `FK12w0loi0ekay1jfqjjsw3362s` FOREIGN KEY (`announcement_id`) REFERENCES `announcement` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attachment`
--

/*!40000 ALTER TABLE `attachment` DISABLE KEYS */;
INSERT INTO `attachment` VALUES (5,'屏幕截图 2025-12-11 152323.png','http://127.0.0.1:9000/flight-files/a3f013ba-1866-46ab-90ff-a9ec4f06b445.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20251225%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251225T161439Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=e611d689baa18c1ebcd40f2b8745fa0956cd43bcbb61aa751437ebdfefb92a64',3),(6,'屏幕截图 2025-12-11 152323.png','http://127.0.0.1:9000/flight-files/8d541057-de13-42b9-8d9e-aa3b6bf78c63.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20251228%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251228T100942Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=1fb64d7398f6ce79c9971f481c24d61b9bcd103fca4c107e74927615a109a830',4),(7,'屏幕截图 2025-12-11 153824.png','http://127.0.0.1:9000/flight-files/541f132c-2cbc-478a-bdcb-68f28ed88ea4.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20251228%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251228T101951Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=dddef39b8af77eb03a0150080bd42e85b3c668d8c14282557177bf18b680788b',5),(8,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/c0630c4b-3cfa-4bd2-9854-e4697547014f.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260109%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260109T191928Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=2a01d37c8fac32d00b63840a4c5f356d33fa42a794daedc424a07e81dd5786bf',6),(9,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/ba2b73cf-e858-470d-992f-4ebc308cdc1a.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260109%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260109T192522Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=ff6df38905ab6401d007663cd89e4dab145ef0c951703735b29b645b6ffc5299',7),(10,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/7ee42fea-b2e7-422f-a117-ef3d60613f84.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260109%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260109T193555Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=05b3681a243ccc2984bcbd3b82671741070896e8f832e613667d2a0274141674',8),(11,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/5241140f-7556-4a0c-9b61-f548a914a15d.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260109%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260109T193840Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=43b921d6dbac250b8e575c1c72a4f9b2ccd768d64ea246191fc18ee804cc0175',9),(12,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/ff326a50-a3f3-4d24-958f-dae5268773b9.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260109%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260109T194736Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=aa9fa4462983397503de06e9da843b3dde73633c55bd69173d1230adac74cc7d',10),(13,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/90c19f34-8b45-4908-8549-b98ce31ae324.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260110%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260110T183848Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=8c5698f8426b47d39e6ef3b649d1e3c5e5e30f926e80f7786b879e9691f332a2',10),(14,'JavaEE大作业报告模板.pdf','http://127.0.0.1:9000/flight-files/1cf998c2-8bc5-41fe-bb85-39ab9b3ed08d.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260111%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260111T085124Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=b15a70ef6800b1e6e4e388916867ab0d1fd231607637f457a8697715597b9cbe',11),(15,'JavaEE大作业报告模板.pdf','http://127.0.0.1:9000/flight-files/2a53f97a-3aa8-4b5b-bbbf-4124d663be5b.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260111%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260111T092003Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=45adce5558b402da00fc79f3ad2a210e30f42c7ad69fe3585e3c1cb6874645c7',12);
/*!40000 ALTER TABLE `attachment` ENABLE KEYS */;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `files` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL COMMENT '文件的原始名称',
  `file_url` varchar(512) NOT NULL COMMENT '文件在MinIO中的访问URL',
  `upload_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文件上传时间',
  `airline_id` int NOT NULL COMMENT '关联的航空公司ID',
  PRIMARY KEY (`id`),
  KEY `fk_files_airline_idx` (`airline_id`),
  CONSTRAINT `fk_files_airline` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`airline_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `files`
--

/*!40000 ALTER TABLE `files` DISABLE KEYS */;
INSERT INTO `files` VALUES (6,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/c8ce3297-383b-431a-a5bf-73165a26bbf8.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260109%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260109T195505Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=4b06aad480a7f834504c49e0d490e38061faac29dc170965d9f91145e1eeacde','2026-01-09 11:55:06',3),(7,'fbf721adde153677fe4756a1a2175d02_720.png','http://127.0.0.1:9000/flight-files/8c9b5abc-8076-44e4-80c8-3d2ef5d3652f.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20260111%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260111T091913Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e349c6d289d818a62a978b375d74523e2085e38bcd16c305853c50b710b3958e','2026-01-11 01:19:14',2);
/*!40000 ALTER TABLE `files` ENABLE KEYS */;

--
-- Temporary view structure for view `flight_details`
--

DROP TABLE IF EXISTS `flight_details`;
/*!50001 DROP VIEW IF EXISTS `flight_details`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `flight_details` AS SELECT 
 1 AS `flight_id`,
 1 AS `flight_number`,
 1 AS `airline_code`,
 1 AS `airline_name`,
 1 AS `departure_airport_name`,
 1 AS `departure_city`,
 1 AS `arrival_airport_name`,
 1 AS `arrival_city`,
 1 AS `departure_time`,
 1 AS `arrival_time`,
 1 AS `duration`,
 1 AS `aircraft_model`,
 1 AS `total_seats`,
 1 AS `business_seats`,
 1 AS `economy_seats`,
 1 AS `economy_seat_price`,
 1 AS `business_seat_price`,
 1 AS `status`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `flight_routes`
--

DROP TABLE IF EXISTS `flight_routes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_routes` (
  `route_id` int NOT NULL AUTO_INCREMENT COMMENT '航线ID，主键',
  `flight_number` varchar(255) NOT NULL COMMENT '基础航班号，不含航司代码',
  `airline_id` int NOT NULL COMMENT '外键，关联航空公司',
  `departure_airport_code` varchar(3) NOT NULL COMMENT '外键，关联出发机场',
  `arrival_airport_code` varchar(3) NOT NULL COMMENT '外键，关联到达机场',
  `departure_time` time NOT NULL COMMENT '每日起飞时刻',
  `arrival_time` time NOT NULL COMMENT '每日到达时刻',
  `flight_duration_minutes` int NOT NULL COMMENT '飞行时长（分钟）',
  `aircraft_model` varchar(255) NOT NULL COMMENT '机型',
  `business_seats` int NOT NULL COMMENT '商务舱座位数',
  `business_seat_price` decimal(10,2) NOT NULL COMMENT '商务舱票价',
  `economy_seats` int NOT NULL COMMENT '经济舱座位数',
  `economy_seat_price` decimal(10,2) NOT NULL COMMENT '经济舱票价',
  `active` bit(1) NOT NULL DEFAULT b'1' COMMENT '航线是否启用 (1=是, 0=否)',
  PRIMARY KEY (`route_id`),
  UNIQUE KEY `uk_airline_flight_number` (`airline_id`,`flight_number`),
  KEY `fk_route_to_departure_airport` (`departure_airport_code`),
  KEY `fk_route_to_arrival_airport` (`arrival_airport_code`),
  CONSTRAINT `fk_route_to_airline` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`airline_id`),
  CONSTRAINT `fk_route_to_arrival_airport` FOREIGN KEY (`arrival_airport_code`) REFERENCES `airports` (`airport_code`),
  CONSTRAINT `fk_route_to_departure_airport` FOREIGN KEY (`departure_airport_code`) REFERENCES `airports` (`airport_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='航线模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_routes`
--

/*!40000 ALTER TABLE `flight_routes` DISABLE KEYS */;
INSERT INTO `flight_routes` VALUES (1,'5101',2,'CAN','PEK','08:00:00','10:00:00',120,'A320',12,2000.00,150,1000.00,0x01),(2,'5102',4,'CAN','CTU','10:00:00','12:00:00',120,'A320',12,2000.00,150,1000.00,0x01),(3,'5103',8,'CAN','CTU','09:00:00','10:00:00',60,'A320',12,2000.00,150,1000.00,0x01),(4,'5105',9,'CAN','CTU','07:00:00','12:00:00',300,'A320',12,2000.00,150,1000.00,0x01),(5,'5106',3,'CTU','PVG','07:00:00','10:00:00',180,'A320',12,2000.00,150,1000.00,0x01);
/*!40000 ALTER TABLE `flight_routes` ENABLE KEYS */;

--
-- Table structure for table `flight_seat_availability`
--

DROP TABLE IF EXISTS `flight_seat_availability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_seat_availability` (
  `flight_id` int NOT NULL,
  `available_business_seats` int NOT NULL DEFAULT '0',
  `available_economy_seats` int NOT NULL DEFAULT '0',
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`flight_id`),
  CONSTRAINT `fk_seat_availability_flight` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_seat_availability`
--

/*!40000 ALTER TABLE `flight_seat_availability` DISABLE KEYS */;
INSERT INTO `flight_seat_availability` VALUES (1,19,156,'2025-06-22 18:44:06'),(2,14,143,'2025-06-01 07:52:55'),(3,38,339,'2025-06-22 18:44:35'),(4,30,260,'2025-06-01 06:34:21'),(5,36,264,'2025-06-01 06:34:21'),(6,30,217,'2025-06-23 18:19:14'),(7,24,120,'2025-06-23 04:01:29'),(8,39,140,'2025-06-24 13:31:16'),(9,24,137,'2025-11-25 02:58:53'),(10,24,140,'2025-11-25 10:59:36'),(11,12,150,'2025-12-26 18:26:29'),(12,12,150,'2025-12-26 18:26:29'),(13,12,150,'2025-12-26 18:26:30'),(18,12,149,'2025-12-26 11:28:45'),(19,12,149,'2025-12-27 09:56:20'),(20,12,149,'2025-12-27 10:22:40'),(27,12,150,'2025-12-27 18:20:52'),(28,12,150,'2025-12-27 18:20:52'),(34,12,150,'2025-12-28 10:12:25'),(35,12,148,'2025-12-29 05:32:36'),(41,12,150,'2025-12-28 10:16:45'),(42,12,150,'2025-12-28 10:16:45'),(52,12,149,'2025-12-29 05:42:15'),(53,12,149,'2026-01-09 10:55:45'),(54,11,150,'2025-12-30 02:13:11'),(55,12,150,'2025-12-29 03:54:39'),(56,12,150,'2025-12-29 03:54:39'),(57,12,150,'2025-12-29 03:54:39'),(58,12,150,'2025-12-29 03:59:58'),(59,12,150,'2025-12-29 03:59:58'),(60,12,150,'2025-12-29 03:59:58'),(61,12,150,'2025-12-29 03:59:58'),(62,12,150,'2025-12-29 03:59:58'),(63,12,150,'2025-12-29 03:59:58'),(64,12,150,'2025-12-29 03:59:58'),(65,12,150,'2025-12-29 03:59:58'),(66,12,150,'2025-12-29 03:59:58'),(67,12,150,'2025-12-29 03:59:58'),(68,12,150,'2025-12-29 03:59:58'),(69,12,150,'2025-12-29 03:59:58'),(70,9,145,'2026-01-10 10:36:42'),(71,12,147,'2026-01-11 00:57:18'),(72,12,150,'2025-12-29 03:59:58'),(73,12,150,'2025-12-29 03:59:58'),(74,12,149,'2026-01-11 00:48:32'),(75,12,150,'2025-12-29 03:59:58'),(76,12,150,'2025-12-29 03:59:58'),(77,12,150,'2025-12-29 03:59:58'),(78,12,150,'2025-12-29 03:59:58'),(79,12,150,'2025-12-29 03:59:58'),(80,12,150,'2025-12-29 03:59:58'),(81,12,150,'2025-12-29 03:59:58'),(82,12,150,'2025-12-29 03:59:58'),(83,12,150,'2025-12-29 03:59:58'),(84,12,149,'2026-01-11 01:21:41'),(85,12,150,'2025-12-29 03:59:58'),(86,12,150,'2025-12-29 03:59:58'),(87,12,150,'2025-12-29 03:59:58'),(88,12,150,'2025-12-29 03:59:58'),(89,12,150,'2025-12-29 03:59:58'),(90,12,150,'2025-12-29 03:59:58'),(91,12,150,'2025-12-29 04:41:51'),(92,12,150,'2025-12-29 04:41:51'),(93,9,150,'2025-12-29 05:01:17'),(94,12,150,'2025-12-29 04:41:52'),(95,12,150,'2025-12-29 04:41:52'),(96,12,150,'2025-12-29 04:41:52'),(97,12,150,'2026-01-10 10:11:41'),(98,12,150,'2026-01-10 10:11:41'),(99,12,150,'2026-01-10 10:11:41'),(100,12,150,'2026-01-10 10:11:41'),(101,12,150,'2026-01-10 10:11:41'),(102,12,150,'2026-01-10 10:11:41'),(103,12,150,'2026-01-10 10:11:41'),(104,12,150,'2026-01-10 10:38:00'),(105,12,150,'2026-01-10 10:38:00'),(106,12,150,'2026-01-10 10:38:00'),(107,12,150,'2026-01-10 10:38:00'),(108,12,150,'2026-01-10 10:38:00'),(109,12,150,'2026-01-10 10:38:00'),(110,12,150,'2026-01-10 10:38:00'),(111,12,150,'2026-01-10 10:38:00'),(112,12,150,'2026-01-10 10:38:00'),(113,12,150,'2026-01-10 10:38:00'),(114,12,150,'2026-01-11 01:02:25'),(115,12,150,'2026-01-11 01:02:25'),(116,12,150,'2026-01-11 01:02:25'),(117,12,150,'2026-01-11 01:02:25'),(118,12,150,'2026-01-11 01:02:25'),(119,12,150,'2026-01-11 01:02:25'),(120,12,150,'2026-01-11 01:02:25');
/*!40000 ALTER TABLE `flight_seat_availability` ENABLE KEYS */;

--
-- Table structure for table `flights`
--

DROP TABLE IF EXISTS `flights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flights` (
  `flight_id` int NOT NULL AUTO_INCREMENT,
  `route_id` int NOT NULL,
  `flight_number` varchar(6) NOT NULL,
  `airline_id` int NOT NULL,
  `departure_airport` varchar(3) NOT NULL,
  `arrival_airport` varchar(3) NOT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime NOT NULL,
  `aircraft_model` varchar(50) NOT NULL,
  `total_seats` int NOT NULL,
  `business_seats` int NOT NULL,
  `business_seat_price` decimal(10,2) NOT NULL,
  `economy_seats` int NOT NULL,
  `economy_seat_price` decimal(10,2) NOT NULL,
  `status` enum('PLANNED','BOARDING','IN_FLIGHT','ARRIVED','CANCELLED') DEFAULT 'PLANNED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`flight_id`),
  KEY `airline_id` (`airline_id`),
  KEY `arrival_airport` (`arrival_airport`),
  KEY `idx_flights_departure` (`departure_airport`,`departure_time`),
  KEY `fk_flights_flight_routes` (`route_id`),
  CONSTRAINT `fk_flights_flight_routes` FOREIGN KEY (`route_id`) REFERENCES `flight_routes` (`route_id`),
  CONSTRAINT `flights_ibfk_1` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`airline_id`),
  CONSTRAINT `flights_ibfk_2` FOREIGN KEY (`departure_airport`) REFERENCES `airports` (`airport_code`),
  CONSTRAINT `flights_ibfk_3` FOREIGN KEY (`arrival_airport`) REFERENCES `airports` (`airport_code`),
  CONSTRAINT `flights_chk_1` CHECK ((`business_seats` >= 0)),
  CONSTRAINT `flights_chk_2` CHECK ((`economy_seats` >= 0)),
  CONSTRAINT `flights_chk_3` CHECK ((`total_seats` = (`business_seats` + `economy_seats`))),
  CONSTRAINT `flights_chk_4` CHECK ((`departure_time` < `arrival_time`))
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flights`
--

/*!40000 ALTER TABLE `flights` DISABLE KEYS */;
INSERT INTO `flights` VALUES (1,1,'CA1501',1,'PEK','SHA','2025-06-01 08:00:00','2025-06-01 10:30:00','Boeing 737-800',180,20,2000.00,160,1000.00,'PLANNED','2025-05-28 16:08:30'),(2,1,'MU5102',2,'SHA','CAN','2025-06-01 12:30:00','2025-06-01 15:00:00','Airbus A320',160,16,1800.00,144,800.00,'PLANNED','2025-05-28 16:08:30'),(3,1,'CZ3101',3,'CAN','PEK','2025-06-02 09:00:00','2025-06-02 12:00:00','Boeing 777-300ER',380,40,2500.00,340,1200.00,'PLANNED','2025-05-28 16:08:30'),(4,1,'HU7801',4,'PEK','SZX','2025-06-01 14:00:00','2025-06-01 17:30:00','Boeing 787-9',290,30,3000.00,260,1500.00,'PLANNED','2025-05-28 16:08:30'),(5,1,'CA1837',1,'SHA','PEK','2025-06-02 18:00:00','2025-06-02 20:30:00','Airbus A330',300,36,2000.00,264,900.00,'PLANNED','2025-05-28 16:08:30'),(6,1,'CA801',1,'PVG','TPE','2025-06-24 12:00:00','2025-06-24 14:00:00','Airbus A330',250,30,4500.00,220,1800.00,'PLANNED','2025-06-22 12:58:34'),(7,1,'HU9980',4,'TPE','CAN','2025-06-25 02:00:00','2025-06-25 06:00:00','Boeing 777',144,24,2400.00,120,1200.00,'PLANNED','2025-06-23 04:01:29'),(8,1,'MZ9980',6,'HEN','PVG','2025-06-29 02:00:00','2025-06-29 06:00:00','Boeing 777',180,40,4000.00,140,1400.00,'PLANNED','2025-06-23 18:22:18'),(9,1,'ZH9964',7,'SZX','SHA','2025-06-30 04:00:00','2025-06-30 06:00:00','Boeing 898',164,24,6000.00,140,1500.00,'PLANNED','2025-06-24 04:08:52'),(10,1,'ZH9964',7,'SZX','SHA','2025-11-29 04:00:00','2025-11-29 06:00:00','Boeing 898',164,24,6000.00,140,1500.00,'PLANNED','2025-11-25 10:53:52'),(13,1,'MU5101',2,'CAN','PEK','2025-12-29 00:00:00','2025-12-29 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:45:29'),(27,3,'SH5103',8,'CAN','CTU','2025-12-28 00:00:00','2025-12-28 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-27 10:20:53'),(34,4,'SB5105',9,'CAN','CTU','2025-12-27 23:00:00','2025-12-28 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-28 02:12:26'),(35,4,'SB5105',9,'CAN','CTU','2025-12-28 23:00:00','2025-12-29 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-28 02:12:26'),(41,5,'CZ5106',3,'CTU','PVG','2025-12-27 23:00:00','2025-12-28 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-28 02:16:45'),(42,5,'CZ5106',3,'CTU','PVG','2025-12-28 23:00:00','2025-12-29 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-28 02:16:45'),(52,1,'MU5101',2,'CAN','PEK','2025-12-30 00:00:00','2025-12-30 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:54:39'),(53,1,'MU5101',2,'CAN','PEK','2025-12-31 00:00:00','2025-12-31 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:54:39'),(54,1,'MU5101',2,'CAN','PEK','2026-01-01 00:00:00','2026-01-01 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:54:39'),(55,1,'MU5101',2,'CAN','PEK','2026-01-02 00:00:00','2026-01-02 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:54:39'),(56,1,'MU5101',2,'CAN','PEK','2026-01-03 00:00:00','2026-01-03 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:54:39'),(57,1,'MU5101',2,'CAN','PEK','2026-01-04 00:00:00','2026-01-04 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:54:39'),(58,1,'MU5101',2,'CAN','PEK','2026-01-05 00:00:00','2026-01-05 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(59,1,'MU5101',2,'CAN','PEK','2026-01-06 00:00:00','2026-01-06 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:59:58'),(60,1,'MU5101',2,'CAN','PEK','2026-01-07 00:00:00','2026-01-07 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(61,1,'MU5101',2,'CAN','PEK','2026-01-08 00:00:00','2026-01-08 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(62,1,'MU5101',2,'CAN','PEK','2026-01-09 00:00:00','2026-01-09 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(63,1,'MU5101',2,'CAN','PEK','2026-01-10 00:00:00','2026-01-10 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(64,1,'MU5101',2,'CAN','PEK','2026-01-11 00:00:00','2026-01-11 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:59:58'),(65,1,'MU5101',2,'CAN','PEK','2026-01-12 00:00:00','2026-01-12 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:59:58'),(66,1,'MU5101',2,'CAN','PEK','2026-01-13 00:00:00','2026-01-13 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:59:58'),(67,1,'MU5101',2,'CAN','PEK','2026-01-14 00:00:00','2026-01-14 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:59:58'),(68,1,'MU5101',2,'CAN','PEK','2026-01-15 00:00:00','2026-01-15 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(69,1,'MU5101',2,'CAN','PEK','2026-01-16 00:00:00','2026-01-16 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:59:58'),(70,1,'MU5101',2,'CAN','PEK','2026-01-17 00:00:00','2026-01-17 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(71,1,'MU5101',2,'CAN','PEK','2026-01-18 00:00:00','2026-01-18 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2025-12-29 03:59:58'),(72,1,'MU5101',2,'CAN','PEK','2026-01-19 00:00:00','2026-01-19 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(73,1,'MU5101',2,'CAN','PEK','2026-01-20 00:00:00','2026-01-20 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(74,1,'MU5101',2,'CAN','PEK','2026-01-21 00:00:00','2026-01-21 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(75,1,'MU5101',2,'CAN','PEK','2026-01-22 00:00:00','2026-01-22 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(76,1,'MU5101',2,'CAN','PEK','2026-01-23 00:00:00','2026-01-23 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(77,1,'MU5101',2,'CAN','PEK','2026-01-24 00:00:00','2026-01-24 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(78,1,'MU5101',2,'CAN','PEK','2026-01-25 00:00:00','2026-01-25 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(79,1,'MU5101',2,'CAN','PEK','2026-01-26 00:00:00','2026-01-26 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(80,1,'MU5101',2,'CAN','PEK','2026-01-27 00:00:00','2026-01-27 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(81,1,'MU5101',2,'CAN','PEK','2026-01-28 00:00:00','2026-01-28 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(82,1,'MU5101',2,'CAN','PEK','2026-01-29 00:00:00','2026-01-29 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(83,1,'MU5101',2,'CAN','PEK','2026-01-30 00:00:00','2026-01-30 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(84,1,'MU5101',2,'CAN','PEK','2026-01-31 00:00:00','2026-01-31 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(85,1,'MU5101',2,'CAN','PEK','2026-02-01 00:00:00','2026-02-01 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(86,1,'MU5101',2,'CAN','PEK','2026-02-02 00:00:00','2026-02-02 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(87,1,'MU5101',2,'CAN','PEK','2026-02-03 00:00:00','2026-02-03 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(88,1,'MU5101',2,'CAN','PEK','2026-02-04 00:00:00','2026-02-04 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(89,1,'MU5101',2,'CAN','PEK','2026-02-05 00:00:00','2026-02-05 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(90,1,'MU5101',2,'CAN','PEK','2026-02-06 00:00:00','2026-02-06 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 03:59:58'),(91,5,'CZ5106',3,'CTU','PVG','2025-12-29 23:00:00','2025-12-30 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 04:41:51'),(92,5,'CZ5106',3,'CTU','PVG','2025-12-30 23:00:00','2025-12-31 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 04:41:51'),(93,5,'CZ5106',3,'CTU','PVG','2025-12-31 23:00:00','2026-01-01 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 04:41:52'),(94,5,'CZ5106',3,'CTU','PVG','2026-01-01 23:00:00','2026-01-02 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 04:41:52'),(95,5,'CZ5106',3,'CTU','PVG','2026-01-02 23:00:00','2026-01-03 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 04:41:52'),(96,5,'CZ5106',3,'CTU','PVG','2026-01-03 23:00:00','2026-01-04 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2025-12-29 04:41:52'),(97,5,'CZ5106',3,'CTU','PVG','2026-01-10 23:00:00','2026-01-11 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:11:41'),(98,5,'CZ5106',3,'CTU','PVG','2026-01-11 23:00:00','2026-01-12 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:11:41'),(99,5,'CZ5106',3,'CTU','PVG','2026-01-12 23:00:00','2026-01-13 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:11:41'),(100,5,'CZ5106',3,'CTU','PVG','2026-01-13 23:00:00','2026-01-14 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:11:41'),(101,5,'CZ5106',3,'CTU','PVG','2026-01-14 23:00:00','2026-01-15 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:11:41'),(102,5,'CZ5106',3,'CTU','PVG','2026-01-15 23:00:00','2026-01-16 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:11:41'),(103,5,'CZ5106',3,'CTU','PVG','2026-01-16 23:00:00','2026-01-17 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:11:41'),(104,2,'HU5102',4,'CAN','CTU','2026-01-11 02:00:00','2026-01-11 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(105,2,'HU5102',4,'CAN','CTU','2026-01-12 02:00:00','2026-01-12 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(106,2,'HU5102',4,'CAN','CTU','2026-01-13 02:00:00','2026-01-13 04:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-10 10:38:00'),(107,2,'HU5102',4,'CAN','CTU','2026-01-14 02:00:00','2026-01-14 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(108,2,'HU5102',4,'CAN','CTU','2026-01-15 02:00:00','2026-01-15 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(109,2,'HU5102',4,'CAN','CTU','2026-01-16 02:00:00','2026-01-16 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(110,2,'HU5102',4,'CAN','CTU','2026-01-17 02:00:00','2026-01-17 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(111,2,'HU5102',4,'CAN','CTU','2026-01-18 02:00:00','2026-01-18 04:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-10 10:38:00'),(112,2,'HU5102',4,'CAN','CTU','2026-01-19 02:00:00','2026-01-19 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(113,2,'HU5102',4,'CAN','CTU','2026-01-20 02:00:00','2026-01-20 04:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-10 10:38:00'),(114,3,'SH5103',8,'CAN','CTU','2026-01-11 01:00:00','2026-01-11 02:00:00','A320',162,12,2000.00,150,1000.00,'PLANNED','2026-01-11 01:02:25'),(115,3,'SH5103',8,'CAN','CTU','2026-01-12 01:00:00','2026-01-12 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-11 01:02:25'),(116,3,'SH5103',8,'CAN','CTU','2026-01-13 01:00:00','2026-01-13 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-11 01:02:25'),(117,3,'SH5103',8,'CAN','CTU','2026-01-14 01:00:00','2026-01-14 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-11 01:02:25'),(118,3,'SH5103',8,'CAN','CTU','2026-01-15 01:00:00','2026-01-15 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-11 01:02:25'),(119,3,'SH5103',8,'CAN','CTU','2026-01-16 01:00:00','2026-01-16 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-11 01:02:25'),(120,3,'SH5103',8,'CAN','CTU','2026-01-17 01:00:00','2026-01-17 02:00:00','A320',162,12,2000.00,150,1000.00,'CANCELLED','2026-01-11 01:02:25');
/*!40000 ALTER TABLE `flights` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `init_seat_availability` AFTER INSERT ON `flights` FOR EACH ROW BEGIN
    INSERT INTO flight_seat_availability (flight_id, available_business_seats, available_economy_seats)
    VALUES (NEW.flight_id, NEW.business_seats, NEW.economy_seats);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `passenger_id` int NOT NULL COMMENT '下单乘客ID',
  `flight_id` int DEFAULT NULL,
  `seat_class` enum('ECONOMY','BUSINESS') DEFAULT NULL,
  `ticket_passenger_id` int NOT NULL COMMENT '乘机人ID',
  `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `base_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `actual_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `discount_rate` decimal(5,2) DEFAULT '0.00',
  `status` enum('PENDING_PAYMENT','PAID','TICKETED','CANCELLED','COMPLETED','REFUNDED') DEFAULT 'PENDING_PAYMENT',
  `contact_phone` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `idx_orders_time` (`order_time`),
  KEY `fk_orders_passenger` (`passenger_id`),
  KEY `fk_orders_ticket_passenger` (`ticket_passenger_id`),
  KEY `idx_flight_passenger_non_unique` (`flight_id`,`ticket_passenger_id`),
  CONSTRAINT `fk_orders_flight` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`),
  CONSTRAINT `fk_orders_passenger` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`),
  CONSTRAINT `fk_orders_ticket_passenger` FOREIGN KEY (`ticket_passenger_id`) REFERENCES `passengers` (`passenger_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,1,'BUSINESS',1,'2025-05-25 10:30:00',2000.00,2000.00,0.00,'CANCELLED','18901952422','2025-05-28 16:08:35'),(2,2,2,'BUSINESS',2,'2025-05-26 14:15:00',1800.00,1800.00,0.00,'PAID','13900139000','2025-05-28 16:08:35'),(3,3,3,'BUSINESS',4,'2025-05-27 09:45:00',2500.00,2500.00,0.00,'PAID','13600136000','2025-05-28 16:08:35'),(4,1,1,'ECONOMY',3,'2025-01-02 06:00:00',1000.00,700.00,30.00,'PAID','13700137000','2025-05-31 14:46:54'),(6,4,2,'ECONOMY',1,'2025-05-27 02:00:51',800.00,640.00,20.00,'CANCELLED','18901952422','2025-05-31 15:06:29'),(7,4,2,'BUSINESS',3,'2025-05-27 06:00:33',1800.00,1260.00,30.00,'PAID','13700137000','2025-05-31 15:47:50'),(8,4,2,'ECONOMY',4,'2025-05-18 08:06:55',800.00,640.00,20.00,'CANCELLED','13600136000','2025-06-01 06:57:37'),(10,1,1,'ECONOMY',2,'2025-06-22 02:02:08',1000.00,1000.00,0.00,'PAID','13900139000','2025-06-21 18:02:08'),(18,1,1,'ECONOMY',1,'2025-06-22 08:33:31',1000.00,1000.00,0.00,'CANCELLED','18901952422','2025-06-22 00:33:31'),(19,1,1,'BUSINESS',4,'2025-06-22 08:51:04',2000.00,2000.00,0.00,'CANCELLED','13600136000','2025-06-22 00:51:04'),(20,1,1,'BUSINESS',6,'2025-06-22 08:52:18',2000.00,2000.00,0.00,'CANCELLED','13600136000','2025-06-22 00:52:18'),(21,1,1,'ECONOMY',4,'2025-06-22 09:16:10',1000.00,1000.00,0.00,'PAID','13600136000','2025-06-22 01:16:10'),(22,1,3,'ECONOMY',1,'2025-06-22 10:41:08',1200.00,1080.00,10.00,'PAID','18901952422','2025-06-22 02:41:08'),(23,1,1,'ECONOMY',1,'2025-06-22 12:02:22',1000.00,900.00,10.00,'CANCELLED','18901952422','2025-06-22 04:02:22'),(24,1,1,'ECONOMY',1,'2025-06-22 12:06:49',1000.00,900.00,10.00,'REFUNDED','18901952422','2025-06-22 04:06:49'),(25,1,1,'ECONOMY',1,'2025-06-22 12:21:28',1000.00,900.00,10.00,'REFUNDED','18901952422','2025-06-22 04:21:28'),(26,1,1,'ECONOMY',1,'2025-06-22 12:52:41',1000.00,900.00,10.00,'PAID','18901952422','2025-06-22 04:52:41'),(27,1,6,'ECONOMY',1,'2025-06-22 13:01:11',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 05:01:11'),(28,1,6,'ECONOMY',1,'2025-06-22 13:56:45',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 05:56:45'),(29,1,6,'ECONOMY',1,'2025-06-23 01:43:52',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 17:43:52'),(30,1,6,'ECONOMY',1,'2025-06-23 01:45:14',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 17:45:14'),(31,1,6,'BUSINESS',1,'2025-06-23 02:45:30',4500.00,3375.00,25.00,'REFUNDED','18901952422','2025-06-22 18:45:30'),(32,1,6,'ECONOMY',1,'2025-06-23 02:59:11',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 18:59:11'),(33,1,6,'ECONOMY',1,'2025-06-23 07:25:09',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 23:25:09'),(34,1,6,'ECONOMY',1,'2025-06-23 07:26:56',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 23:26:56'),(35,1,6,'ECONOMY',1,'2025-06-23 07:39:49',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 23:39:49'),(36,1,6,'ECONOMY',1,'2025-06-23 07:43:26',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 23:43:26'),(37,1,6,'ECONOMY',1,'2025-06-23 07:46:02',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 23:46:02'),(38,1,6,'ECONOMY',1,'2025-06-23 07:48:25',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-22 23:48:25'),(39,1,6,'ECONOMY',1,'2025-06-23 08:08:16',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-23 00:08:16'),(40,1,6,'ECONOMY',1,'2025-06-23 08:12:22',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-23 00:12:22'),(41,1,6,'ECONOMY',1,'2025-06-23 08:14:49',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-23 00:14:49'),(42,1,6,'ECONOMY',1,'2025-06-23 08:44:21',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-23 00:44:21'),(43,1,6,'ECONOMY',1,'2025-06-23 09:18:13',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-23 01:18:13'),(44,1,6,'ECONOMY',1,'2025-06-23 09:19:02',1800.00,1620.00,10.00,'REFUNDED','18901952422','2025-06-23 01:19:02'),(45,1,6,'ECONOMY',1,'2025-06-23 12:30:19',1800.00,1620.00,10.00,'REFUNDED','18901952423','2025-06-23 04:30:19'),(46,1,6,'ECONOMY',1,'2025-06-23 13:07:17',1800.00,1620.00,10.00,'REFUNDED','18901952423','2025-06-23 05:07:17'),(47,1,6,'ECONOMY',1,'2025-06-23 13:16:57',1800.00,1620.00,10.00,'REFUNDED','18901952423','2025-06-23 05:16:57'),(48,1,6,'ECONOMY',1,'2025-06-23 13:45:13',1800.00,1620.00,10.00,'REFUNDED','18901952423','2025-06-23 05:45:13'),(49,1,6,'ECONOMY',1,'2025-06-23 13:45:51',1800.00,1620.00,10.00,'REFUNDED','18901952423','2025-06-23 05:45:51'),(50,1,6,'ECONOMY',1,'2025-06-23 13:54:18',1800.00,1620.00,10.00,'REFUNDED','18901952423','2025-06-23 05:54:18'),(51,1,6,'ECONOMY',1,'2025-06-24 01:02:06',1800.00,1620.00,10.00,'REFUNDED','18901952426','2025-06-23 17:02:06'),(52,1,6,'ECONOMY',1,'2025-06-24 02:08:41',1800.00,1620.00,10.00,'REFUNDED','18901952426','2025-06-23 18:08:41'),(53,1,6,'BUSINESS',9,'2025-06-24 02:14:05',4500.00,3375.00,25.00,'CANCELLED','13600136001','2025-06-23 18:14:05'),(54,1,6,'ECONOMY',9,'2025-06-24 02:15:01',1800.00,1620.00,10.00,'CANCELLED','13600136001','2025-06-23 18:15:01'),(55,1,6,'ECONOMY',9,'2025-06-24 02:16:38',1800.00,1620.00,10.00,'PAID','13600136001','2025-06-23 18:16:38'),(56,1,6,'ECONOMY',8,'2025-06-24 02:18:28',1800.00,1620.00,10.00,'PAID','18901952451','2025-06-23 18:18:28'),(57,1,6,'ECONOMY',1,'2025-06-24 02:19:11',1800.00,1620.00,10.00,'PAID','18901952456','2025-06-23 18:19:11'),(58,1,8,'ECONOMY',1,'2025-06-24 10:49:04',1400.00,1260.00,10.00,'REFUNDED','18901952456','2025-06-24 02:49:04'),(59,1,8,'ECONOMY',1,'2025-06-24 11:06:41',1400.00,1260.00,10.00,'REFUNDED','18901952456','2025-06-24 03:06:41'),(60,1,8,'ECONOMY',1,'2025-06-24 11:07:49',1400.00,1260.00,10.00,'REFUNDED','18901952456','2025-06-24 03:07:49'),(61,1,8,'ECONOMY',1,'2025-06-24 11:19:30',1400.00,1260.00,10.00,'REFUNDED','18901952457','2025-06-24 03:19:30'),(62,1,8,'ECONOMY',1,'2025-06-24 11:21:07',1400.00,1260.00,10.00,'REFUNDED','18901952457','2025-06-24 03:21:07'),(63,1,8,'ECONOMY',1,'2025-06-24 11:31:52',1400.00,1260.00,10.00,'REFUNDED','18901952459','2025-06-24 03:31:52'),(64,1,8,'ECONOMY',1,'2025-06-24 11:56:53',1400.00,1260.00,10.00,'REFUNDED','18901952459','2025-06-24 03:56:53'),(65,1,8,'BUSINESS',10,'2025-06-24 12:00:08',4000.00,3000.00,25.00,'PAID','15666988874','2025-06-24 04:00:08'),(66,1,9,'BUSINESS',1,'2025-06-24 13:31:59',6000.00,4500.00,25.00,'REFUNDED','18901952458','2025-06-24 05:31:59'),(67,11,9,'BUSINESS',11,'2025-06-24 13:55:09',6000.00,4500.00,25.00,'REFUNDED','18901665455','2025-06-24 05:55:09'),(68,1,9,'ECONOMY',1,'2025-06-25 01:01:38',1500.00,1350.00,10.00,'REFUNDED','18901952458','2025-06-24 17:01:38'),(69,1,9,'ECONOMY',1,'2025-06-25 01:31:14',1500.00,1350.00,10.00,'REFUNDED','18901952458','2025-06-24 17:31:14'),(70,1,9,'ECONOMY',12,'2025-06-25 01:34:20',1500.00,1350.00,10.00,'PAID','13366669999','2025-06-24 17:34:20'),(71,1,9,'ECONOMY',1,'2025-06-25 02:48:03',1500.00,1350.00,10.00,'REFUNDED','18901952457','2025-06-24 18:48:03'),(72,1,9,'ECONOMY',4,'2025-06-25 02:48:58',1500.00,1350.00,10.00,'PAID','13600136000','2025-06-24 18:48:58'),(73,1,9,'ECONOMY',1,'2025-06-25 10:06:07',1500.00,1350.00,10.00,'PAID','18901952457','2025-06-25 02:06:07'),(74,1,10,'BUSINESS',1,'2025-11-25 10:56:46',6000.00,4500.00,25.00,'REFUNDED','18901952457','2025-11-25 02:56:46'),(88,1,35,'ECONOMY',15,'2025-12-28 16:46:58',1000.00,900.00,10.00,'PAID','18900990098','2025-12-28 08:46:58'),(89,1,35,'ECONOMY',1,'2025-12-28 16:46:58',1000.00,900.00,10.00,'PAID','18901952450','2025-12-28 08:46:58'),(90,1,35,'ECONOMY',2,'2025-12-28 16:46:58',1000.00,900.00,10.00,'PENDING_PAYMENT','13900139000','2025-12-28 08:46:58'),(91,1,93,'BUSINESS',1,'2025-12-29 12:47:33',2000.00,1500.00,25.00,'PAID','18901952450','2025-12-29 04:47:33'),(92,1,93,'BUSINESS',2,'2025-12-29 12:47:34',2000.00,1500.00,25.00,'PAID','13900139000','2025-12-29 04:47:34'),(93,1,93,'BUSINESS',15,'2025-12-29 12:47:34',2000.00,1500.00,25.00,'PAID','18900990098','2025-12-29 04:47:34'),(94,1,70,'ECONOMY',1,'2025-12-29 13:21:41',1000.00,900.00,10.00,'PAID','18901952450','2025-12-29 05:21:41'),(95,1,70,'ECONOMY',15,'2025-12-29 13:21:41',1000.00,900.00,10.00,'PAID','18900990098','2025-12-29 05:21:41'),(96,1,70,'ECONOMY',2,'2025-12-29 13:21:41',1000.00,900.00,10.00,'PAID','13900139000','2025-12-29 05:21:41'),(97,1,52,'ECONOMY',1,'2025-12-29 13:34:42',1000.00,900.00,10.00,'PAID','18901952450','2025-12-29 05:34:42'),(98,1,54,'BUSINESS',2,'2025-12-29 13:41:48',2000.00,1500.00,25.00,'PAID','13900139000','2025-12-29 05:41:48'),(99,1,53,'ECONOMY',15,'2025-12-30 10:12:12',1000.00,900.00,10.00,'PAID','18900990098','2025-12-30 02:12:12'),(100,1,65,'BUSINESS',1,'2026-01-09 19:03:43',2000.00,2000.00,0.00,'PENDING_PAYMENT','18901952450','2026-01-09 11:03:43'),(101,1,65,'BUSINESS',2,'2026-01-09 19:03:43',2000.00,2000.00,0.00,'PENDING_PAYMENT','13900139000','2026-01-09 11:03:43'),(102,1,70,'BUSINESS',1,'2026-01-09 19:27:57',2000.00,2000.00,0.00,'PAID','18901952450','2026-01-09 11:27:57'),(103,1,70,'BUSINESS',15,'2026-01-09 19:27:57',2000.00,2000.00,0.00,'PAID','18900990098','2026-01-09 11:27:57'),(104,1,70,'BUSINESS',2,'2026-01-09 19:27:57',2000.00,2000.00,0.00,'PENDING_PAYMENT','13900139000','2026-01-09 11:27:57'),(105,1,70,'ECONOMY',1,'2026-01-09 19:41:14',1000.00,1000.00,0.00,'PAID','18901952450','2026-01-09 11:41:14'),(106,1,70,'ECONOMY',15,'2026-01-09 19:41:14',1000.00,1000.00,0.00,'PAID','18900990098','2026-01-09 11:41:14'),(107,1,70,'ECONOMY',2,'2026-01-09 19:41:14',1000.00,1000.00,0.00,'CANCELLED','13900139000','2026-01-09 11:41:14'),(108,1,71,'ECONOMY',1,'2026-01-09 19:49:11',1000.00,1000.00,0.00,'PAID','18901952450','2026-01-09 11:49:11'),(109,1,71,'ECONOMY',15,'2026-01-09 19:49:12',1000.00,1000.00,0.00,'PAID','18900990098','2026-01-09 11:49:12'),(110,1,71,'ECONOMY',2,'2026-01-09 19:49:12',1000.00,1000.00,0.00,'CANCELLED','13900139000','2026-01-09 11:49:12'),(111,1,70,'BUSINESS',1,'2026-01-10 18:36:26',2000.00,2000.00,0.00,'PAID','18901952450','2026-01-10 10:36:26'),(112,1,71,'ECONOMY',1,'2026-01-11 05:32:31',1000.00,1000.00,0.00,'PAID','18901952450','2026-01-10 21:32:31'),(113,1,74,'ECONOMY',1,'2026-01-11 08:39:21',1000.00,1000.00,0.00,'PAID','18901952450','2026-01-11 00:39:21'),(114,1,84,'ECONOMY',1,'2026-01-11 09:21:25',1000.00,1000.00,0.00,'PAID','18901952450','2026-01-11 01:21:25'),(115,1,84,'ECONOMY',15,'2026-01-11 09:21:25',1000.00,1000.00,0.00,'PENDING_PAYMENT','18900990098','2026-01-11 01:21:25'),(116,1,84,'ECONOMY',2,'2026-01-11 09:21:25',1000.00,1000.00,0.00,'PENDING_PAYMENT','13900139000','2026-01-11 01:21:25'),(117,1,84,'ECONOMY',11,'2026-01-11 09:21:25',1000.00,1000.00,0.00,'PENDING_PAYMENT','18901665455','2026-01-11 01:21:25');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `fill_contact_phone` BEFORE INSERT ON `orders` FOR EACH ROW BEGIN
    DECLARE contact_phone_val VARCHAR(20);
    
    -- 如果contact_phone为空或空字符串，从乘客表获取电话
    IF NEW.contact_phone IS NULL OR NEW.contact_phone = '' THEN
        SELECT phone INTO
 contact_phone_val
        FROM
 passengers 
        WHERE passenger_id = NEW.
ticket_passenger_id
        LIMIT 1;
        
        SET NEW.contact_phone = contact_phone_val;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `validate_passengers_before_order` BEFORE INSERT ON `orders` FOR EACH ROW BEGIN
    DECLARE passenger_count INT;
    DECLARE ticket_passenger_count INT;
    
    -- 验证下单乘客是否存在
    SELECT COUNT(*) INTO
 passenger_count
    FROM
 passengers
    WHERE passenger_id = NEW.passenger_id;
    
    IF passenger_count = 0 THEN
        SIGNAL SQLSTATE 
'45000'
        SET MESSAGE_TEXT = '下单乘客ID不存在，请重新输入';
    END IF;
    
    -- 验证乘机人是否存在
    SELECT COUNT(*) INTO
 ticket_passenger_count
    FROM
 passengers
    WHERE passenger_id = NEW.ticket_passenger_id;
    
    IF ticket_passenger_count = 0 THEN
        SIGNAL SQLSTATE 
'45000'
        SET MESSAGE_TEXT = '乘机人ID不存在，请重新输入';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `before_order_insert` BEFORE INSERT ON `orders` FOR EACH ROW BEGIN
    -- 声明所有需要的变量
    DECLARE v_base_price DECIMAL(10, 2);
    DECLARE v_flight_departure_time DATETIME;
    DECLARE v_days_in_advance INT;
    DECLARE v_passenger_membership_level ENUM('REGULAR', 'SILVER', 'GOLD');
    DECLARE v_flight_total_seats INT;
    DECLARE v_flight_available_seats INT;
    DECLARE v_occupancy_rate DECIMAL(5, 2);
    DECLARE v_max_discount_rate DECIMAL(5, 2) DEFAULT 0.00;
    DECLARE v_strategy_discount_rate DECIMAL(5, 2);
    DECLARE v_calculated_actual_price DECIMAL(10, 2);
    DECLARE v_min_price DECIMAL(10, 2);

    -- 1. 获取基础价格和航班信息
    IF NEW.flight_id IS NOT NULL AND NEW.seat_class IS NOT NULL THEN
        SELECT 
            CASE NEW.seat_class
                WHEN 'ECONOMY' THEN f.economy_seat_price
                WHEN 'BUSINESS' THEN f.business_seat_price
                ELSE 0.00
            END,
            f.departure_time,
            f.total_seats
        INTO v_base_price, v_flight_departure_time, v_flight_total_seats
        FROM flights f
        WHERE f.flight_id = NEW.flight_id;
        
        -- 如果航班不存在，则直接退出
        IF v_base_price IS NULL THEN
            SET v_base_price = 0.00;
        END IF;

    ELSE
        SET v_base_price = 0.00;
    END IF;
    
    -- 将基础价格设置到新行
    SET NEW.base_price = v_base_price;

    -- 如果基础价格为0，则无需计算折扣
    IF v_base_price > 0.00 THEN

        -- 2. 获取计算折扣所需的其他信息
        -- a. 计算提前天数
        SET v_days_in_advance = DATEDIFF(v_flight_departure_time, NOW());

        -- b. 获取乘客会员等级
        SELECT membership_level INTO v_passenger_membership_level
        FROM passengers p WHERE p.passenger_id = NEW.passenger_id;

        -- c. 计算上座率
        SELECT (fsa.available_business_seats + fsa.available_economy_seats)
        INTO v_flight_available_seats
        FROM flight_seat_availability fsa
        WHERE fsa.flight_id = NEW.flight_id;

        IF v_flight_total_seats > 0 AND v_flight_available_seats IS NOT NULL THEN
            SET v_occupancy_rate = (1 - (v_flight_available_seats / v_flight_total_seats)) * 100;
        ELSE
            SET v_occupancy_rate = 0.00;
        END IF;

        -- 3. 遍历所有有效策略，查找最大折扣率
        -- 假设：pricing_strategies 表中的 base_discount 是折扣的百分比，例如15.00代表15%
        -- (这是一个简化的游标示例，对于少量策略，直接用多个SELECT INTO也可以)
        
        -- a. 检查早鸟优惠 (strategy_id = 1)
        SELECT base_discount INTO v_strategy_discount_rate FROM pricing_strategies 
        WHERE strategy_id = 1 AND NOW() BETWEEN start_date AND end_date AND v_days_in_advance >= advance_days;
        IF v_strategy_discount_rate IS NOT NULL AND v_strategy_discount_rate > v_max_discount_rate THEN
            SET v_max_discount_rate = v_strategy_discount_rate;
        END IF;

        -- b. 检查会员折扣 (strategy_id = 4)
        IF v_passenger_membership_level IN ('GOLD', 'SILVER') THEN
            SELECT base_discount INTO v_strategy_discount_rate FROM pricing_strategies 
            WHERE strategy_id = 4 AND NOW() BETWEEN start_date AND end_date;
            IF v_strategy_discount_rate IS NOT NULL AND v_strategy_discount_rate > v_max_discount_rate THEN
                SET v_max_discount_rate = v_strategy_discount_rate;
            END IF;
        END IF;
        
        -- 你可以根据需要添加对其他策略（如淡季、商务舱）的检查...
        -- 例如：检查商务舱特惠
        IF NEW.seat_class = 'BUSINESS' THEN
            SELECT base_discount INTO v_strategy_discount_rate FROM pricing_strategies
            WHERE strategy_id = 3 AND NOW() BETWEEN start_date AND end_date;
             IF v_strategy_discount_rate IS NOT NULL AND v_strategy_discount_rate > v_max_discount_rate THEN
                SET v_max_discount_rate = v_strategy_discount_rate;
            END IF;
        END IF;


        -- 4. 计算最终价格并应用7折限制
        SET v_calculated_actual_price = v_base_price * (1 - v_max_discount_rate / 100.00);
        SET v_min_price = v_base_price * 0.70; -- 最多打7折，即价格不低于70%

        IF v_calculated_actual_price < v_min_price THEN
            SET NEW.actual_price = v_min_price;
        ELSE
            SET NEW.actual_price = v_calculated_actual_price;
        END IF;
        
        -- 5. 计算并设置最终折扣率
        SET NEW.discount_rate = (v_base_price - NEW.actual_price) * 100 / v_base_price;

    ELSE
        -- 基础价格为0，则实际价格和折扣率也为0
        SET NEW.actual_price = 0.00;
        SET NEW.discount_rate = 0.00;
    END IF;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `handle_refund_logic` AFTER UPDATE ON `orders` FOR EACH ROW BEGIN
    DECLARE v_ticket_id INT;
    DECLARE v_flight_departure_time DATETIME;
    DECLARE v_hours_diff INT;
    DECLARE v_refund_amount DECIMAL(10, 2);
    DECLARE v_refund_rate DECIMAL(5, 2);
    DECLARE v_ticket_passenger_id INT;
    
    -- 仅当订单状态从一个“已支付”相关的状态变为“已退款”时才执行
    IF (OLD.status IN ('PAID', 'TICKETED', 'COMPLETED')) AND (NEW.status = 'REFUNDED') THEN
        
        -- 1. 查找此订单对应的机票ID和乘机人ID
        SELECT ticket_id, passenger_id INTO v_ticket_id, v_ticket_passenger_id
        FROM tickets WHERE order_id = OLD.order_id LIMIT 1;

        -- 如果找到了机票
        IF v_ticket_id IS NOT NULL THEN
            -- 2. 获取航班起飞时间
            SELECT departure_time INTO v_flight_departure_time
            FROM flights WHERE flight_id = OLD.flight_id;
            
            -- 3. 计算距离起飞的小时数
            SET v_hours_diff = TIMESTAMPDIFF(HOUR, NOW(), v_flight_departure_time);

            -- 4. 根据退票政策计算退款比例
            IF v_hours_diff > 48 THEN
                SET v_refund_rate = 1.00; -- 提前48小时以上，全额退
            ELSEIF v_hours_diff > 24 THEN
                SET v_refund_rate = 0.80; -- 24-48小时，退80% (收20%手续费)
            ELSEIF v_hours_diff > 2 THEN
                SET v_refund_rate = 0.50; -- 2-24小时，退50%
            ELSE
                SET v_refund_rate = 0.00; -- 2小时内，不予退款
            END IF;
            
            -- 5. 计算退款金额
            SET v_refund_amount = OLD.actual_price * v_refund_rate;
            
            -- 6. 在 refunds 表中插入退款记录
            INSERT INTO refunds (order_id, ticket_id, refund_amount, refund_time, reason, status, created_at)
            VALUES (OLD.order_id, v_ticket_id, v_refund_amount, NOW(), '用户申请退票', 'COMPLETED', NOW());
            
            -- 7. 更新机票状态为已退票
            UPDATE tickets SET status = 'REFUNDED' WHERE ticket_id = v_ticket_id;
            
            -- 8. 返还座位库存
            IF OLD.seat_class = 'BUSINESS' THEN
                UPDATE flight_seat_availability 
                SET available_business_seats = available_business_seats + 1 
                WHERE flight_id = OLD.flight_id;
            ELSE
                UPDATE flight_seat_availability 
                SET available_economy_seats = available_economy_seats + 1 
                WHERE flight_id = OLD.flight_id;
            END IF;
            
            -- 9. 更新乘客累计消费额 (减去原始订单的实际支付金额)
            UPDATE passengers
            SET total_spent = GREATEST(0, total_spent - OLD.actual_price)
            WHERE passenger_id = v_ticket_passenger_id;

        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `update_passenger_spent_on_payment` AFTER UPDATE ON `orders` FOR EACH ROW BEGIN
    -- 1. 检查订单状态是否从“待支付”变为“已支付”
    --    这是最常见的路径，也是我们认定消费发生的时刻。
    --    OLD.status 代表更新前的状态，NEW.status 代表更新后的状态。
    IF OLD.status = 'PENDING_PAYMENT' AND NEW.status = 'PAID' THEN
        
        -- 2. 更新对应乘机人(ticket_passenger_id)的累计消费额度
        --    我们将订单的实际支付金额(actual_price)加到乘客的 total_spent 字段上。
        UPDATE passengers
        SET 
            total_spent = total_spent + NEW.actual_price
        WHERE 
            passenger_id = NEW.ticket_passenger_id;
            
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `passengers`
--

DROP TABLE IF EXISTS `passengers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `passengers` (
  `passenger_id` int NOT NULL AUTO_INCREMENT,
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `name` varchar(50) NOT NULL,
  `gender` enum('MALE','FEMALE','OTHER') NOT NULL DEFAULT 'OTHER',
  `birth_date` date NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `membership_level` enum('REGULAR','SILVER','GOLD') DEFAULT 'REGULAR',
  `education` enum('PRIMARY','JUNIOR','HIGH','COLLEGE','BACHELOR','MASTER','PHD','OTHER') DEFAULT NULL,
  `occupation` varchar(50) DEFAULT NULL,
  `total_spent` decimal(10,2) DEFAULT '0.00' COMMENT '累计消费额度',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`passenger_id`),
  UNIQUE KEY `id_card` (`id_card`),
  KEY `idx_passengers_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `passengers`
--

/*!40000 ALTER TABLE `passengers` DISABLE KEYS */;
INSERT INTO `passengers` VALUES (1,'310117200506010233','宗宸','MALE','2005-06-01','18901952450','1205618350@qq.com','GOLD','MASTER','学生',137271.00,'2025-05-28 16:08:26'),(2,'310117200505270889','宸宝','FEMALE','2005-05-27','13900139000','8965412566@qq.com','SILVER','MASTER','教师',60798.00,'2025-05-28 16:08:26'),(3,'310114200503020511','李宸','MALE','2005-03-02','13700137000','8909990876@qq.com','REGULAR','HIGH','自由职业',2495.00,'2025-05-28 16:08:26'),(4,'310112200602130633','刘宸','FEMALE','2006-02-12','13600136000','7564223145@qq.com','REGULAR','BACHELOR','设计师',4965.00,'2025-05-28 16:08:26'),(5,'310117200401230911','宸宸','MALE','2004-01-23','13500135000','3216665458@qq.com','GOLD','PHD','研究员',253310.00,'2025-05-28 16:08:26'),(6,'310112200602130634','宗宸','OTHER','1990-01-01','13600136000','7564223145@qq.com','REGULAR',NULL,NULL,0.00,'2025-06-22 00:52:18'),(7,'310107200506010231','刘栩年','MALE','2005-06-01','18901952452','1205618353@qq.com','REGULAR','PRIMARY','女优',0.00,'2025-06-23 01:37:21'),(8,'310117200506010231','刘栩年','MALE','2005-06-01','18901952451','1205618351@qq.com','REGULAR','PRIMARY','日本女优',1620.00,'2025-06-23 01:49:40'),(9,'310000199006030566','乐天','FEMALE','1990-06-03','13600136001','1205618354@qq.com','REGULAR','MASTER','大学生',1620.00,'2025-06-23 18:13:08'),(10,'310006200406010233','许青峰','FEMALE','2004-06-01','15666988874','','REGULAR','BACHELOR','在校学生',3000.00,'2025-06-24 03:59:03'),(11,'310117200506010699','胡定军','MALE','2005-06-01','18901665455','','REGULAR','MASTER','小丑',0.00,'2025-06-24 05:53:19'),(12,'310115200602210651','张时嘉','MALE','2006-02-21','13366669999','','REGULAR','PHD','日本女优',1350.00,'2025-06-24 17:33:19'),(13,'310112200602210655','宸宸','MALE','2006-02-21','16666778890','','REGULAR','PHD','学生',0.00,'2025-06-24 18:53:07'),(14,'310117190001010355','哈基刘','OTHER','1900-01-01','18912345678','','REGULAR','PRIMARY','日本女优',0.00,'2025-11-25 03:03:52'),(15,'310001198005270001','牢天','FEMALE','1980-05-27','18900990098',NULL,'REGULAR',NULL,NULL,8200.00,'2025-12-28 08:42:00'),(16,'310987654321145677','哈基军','OTHER','2000-01-01','18901952499',NULL,'REGULAR',NULL,NULL,0.00,'2026-01-10 10:21:03');
/*!40000 ALTER TABLE `passengers` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `update_membership_level` BEFORE UPDATE ON `passengers` FOR EACH ROW BEGIN
    -- 仅当total_spent字段发生变化时执行
    IF NEW.total_spent <> OLD.total_spent THEN
        -- 根据新的消费额度自动设置会员等级 (使用英文)
        SET NEW.membership_level = 
            CASE 
                WHEN NEW.total_spent >= 100000 THEN 'GOLD'
                WHEN NEW.total_spent >= 50000 THEN 'SILVER'
                ELSE 'REGULAR'
            END;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `pricing_strategies`
--

DROP TABLE IF EXISTS `pricing_strategies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pricing_strategies` (
  `strategy_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `base_discount` decimal(5,2) DEFAULT '0.00',
  `advance_days` int DEFAULT '0',
  `min_occupancy` decimal(5,2) DEFAULT '0.00',
  `max_occupancy` decimal(5,2) DEFAULT '100.00',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`strategy_id`),
  CONSTRAINT `pricing_strategies_chk_1` CHECK ((`start_date` <= `end_date`)),
  CONSTRAINT `pricing_strategies_chk_2` CHECK ((`advance_days` >= 0)),
  CONSTRAINT `pricing_strategies_chk_3` CHECK (((`min_occupancy` >= 0) and (`min_occupancy` <= 100))),
  CONSTRAINT `pricing_strategies_chk_4` CHECK (((`max_occupancy` >= 0) and (`max_occupancy` <= 100))),
  CONSTRAINT `pricing_strategies_chk_5` CHECK ((`min_occupancy` <= `max_occupancy`))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pricing_strategies`
--

/*!40000 ALTER TABLE `pricing_strategies` DISABLE KEYS */;
INSERT INTO `pricing_strategies` VALUES (1,'早鸟优惠','提前30天以上预订可享受折扣','2025-01-01','2025-12-31',15.00,30,0.00,100.00,'2025-05-28 16:08:47'),(2,'淡季促销','航班上座率低于50%时提供折扣','2025-01-01','2025-12-31',20.00,0,0.00,50.00,'2025-05-28 16:08:47'),(3,'商务舱特惠','商务舱上座率低于30%时提供折扣','2025-01-01','2025-12-31',25.00,0,0.00,30.00,'2025-05-28 16:08:47'),(4,'会员折扣','金卡会员专享折扣','2025-01-01','2025-12-31',10.00,0,0.00,100.00,'2025-05-28 16:08:47');
/*!40000 ALTER TABLE `pricing_strategies` ENABLE KEYS */;

--
-- Table structure for table `refunds`
--

DROP TABLE IF EXISTS `refunds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refunds` (
  `refund_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `reason` text,
  `refund_amount` decimal(10,2) NOT NULL,
  `refund_time` datetime(6) NOT NULL,
  `status` enum('APPROVED','COMPLETED','PENDING','REJECTED') DEFAULT NULL,
  `order_id` int NOT NULL,
  `ticket_id` int DEFAULT NULL,
  PRIMARY KEY (`refund_id`),
  UNIQUE KEY `UK8iu8b9p5iouvq9epk2ua5rimn` (`ticket_id`),
  KEY `FKsk9rqm7f6y8b1g0qob018hdm7` (`order_id`),
  CONSTRAINT `FKlcljjuvuakfkpg7i4knsj6se8` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  CONSTRAINT `FKsk9rqm7f6y8b1g0qob018hdm7` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refunds`
--

/*!40000 ALTER TABLE `refunds` DISABLE KEYS */;
INSERT INTO `refunds` VALUES (1,'2025-06-22 20:20:39.000000','用户申请退票',0.00,'2025-06-22 20:20:39.000000','COMPLETED',24,26),(2,'2025-06-22 20:22:23.000000','用户申请退票',0.00,'2025-06-22 20:22:23.000000','COMPLETED',25,27),(3,'2025-06-22 21:05:59.000000','用户申请退票',1296.00,'2025-06-22 21:05:59.000000','COMPLETED',27,29),(4,'2025-06-22 21:57:19.000000','用户申请退票',1296.00,'2025-06-22 21:57:19.000000','COMPLETED',28,30),(5,'2025-06-23 09:44:58.000000','用户申请退票',1296.00,'2025-06-23 09:44:58.000000','COMPLETED',29,31),(6,'2025-06-23 10:45:04.000000','用户申请退票',1296.00,'2025-06-23 10:45:04.000000','COMPLETED',30,32),(7,'2025-06-23 10:58:53.000000','用户申请退票',2700.00,'2025-06-23 10:58:53.000000','COMPLETED',31,35),(8,'2025-06-23 15:24:32.000000','用户申请退票',810.00,'2025-06-23 15:24:32.000000','COMPLETED',32,36),(9,'2025-06-23 15:25:53.000000','用户申请退票',810.00,'2025-06-23 15:25:53.000000','COMPLETED',33,37),(10,'2025-06-23 15:39:13.000000','用户申请退票',810.00,'2025-06-23 15:39:13.000000','COMPLETED',34,38),(11,'2025-06-23 15:42:42.000000','用户申请退票',810.00,'2025-06-23 15:42:42.000000','COMPLETED',35,39),(12,'2025-06-23 15:45:16.000000','用户申请退票',810.00,'2025-06-23 15:45:16.000000','COMPLETED',36,40),(13,'2025-06-23 15:47:19.000000','用户申请退票',810.00,'2025-06-23 15:47:19.000000','COMPLETED',37,41),(14,'2025-06-23 16:07:14.000000','用户申请退票',810.00,'2025-06-23 16:07:14.000000','COMPLETED',38,42),(15,'2025-06-23 16:11:32.000000','用户申请退票',810.00,'2025-06-23 16:11:32.000000','COMPLETED',39,43),(16,'2025-06-23 16:14:22.000000','用户申请退票',810.00,'2025-06-23 16:14:22.000000','COMPLETED',40,44),(17,'2025-06-23 16:15:10.000000','用户申请退票',810.00,'2025-06-23 16:15:10.000000','COMPLETED',41,45),(18,'2025-06-23 16:44:41.000000','用户申请退票',810.00,'2025-06-23 16:44:41.000000','COMPLETED',42,46),(19,'2025-06-23 17:18:39.000000','用户申请退票',810.00,'2025-06-23 17:18:39.000000','COMPLETED',43,47),(20,'2025-06-23 20:29:53.000000','用户申请退票',810.00,'2025-06-23 20:29:53.000000','COMPLETED',44,48),(21,'2025-06-23 21:07:01.000000','用户申请退票',810.00,'2025-06-23 21:07:01.000000','COMPLETED',45,49),(22,'2025-06-23 21:16:39.000000','用户申请退票',810.00,'2025-06-23 21:16:39.000000','COMPLETED',46,50),(23,'2025-06-23 21:45:00.000000','用户申请退票',810.00,'2025-06-23 21:45:00.000000','COMPLETED',47,51),(24,'2025-06-23 21:45:37.000000','用户申请退票',810.00,'2025-06-23 21:45:37.000000','COMPLETED',48,52),(25,'2025-06-23 21:54:05.000000','用户申请退票',810.00,'2025-06-23 21:54:05.000000','COMPLETED',49,53),(26,'2025-06-24 09:01:51.000000','用户申请退票',0.00,'2025-06-24 09:01:51.000000','COMPLETED',50,54),(27,'2025-06-24 10:08:29.000000','用户申请退票',0.00,'2025-06-24 10:08:29.000000','COMPLETED',51,55),(28,'2025-06-24 10:18:50.000000','用户申请退票',0.00,'2025-06-24 10:18:50.000000','COMPLETED',52,56),(29,'2025-06-24 19:06:00.000000','用户申请退票',1260.00,'2025-06-24 19:06:00.000000','COMPLETED',58,60),(30,'2025-06-24 19:07:24.000000','用户申请退票',1260.00,'2025-06-24 19:07:24.000000','COMPLETED',59,61),(31,'2025-06-24 19:19:15.000000','用户申请退票',1260.00,'2025-06-24 19:19:15.000000','COMPLETED',60,62),(32,'2025-06-24 19:20:53.000000','用户申请退票',1260.00,'2025-06-24 19:20:53.000000','COMPLETED',61,63),(33,'2025-06-24 19:31:36.000000','用户申请退票',1260.00,'2025-06-24 19:31:36.000000','COMPLETED',62,64),(34,'2025-06-24 19:56:35.000000','用户申请退票',1260.00,'2025-06-24 19:56:35.000000','COMPLETED',63,65),(35,'2025-06-24 21:31:16.000000','用户申请退票',1260.00,'2025-06-24 21:31:16.000000','COMPLETED',64,66),(36,'2025-06-24 21:56:03.000000','用户申请退票',4500.00,'2025-06-24 21:56:03.000000','COMPLETED',67,69),(37,'2025-06-25 09:01:20.000000','用户申请退票',4500.00,'2025-06-25 09:01:20.000000','COMPLETED',66,68),(38,'2025-06-25 09:30:49.000000','用户申请退票',1350.00,'2025-06-25 09:30:49.000000','COMPLETED',68,70),(39,'2025-06-25 09:36:07.000000','用户申请退票',1350.00,'2025-06-25 09:36:07.000000','COMPLETED',69,71),(40,'2025-06-25 10:49:58.000000','用户申请退票',1350.00,'2025-06-25 10:49:58.000000','COMPLETED',71,73),(41,'2025-11-25 18:59:36.000000','用户申请退票',4500.00,'2025-11-25 18:59:36.000000','COMPLETED',74,75);
/*!40000 ALTER TABLE `refunds` ENABLE KEYS */;

--
-- Table structure for table `system_users`
--

DROP TABLE IF EXISTS `system_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `role` enum('ADMIN','PASSENGER') NOT NULL COMMENT '用户角色：管理员或乘客',
  `last_login` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `passenger_id` int DEFAULT NULL COMMENT '关联的乘客ID，仅对PASSENGER角色有效',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_systemuser_passenger` (`passenger_id`),
  CONSTRAINT `fk_systemuser_passenger` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_users`
--

/*!40000 ALTER TABLE `system_users` DISABLE KEYS */;
INSERT INTO `system_users` VALUES (1,'admin','$2a$10$cRBoMFmHjAwo7gEIcjEndeDtGkw1ob/qh6cFGw7L9jLUbzu4peK7W','系统管理员','ADMIN','2026-01-11 01:18:52','2025-06-21 13:05:46',NULL),(2,'zongchen','$2a$10$TeF37xcYt20zLGOiQH1ts.L2gE.AHU5RBJF.DtDVuV8czVJ2mQ8ES','宗宸','PASSENGER','2026-01-11 01:20:42','2025-06-21 13:05:46',1),(3,'passenger2','$2a$10$A.7XJQRFsR1jKZdMUjpmwOA3IbcyadnL4UdG182tkxLBONoM2QX1W','宗宝','PASSENGER',NULL,'2025-06-21 13:05:46',2),(4,'liuxunian','$2a$10$PnM4GzrUy4RIvYPqlaHJwewytKw3R2QhSdch.rwr3ITBqnc.cvWXe','刘栩年','PASSENGER','2025-06-23 04:47:30','2025-06-23 04:47:20',7),(5,'passenger1','$2a$10$cKJNv7SeeHBR8uSd2.bgt..uX8bA6SLxMdEgrw2ow./OA2.LW3ofm','宗宸','PASSENGER','2025-06-24 17:40:15',NULL,1),(6,'passenger3','$2a$10$1tcScDvZnk5B2ZsHLzs1BedGYyC74Kbr48FDYnD7HjhS4K8cU4d5K','宸宸','PASSENGER','2025-06-23 16:59:17','2025-06-23 16:59:05',5),(7,'passenger4','$2a$10$aIa/ipfY24N7/fWETD2SYe7DEOnKYokEkJUGEzrxXvP1uYpB/U3LO','乐天','PASSENGER','2025-12-23 04:59:01','2025-06-23 18:29:37',9),(8,'xuqinfeng','$2a$10$gD7M47xow7kTQHpt9C8YK.zMuo5rsz.plDUNHQTaBF4gPk9WE2/0.','许青峰','PASSENGER','2025-06-24 04:03:35','2025-06-24 04:01:24',10),(9,'hudingjun','$2a$10$U0hMp5CTElu4zoPmHftuJuk5NbwhTsFQmDVPm.pLSSQfrRNoGE9cy','胡定军','PASSENGER','2025-06-24 05:56:11','2025-06-24 05:54:21',11),(10,'zsj123','$2a$10$E1OQRfYkCsttcSHku0kddudIZ7q2EJyZ0ozvmHhM37p8p3QYULXHy','张时嘉','PASSENGER','2025-11-25 02:43:44','2025-11-25 02:43:34',12),(21,'letian123','$2a$10$2WAPd/xws779qB9LhVZmkOKILkZ81WZD7CWRlsMWnIHhUA6g6Sq0y','牢天','PASSENGER','2026-01-09 11:50:39','2025-12-28 09:05:04',15),(22,'hajijun','$2a$10$y8iPXn9R5QKAYuEQrpoNcu5PlMiB77TqIupUFP8fOFNU.hDg7VNs.','哈基军','PASSENGER',NULL,'2026-01-10 10:21:03',16);
/*!40000 ALTER TABLE `system_users` ENABLE KEYS */;

--
-- Temporary view structure for view `ticket_order_details`
--

DROP TABLE IF EXISTS `ticket_order_details`;
/*!50001 DROP VIEW IF EXISTS `ticket_order_details`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `ticket_order_details` AS SELECT 
 1 AS `ticket_id`,
 1 AS `order_id`,
 1 AS `order_time`,
 1 AS `order_status`,
 1 AS `flight_id`,
 1 AS `flight_number`,
 1 AS `departure_time`,
 1 AS `arrival_time`,
 1 AS `departure_airport`,
 1 AS `arrival_airport`,
 1 AS `passenger_id`,
 1 AS `passenger_name`,
 1 AS `id_card`,
 1 AS `seat_class`,
 1 AS `price`,
 1 AS `ticket_status`,
 1 AS `base_price`,
 1 AS `actual_price`,
 1 AS `discount_rate`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `ticket_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `flight_id` int NOT NULL,
  `passenger_id` int NOT NULL COMMENT '乘机人ID',
  `seat_class` enum('ECONOMY','BUSINESS') NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` enum('UNUSED','USED','REFUNDED') DEFAULT 'UNUSED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ticket_id`),
  KEY `order_id` (`order_id`),
  KEY `idx_tickets_flight` (`flight_id`),
  KEY `idx_tickets_passenger` (`passenger_id`),
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`),
  CONSTRAINT `tickets_ibfk_3` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,1,1,1,'BUSINESS',1200.00,'UNUSED','2025-05-28 16:08:40'),(2,1,1,2,'ECONOMY',1000.00,'UNUSED','2025-05-28 16:08:40'),(3,2,2,2,'BUSINESS',1500.00,'UNUSED','2025-05-28 16:08:40'),(4,2,2,3,'ECONOMY',1000.00,'UNUSED','2025-05-28 16:08:40'),(5,3,3,4,'BUSINESS',1800.00,'UNUSED','2025-05-28 16:08:40'),(6,3,3,5,'BUSINESS',1800.00,'UNUSED','2025-05-28 16:08:40'),(17,6,2,1,'ECONOMY',640.00,'UNUSED','2025-06-01 07:49:21'),(18,4,1,3,'ECONOMY',700.00,'UNUSED','2025-06-01 07:50:24'),(19,2,2,2,'BUSINESS',1800.00,'UNUSED','2025-06-01 07:52:55'),(20,10,1,2,'ECONOMY',1000.00,'UNUSED','2025-06-21 18:12:48'),(21,10,1,2,'ECONOMY',1000.00,'UNUSED','2025-06-22 02:12:48'),(22,1,1,1,'BUSINESS',2000.00,'UNUSED','2025-06-21 18:13:01'),(23,1,1,1,'BUSINESS',2000.00,'UNUSED','2025-06-22 02:13:00'),(24,18,1,1,'ECONOMY',1000.00,'UNUSED','2025-06-22 00:34:30'),(25,18,1,1,'ECONOMY',1000.00,'UNUSED','2025-06-22 08:34:29'),(26,24,1,1,'ECONOMY',900.00,'REFUNDED','2025-06-22 04:20:25'),(27,25,1,1,'ECONOMY',900.00,'REFUNDED','2025-06-22 04:21:57'),(28,26,1,1,'ECONOMY',900.00,'UNUSED','2025-06-22 04:52:46'),(29,27,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 05:01:24'),(30,28,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 05:57:08'),(31,29,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 17:44:19'),(32,30,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 18:23:20'),(33,21,1,4,'ECONOMY',1000.00,'UNUSED','2025-06-22 18:44:06'),(34,22,3,1,'ECONOMY',1080.00,'UNUSED','2025-06-22 18:44:35'),(35,31,6,1,'BUSINESS',3375.00,'REFUNDED','2025-06-22 18:45:40'),(36,32,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 18:59:17'),(37,33,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 23:25:18'),(38,34,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 23:27:00'),(39,35,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 23:39:53'),(40,36,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 23:43:43'),(41,37,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 23:46:14'),(42,38,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-22 23:48:35'),(43,39,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 00:08:23'),(44,40,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 00:12:30'),(45,41,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 00:14:54'),(46,42,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 00:44:26'),(47,43,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 01:18:30'),(48,44,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 01:19:42'),(49,45,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 05:06:35'),(50,46,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 05:07:21'),(51,47,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 05:17:03'),(52,48,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 05:45:17'),(53,49,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 05:46:19'),(54,50,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 05:54:24'),(55,51,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 17:02:17'),(56,52,6,1,'ECONOMY',1620.00,'REFUNDED','2025-06-23 18:08:51'),(57,55,6,9,'ECONOMY',1620.00,'UNUSED','2025-06-23 18:16:44'),(58,56,6,8,'ECONOMY',1620.00,'UNUSED','2025-06-23 18:18:34'),(59,57,6,1,'ECONOMY',1620.00,'UNUSED','2025-06-23 18:19:14'),(60,58,8,1,'ECONOMY',1260.00,'REFUNDED','2025-06-24 02:49:20'),(61,59,8,1,'ECONOMY',1260.00,'REFUNDED','2025-06-24 03:06:49'),(62,60,8,1,'ECONOMY',1260.00,'REFUNDED','2025-06-24 03:07:55'),(63,61,8,1,'ECONOMY',1260.00,'REFUNDED','2025-06-24 03:19:36'),(64,62,8,1,'ECONOMY',1260.00,'REFUNDED','2025-06-24 03:21:26'),(65,63,8,1,'ECONOMY',1260.00,'REFUNDED','2025-06-24 03:32:08'),(66,64,8,1,'ECONOMY',1260.00,'REFUNDED','2025-06-24 03:56:59'),(67,65,8,10,'BUSINESS',3000.00,'UNUSED','2025-06-24 04:00:19'),(68,66,9,1,'BUSINESS',4500.00,'REFUNDED','2025-06-24 05:32:16'),(69,67,9,11,'BUSINESS',4500.00,'REFUNDED','2025-06-24 05:55:45'),(70,68,9,1,'ECONOMY',1350.00,'REFUNDED','2025-06-24 17:03:58'),(71,69,9,1,'ECONOMY',1350.00,'REFUNDED','2025-06-24 17:31:40'),(72,70,9,12,'ECONOMY',1350.00,'UNUSED','2025-06-24 17:36:25'),(73,71,9,1,'ECONOMY',1350.00,'REFUNDED','2025-06-24 18:49:21'),(74,73,9,1,'ECONOMY',1350.00,'UNUSED','2025-06-25 02:06:29'),(75,74,10,1,'BUSINESS',4500.00,'REFUNDED','2025-11-25 02:58:13'),(76,72,9,4,'ECONOMY',1350.00,'UNUSED','2025-11-25 02:58:53'),(86,88,35,15,'ECONOMY',900.00,'UNUSED','2025-12-28 08:47:20'),(87,93,93,15,'BUSINESS',1500.00,'UNUSED','2025-12-29 04:48:41'),(88,92,93,2,'BUSINESS',1500.00,'UNUSED','2025-12-29 05:00:38'),(89,91,93,1,'BUSINESS',1500.00,'UNUSED','2025-12-29 05:01:17'),(90,94,70,1,'ECONOMY',900.00,'UNUSED','2025-12-29 05:22:03'),(91,95,70,15,'ECONOMY',900.00,'UNUSED','2025-12-29 05:26:08'),(92,96,70,2,'ECONOMY',900.00,'UNUSED','2025-12-29 05:31:55'),(93,89,35,1,'ECONOMY',900.00,'UNUSED','2025-12-29 05:32:36'),(94,97,52,1,'ECONOMY',900.00,'UNUSED','2025-12-29 05:42:15'),(95,98,54,2,'BUSINESS',1500.00,'UNUSED','2025-12-30 02:13:11'),(96,99,53,15,'ECONOMY',900.00,'UNUSED','2026-01-09 10:55:45'),(97,102,70,1,'BUSINESS',2000.00,'UNUSED','2026-01-09 11:28:28'),(98,103,70,15,'BUSINESS',2000.00,'UNUSED','2026-01-09 11:28:53'),(99,105,70,1,'ECONOMY',1000.00,'UNUSED','2026-01-09 11:41:38'),(100,106,70,15,'ECONOMY',1000.00,'UNUSED','2026-01-09 11:41:50'),(101,109,71,15,'ECONOMY',1000.00,'UNUSED','2026-01-09 11:49:27'),(102,108,71,1,'ECONOMY',1000.00,'UNUSED','2026-01-09 11:49:48'),(103,111,70,1,'BUSINESS',2000.00,'UNUSED','2026-01-10 10:36:42'),(104,113,74,1,'ECONOMY',1000.00,'UNUSED','2026-01-11 00:48:32'),(105,112,71,1,'ECONOMY',1000.00,'UNUSED','2026-01-11 00:57:18'),(106,114,84,1,'ECONOMY',1000.00,'UNUSED','2026-01-11 01:21:41');
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;

--
-- Table structure for table `user_contacts`
--

DROP TABLE IF EXISTS `user_contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_contacts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '关联系统用户ID',
  `passenger_id` int NOT NULL COMMENT '关联乘客ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_passenger` (`user_id`,`passenger_id`),
  KEY `fk_contact_passenger` (`passenger_id`),
  CONSTRAINT `fk_contact_passenger` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_contact_user` FOREIGN KEY (`user_id`) REFERENCES `system_users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户常用乘机人关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_contacts`
--

/*!40000 ALTER TABLE `user_contacts` DISABLE KEYS */;
INSERT INTO `user_contacts` VALUES (4,2,2,'2026-01-10 18:37:17'),(5,2,15,'2026-01-10 18:37:17'),(6,2,11,'2026-01-10 18:37:17');
/*!40000 ALTER TABLE `user_contacts` ENABLE KEYS */;

--
-- Dumping routines for database 'flight'
--
/*!50003 DROP FUNCTION IF EXISTS `calculate_actual_price` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `calculate_actual_price`(
    base_price DECIMAL(10,2),
    seat_class VARCHAR(10),
    flight_id INT,
    passenger_id INT
) RETURNS decimal(10,2)
    READS SQL DATA
BEGIN
    DECLARE membership_level VARCHAR(10);
    DECLARE days_advance INT;
    DECLARE occupancy_rate DECIMAL(5, 2);
    DECLARE total_discount DECIMAL(5, 2) DEFAULT 0.0;
    DECLARE max_discount DECIMAL(5, 2) DEFAULT 30.0;
    DECLARE actual_price DECIMAL(10,2);
    DECLARE flight_departure_time DATETIME;
    DECLARE flight_total_seats INT;
    DECLARE ticket_count INT;
    
    -- 获取会员级别
    SELECT p.membership_level INTO membership_level
    FROM passengers p
    WHERE p.passenger_id = passenger_id;
    
    -- 获取航班信息
    SELECT 
        f.departure_time,
        f.total_seats,
        COUNT(t.ticket_id)
    INTO flight_departure_time, flight_total_seats, ticket_count
    FROM flights f
    LEFT JOIN tickets t ON t.flight_id = f.flight_id AND t.status != '已退票'
    WHERE f.flight_id = flight_id
    GROUP BY f.flight_id;
    
    -- 计算提前天数和客座率
    SET days_advance = DATEDIFF(flight_departure_time, NOW());
    SET occupancy_rate = (ticket_count * 100.0) / flight_total_seats;
    
    -- 应用定价策略
    SELECT COALESCE(SUM(ps.base_discount), 0)
    INTO total_discount
    FROM pricing_strategies ps
    WHERE NOW() BETWEEN ps.start_date AND ps.end_date
    AND (
        (ps.name = '早鸟优惠' AND days_advance >= ps.advance_days) OR
        (ps.name = '淡季促销' AND occupancy_rate BETWEEN ps.min_occupancy AND ps.max_occupancy) OR
        (ps.name = '商务舱特惠' AND seat_class = '商务舱' AND occupancy_rate BETWEEN ps.min_occupancy AND ps.max_occupancy) OR
        (ps.name = '会员折扣' AND membership_level = '金卡')
    );
    
    -- 限制最大折扣
    IF total_discount > max_discount THEN
        SET total_discount = max_discount;
    END IF;
    
    -- 计算实际价格
    SET actual_price = base_price * (1 - total_discount / 100);
    
    -- 确保最低票价不低于基础价格的70%
    IF actual_price < base_price * 0.7 THEN
        SET actual_price = base_price * 0.7;
    END IF;
    
    RETURN actual_price;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `flight_details`
--

/*!50001 DROP VIEW IF EXISTS `flight_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `flight_details` AS select `f`.`flight_id` AS `flight_id`,`f`.`flight_number` AS `flight_number`,`a`.`airline_code` AS `airline_code`,`a`.`airline_name` AS `airline_name`,`d`.`airport_name` AS `departure_airport_name`,`d`.`city` AS `departure_city`,`ar`.`airport_name` AS `arrival_airport_name`,`ar`.`city` AS `arrival_city`,`f`.`departure_time` AS `departure_time`,`f`.`arrival_time` AS `arrival_time`,timediff(`f`.`arrival_time`,`f`.`departure_time`) AS `duration`,`f`.`aircraft_model` AS `aircraft_model`,`f`.`total_seats` AS `total_seats`,`f`.`business_seats` AS `business_seats`,`f`.`economy_seats` AS `economy_seats`,`f`.`economy_seat_price` AS `economy_seat_price`,`f`.`business_seat_price` AS `business_seat_price`,`f`.`status` AS `status` from (((`flights` `f` join `airlines` `a` on((`f`.`airline_id` = `a`.`airline_id`))) join `airports` `d` on((`f`.`departure_airport` = `d`.`airport_code`))) join `airports` `ar` on((`f`.`arrival_airport` = `ar`.`airport_code`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `ticket_order_details`
--

/*!50001 DROP VIEW IF EXISTS `ticket_order_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ticket_order_details` AS select `t`.`ticket_id` AS `ticket_id`,`o`.`order_id` AS `order_id`,`o`.`order_time` AS `order_time`,`o`.`status` AS `order_status`,`t`.`flight_id` AS `flight_id`,`f`.`flight_number` AS `flight_number`,`f`.`departure_time` AS `departure_time`,`f`.`arrival_time` AS `arrival_time`,`d`.`airport_name` AS `departure_airport`,`ar`.`airport_name` AS `arrival_airport`,`p`.`passenger_id` AS `passenger_id`,`p`.`name` AS `passenger_name`,`p`.`id_card` AS `id_card`,`t`.`seat_class` AS `seat_class`,`t`.`price` AS `price`,`t`.`status` AS `ticket_status`,`o`.`base_price` AS `base_price`,`o`.`actual_price` AS `actual_price`,`o`.`discount_rate` AS `discount_rate` from (((((`tickets` `t` join `orders` `o` on((`t`.`order_id` = `o`.`order_id`))) join `flights` `f` on((`t`.`flight_id` = `f`.`flight_id`))) join `passengers` `p` on((`t`.`passenger_id` = `p`.`passenger_id`))) join `airports` `d` on((`f`.`departure_airport` = `d`.`airport_code`))) join `airports` `ar` on((`f`.`arrival_airport` = `ar`.`airport_code`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-03 22:07:16
