-- --------------------------------------------------------
-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 18, 2025 at 04:38 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30
-- --------------------------------------------------------

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

-- Tạo Database (nếu chưa có) và chọn
CREATE DATABASE IF NOT EXISTS `dataonline`
  CHARACTER SET utf8
  COLLATE utf8_unicode_ci;
USE `dataonline`;

-- --------------------------------------------------------
-- Table structure for table `user`
-- --------------------------------------------------------
CREATE TABLE `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `pass` VARCHAR(255) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `avatar_url` TEXT DEFAULT NULL,
  `mobile` VARCHAR(15) NOT NULL,
  `gender` ENUM('M','F','O') NOT NULL DEFAULT 'O',
  `birthdate` DATE DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8
  COLLATE=utf8_unicode_ci;


INSERT INTO `user`
  (`id`, `email`,                  `pass`, `username`,      `name`,            `avatar_url`, `mobile`,      `gender`, `birthdate`)
VALUES
  (4, 'duy@gmail.com',              '12345', 'duy anh',      'duy anh',          NULL,         '0343463429', 'O',      NULL),
  (5, 'bi@gmail.com',               '1234',  'Bi Tran',      'Bi Tran',          NULL,         '0987865467', 'O',      NULL),
  (6, 'trananhduyvlm@gmail.com',    '1234',  'duy duy anh',  'duy duy anh',      NULL,         '0989825987', 'O',      NULL);




-- AUTO_INCREMENT for table `user`
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;


-- --------------------------------------------------------
-- Table structure for table `sanpham`
-- --------------------------------------------------------
CREATE TABLE `sanpham` (
  `id` int(11) NOT NULL,
  `tensanpham` varchar(100) NOT NULL,
  `hinhanh` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dumping data for table `sanpham`
INSERT INTO `sanpham` (`id`, `tensanpham`, `hinhanh`) VALUES
(1, 'Điện thoại', 'data:image/png;base64,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'),
(2, 'Laptop', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAw1BMVEX///9EmdJswO+lpaUZFxgAAAD7+/tSUlKnp6cQDQ+dnZ3CwsKIiIh4eHiCgoKzs7OQkJAYJTEcM0MZFRN21PFKpuZvyPJmZmZHo9wVGSEaFhgIAAAaGBdzz+VEn91tbW1eXl4nJictLS1yxvW5ubmWlpYXIzRitehDl9Rsv/LDw8Pi4uLW1tZ4zPlEmc49j8lHR0fw8PAdHh44NzgZFxwYFxMeFREdKzR6zNd41ORgveBLqMdbrOFGl9ZluvE2i8hTptQge/j2AAAGEklEQVR4nO2dfVejOBTGaQoWsAoOs7bLsNNWYDS21bW2s2+ddb7/p9okOHvUA07JJXDpub96/KeW8PDc3BtSTCyLIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAgMnJxY1jILhi6YYSmueDVx8CBb6ivMrpw+cJXpurhyPDuxbTuWL81ftr2xPbsCzxPvFg3oHV4gTtDxVpoKQ2cTV51cHRalCIGLRaX4OmycUEvfycnESxpo37uaTUqYPXqL2eyqCYmJM5FJQ0PiSATpNrELmXXcFB8Sn0oUtl2l0BEKH70Ehr2NYtsbaQlUCu042W6LQ9Vpd7fdyp4isWPvqhT1hicvXKzPNo53W5DCbbxLvka1iaPdLo6k1ki8vDIi8bYXqfcBRJttEoEUxuI0/vgiuajLr60QyX4EUriLPvz5mwZ//f152gL/fPkqkxmkHybRh4+c8Zownl4/DVrgl4uoGYX1Sa/ve6RQQyBL+bErZPymHwoTfYU98VBfIWvNQ1i10FbI+6JQux/ytA2BXXrI0mk/FOrn0iNXKMYI6b6NZNpZlPZIISBKWxm2dZlpGOuHQv1qwdIjV8jYWpR841W/0yhdD27uTAvs1sN03wuFAA9bSabdeigVmu6InSoU5eLOeJx2Ww/F3UUPFEI8TO/M3yJ27OG+DwpBHl5/O/JMk7L7HvRDSJSKkWkPRm0QD3l65GMaoXDfA4Wgit/CuK1jhWJUgz+XgjKNGNXgVwjzMJ3iVwjykJvviB17yM1/AdV1lDLjX150nWnMT+1376HpjtjxmKaFSdOuPeRsbVZg5wqLu2CTNbHzTMNSw49kdO8hS28GJqdruvdQfo2IWyHcQ1EvjlshN1svEEQpNzusQeCh4WENAg8Nj74xeMjWJm+DMSjkqclHFlBEqdEwxeCh2VyDw0OTt1AIPJRPwa8HxiQiUFhgbGYYSZQanBnG4qG5XIPFQ3MFA4uH5h42RaPQWE9EE6V8bchENB5yU8/TovFQmPgdqcJmPDQ3+41FocRMOsUTpcXzQxgVNuehGRMxeWimJqLyUIxOn74dt0KWThEqbDJKTfz3MzIPDSQbZB4amM9A5SGX/7L37zErVDQdp+iiVD3A32RVxOchb3jeDZ+HvOFZKXweSokNCsSokMl/vWzORXxRKknZzaCpp/hxesgur5+ayjY4PSwessGi0IiHnDU2uYhUoSD9LiXCYxVrlAqFqmbAQxWvh0LiXROBitdDqbGJOynEHkqJDbiIXGGRbjpWaDBKpcQpVCJuD1kDN8TIPWTqG35QUUTvIXjmBr+HQuL6872619Dw8kYqjGFr0Nq25uqedTTu9SO1AQ/tHx7WXaT1sJVc5cQNT/lAdzlXoTCBrQWtFF6aJb28XLPPeuwvoh1+hZKPfKo1Dp9exAlwhVbb/tTS+v+ftFALggMVtoTeet6xWiq+HwohHLPCRC36D8s0tk4A/Wi+5gdqt/QsUleh5Tsb8EUudgZ49xe4jY3j6wm0lgtHt1GdjTG0N9NwFro7zVireUuVAsb80E1YbrPT7CW32WqZ56Em+Xg0P5zRWL+hfLk6fX3mp9ltqcIg9N+Q+4E2mTufHc7czfSbEuf5hjAoVZj5rjuUP88flLsxlW/VdAiuOyrbfKWCEaQptXXUix2g3KFfvj/SrV+cmT+RV3UyBjQqWwnmNRTOg2KHK93mXn5WnLdfHqWrXL0b5I+/Cx7PAm2BqtEgPNzEURjo6ytpOy/PPctcBekwUL01CAAXtTDx/OBEcw6z8HW3FIGal5cPqVCd2/MfAuNGHMc/Pww/cIdDwPWcnb1EHKxCoVWcmxvKOu85E1CUFsc6MBkCYkUSvi6P0ptygdZp7qo+O5ZlZpy7kMvaJm/29wuqUqllPYRK0f9RChZYuTOgxl/95Agv9wt0w4cKhVbgK1lFiy6sHrbLqxP1q4JU1gv4josIqKgVimz8dvzTQ8bvbfj4MD47MMHj5Wxc2QslJ+ezcb+Znf/kfv8heF0++8YseNdBxfK0z2jf7RMEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAE'),

(3, 'Trang chủ', 'https://cdn.pixabay.com/photo/2017/09/12/06/26/home-2741413_1280.png'),
(4, 'Thông tin', 'https://thumb.ac-illust.com/1f/1fd26e64f925f9079a96374ae41b3fb5_t.jpeg'),
(5, 'Liên hệ', 'https://sie.tlu.edu.vn/portals/0/2017/1_files/icon_phone.png'),
(6, 'Lịch sử đơn hàng', 'https://tiemtraannhien.vn/wp-content/uploads/2023/07/icon-done.png'),
(7, 'Hồ Sơ của tôi', 'https://images.rawpixel.com/image_png_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTAxL3JtNjA5LXNvbGlkaWNvbi13LTAwMi1wLnBuZw.png');

ALTER TABLE `sanpham`
  ADD PRIMARY KEY (`id`);

-- AUTO_INCREMENT for table `sanpham`
ALTER TABLE `sanpham`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;


-- --------------------------------------------------------
-- Table structure for table `sanphammoi`
-- --------------------------------------------------------
CREATE TABLE `sanphammoi` (
  `id` int(11) NOT NULL,
  `tensp` varchar(250) NOT NULL,
  `giasp` varchar(100) NOT NULL,
  `hinhanh` text NOT NULL,
  `mota` text NOT NULL,
  `loai` int(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dumping data for table `sanphammoi`
INSERT INTO `sanphammoi` (`id`, `tensp`, `giasp`, `hinhanh`, `mota`, `loai`) VALUES
(1, 'Laptop Apple MacBook Air 13 inch M1 8GB/256GB (MGN63SA/A)', '16990000', 'https://cdn.tgdd.vn/Products/Images/44/231244/grey-1-750x500.jpg', 'Phiếu mua hàng mua sản phẩm giá niêm yết từ 5 triệu thuộc các nhóm TAI NGHE - LOA hãng HARMAN KARDON, JBL,MONSTER, KLIPSCH, MARSHALL, SONY; Smartwatch hãng HUAWEI, GARMIN, IMOO trị giá 500.000đ', 2),
(2, 'Laptop Lenovo Ideapad Slim 3 15IAH8 i5 12450H/16GB/512GB/Win11 (83ER000EVN)', '18900000', 'https://cdn.tgdd.vn/Products/Images/44/313333/lenovo-ideapad-slim-3-15iah8-i5-83er00evn-glr-3-750x500.jpg', 'Sản phầm tốt mình test độ mượt thấy ok 9,5/10mọi người review pin máy này nhanh hết nhưng mình test thấy ok chắc là do cách mọi người chơi game với độ sáng màn hình nữa mình test 1h15 hết 20% pin 9/10 Thái độ nhân viên ok mình mua ở Siêu thị 237 Nguyễn An Ninh, KP. Bình Minh 2, P. Dĩ An, TP. Dĩ An, T. Bình Dương Ngay ngã ba Nguyễn Tri PhươngNguyễn An Ninh ngày 12/2/2025 bạn nhân viên Nam tư vấn cho mình rất ok gửi lời cảm ơn bạn 10/10.TGDĐ có nhiều KM mình có dc voucher bên app quà tặng vip nên rất ok ,có giảm giá cho HSSV. 10/10Mình phân vân giữa Cell phones và TGDĐ nhưng nghe mọi người rivew TGDĐ chính sách bảo hành rõ ràng hơn nên mình đã thấy mình lựa chọn đúng.', 2),
(3, 'Điện thoại Samsung Galaxy S25 Ultra 5G 12GB/256GB', '7900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/42/333347/samsung-galaxy-s25-ultra-bac-4-638747791473573083-750x500.jpg', 'Tặng Phiếu mua hàng mua đơn hàng từ 250,000đ các sản phẩm miếng dán kính, cáp, sạc, sạc dự phòng, ốp lưng, loa di động, loa vi tính và đồng hồ trị giá 50,000đ.', 1),
(4, 'Điện thoại iPhone 16 Pro Max 256GB', '6900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/42/329149/iphone-16-pro-max-titan-sa-mac-1-638638962337813406-750x500.jpg', 'Phiếu mua hàng mua sản phẩm giá niêm yết từ 5 triệu thuộc các nhóm TAI NGHE - LOA hãng HARMAN KARDON, JBL,MONSTER, KLIPSCH, MARSHALL, SONY; Smartwatch hãng HUAWEI, GARMIN, IMOO trị giá 500.000đ', 1),
(5, 'Laptop HP 15 fc0085AU R5 7430U/16GB/512GB/Win11 (A6VV8PA)', '16900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/327098/hp-15-fc0085au-r5-a6vv8pa-glr-2-638624254802444044-750x500.jpg', '(*) Giá hoặc khuyến mãi không áp dụng trả chậm lãi suất đặc biệt (0%, 0.5%, 1%, 1.5%, 2%)\r\n\r\nGiao hàng nhanh chóng (tuỳ khu vực)\r\nMỗi số điện thoại chỉ mua 3 sản phẩm trong 1 tháng', 2),
(6, 'Laptop Dell Latitude 7420 - Core i7 1165G7 Ram 16G SSD 256G 14 inch', '18900000', 'https://bizweb.dktcdn.net/thumb/large/100/446/400/products/laptop-dell-latitude-7420-1-gia-loc.jpg?v=1686626945173', ' Máy nhập khẩu US, nguyên zin 100%\r\n• Bộ xử lý Intel thế hệ 11 Tiger Lake\r\n• Màn hình 14 inch cảm ứng IPS sáng đẹp, sắc nét\r\n• Đĩa cứng NVMe tốc độ truy xuất cao\r\n\r\nBảo hành\r\n• 06 tháng\r\n\r\nKhuyến mãi\r\n• Túi chống sốc\r\n• Chuột không dây', 2),
(7, 'Laptop HP Pavilion 15 eg2081TU i5 1240P/16GB/512GB/Win11 (7C0Q4PA)', '19800000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/44/309565/hp-pavilion-15-eg2081tu-i5-7c0q4pa-2-180x125.jpg', 'Máy mình mới mua ở Điện máy Xanh 254 Hùng Vương, Phường 03, Thành phố Tân An, Tỉnh Long An máy đẹp, chưa biết sử dụng ổn không nhưng mà anh nhân viên siêu siêu nhiệt tình, mọi người có ghé chi nhánh này mua nhớ kiếm anh Minh Nhựt nha, ảnh dễ thương lắm lắm luôn, 1001 điểm, không có hài lòng nhất chỉ có hài lòng hơn', 2),
(8, 'Laptop Acer Nitro V 15 ANV15 41 R1JY R5 6600H/16GB/512GB/6GB RTX3050/165Hz/Win11', '25900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/333427/acer-nitro-v-15-anv15-41-r1jy-r5-nhqpfsv001-2-638717762795531290-180x125.jpg', 'Phiếu mua hàng mua AirPods và Apple Watch mệnh giá 300.000 VND\r\n\r\n4\r\nPhiếu mua hàng mua Xiaomi Watch S3 trị giá 700.000đ\r\n\r\n6\r\nTặng thẻ mua sắm GotIT trị giá 200.000 và cơ hội nhận thêm quà trị giá đến 3.500.000 (Xem chi tiết)', 2),
(9, 'Laptop MacBook Air 13 inch M2 16GB/256GB', '19900000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/44/289472/apple-macbook-air-m2-2022-16gb-256gb-1-180x125.jpg', 'Tặng Phiếu mua hàng 200,000đ, áp dụng mua Balo, Túi chống sốc có giá trị đơn hàng từ 500,000đ trở lên\r\n\r\n3\r\nPhiếu mua hàng mua sản phẩm giá niêm yết từ 5 triệu thuộc các nhóm Tai nghe Loa hãng HARMAN KARDON, JBL,MONSTER, KLIPSCH, MARSHALL, SONY; Smartwatch hãng HUAWEI, GARMIN, IMOO trị giá 500,000đ', 2),
(10, 'Laptop Lenovo Gaming LOQ Essential 15IAX9E i5 12450HX/16GB/512GB/4GB RTX2050/144Hz/Win11', '16900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/332365/lenovo-loq-15iax9e-i5-83lk000bvn-glr-2-638687557387680289-180x125.jpg', 'Nhân viên chăm sóc khách hàng/kỹ thuật đã liên hệ tôi và hỗ trợ xử lý Nhanh chóng Nhiệt tìnhHy vọng sau khi qua xử lý máy sẽ hoạt động ổn định. Xin cảm ơn', 2),
(11, 'Điện thoại TCL 406s 4GB/64GB', '1890000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/42/324994/tcl-406s-grey-2-180x125.jpg', 'Tặng Phiếu mua hàng mua đơn hàng từ 250,000đ các sản phẩm miếng dán kính, cáp, sạc, sạc dự phòng, ốp lưng, loa di động và đồng hồ trị giá 50,000đ.', 1),
(12, 'Điện thoại iPhone 16 Pro 128GB', '29990000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/42/329143/iphone-16-pro-titan-tu-nhien-2-638638980487068739-180x125.jpg', 'Phiếu mua hàng AirPods, Apple Watch, Macbook mệnh giá 500,000đ\r\n\r\n2\r\nPhiếu mua hàng mua sản phẩm giá niêm yết từ 5 triệu thuộc các nhóm Tai nghe Loa hãng HARMAN KARDON, JBL,MONSTER, KLIPSCH, MARSHALL, SONY; Smartwatch hãng HUAWEI, GARMIN, IMOO trị giá 500,000đ', 1),
(13, 'Điện thoại realme C75 8GB/128GB', '5690000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/42/332234/realme-c75-red-4-638714158129947747-180x125.jpg', 'Bộ sản phẩm gồm: Hộp, Sách hướng dẫn, Cây lấy sim, Ốp lưng, Cáp Type C, Củ sạc nhanh rời đầu Type A, Miếng dán màn hình', 1),
(14, 'Điện thoại Samsung Galaxy S25 Ultra 5G 12GB/256GB', '15900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/42/333347/samsung-galaxy-s25-ultra-bac-4-63874779147357383-750x500.jpg', 'Tặng Phiếu mua hàng mua đơn hàng từ 250,000đ các sản phẩm miếng dán kính, cáp, sạc, sạc dự phòng, ốp lưng, loa di động và đồng hồ trị giá 50,000đ.', 1),
(15, 'Samsung Galaxy Z Fold6 12GB 256GB', '5990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:58:58/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-z-fold-6-xanh_5_.png', 'Giảm 3 triệu khi mua combo...', 1),
(16, 'TECNO CAMON 30S 8GB 256GB', '7900000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:58:58/q:90/plain/https://cellphones.com.vn/media/catalog/product/d/i/dien-thoai-tecno-camon-30s_20_.png', 'Sale "She" mê...', 1),
(17, 'Xiaomi Redmi Note 14 6GB 128GB', '3900000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:58:58/q:90/plain/https://cellphones.com.vn/media/catalog/product/d/i/dien-thoai-xiaomi-redmi-note-14_2__2.png', 'Bảo hành 18 tháng...', 1),
(18, 'iPhone 16', '19700000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:300:0/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-xanh-luu-ly.png', 'Giảm 200.000đ...', 1),
(19, 'Xiaomi 15 5G 12GB 256GB', '8900000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:200:0/q:90/plain/https://cellphones.com.vn/media/catalog/product/d/i/dien-thoai-xiaomi-15_11_.png', 'CellphoneS chuyên thu cũ...', 1),
(20, 'Xiaomi Redmi Note 14 6GB 128GB', '9900000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:58:58/q:90/plain/https://cellphones.com.vn/media/catalog/product/d/i/dien-thoai-xiaomi-redmi-note-14_2__2.png', 'Redmi Note 14, Cáp USB Type C...', 1),
(21, 'Laptop HP Probook 450 G10 i7 1355U/16GB/512GB/Win11', '15900000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/44/327408/hp-probook-450-g10-i7-9h8h1pt-1-180x125.jpg', 'Phiếu mua hàng trị giá 150.000đ...', 2),
(22, 'Laptop Acer Aspire 16 AI A16 71M 71U7 Ultra', '23900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/333425/acer-aspire-16-ai-a16-71m-71u7-ultra-7-nxj4ysv002-2-638754915930943532-180x125.jpg', 'Tặng Balo Laptop Acer...', 2),
(23, 'Laptop HP 240 G9 i5 1235U/16GB/512GB/Win11', '21900000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/44/328814/hp-240-g9-i5-ag2j7at-2-180x125.jpg', 'Phiếu mua hàng...', 2),
(24, 'Laptop MacBook Air 13 inch M2 16GB/256GB', '16800000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/44/289472/apple-macbook-air-m2-2022-16gb-256gb-1-180x125.jpg', 'Phiếu...', 2),
(25, 'Laptop MSI Gaming Thin 15 B12UCX i5', '25900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/326124/msi-thin-15-b12ucx-i5-2046vn-1-638718440275960644-180x125.jpg', 'Phiếu...', 2),
(26, 'Laptop Asus Gaming Vivobook K3605ZF i5', '16900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/328237/asus-vivobook-k3605zf-i5-rp745w-glr-2-638611359324594835-180x125.jpg', 'Phiếu...', 2),
(27, 'Laptop Acer Gaming Aspire 5 A515 58GM 51LB i5', '23600000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/44/314630/acer-aspire-5-a515-58gm-51lb-i5-nxkq4sv002-glr-2-180x125.jpg', 'Phiếu...', 2),
(28, 'Laptop Dell Inspiron 15 3520 i5', '15900000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/325242/dell-inspiron-15-3520-i5-n5i5052w1-glr-2-638629598139234508-180x125.jpg', 'Phiếu...', 2),
(29, 'Laptop MSI Gaming GF63 Thin 12VE i5', '19900000', 'https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_75,s_100x100/https://cdn.tgdd.vn/Products/Images/44/326861/msi-gf63-thin-12ve-i5-460vn-2-180x125.jpg', 'Phiếu...', 2),
(30, 'Laptop Acer Aspire 7 A715 76 53PJ i5', '15600000', 'https://cdnv2.tgdd.vn/mwg-static/tgdd/Products/Images/44/332578/acer-aspire-7-a715-76-53pj-i5-nhqgesv007-2-638766081277466558-180x125.jpg', 'Phiếu...', 2);

-- Indexes for table `sanphammoi`
ALTER TABLE `sanphammoi`
  ADD PRIMARY KEY (`id`);

-- AUTO_INCREMENT for table `sanphammoi`
ALTER TABLE `sanphammoi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;


