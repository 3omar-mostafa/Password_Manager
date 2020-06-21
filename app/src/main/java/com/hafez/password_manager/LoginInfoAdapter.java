package com.hafez.password_manager;

import android.view.LayoutInflater;
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
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull LoginInfo oldItem, @NonNull LoginInfo newItem) {
            return oldItem.equals(newItem);
        }
    };

    public LoginInfoAdapter() {
        super(DIFF_CALLBACK);
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


    static class LoginInfoViewHolder extends ViewHolder {

        private LoginInfoListItemBinding viewBinding;

        LoginInfoViewHolder(@NonNull LoginInfoListItemBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }

        void bind(@NonNull LoginInfo loginInfo) {
            viewBinding.icon.setImageResource(loginInfo.getIconResourceId());
            viewBinding.username.setText(loginInfo.getUsername());
            viewBinding.password.setText(loginInfo.getPassword());
        }

    }
}
