<?php
include 'connect.php';
$tensp = $_POST['tensp'];
$gia = $_POST['gia'];
$hinhanh = $_POST['hinhanh'];
$mota = $_POST['mota'];
$loai = $_POST['loai'];

$query = 'INSERT INTO `sanphammoi`(`tensp`, `giasp`, `hinhanh`, `mota`, `loai`) VALUES ("'.$tensp.'",'.$gia.',"'.$hinhanh.'","'.$mota.'","'.$loai.'")';
$data = mysqli_query($conn, $query);
if ($data == true) {
    $arr = [
        'success' => true,
        'message' => "thanh cong"
    ];
} else {
    $arr = [
        'success' => false,
        'message' => "khong thanh cong"
    ];
}

print_r(json_encode($arr));
