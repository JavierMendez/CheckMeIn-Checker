/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50524
Source Host           : localhost:3306
Source Database       : checkmein

Target Server Type    : MYSQL
Target Server Version : 50524
File Encoding         : 65001

Date: 2014-02-24 22:01:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tbladmin`
-- ----------------------------
DROP TABLE IF EXISTS `tbladmin`;
CREATE TABLE `tbladmin` (
  `strAdmin` varchar(10) NOT NULL,
  `strPassword` varchar(10) NOT NULL,
  PRIMARY KEY (`strAdmin`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of tbladmin
-- ----------------------------

-- ----------------------------
-- Table structure for `tblattendance`
-- ----------------------------
DROP TABLE IF EXISTS `tblattendance`;
CREATE TABLE `tblattendance` (
  `idAttendance` int(4) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `idEmployee` int(4) NOT NULL,
  `dteDate` date NOT NULL,
  `tmeTime` time NOT NULL,
  PRIMARY KEY (`idAttendance`),
  KEY `emp` (`idEmployee`),
  CONSTRAINT `emp` FOREIGN KEY (`idEmployee`) REFERENCES `tblemployee` (`idEmployee`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of tblattendance
-- ----------------------------

-- ----------------------------
-- Table structure for `tblemployee`
-- ----------------------------
DROP TABLE IF EXISTS `tblemployee`;
CREATE TABLE `tblemployee` (
  `idEmployee` int(4) NOT NULL,
  `strName` varchar(30) NOT NULL,
  `strLastName` varchar(30) NOT NULL,
  `strUsername` varchar(10) NOT NULL,
  `pssPassword` varchar(10) NOT NULL,
  `blobFinger` blob,
  PRIMARY KEY (`idEmployee`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of tblemployee
-- ----------------------------
