-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 28, 2025 at 04:51 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `warmel`
--

-- --------------------------------------------------------

--
-- Table structure for table `agen`
--

CREATE TABLE `agen` (
  `idAgen` int(11) NOT NULL,
  `namaAgen` varchar(255) NOT NULL,
  `alamat` text DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `agen`
--

INSERT INTO `agen` (`idAgen`, `namaAgen`, `alamat`, `created_at`, `updated_at`) VALUES
(1, 'Yeni', 'Jl. Soekarno-Hatta Km. 1', '2025-04-28 20:39:17', '2025-04-28 20:39:17'),
(2, 'Sultan', 'Jl. Soekarno-Hatta Km. 1', '2025-04-28 20:39:40', '2025-04-28 20:39:40'),
(3, 'Ghina', 'Jl. Soekarno-Hatta Km. 1', '2025-04-28 20:39:59', '2025-04-28 20:39:59');

-- --------------------------------------------------------

--
-- Table structure for table `detail_pembelian`
--

CREATE TABLE `detail_pembelian` (
  `idDetailPembelian` int(11) NOT NULL,
  `idPembelian` int(11) DEFAULT NULL,
  `idProduk` int(11) DEFAULT NULL,
  `jumlah` int(11) DEFAULT NULL,
  `subtotal` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `detail_transaksi`
--

CREATE TABLE `detail_transaksi` (
  `idDetailTransaksi` int(11) NOT NULL,
  `idTransaksi` int(11) DEFAULT NULL,
  `idDetailPembelian` int(11) DEFAULT NULL,
  `jumlah` int(11) DEFAULT NULL,
  `subtotal` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `kategori`
--

CREATE TABLE `kategori` (
  `idKategori` int(11) NOT NULL,
  `namaKategori` varchar(100) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kategori`
--

INSERT INTO `kategori` (`idKategori`, `namaKategori`, `created_at`, `updated_at`) VALUES
(1, 'Makanan', '2025-04-28 15:06:48', '2025-04-28 15:06:48'),
(2, 'Minuman', '2025-04-28 15:06:55', '2025-04-28 15:06:55'),
(3, 'Obat-obatan', '2025-04-28 20:18:00', '2025-04-28 20:18:00'),
(4, 'Sabun dan Pewangi', '2025-04-28 20:19:19', '2025-04-28 20:19:19'),
(5, 'Lainnya', '2025-04-28 20:19:32', '2025-04-28 20:19:32'),
(6, 'Bahan Masak', '2025-04-28 21:31:35', '2025-04-28 21:31:35');

-- --------------------------------------------------------

--
-- Table structure for table `pembelian`
--

CREATE TABLE `pembelian` (
  `idPembelian` int(11) NOT NULL,
  `idAgen` int(11) DEFAULT NULL,
  `keterangan` text DEFAULT NULL,
  `totalHarga` decimal(15,2) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `produk`
--

CREATE TABLE `produk` (
  `idProduk` int(11) NOT NULL,
  `namaProduk` varchar(255) NOT NULL,
  `idKategori` int(11) DEFAULT NULL,
  `hargaBeli` decimal(15,2) DEFAULT NULL,
  `hargaJual` decimal(15,2) DEFAULT NULL,
  `stok` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `produk`
--

INSERT INTO `produk` (`idProduk`, `namaProduk`, `idKategori`, `hargaBeli`, `hargaJual`, `stok`, `created_at`, `updated_at`) VALUES
(2, 'Roma Malkist Crackers', 1, 800.00, 1000.00, 10, '2025-04-28 20:22:09', '2025-04-28 20:22:09'),
(3, 'Wafer Nabati Keju', 1, 800.00, 1000.00, 10, '2025-04-28 20:23:00', '2025-04-28 20:23:00'),
(4, 'Roma Malkist Abon', 1, 800.00, 1000.00, 10, '2025-04-28 20:23:15', '2025-04-28 20:23:15'),
(5, 'Wafer Nabati Cokelat', 1, 800.00, 1000.00, 10, '2025-04-28 20:23:35', '2025-04-28 20:23:35'),
(6, 'Alpenliebe Lolipop', 1, 800.00, 1000.00, 10, '2025-04-28 20:24:00', '2025-04-28 20:24:00'),
(7, 'Go Potato', 1, 700.00, 1000.00, 10, '2025-04-28 20:24:41', '2025-04-28 20:24:41'),
(8, 'Goriorio', 1, 700.00, 1000.00, 10, '2025-04-28 20:24:56', '2025-04-28 20:24:56'),
(9, 'Siip Bite Cokelat', 1, 2000.00, 2500.00, 10, '2025-04-28 20:25:45', '2025-04-28 20:25:45'),
(10, 'TicTac rasa Bawang', 1, 2000.00, 2500.00, 10, '2025-04-28 20:26:04', '2025-04-28 20:26:04'),
(11, 'French Fries', 1, 2000.00, 2500.00, 10, '2025-04-28 20:26:21', '2025-04-28 20:26:21'),
(12, 'Qtela ', 1, 2000.00, 2500.00, 10, '2025-04-28 20:26:41', '2025-04-28 20:26:41'),
(13, 'Chocolate Pie ', 1, 2000.00, 2500.00, 10, '2025-04-28 20:26:56', '2025-04-28 20:26:56'),
(14, 'Nextar Brownies Cokelat', 1, 2000.00, 2500.00, 10, '2025-04-28 20:27:12', '2025-04-28 20:27:12'),
(15, 'Wafello Choco Blast', 1, 2000.00, 2500.00, 10, '2025-04-28 20:27:33', '2025-04-28 20:27:33'),
(16, 'Oreo Chocolate Creme', 1, 2000.00, 2500.00, 10, '2025-04-28 20:27:52', '2025-04-28 20:27:52'),
(17, 'Kalpa', 1, 2000.00, 2500.00, 10, '2025-04-28 20:28:06', '2025-04-28 20:28:06'),
(18, 'Hatari Pineapple Cream Biscuits', 1, 7000.00, 8000.00, 10, '2025-04-28 20:29:01', '2025-04-28 20:29:01'),
(19, 'Roma Malkist Kelapa Kopyor', 1, 9000.00, 10000.00, 5, '2025-04-28 20:29:37', '2025-04-28 20:29:37'),
(20, 'Roma Biskuit Kelapa', 1, 11000.00, 12000.00, 5, '2025-04-28 20:30:10', '2025-04-28 20:30:10'),
(21, 'Teh Celup Gunung Satria', 2, 7000.00, 8000.00, 1, '2025-04-28 20:30:58', '2025-04-28 20:30:58'),
(22, 'Sikat Gigi Formula', 5, 4000.00, 5000.00, 4, '2025-04-28 20:31:31', '2025-04-28 20:31:31'),
(23, 'Lilin', 5, 3000.00, 4000.00, 4, '2025-04-28 20:31:48', '2025-04-28 20:31:48'),
(24, 'Amplop', 5, 300.00, 500.00, 60, '2025-04-28 20:32:22', '2025-04-28 20:32:22'),
(25, 'Hansaplast', 5, 300.00, 500.00, 15, '2025-04-28 20:46:07', '2025-04-28 20:46:07'),
(26, 'Cutton Buds', 5, 3000.00, 4000.00, 1, '2025-04-28 20:46:34', '2025-04-28 20:46:34'),
(27, 'Korek Gas', 5, 3000.00, 3500.00, 8, '2025-04-28 20:46:52', '2025-04-28 20:46:52'),
(28, 'MamyPoko Pants XL', 5, 3500.00, 4000.00, 4, '2025-04-28 20:47:21', '2025-04-28 20:47:21'),
(29, 'MamyPoko Pants L', 5, 3000.00, 3500.00, 4, '2025-04-28 20:47:37', '2025-04-28 20:47:37'),
(30, 'Rexona Free Spirit', 5, 3000.00, 3500.00, 4, '2025-04-28 20:47:59', '2025-04-28 20:47:59'),
(31, 'Rexona Men Ice Cool', 5, 3000.00, 3500.00, 4, '2025-04-28 20:48:12', '2025-04-28 20:48:12'),
(32, 'Kawat Cuci Piring', 5, 2500.00, 3000.00, 6, '2025-04-28 20:48:41', '2025-04-28 20:48:41'),
(33, 'Pasta Gigi Pepsodent', 5, 9000.00, 10000.00, 1, '2025-04-28 20:49:14', '2025-04-28 20:49:14'),
(34, 'Sunlight', 4, 6000.00, 7000.00, 4, '2025-04-28 20:49:44', '2025-04-28 20:49:44'),
(35, 'Mama Lemon', 4, 3000.00, 3500.00, 4, '2025-04-28 20:50:11', '2025-04-28 20:50:11'),
(36, 'Daia Putih', 4, 6000.00, 7000.00, 3, '2025-04-28 20:50:35', '2025-04-28 20:50:35'),
(37, 'Rinso+Molto', 4, 11000.00, 12000.00, 4, '2025-04-28 20:51:07', '2025-04-28 20:51:07'),
(38, 'GIV White', 4, 2500.00, 3000.00, 5, '2025-04-28 20:51:38', '2025-04-28 20:51:38'),
(39, 'Lifeboy', 4, 4000.00, 5000.00, 3, '2025-04-28 20:51:57', '2025-04-28 20:51:57'),
(40, 'LUX Botanicals', 4, 5000.00, 6000.00, 2, '2025-04-28 20:52:23', '2025-04-28 20:52:23'),
(41, 'Molto parfum protect', 4, 600.00, 1000.00, 7, '2025-04-28 20:53:07', '2025-04-28 20:53:07'),
(42, 'Molto parfum boost', 4, 600.00, 1000.00, 6, '2025-04-28 20:53:23', '2025-04-28 20:53:23'),
(43, 'Rejoice shampo hijab', 4, 600.00, 1000.00, 5, '2025-04-28 20:54:17', '2025-04-28 20:54:17'),
(44, 'Clear Menthol Fresh Active', 4, 600.00, 1000.00, 4, '2025-04-28 20:54:46', '2025-04-28 20:54:46'),
(45, 'Head & Shoulders', 4, 1500.00, 2000.00, 4, '2025-04-28 20:55:28', '2025-04-28 20:55:28'),
(46, 'Lifeboy shampoo', 4, 700.00, 1000.00, 4, '2025-04-28 20:55:59', '2025-04-28 20:55:59'),
(47, 'Pantene shampo', 4, 1500.00, 2000.00, 4, '2025-04-28 20:56:25', '2025-04-28 20:56:25'),
(48, 'Pantene shampo Hitam Glow', 4, 1500.00, 2000.00, 4, '2025-04-28 20:56:38', '2025-04-28 20:56:38'),
(49, 'Rinso Anti Noda+Molto', 4, 1500.00, 2000.00, 4, '2025-04-28 20:57:02', '2025-04-28 20:57:02'),
(50, 'Vanish Penghilang Noda Cair', 4, 2500.00, 3000.00, 2, '2025-04-28 20:57:30', '2025-04-28 20:57:30'),
(51, 'Diapet', 3, 1500.00, 2000.00, 10, '2025-04-28 20:58:29', '2025-04-28 20:58:29'),
(52, 'Bodrexin', 3, 500.00, 1000.00, 6, '2025-04-28 20:58:50', '2025-04-28 20:58:50'),
(53, 'Bodrex', 3, 500.00, 1000.00, 5, '2025-04-28 20:59:01', '2025-04-28 20:59:01'),
(54, 'Promag', 3, 500.00, 1000.00, 5, '2025-04-28 20:59:17', '2025-04-28 20:59:17'),
(55, 'Paramex', 3, 3000.00, 4000.00, 2, '2025-04-28 20:59:40', '2025-04-28 20:59:40'),
(56, 'Mixagrip Flu', 3, 3000.00, 4000.00, 2, '2025-04-28 21:00:01', '2025-04-28 21:00:01'),
(57, 'Paracetamol', 3, 1000.00, 2000.00, 6, '2025-04-28 21:00:38', '2025-04-28 21:00:38'),
(58, 'Vape anti nyamuk', 3, 5000.00, 6000.00, 2, '2025-04-28 21:01:07', '2025-04-28 21:01:07'),
(59, 'Mie Gelas Kari Ayam', 1, 1500.00, 2000.00, 5, '2025-04-28 21:01:46', '2025-04-28 21:01:46'),
(60, 'Mie Gelas Soto Ayam', 1, 1500.00, 2000.00, 4, '2025-04-28 21:01:59', '2025-04-28 21:01:59'),
(61, 'Mie Gelas Baso Sapi', 1, 1500.00, 2000.00, 7, '2025-04-28 21:02:12', '2025-04-28 21:02:12'),
(62, 'Mie Gelas Goreng', 1, 1500.00, 2000.00, 2, '2025-04-28 21:02:24', '2025-04-28 21:02:24'),
(63, 'Mie Sedaap Kari Ayam', 1, 3500.00, 4000.00, 5, '2025-04-28 21:02:52', '2025-04-28 21:02:52'),
(64, 'Mie Sedaap Goreng', 1, 3500.00, 4000.00, 8, '2025-04-28 21:03:11', '2025-04-28 21:03:11'),
(65, 'Mie Sedaap Soto', 1, 3500.00, 4000.00, 8, '2025-04-28 21:03:31', '2025-04-28 21:03:31'),
(66, 'Mie Sedaap Korean Spicy Chicken', 1, 3500.00, 4000.00, 8, '2025-04-28 21:03:59', '2025-04-28 21:03:59'),
(67, 'Mie Sarimi Baso Sapi', 1, 3000.00, 3500.00, 7, '2025-04-28 21:04:31', '2025-04-28 21:04:31'),
(68, 'Mie Sakura Kaldu Ayam', 1, 2000.00, 2500.00, 5, '2025-04-28 21:04:54', '2025-04-28 21:04:54'),
(69, 'Marimas Nanas', 2, 500.00, 1000.00, 10, '2025-04-28 21:05:46', '2025-04-28 21:05:46'),
(70, 'Marimas Jeruk Nipis', 2, 500.00, 1000.00, 10, '2025-04-28 21:09:12', '2025-04-28 21:09:12'),
(71, 'Marimas Cincau', 2, 500.00, 1000.00, 10, '2025-04-28 21:09:32', '2025-04-28 21:09:32'),
(72, 'Marimas Jeruk', 2, 500.00, 1000.00, 10, '2025-04-28 21:09:41', '2025-04-28 21:09:41'),
(73, 'Top Ice Melon', 2, 500.00, 1000.00, 10, '2025-04-28 21:09:54', '2025-04-28 21:09:54'),
(74, 'Marimas Cendol Dawet', 2, 500.00, 1000.00, 10, '2025-04-28 21:10:09', '2025-04-28 21:10:09'),
(75, 'Top Ice Anggur', 2, 500.00, 1000.00, 10, '2025-04-28 21:10:22', '2025-04-28 21:10:22'),
(76, 'Top Ice Cokelat', 2, 500.00, 1000.00, 10, '2025-04-28 21:10:35', '2025-04-28 21:10:35'),
(77, 'Top Ice Bizchoco', 2, 500.00, 1000.00, 10, '2025-04-28 21:10:48', '2025-04-28 21:10:48'),
(78, 'Top Ice Stroberi', 2, 500.00, 1000.00, 10, '2025-04-28 21:10:59', '2025-04-28 21:10:59'),
(79, 'Top Ice Cappuccino', 2, 500.00, 1000.00, 10, '2025-04-28 21:11:09', '2025-04-28 21:11:09'),
(80, 'Top Ice Vanilla Blue', 2, 500.00, 1000.00, 10, '2025-04-28 21:11:20', '2025-04-28 21:11:20'),
(81, 'Top Ice Bubble Gum', 2, 500.00, 1000.00, 10, '2025-04-28 21:11:33', '2025-04-28 21:11:33'),
(82, 'Teh Sisri Gula Batu', 2, 500.00, 1000.00, 10, '2025-04-28 21:11:47', '2025-04-28 21:11:47'),
(83, 'POP Ice Italian Chociato', 2, 1500.00, 2000.00, 10, '2025-04-28 21:12:12', '2025-04-28 21:12:12'),
(84, 'POP Ice Strawberry', 2, 1500.00, 2000.00, 10, '2025-04-28 21:12:28', '2025-04-28 21:12:28'),
(85, 'POP Ice Durian', 2, 1500.00, 2000.00, 10, '2025-04-28 21:12:36', '2025-04-28 21:12:36'),
(86, 'POP Ice Cappuccino', 2, 1500.00, 2000.00, 10, '2025-04-28 21:12:47', '2025-04-28 21:12:47'),
(87, 'POP Ice Taro', 2, 1500.00, 2000.00, 10, '2025-04-28 21:12:54', '2025-04-28 21:12:54'),
(88, 'POP Ice Chocolate', 2, 1500.00, 2000.00, 10, '2025-04-28 21:13:02', '2025-04-28 21:13:02'),
(89, 'POP Ice Avocado', 2, 1500.00, 2000.00, 10, '2025-04-28 21:13:10', '2025-04-28 21:13:10'),
(90, 'POP Ice Mango', 2, 1500.00, 2000.00, 10, '2025-04-28 21:13:19', '2025-04-28 21:13:19'),
(91, 'POP Ice Melon', 2, 1500.00, 2000.00, 10, '2025-04-28 21:13:27', '2025-04-28 21:13:27'),
(92, 'MILO', 2, 2000.00, 2500.00, 10, '2025-04-28 21:14:43', '2025-04-28 21:14:43'),
(93, 'ENERGEN Vanilla', 2, 2500.00, 3000.00, 10, '2025-04-28 21:15:55', '2025-04-28 21:15:55'),
(94, 'ENERGEN Cokelat', 2, 2500.00, 3000.00, 10, '2025-04-28 21:16:23', '2025-04-28 21:16:23'),
(95, 'TORABIKA Cappuccino', 2, 2000.00, 2500.00, 10, '2025-04-28 21:17:04', '2025-04-28 21:17:04'),
(96, 'Kapal Api Mantap Kopi+Gula', 2, 1500.00, 2000.00, 10, '2025-04-28 21:17:33', '2025-04-28 21:17:33'),
(97, 'TOP Coffe Gula Aren', 2, 2000.00, 2500.00, 10, '2025-04-28 21:18:07', '2025-04-28 21:18:07'),
(98, 'INDOCAFE Coffeemix', 2, 1500.00, 2000.00, 10, '2025-04-28 21:18:37', '2025-04-28 21:18:37'),
(99, 'ABC Kopi Mocca', 2, 1500.00, 2000.00, 10, '2025-04-28 21:18:50', '2025-04-28 21:18:50'),
(100, 'TOP Cappuccino', 2, 2000.00, 2500.00, 10, '2025-04-28 21:19:15', '2025-04-28 21:19:15'),
(101, 'Kapal Api Special', 2, 6000.00, 7000.00, 10, '2025-04-28 21:19:33', '2025-04-28 21:19:33'),
(102, 'Frisian Flag Cokelat', 2, 2000.00, 2500.00, 10, '2025-04-28 21:20:15', '2025-04-28 21:20:15'),
(103, 'Frisian Flag Original', 2, 2000.00, 2500.00, 10, '2025-04-28 21:20:36', '2025-04-28 21:20:36'),
(104, 'ENAAK Cokelat', 2, 1500.00, 2000.00, 10, '2025-04-28 21:21:00', '2025-04-28 21:21:00'),
(105, 'ENAAK Original', 2, 1500.00, 2000.00, 10, '2025-04-28 21:21:11', '2025-04-28 21:21:11'),
(106, 'CLEVO Stroberi', 2, 3000.00, 4000.00, 5, '2025-04-28 21:21:35', '2025-04-28 21:21:35'),
(107, 'CLEVO Cokelat', 2, 3000.00, 4000.00, 5, '2025-04-28 21:21:44', '2025-04-28 21:21:44'),
(108, 'INDOMILK Cokelat', 2, 3000.00, 4000.00, 5, '2025-04-28 21:22:00', '2025-04-28 21:22:00'),
(109, 'INDOMILK Stroberi', 2, 3000.00, 4000.00, 5, '2025-04-28 21:22:11', '2025-04-28 21:22:11'),
(110, 'Teh Gelas BIG', 2, 1500.00, 2000.00, 5, '2025-04-28 21:22:30', '2025-04-28 21:22:30'),
(111, 'Djinggo Es Teler BIG', 2, 1500.00, 2000.00, 5, '2025-04-28 21:22:58', '2025-04-28 21:22:58'),
(112, 'Larutan CAP Kaki Tiga Anak rasa Leci', 2, 3000.00, 4000.00, 1, '2025-04-28 21:23:36', '2025-04-28 21:23:36'),
(113, 'Larutan Penyegar cap Badak rasa Leci', 2, 6500.00, 7000.00, 2, '2025-04-28 21:24:15', '2025-04-28 21:24:15'),
(114, 'cap ENAAK Milky Creamy', 2, 10000.00, 11000.00, 2, '2025-04-28 21:24:46', '2025-04-28 21:24:46'),
(115, 'Nestle Bear Brand', 2, 11000.00, 12000.00, 4, '2025-04-28 21:25:18', '2025-04-28 21:25:18'),
(116, 'Sajiku Tepung Bumbu Serbaguna', 6, 3000.00, 3500.00, 5, '2025-04-28 21:32:10', '2025-04-28 21:32:10'),
(117, 'Racik Bumbu Sayur Sop', 6, 2000.00, 2500.00, 5, '2025-04-28 21:32:37', '2025-04-28 21:32:37'),
(118, 'Sajiku bumbu Nasi Goreng', 6, 2000.00, 2500.00, 5, '2025-04-28 21:32:54', '2025-04-28 21:32:54'),
(119, 'Racik bumbu Ikan Goreng', 6, 2000.00, 2500.00, 5, '2025-04-28 21:33:10', '2025-04-28 21:33:10'),
(120, 'Racik bumbu Ayam Goreng', 6, 2000.00, 2500.00, 5, '2025-04-28 21:33:19', '2025-04-28 21:33:19'),
(121, 'Royco rasa Ayam', 6, 700.00, 1000.00, 10, '2025-04-28 21:33:42', '2025-04-28 21:33:42'),
(122, 'Masako rasa Ayam', 6, 700.00, 1000.00, 10, '2025-04-28 21:33:53', '2025-04-28 21:33:53'),
(123, 'Ladaku Merica Bubuk', 6, 1000.00, 1500.00, 8, '2025-04-28 21:34:16', '2025-04-28 21:34:16'),
(124, 'Desaku Ketumbar Bubuk', 6, 1000.00, 1500.00, 8, '2025-04-28 21:34:37', '2025-04-28 21:34:37'),
(125, 'Desaku Kunyit Bubuk', 6, 1000.00, 1500.00, 6, '2025-04-28 21:34:56', '2025-04-28 21:34:56'),
(126, 'Desaku Marinasi', 6, 1000.00, 2000.00, 1, '2025-04-28 21:35:20', '2025-04-28 21:35:20'),
(127, 'Tepung Segitiga Biru', 6, 9000.00, 10000.00, 2, '2025-04-28 21:36:02', '2025-04-28 21:36:02'),
(128, 'Tepung Tapioka', 6, 9000.00, 10000.00, 1, '2025-04-28 21:36:28', '2025-04-28 21:36:28'),
(129, 'Minyak Kita ', 6, 19000.00, 20000.00, 3, '2025-04-28 21:37:04', '2025-04-28 21:37:04'),
(130, 'Kecap Sedaap Manis', 6, 3000.00, 3500.00, 3, '2025-04-28 21:37:31', '2025-04-28 21:37:31'),
(131, 'Telur', 6, 2000.00, 2500.00, 9, '2025-04-28 21:37:51', '2025-04-28 21:37:51'),
(132, 'ABC Terasi Udang', 6, 700.00, 1000.00, 7, '2025-04-28 21:38:14', '2025-04-28 21:38:14'),
(133, 'Ajinomoto', 6, 6000.00, 7000.00, 3, '2025-04-28 21:38:38', '2025-04-28 21:38:38'),
(134, 'Garam Jempol', 6, 3000.00, 4000.00, 4, '2025-04-28 21:39:01', '2025-04-28 21:39:01');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `idTransaksi` int(11) NOT NULL,
  `kodeTransaksi` varchar(255) NOT NULL,
  `jenisTransaksi` varchar(50) NOT NULL,
  `keterangan` text DEFAULT NULL,
  `total` decimal(15,2) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `agen`
--
ALTER TABLE `agen`
  ADD PRIMARY KEY (`idAgen`);

--
-- Indexes for table `detail_pembelian`
--
ALTER TABLE `detail_pembelian`
  ADD PRIMARY KEY (`idDetailPembelian`),
  ADD KEY `idPembelian` (`idPembelian`),
  ADD KEY `idProduk` (`idProduk`);

--
-- Indexes for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD PRIMARY KEY (`idDetailTransaksi`),
  ADD KEY `idTransaksi` (`idTransaksi`),
  ADD KEY `idDetailPembelian` (`idDetailPembelian`);

--
-- Indexes for table `kategori`
--
ALTER TABLE `kategori`
  ADD PRIMARY KEY (`idKategori`);

--
-- Indexes for table `pembelian`
--
ALTER TABLE `pembelian`
  ADD PRIMARY KEY (`idPembelian`),
  ADD KEY `idAgen` (`idAgen`);

--
-- Indexes for table `produk`
--
ALTER TABLE `produk`
  ADD PRIMARY KEY (`idProduk`),
  ADD KEY `idKategori` (`idKategori`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`idTransaksi`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `agen`
--
ALTER TABLE `agen`
  MODIFY `idAgen` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `detail_pembelian`
--
ALTER TABLE `detail_pembelian`
  MODIFY `idDetailPembelian` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  MODIFY `idDetailTransaksi` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `kategori`
--
ALTER TABLE `kategori`
  MODIFY `idKategori` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `pembelian`
--
ALTER TABLE `pembelian`
  MODIFY `idPembelian` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `produk`
--
ALTER TABLE `produk`
  MODIFY `idProduk` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=135;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `idTransaksi` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_pembelian`
--
ALTER TABLE `detail_pembelian`
  ADD CONSTRAINT `fk_detailpembelian_pembelian` FOREIGN KEY (`idPembelian`) REFERENCES `pembelian` (`idPembelian`),
  ADD CONSTRAINT `fk_detailpembelian_produk` FOREIGN KEY (`idProduk`) REFERENCES `produk` (`idProduk`);

--
-- Constraints for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD CONSTRAINT `fk_detailtransaksi_detailpembelian` FOREIGN KEY (`idDetailPembelian`) REFERENCES `detail_pembelian` (`idDetailPembelian`),
  ADD CONSTRAINT `fk_detailtransaksi_transaksi` FOREIGN KEY (`idTransaksi`) REFERENCES `transaksi` (`idTransaksi`);

--
-- Constraints for table `pembelian`
--
ALTER TABLE `pembelian`
  ADD CONSTRAINT `fk_pembelian_agen` FOREIGN KEY (`idAgen`) REFERENCES `agen` (`idAgen`);

--
-- Constraints for table `produk`
--
ALTER TABLE `produk`
  ADD CONSTRAINT `fk_produk_kategori` FOREIGN KEY (`idKategori`) REFERENCES `kategori` (`idKategori`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
