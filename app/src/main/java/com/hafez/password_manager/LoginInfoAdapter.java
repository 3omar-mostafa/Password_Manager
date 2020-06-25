package com.hafez.password_manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.hafez.password_manager.databinding.LoginInfoListItemBinding;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.LoginInfoAdapter.LoginInfoViewHolder;

public class LoginInfoAdapter extends ListAdapter<LoginInfo, LoginInfoViewHolder> {

    private static ItemCallback<LoginInfo> DIFF_CALLBACK = new ItemCallback<LoginInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull LoginInfo oldItem, @NonNull LoginInfo newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LoginInfo oldItem, @NonNull LoginInfo newItem) {
            return oldItem.equals(newItem);
        }
    };

    private OnItemClickListener onItemClickListener;

    public LoginInfoAdapter() {
        super(DIFF_CALLBACK);
        onItemClickListener = null;
    }

    @NonNull
    @Override
    public LoginInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        LoginInfoListItemBinding viewBinding = LoginInfoListItemBinding
                .inflate(layoutInflater, parent, false);

        return new LoginInfoViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LoginInfoViewHolder holder, int position) {
        LoginInfo loginInfo = getItem(position);
        holder.bind(loginInfo);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class LoginInfoViewHolder extends ViewHolder {

        private LoginInfoListItemBinding viewBinding;

        LoginInfoViewHolder(@NonNull LoginInfoListItemBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;

            viewBinding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, getItem(getLayoutPosition()));
                }
            });

        }

        void bind(@NonNull LoginInfo loginInfo) {
            viewBinding.icon.setImageResource(loginInfo.getIconResourceId());
            viewBinding.username.setText(loginInfo.getUsername());
            viewBinding.password.setText(loginInfo.getPassword());
        }

    }

    public interface OnItemClickListener {

        void onItemClick(View view, @NonNull LoginInfo loginInfo);
    }

}
