package com.example.musiclo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclo.R;
import com.example.musiclo.models.UserModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<UserModel> userList;
    private OnRoleChangeListener onRoleChangeListener;
    private OnDeleteUserListener onDeleteUserListener;

    public interface OnRoleChangeListener {
        void onRoleChange(UserModel user, String newRole);
    }

    public interface OnDeleteUserListener {
        void onDeleteUser(UserModel user);
    }

    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setOnRoleChangeListener(OnRoleChangeListener listener) {
        this.onRoleChangeListener = listener;
    }

    public void setOnDeleteUserListener(OnDeleteUserListener listener) {
        this.onDeleteUserListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.tvFullName.setText(user.getFullName() != null ? user.getFullName() : "");
        holder.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        holder.tvRole.setText("Role: " + (user.getRole() != null ? user.getRole() : "user"));

        if ("admin".equals(user.getRole())) {
            holder.btnChangeRole.setText("Đổi thành User");
        } else {
            holder.btnChangeRole.setText("Đổi thành Admin");
        }

        holder.btnChangeRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRoleChangeListener != null) {
                    String newRole = "admin".equals(user.getRole()) ? "user" : "admin";
                    onRoleChangeListener.onRoleChange(user, newRole);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteUserListener != null) {
                    onDeleteUserListener.onDeleteUser(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName;
        TextView tvEmail;
        TextView tvRole;
        Button btnChangeRole;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnChangeRole = itemView.findViewById(R.id.btnChangeRole);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
