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

 Date: 09/12/2022 18:33:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_appdomain_relation
-- ----------------------------
DROP TABLE IF EXISTS `app_appdomain_relation`;
CREATE TABLE `app_appdomain_relation`  (
  `app_id` bigint NULL DEFAULT NULL,
  `app_domain_id` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_appdomain_relation
-- ----------------------------

-- ----------------------------
-- Table structure for app_info
-- ----------------------------
DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info`  (
  `app_id` bigint NOT NULL,
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'some introductions',
  `app_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app version instruction',
  `install_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app install type, like docker , binary, deb, rpm, helm.etc',
  `ports` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app ports split by ,',
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
INSERT INTO `app_info` VALUES (1594274162157940738, 'appName_j0yax', 'appInfo_1g3ln', 'appVersion_n8flb', NULL, NULL, '2022-11-20 18:19:08', '2022-11-20 18:19:08', 'commont_9o1ot', 0);
INSERT INTO `app_info` VALUES (1594275402296066049, 'appName_xaxme', 'appInfo_5vujy', 'appVersion_jeyrq', NULL, NULL, '2022-11-20 18:24:09', '2022-11-20 18:25:34', 'commont_fg703', 1);
INSERT INTO `app_info` VALUES (1594275875803627522, 'appName_d2g2s', 'appInfo_obsl5', 'appVersion_ncbv2', NULL, NULL, '2022-11-20 18:26:01', '2022-11-20 18:26:01', 'commont_uhkqi', 0);
INSERT INTO `app_info` VALUES (1594276052018921473, 'appName_dfaq3', 'appInfo_n2u1l', 'appVersion_utf8v', NULL, NULL, '2022-11-20 18:26:05', '2022-11-20 18:26:05', 'commont_qb8pf', 0);
INSERT INTO `app_info` VALUES (1594276062164942850, 'appName_2wjp0', 'appInfo_um4kz', 'appVersion_nejf2', NULL, NULL, '2022-11-20 18:26:47', '2022-11-20 18:26:47', 'commont_fg0gm', 0);
INSERT INTO `app_info` VALUES (1594276070146703361, 'appName_oay4l', 'appInfo_4f9ul', 'appVersion_wx2f8', NULL, NULL, '2022-11-20 18:26:49', '2022-11-20 18:26:49', 'commont_mf8s7', 0);
INSERT INTO `app_info` VALUES (1594614210690662402, 'appName_vgbir', 'appInfo_npdqv', 'appVersion_wyuja', NULL, NULL, '2022-11-21 16:50:26', '2022-11-21 16:50:26', 'commont_xag4e', 0);
INSERT INTO `app_info` VALUES (1596765526292430849, 'appName_obevz', 'appInfo_utq3a', 'appVersion_h94r6', NULL, NULL, NULL, NULL, 'commont_9z47f', 0);

-- ----------------------------
-- Table structure for appdomain_info
-- ----------------------------
DROP TABLE IF EXISTS `appdomain_info`;
CREATE TABLE `appdomain_info`  (
  `app_domain_id` bigint NOT NULL,
  `app_domain_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app associated domain name',
  `app_domain_port` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app domain port',
  `domain_id` bigint NULL DEFAULT NULL COMMENT 'app associated domain_info id',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `id_delete` tinyint UNSIGNED NULL DEFAULT 0,
  PRIMARY KEY (`app_domain_id`) USING BTREE,
  INDEX `domain_fk`(`domain_id`) USING BTREE,
  CONSTRAINT `domain_fk` FOREIGN KEY (`domain_id`) REFERENCES `domain_info` (`domain_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appdomain_info
-- ----------------------------

-- ----------------------------
-- Table structure for domain_info
-- ----------------------------
DROP TABLE IF EXISTS `domain_info`;
CREATE TABLE `domain_info`  (
  `domain_id` bigint NOT NULL,
  `domain_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'complete domain url',
  `domain_provider` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'domain provider name',
  `register_time` datetime(0) NULL DEFAULT NULL,
  `expire_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `dns_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'the dns record to the server ip',
  `dns_provider` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'domain dns provider name',
  `dns_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'dns type for A AAAA CNAME\r\n',
  `dns_manage_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_delete` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`domain_id`) USING BTREE,
  UNIQUE INDEX `domain_name_unique_key`(`domain_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of domain_info
-- ----------------------------
INSERT INTO `domain_info` VALUES (1596794654509051905, 'domainName_cvtap', 'domainProvider_1clez', '2022-11-27 17:14:35', '2022-11-27 17:14:35', '2022-11-27 17:14:35', '2022-11-27 17:14:35', 'dnsIp_hhsyp', 'dnsProvider_ncz9x', 'dnsType_pt6al', 'dnsManageApi_0zmnt', 0);
INSERT INTO `domain_info` VALUES (1596794731503890433, 'domainName_h2s3a', 'domainProvider_ahozp', '2022-11-27 17:15:04', '2022-11-27 17:15:04', '2022-11-27 17:15:04', '2022-11-27 17:15:04', 'dnsIp_mhob6', 'dnsProvider_de1u6', 'dnsType_gc435', 'dnsManageApi_zid6c', 0);
INSERT INTO `domain_info` VALUES (1596794739821195265, 'domainName_nazlv', 'domainProvider_frh99', '2022-11-27 17:15:07', '2022-11-27 17:15:07', '2022-11-27 17:15:07', '2022-11-27 17:15:07', 'dnsIp_z6ymo', 'dnsProvider_ttea1', 'dnsType_p4vhe', 'dnsManageApi_6hatt', 0);
INSERT INTO `domain_info` VALUES (1596794745017937922, '我是你爸爸', 'domainProvider_m0dcv', '2022-11-27 17:17:14', '2022-11-27 17:17:14', '2022-11-27 17:17:14', '2022-11-27 17:17:14', 'dasdasd', 'dnsProvider_25lhg', 'dnsType_vcx7x', 'dnsManageApi_b8oju', 0);
INSERT INTO `domain_info` VALUES (1596794828568473602, 'domainName_ymzja', 'domainProvider_mtw6j', '2022-11-27 17:15:28', '2022-11-27 17:15:28', '2022-11-27 17:15:28', '2022-11-27 17:15:28', 'dnsIp_yldb3', 'dnsProvider_d9jah', 'dnsType_80ovf', 'dnsManageApi_u0rcu', 0);

-- ----------------------------
-- Table structure for manage_info
-- ----------------------------
DROP TABLE IF EXISTS `manage_info`;
CREATE TABLE `manage_info`  (
  `manage_id` bigint NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'also for rsa-pub\r\n',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'also for rsa-private',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_tme` datetime(0) NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  `is_delete` tinyint NULL DEFAULT 0,
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
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_delete` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
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
  `server_id` bigint NULL DEFAULT NULL,
  `app_id` bigint NULL DEFAULT NULL,
  INDEX `app_id`(`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server_app_relation
-- ----------------------------
INSERT INTO `server_app_relation` VALUES (1593452954004774925, 1594275875803627522);
INSERT INTO `server_app_relation` VALUES (1593452954004774925, 1594276062164942850);
INSERT INTO `server_app_relation` VALUES (1593452954004774925, 1594276070146703361);
INSERT INTO `server_app_relation` VALUES (1593452954004774925, 1594614210690662402);
INSERT INTO `server_app_relation` VALUES (1593452954004774925, 1596765526292430849);

-- ----------------------------
-- Table structure for server_domain_relation
-- ----------------------------
DROP TABLE IF EXISTS `server_domain_relation`;
CREATE TABLE `server_domain_relation`  (
  `server_id` bigint NULL DEFAULT NULL,
  `domain_id` bigint NULL DEFAULT NULL
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
  `topic_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'octopus message unique key name',
  `server_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'server host name',
  `server_ip_pb_v4` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server public ipv4\r\n',
  `server_ip_in_v4` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server inner ipv4\r\n',
  `server_ip_pb_v6` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server public ipv6\r\n',
  `server_ip_in_v6` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server inner ipv6\r\n',
  `register_time` datetime(0) NULL DEFAULT NULL COMMENT 'server register time',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT 'expire time',
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server location , type City Country',
  `provider` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server isp manager',
  `manage_port` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'split by ,',
  `cpu_brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `cpu_core` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `memory_total` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `disk_total` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `disk_usage` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `io_speed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `tcp_control` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `virtualization` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'server virtualization method',
  `os_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `os_kernel_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `machine_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'machine uuid from /etc/machineid',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '',
  `is_delete` tinyint NULL DEFAULT 0 COMMENT '0 alive || 1 deleted',
  `version` int NULL DEFAULT NULL COMMENT 'optimistic lock for concurrent',
  PRIMARY KEY (`server_id`) USING BTREE,
  UNIQUE INDEX `server_info_pk`(`server_name`) USING BTREE,
  UNIQUE INDEX `uuid_pk`(`topic_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server_info
-- ----------------------------
INSERT INTO `server_info` VALUES (1593452954004774914, NULL, 'serverName_e5nme', 'serverIpPbV4_ipwy3', 'serverIpInV4_ehsma', 'serverIpPbV6_l76dq', 'serverIpInV6_dqdu0', '2022-11-18 10:49:06', '2022-11-18 10:49:06', NULL, NULL, 'location_lydk1', 'provider_brv13', '1', 'cpuBrand_lpm9y', '1', NULL, NULL, NULL, NULL, NULL, NULL, 'osInfo_0rm2w', 'osKernelInfo_9p6qv', NULL, 'comment_3d2zz', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774915, NULL, 'serverName_490w7', 'serverIpPbV4_s4n6i', 'serverIpInV4_vflsm', 'serverIpPbV6_but4f', 'serverIpInV6_zkf5t', '2022-11-19 10:21:45', '2022-11-19 10:21:45', NULL, NULL, 'location_x1pyi', 'provider_sjhz5', '1', 'cpuBrand_sfbca', '1', NULL, NULL, NULL, NULL, NULL, NULL, 'osInfo_3rxn3', 'osKernelInfo_u9pu6', NULL, 'comment_08c0y', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774916, NULL, 'serverName_2v68a', 'serverIpPbV4_xg57p', 'serverIpInV4_a1p94', 'serverIpPbV6_u2u56', 'serverIpInV6_vsr6d', '2022-11-19 10:22:10', '2022-11-19 10:22:10', NULL, NULL, 'location_ov89u', 'provider_ylwsz', '1', 'cpuBrand_h3utp', '1', NULL, NULL, NULL, NULL, NULL, NULL, 'osInfo_3j3dp', 'osKernelInfo_qzb53', NULL, 'comment_n2xx5', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774919, NULL, 'serverName_t7ygq', 'serverIpPbV4_ifaab', 'serverIpInV4_zxuhz', 'serverIpPbV6_2r6bc', 'serverIpInV6_jufx5', '2022-11-19 10:47:29', '2022-11-19 10:47:29', NULL, NULL, 'location_3uu07', 'provider_wv1dk', '1', 'cpuBrand_4w4ih', '1', NULL, NULL, NULL, NULL, NULL, NULL, 'osInfo_uknst', 'osKernelInfo_3n6qo', NULL, 'comment_y5twi', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774924, NULL, 'serverName_7szbj', 'serverIpPbV4_qpuc4', 'serverIpInV4_uud4z', 'serverIpPbV6_yqazp', 'serverIpInV6_fyg0w', '2022-11-19 11:05:26', '2022-11-19 11:05:26', NULL, NULL, 'location_n4l92', 'provider_yvd0o', '1', 'cpuBrand_y3fx3', '1', NULL, NULL, NULL, NULL, NULL, NULL, 'osInfo_tlrxr', 'osKernelInfo_qnlxj', NULL, 'comment_389o5', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774925, NULL, 'serverName_b8ov7', 'serverIpPbV4_xpd29', 'serverIpInV4_fmqjz', 'serverIpPbV6_543ad', 'serverIpInV6_4tv45', '2022-11-19 11:05:30', '2022-11-19 11:05:30', NULL, NULL, 'location_l8tdl', 'provider_ipzp2', '1', 'cpuBrand_5w7yp', '1', NULL, NULL, NULL, NULL, NULL, NULL, 'osInfo_iwe8t', 'osKernelInfo_39uqt', NULL, 'comment_5svjo', 0, NULL);
INSERT INTO `server_info` VALUES (1593452954004774929, 'Chengdu-amd64-99-adasda', 'Chengdu-amd64-99', '\"\"', '\"\"', '\"\"', '\"\"', NULL, NULL, '2022-12-03 11:14:09', '2022-12-03 11:14:09', 'Chengdu', 'China Mobile', '3389', 'Xeon Proceoosr', '28 @ 2.9GHz', '64 GB', '512 GB', '200GB', '1 GB/s', 'bbr', 'dedicated', 'Windwos 11', 'Window NT 10.0', 'adasdasdas1q122131', '\"\"', 0, NULL);

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
