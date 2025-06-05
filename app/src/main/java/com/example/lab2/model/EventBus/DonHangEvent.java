package com.example.lab2.model.EventBus;

import com.example.lab2.model.DonHang;

public class DonHangEvent {
    DonHang donHang;

    public DonHangEvent(DonHang donHang) {
        this.donHang = donHang;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }
}
