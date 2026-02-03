USE flight;
CREATE TABLE `airlines` (
    `airline_id` int NOT NULL AUTO_INCREMENT,
    `airline_code` varchar(2) NOT NULL COMMENT '航空公司代码(如CA, MU)',
    `airline_name` varchar(50) NOT NULL,
    `contact_phone` varchar(20) DEFAULT NULL,
    `website` varchar(100) DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`airline_id`),
    UNIQUE KEY `airline_code` (`airline_code`)
) ENGINE = InnoDB AUTO_INCREMENT = 10 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci






CREATE TABLE `airports` (
    `airport_code` varchar(3) NOT NULL COMMENT '机场三字码',
    `airport_name` varchar(100) NOT NULL,
    `city` varchar(50) NOT NULL,
    `country` varchar(50) NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`airport_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci







CREATE TABLE `announcement` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `content` tinytext NOT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `file_name` varchar(255) DEFAULT NULL,
    `file_path` varchar(255) DEFAULT NULL,
    `title` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci






CREATE TABLE `attachment` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `file_name` varchar(255) DEFAULT NULL,
    `file_path` text,
    `announcement_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK12w0loi0ekay1jfqjjsw3362s` (`announcement_id`),
    CONSTRAINT `FK12w0loi0ekay1jfqjjsw3362s` FOREIGN KEY (`announcement_id`) REFERENCES `announcement` (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 8 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci



CREATE TABLE `files` (
    `id` int NOT NULL AUTO_INCREMENT,
    `file_name` varchar(255) NOT NULL COMMENT '文件的原始名称',
    `file_url` varchar(512) NOT NULL COMMENT '文件在MinIO中的访问URL',
    `upload_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文件上传时间',
    `airline_id` int NOT NULL COMMENT '关联的航空公司ID',
    PRIMARY KEY (`id`),
    KEY `fk_files_airline_idx` (`airline_id`),
    CONSTRAINT `fk_files_airline` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`airline_id`) ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci





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
    `business_seat_price` decimal(10, 2) NOT NULL COMMENT '商务舱票价',
    `economy_seats` int NOT NULL COMMENT '经济舱座位数',
    `economy_seat_price` decimal(10, 2) NOT NULL COMMENT '经济舱票价',
    `active` bit(1) NOT NULL DEFAULT b'1' COMMENT '航线是否启用 (1=是, 0=否)',
    PRIMARY KEY (`route_id`),
    UNIQUE KEY `uk_airline_flight_number` (`airline_id`, `flight_number`),
    KEY `fk_route_to_departure_airport` (`departure_airport_code`),
    KEY `fk_route_to_arrival_airport` (`arrival_airport_code`),
    CONSTRAINT `fk_route_to_airline` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`airline_id`),
    CONSTRAINT `fk_route_to_arrival_airport` FOREIGN KEY (`arrival_airport_code`) REFERENCES `airports` (`airport_code`),
    CONSTRAINT `fk_route_to_departure_airport` FOREIGN KEY (`departure_airport_code`) REFERENCES `airports` (`airport_code`)
) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '航线模板表'