-- --------------------------------------------------------
-- Table structure for table `donhang`
-- --------------------------------------------------------
CREATE TABLE `donhang` (
  `id` int(11) NOT NULL,
  `iduser` int(11) NOT NULL,
  `diachi` text NOT NULL,
  `sodienthoai` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `soluong` int(11) NOT NULL,
  `tongtien` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dumping data for table `donhang`
INSERT INTO `donhang` (`id`, `iduser`, `diachi`, `sodienthoai`, `email`, `soluong`, `tongtien`) VALUES
(1, 4, 'Vinh long', '', 'duy@gmail.com', 3, '51700000'),
(2, 4, 'Vinh long', '', 'duy@gmail.com', 3, '51700000'),
(3, 4, 'ca mau\n', '', 'duy@gmail.com', 2, '79600000'),
(4, 4, 'nha trang', '', 'duy@gmail.com', 1, '16900000'),
(5, 4, 'ca mau', '', 'duy@gmail.com', 1, '23600000');

-- Indexes for table `donhang`
ALTER TABLE `donhang`
  ADD PRIMARY KEY (`id`);

-- AUTO_INCREMENT for table `donhang`
ALTER TABLE `donhang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;


-- --------------------------------------------------------
-- Table structure for table `chitietdonhang`
-- --------------------------------------------------------
CREATE TABLE `chitietdonhang` (
  `iddonhang` int(11) NOT NULL,
  `idsp` int(11) NOT NULL,
  `soluong` int(11) NOT NULL,
  `gia` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Dumping data for table `chitietdonhang`
INSERT INTO `chitietdonhang` (`iddonhang`, `idsp`, `soluong`, `gia`) VALUES
(1, 29, 1, '19900000'),
(1, 28, 2, '15900000'),
(2, 29, 1, '19900000'),
(2, 28, 2, '15900000'),
(3, 29, 2, '39800000'),
(4, 26, 1, '16900000'),
(5, 27, 1, '23600000');

-- Không có PRIMARY KEY / AUTO_INCREMENT cho table chi tiết
    
