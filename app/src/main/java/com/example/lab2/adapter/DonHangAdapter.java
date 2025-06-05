package com.example.lab2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.Interface.ItemClickListener;
import com.example.lab2.R;
import com.example.lab2.model.DonHang;
import com.example.lab2.model.EventBus.DonHangEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> listdonhang;

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_don_hang,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        holder.txtdonhang.setText(donHang.getId() + " ");
        System.out.println(donHang.getStatus());
        holder.txttrangthai.setText(trangThaiDonHang(donHang.getStatus()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.recyclerView.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        ChitietAdapter chitietAdapter = new ChitietAdapter(context, donHang.getItem());
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(chitietAdapter);
        holder.recyclerView.setRecycledViewPool(viewPool);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(isLongClick) {
                    EventBus.getDefault().postSticky(new DonHangEvent(donHang));
                }
            }
        });

    }

    private String trangThaiDonHang(int trangthai) {
        String res = "";
        if(trangthai == 0) {
            res = "Đơn hàng đang được xử lý";
        }else if(trangthai == 1) {
            res = "Đơn hàng đã chấp nhận";
        }else if(trangthai == 2) {
            res = "Đơn hàng đã giao cho đơn vị vận chuyển";
        }else if(trangthai == 3) {
            res = "Thành công";
        }else{
            res = "Đơn hàng đã hủy";
        }
        return res;
    }

    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView txtdonhang, txttrangthai;
        RecyclerView recyclerView;
        ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            txttrangthai = itemView.findViewById(R.id.tinhtrang);
            recyclerView = itemView.findViewById(R.id.recycleview_chitiet);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return false;
        }
    }
}
