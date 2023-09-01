-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 01, 2023 at 04:42 PM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `songs`
--

-- --------------------------------------------------------

--
-- Table structure for table `song`
--

CREATE TABLE `song` (
  `id` varchar(10) NOT NULL,
  `name` varchar(30) NOT NULL,
  `artist` varchar(30) NOT NULL,
  `album` varchar(30) NOT NULL,
  `description` text NOT NULL,
  `rating` tinyint(2) NOT NULL,
  `year` smallint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `song`
--

INSERT INTO `song` (`id`, `name`, `artist`, `album`, `description`, `rating`, `year`) VALUES
('0', 'Cyberphonk', 'Khaos Lan', 'Cyberphonk', '', 9, 2022),
('1', 'The Lost Soul Down', 'NBSPLV', 'The Lost Soul Down', '', 8, 2015),
('2', 'EASY PEAZY', 'ROMANTICA  y  Lestmor', 'EASY PEAZY', '', 7, 2023),
('3', 'i dont wna cry', 'kurffew', 'i dont wna cry', '', 7, 2023),
('4', 'imoverstimulated', 'aWannabe', 'imoverstimulated', '', 8, 2022),
('5', '505', 'Arctic Monkeys', 'Favourite Worst Nightmare', '', 10, 2007),
('6', 'Crimewave', 'Crystal Castles', 'Crystal Castles', '', 7, 2008),
('7', 'Beauty & The Beast', 'Nyxjvh', 'Beauty & The Beast', '', 9, 2022),
('8', 'Summertime', 'Mareux', 'Predestiny Ep', '', 10, 2020),
('9', 'Судно (Борис Рижий)', 'Molchat Doma', 'Этажи', '', 10, 2018);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `song`
--
ALTER TABLE `song`
  ADD PRIMARY KEY (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