CREATE TABLE `flight_seat_availability` (
    `flight_id` int NOT NULL,
    `available_business_seats` int NOT NULL DEFAULT '0',
    `available_economy_seats` int NOT NULL DEFAULT '0',
    `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`flight_id`),
    CONSTRAINT `fk_seat_availability_flight` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci





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
    `business_seat_price` decimal(10, 2) NOT NULL,
    `economy_seats` int NOT NULL,
    `economy_seat_price` decimal(10, 2) NOT NULL,
    `status` enum(
        'PLANNED',
        'BOARDING',
        'IN_FLIGHT',
        'ARRIVED',
        'CANCELLED'
    ) DEFAULT 'PLANNED',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`flight_id`),
    KEY `airline_id` (`airline_id`),
    KEY `arrival_airport` (`arrival_airport`),
    KEY `idx_flights_departure` (
        `departure_airport`,
        `departure_time`
    ),
    KEY `fk_flights_flight_routes` (`route_id`),
    CONSTRAINT `fk_flights_flight_routes` FOREIGN KEY (`route_id`) REFERENCES `flight_routes` (`route_id`),
    CONSTRAINT `flights_ibfk_1` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`airline_id`),
    CONSTRAINT `flights_ibfk_2` FOREIGN KEY (`departure_airport`) REFERENCES `airports` (`airport_code`),
    CONSTRAINT `flights_ibfk_3` FOREIGN KEY (`arrival_airport`) REFERENCES `airports` (`airport_code`),
    CONSTRAINT `flights_chk_1` CHECK ((`business_seats` >= 0)),
    CONSTRAINT `flights_chk_2` CHECK ((`economy_seats` >= 0)),
    CONSTRAINT `flights_chk_3` CHECK (
        (
            `total_seats` = (
                `business_seats` + `economy_seats`
            )
        )
    ),
    CONSTRAINT `flights_chk_4` CHECK (
        (
            `departure_time` < `arrival_time`
        )
    )
) ENGINE = InnoDB AUTO_INCREMENT = 48 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci






CREATE TABLE `orders` (
    `order_id` int NOT NULL AUTO_INCREMENT,
    `passenger_id` int NOT NULL COMMENT '下单乘客ID',
    `flight_id` int DEFAULT NULL,
    `seat_class` enum('ECONOMY', 'BUSINESS') DEFAULT NULL,
    `ticket_passenger_id` int NOT NULL COMMENT '乘机人ID',
    `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `base_price` decimal(10, 2) NOT NULL DEFAULT '0.00',
    `actual_price` decimal(10, 2) NOT NULL DEFAULT '0.00',
    `discount_rate` decimal(5, 2) DEFAULT '0.00',
    `status` enum(
        'PENDING_PAYMENT',
        'PAID',
        'TICKETED',
        'CANCELLED',
        'COMPLETED',
        'REFUNDED'
    ) DEFAULT 'PENDING_PAYMENT',
    `contact_phone` varchar(20) NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`),
    KEY `idx_orders_time` (`order_time`),
    KEY `fk_orders_passenger` (`passenger_id`),
    KEY `fk_orders_ticket_passenger` (`ticket_passenger_id`),
    KEY `idx_flight_passenger_non_unique` (
        `flight_id`,
        `ticket_passenger_id`
    ),
    CONSTRAINT `fk_orders_flight` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`),
    CONSTRAINT `fk_orders_passenger` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`),
    CONSTRAINT `fk_orders_ticket_passenger` FOREIGN KEY (`ticket_passenger_id`) REFERENCES `passengers` (`passenger_id`),
    CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`),
    CONSTRAINT `chk_valid_order_time` CHECK (
        (
            (
                `order_time` <= _utf8mb4 '2026-01-01 00:00:00'
            )
            and (
                `order_time` >= _utf8mb4 '2020-01-01 00:00:00'
            )
        )
    )
) ENGINE = InnoDB AUTO_INCREMENT = 88 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci







CREATE TABLE `passengers` (
    `passenger_id` int NOT NULL AUTO_INCREMENT,
    `id_card` varchar(18) NOT NULL COMMENT '身份证号',
    `name` varchar(50) NOT NULL,
    `gender` enum('MALE', 'FEMALE', 'OTHER') NOT NULL DEFAULT 'OTHER',
    `birth_date` date NOT NULL,
    `phone` varchar(20) NOT NULL,
    `email` varchar(100) DEFAULT NULL,
    `membership_level` enum('REGULAR', 'SILVER', 'GOLD') DEFAULT 'REGULAR',
    `education` enum(
        'PRIMARY',
        'JUNIOR',
        'HIGH',
        'COLLEGE',
        'BACHELOR',
        'MASTER',
        'PHD',
        'OTHER'
    ) DEFAULT NULL,
    `occupation` varchar(50) DEFAULT NULL,
    `total_spent` decimal(10, 2) DEFAULT '0.00' COMMENT '累计消费额度',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`passenger_id`),
    UNIQUE KEY `id_card` (`id_card`),
    KEY `idx_passengers_name` (`name`)
) ENGINE = InnoDB AUTO_INCREMENT = 15 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci





CREATE TABLE `pricing_strategies` (
    `strategy_id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `description` text,
    `start_date` date NOT NULL,
    `end_date` date NOT NULL,
    `base_discount` decimal(5, 2) DEFAULT '0.00',
    `advance_days` int DEFAULT '0',
    `min_occupancy` decimal(5, 2) DEFAULT '0.00',
    `max_occupancy` decimal(5, 2) DEFAULT '100.00',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`strategy_id`),
    CONSTRAINT `pricing_strategies_chk_1` CHECK ((`start_date` <= `end_date`)),
    CONSTRAINT `pricing_strategies_chk_2` CHECK ((`advance_days` >= 0)),
    CONSTRAINT `pricing_strategies_chk_3` CHECK (
        (
            (`min_occupancy` >= 0)
            and (`min_occupancy` <= 100)
        )
    ),
    CONSTRAINT `pricing_strategies_chk_4` CHECK (
        (
            (`max_occupancy` >= 0)
            and (`max_occupancy` <= 100)
        )
    ),
    CONSTRAINT `pricing_strategies_chk_5` CHECK (
        (
            `min_occupancy` <= `max_occupancy`
        )
    )
) ENGINE = InnoDB AUTO_INCREMENT = 9 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci



CREATE TABLE `refunds` (
    `refund_id` int NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `reason` text,
    `refund_amount` decimal(10, 2) NOT NULL,
    `refund_time` datetime(6) NOT NULL,
    `status` enum(
        'APPROVED',
        'COMPLETED',
        'PENDING',
        'REJECTED'
    ) DEFAULT NULL,
    `order_id` int NOT NULL,
    `ticket_id` int DEFAULT NULL,
    PRIMARY KEY (`refund_id`),
    UNIQUE KEY `UK8iu8b9p5iouvq9epk2ua5rimn` (`ticket_id`),
    KEY `FKsk9rqm7f6y8b1g0qob018hdm7` (`order_id`),
    CONSTRAINT `FKlcljjuvuakfkpg7i4knsj6se8` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
    CONSTRAINT `FKsk9rqm7f6y8b1g0qob018hdm7` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 42 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci





CREATE TABLE `system_users` (
    `user_id` int NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(255) NOT NULL,
    `full_name` varchar(100) NOT NULL,
    `role` enum('ADMIN', 'PASSENGER') NOT NULL COMMENT '用户角色：管理员或乘客',
    `last_login` timestamp NULL DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `passenger_id` int DEFAULT NULL COMMENT '关联的乘客ID，仅对PASSENGER角色有效',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `username` (`username`),
    KEY `fk_systemuser_passenger` (`passenger_id`),
    CONSTRAINT `fk_systemuser_passenger` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`) ON DELETE SET NULL
) ENGINE = InnoDB AUTO_INCREMENT = 12 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci




CREATE TABLE `tickets` (
    `ticket_id` int NOT NULL AUTO_INCREMENT,
    `order_id` int NOT NULL,
    `flight_id` int NOT NULL,
    `passenger_id` int NOT NULL COMMENT '乘机人ID',
    `seat_class` enum('ECONOMY', 'BUSINESS') NOT NULL,
    `price` decimal(10, 2) NOT NULL,
    `status` enum('UNUSED', 'USED', 'REFUNDED') DEFAULT 'UNUSED',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`ticket_id`),
    KEY `order_id` (`order_id`),
    KEY `idx_tickets_flight` (`flight_id`),
    KEY `idx_tickets_passenger` (`passenger_id`),
    CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`),
    CONSTRAINT `tickets_ibfk_3` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 86 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci






CREATE TABLE `user_contacts` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL COMMENT '关联系统用户ID',
    `passenger_id` int NOT NULL COMMENT '关联乘客ID',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_passenger` (`user_id`, `passenger_id`),
    KEY `fk_contact_passenger` (`passenger_id`),
    CONSTRAINT `fk_contact_passenger` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_contact_user` FOREIGN KEY (`user_id`) REFERENCES `system_users` (`user_id`) ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户常用乘机人关联表'