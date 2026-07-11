package com.example.musiclo.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musiclo.R;
import com.example.musiclo.models.NguoiDung;

import java.util.ArrayList;

public class NguoiDungAdapter extends ArrayAdapter<NguoiDung> {
    Activity context;
    int resource;
    ArrayList<NguoiDung> listNguoiDung;

    public NguoiDungAdapter(Activity context, int resource, ArrayList<NguoiDung> listNguoiDung) {
        super(context, resource, listNguoiDung);
        this.context = context;
        this.resource = resource;
        this.listNguoiDung = listNguoiDung;
    }

    @Override
    public int getCount() {
        return this.listNguoiDung.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View customView = layoutInflater.inflate(resource, null);

        TextView tvHoTen = customView.findViewById(R.id.tvHoTen);
        TextView tvEmail = customView.findViewById(R.id.tvEmail);
        TextView tvVaiTro = customView.findViewById(R.id.tvVaiTro);
        Button btnDoiVaiTro = customView.findViewById(R.id.btnDoiVaiTro);
        Button btnXoa = customView.findViewById(R.id.btnXoa);

        NguoiDung nguoiDung = listNguoiDung.get(position);

        if (nguoiDung.getEmail() != null) {
            tvEmail.setText(nguoiDung.getEmail());
        } else {
            tvEmail.setText("");
        }

        String hoTen = nguoiDung.getHoTen();
        if (hoTen != null && !hoTen.isEmpty()) {
            tvHoTen.setText(hoTen);
        } else {
            tvHoTen.setText("Chưa cập nhật");
        }

        String vaiTroHienTai = nguoiDung.getVaiTro();
        if ("admin".equals(vaiTroHienTai)) {
            tvVaiTro.setText("Vai trò: Admin");
            btnDoiVaiTro.setText("Đổi thành User");
        } else {
            tvVaiTro.setText("Vai trò: Người dùng");
            btnDoiVaiTro.setText("Đổi thành Admin");
        }

        btnDoiVaiTro.setTag(nguoiDung);
        btnDoiVaiTro.setOnClickListener((View.OnClickListener) context);

        btnXoa.setTag(nguoiDung);
        btnXoa.setOnClickListener((View.OnClickListener) context);

        return customView;
    }
}
