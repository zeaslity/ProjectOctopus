/*
 Navicat Premium Data Transfer

 Source Server         : k8s-local
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:33306
 Source Schema         : wdd_server

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 22/11/2022 16:04:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_domain_info
-- ----------------------------
DROP TABLE IF EXISTS `app_domain_info`;
CREATE TABLE `app_domain_info`  (
  `app_domain_id` bigint NOT NULL,
  `app_domain_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `app_domain_port` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_delete` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`app_domain_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_domain_info
-- ----------------------------

-- ----------------------------
-- Table structure for app_info
-- ----------------------------
DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info`  (
  `app_id` bigint NOT NULL,
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `app_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `app_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `commont` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_delete` tinyint NULL DEFAULT 0 COMMENT '0 alive || 1 deleted',
  PRIMARY KEY (`app_id`) USING BTREE,
  UNIQUE INDEX `app_info_pk`(`app_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_info
-- ----------------------------
INSERT INTO `app_info` VALUES (1594274162157940738, 'appName_j0yax', 'appInfo_1g3ln', 'appVersion_n8flb', '2022-11-20 18:19:08', '2022-11-20 18:19:08', 'commont_9o1ot', 0);
INSERT INTO `app_info` VALUES (1594275402296066049, 'appName_xaxme', 'appInfo_5vujy', 'appVersion_jeyrq', '2022-11-20 18:24:09', '2022-11-20 18:25:34', 'commont_fg703', 1);
INSERT INTO `app_info` VALUES (1594275875803627522, 'appName_d2g2s', 'appInfo_obsl5', 'appVersion_ncbv2', '2022-11-20 18:26:01', '2022-11-20 18:26:01', 'commont_uhkqi', NULL);
INSERT INTO `app_info` VALUES (1594276052018921473, 'appName_dfaq3', 'appInfo_n2u1l', 'appVersion_utf8v', '2022-11-20 18:26:05', '2022-11-20 18:26:05', 'commont_qb8pf', 0);
INSERT INTO `app_info` VALUES (1594276062164942850, 'appName_2wjp0', 'appInfo_um4kz', 'appVersion_nejf2', '2022-11-20 18:26:47', '2022-11-20 18:26:47', 'commont_fg0gm', 0);
INSERT INTO `app_info` VALUES (1594276070146703361, 'appName_oay4l', 'appInfo_4f9ul', 'appVersion_wx2f8', '2022-11-20 18:26:49', '2022-11-20 18:26:49', 'commont_mf8s7', 0);
INSERT INTO `app_info` VALUES (1594614210690662402, 'appName_vgbir', 'appInfo_npdqv', 'appVersion_wyuja', '2022-11-21 16:50:26', '2022-11-21 16:50:26', 'commont_xag4e', 0);

-- ----------------------------
-- Table structure for domain_info
-- ----------------------------
DROP TABLE IF EXISTS `domain_info`;
CREATE TABLE `domain_info`  (
  `domain_id` bigint NULL DEFAULT NULL,
  `domain_provider` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `domain_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `domain_register_time` datetime(0) NULL DEFAULT NULL,
  `domain_expire_time` datetime(0) NULL DEFAULT NULL,
  `domain_dns_provider` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `domain_manage_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of domain_info
-- ----------------------------

-- ----------------------------
-- Table structure for manage_info
-- ----------------------------
DROP TABLE IF EXISTS `manage_info`;
CREATE TABLE `manage_info`  (
  `manage_id` bigint NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'also for rsa-pub\r\n',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'also for rsa-private',
  PRIMARY KEY (`manage_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of manage_info
-- ----------------------------

-- ----------------------------
-- Table structure for provider_info
-- ----------------------------
DROP TABLE IF EXISTS `provider_info`;
CREATE TABLE `provider_info`  (
  `provider_id` bigint NOT NULL,
  `provider_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `provider_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_web` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `register_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`provider_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of provider_info
-- ----------------------------

-- ----------------------------
-- Table structure for server_app_relation
-- ----------------------------
DROP TABLE IF EXISTS `server_app_relation`;
CREATE TABLE `server_app_relation`  (
  `server_id` bigint NOT NULL,
  `app_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`server_id`) USING BTREE,
  INDEX `app_id`(`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server_app_relation
-- ----------------------------

-- ----------------------------
-- Table structure for server_domain_relation
-- ----------------------------
DROP TABLE IF EXISTS `server_domain_relation`;
CREATE TABLE `server_domain_relation`  (
  `server_id` bigint NOT NULL,
  `domain_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`server_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server_domain_relation
-- ----------------------------

-- ----------------------------
-- Table structure for server_info
-- ----------------------------
DROP TABLE IF EXISTS `server_info`;
CREATE TABLE `server_info`  (
  `server_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'server primary key',
  `server_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'server host name',
  `server_ip_pb_v4` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server public ipv4\r\n',
  `server_ip_in_v4` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server inner ipv4\r\n',
  `server_ip_pb_v6` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server public ipv6\r\n',
  `server_ip_in_v6` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server inner ipv6\r\n',
  `register_time` timestamp(0) NULL DEFAULT NULL,
  `expire_time` timestamp(0) NULL DEFAULT NULL,
  `update_time` timestamp(0) NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `provider` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `manage_port` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'split by ,',
  `cpu_core` int NULL DEFAULT NULL,
  `cpu_brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `os_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `os_kernel_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '',
  `is_delete` tinyint NULL DEFAULT 0 COMMENT '0 alive || 1 deleted',
  `version` int NULL DEFAULT NULL COMMENT 'optimistic lock for concurrent',
  PRIMARY KEY (`server_id`) USING BTREE,
  UNIQUE INDEX `server_info_pk`(`server_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server_info
-- ----------------------------
INSERT INTO `server_info` VALUES (1593452954004774914, 'serverName_e5nme', 'serverIpPbV4_ipwy3', 'serverIpInV4_ehsma', 'serverIpPbV6_l76dq', 'serverIpInV6_dqdu0', '2022-11-18 10:49:06', '2022-11-18 10:49:06', NULL, 'location_lydk1', 'provider_brv13', '1', 1, 'cpuBrand_lpm9y', 'osInfo_0rm2w', 'osKernelInfo_9p6qv', 'comment_3d2zz', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774915, 'serverName_490w7', 'serverIpPbV4_s4n6i', 'serverIpInV4_vflsm', 'serverIpPbV6_but4f', 'serverIpInV6_zkf5t', '2022-11-19 10:21:45', '2022-11-19 10:21:45', NULL, 'location_x1pyi', 'provider_sjhz5', '1', 1, 'cpuBrand_sfbca', 'osInfo_3rxn3', 'osKernelInfo_u9pu6', 'comment_08c0y', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774916, 'serverName_2v68a', 'serverIpPbV4_xg57p', 'serverIpInV4_a1p94', 'serverIpPbV6_u2u56', 'serverIpInV6_vsr6d', '2022-11-19 10:22:10', '2022-11-19 10:22:10', NULL, 'location_ov89u', 'provider_ylwsz', '1', 1, 'cpuBrand_h3utp', 'osInfo_3j3dp', 'osKernelInfo_qzb53', 'comment_n2xx5', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774919, 'serverName_t7ygq', 'serverIpPbV4_ifaab', 'serverIpInV4_zxuhz', 'serverIpPbV6_2r6bc', 'serverIpInV6_jufx5', '2022-11-19 10:47:29', '2022-11-19 10:47:29', NULL, 'location_3uu07', 'provider_wv1dk', '1', 1, 'cpuBrand_4w4ih', 'osInfo_uknst', 'osKernelInfo_3n6qo', 'comment_y5twi', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774924, 'serverName_7szbj', 'serverIpPbV4_qpuc4', 'serverIpInV4_uud4z', 'serverIpPbV6_yqazp', 'serverIpInV6_fyg0w', '2022-11-19 11:05:26', '2022-11-19 11:05:26', NULL, 'location_n4l92', 'provider_yvd0o', '1', 1, 'cpuBrand_y3fx3', 'osInfo_tlrxr', 'osKernelInfo_qnlxj', 'comment_389o5', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774925, 'serverName_b8ov7', 'serverIpPbV4_xpd29', 'serverIpInV4_fmqjz', 'serverIpPbV6_543ad', 'serverIpInV6_4tv45', '2022-11-19 11:05:30', '2022-11-19 11:05:30', NULL, 'location_l8tdl', 'provider_ipzp2', '1', 1, 'cpuBrand_5w7yp', 'osInfo_iwe8t', 'osKernelInfo_39uqt', 'comment_5svjo', 1, NULL);

-- ----------------------------
-- Table structure for server_manage_relation
-- ----------------------------
DROP TABLE IF EXISTS `server_manage_relation`;
CREATE TABLE `server_manage_relation`  (
  `server_id` bigint NOT NULL,
  `manage_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`server_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server_manage_relation
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
