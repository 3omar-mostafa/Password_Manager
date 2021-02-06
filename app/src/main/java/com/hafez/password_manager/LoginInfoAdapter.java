package com.hafez.password_manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.hafez.password_manager.databinding.LoginInfoListItemBinding;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.LoginInfoAdapter.LoginInfoViewHolder;

public class LoginInfoAdapter extends ListAdapter<LoginInfoFull, LoginInfoViewHolder> {

    private static ItemCallback<LoginInfoFull> DIFF_CALLBACK = new ItemCallback<LoginInfoFull>() {
        @Override
        public boolean areItemsTheSame(@NonNull LoginInfoFull oldItem, @NonNull LoginInfoFull newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LoginInfoFull oldItem, @NonNull LoginInfoFull newItem) {
            return oldItem.equals(newItem);
        }
    };

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public LoginInfoAdapter() {
        super(DIFF_CALLBACK);
        onItemClickListener = null;
        onItemLongClickListener = null;
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
        LoginInfoFull loginInfo = getItem(position);
        holder.bind(loginInfo);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
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

            viewBinding.getRoot().setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(v, getItem(getLayoutPosition()));
                    return true;
                }
                return false;
            });

        }

        void bind(@NonNull LoginInfoFull loginInfo) {
            viewBinding.icon.setImageBitmap(loginInfo.getIcon());
            viewBinding.username.setText(loginInfo.getUsername());
            viewBinding.password.setText(loginInfo.getPassword());
        }

    }

    public interface OnItemClickListener {

        void onItemClick(View view, @NonNull LoginInfoFull loginInfo);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(View view, @NonNull LoginInfoFull loginInfo);
    }

}
