package com.hafez.password_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hafez.password_manager.databinding.CategoryListItemBinding;
import com.hafez.password_manager.models.Category;
import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    boolean hasHint = false;

    public CategorySpinnerAdapter(Context context, List<Category> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        CategoryListItemBinding viewBinding;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            viewBinding = CategoryListItemBinding.inflate(layoutInflater, parent, false);
        } else {
            viewBinding = CategoryListItemBinding.bind(convertView);
        }

        viewBinding.name.setText(getItem(position).getName());
        viewBinding.icon.setImageBitmap(getItem(position).getIcon());

        return viewBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
            @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public void setHint(Category hint) {
        if (hint != null) {
            super.add(hint);
            hasHint = true;
        }
    }

    @Override
    public int getCount() {
        // if there is a hint then it is always the last element
        return (hasHint) ? super.getCount() - 1 : super.getCount();
    }

    public int getHintPosition() {
        return (hasHint) ? getCount() : AdapterView.INVALID_POSITION;
    }
}