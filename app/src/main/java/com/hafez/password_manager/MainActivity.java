package com.hafez.password_manager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.hafez.password_manager.databinding.ActivityMainBinding;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.view_models.LoginInfoViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        LoginInfoViewModel viewModel = getViewModel(new LoginInfoViewModel.Factory());

        LoginInfoAdapter adapter = new LoginInfoAdapter();
        viewBinding.loginInfoRecyclerView.setAdapter(adapter);
        viewBinding.loginInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getLoginInfoLiveDataList()
                .observe(this, loginInfoList -> adapter.submitList(loginInfoList));

        adapter.setOnItemClickListener((view, loginInfo) -> {
            Intent intent = new Intent(MainActivity.this, AddEditLoginInfoActivity.class);
            intent.putExtra(AddEditLoginInfoActivity.ARGUMENT_LOGIN_INFO_ID, loginInfo.getId());
            startActivity(intent);
        });

        adapter.setOnItemLongClickListener((view, loginInfo) -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData data = ClipData.newPlainText(null, loginInfo.getPassword());
                clipboard.setPrimaryClip(data);
                Toast.makeText(this, R.string.password_copied, Toast.LENGTH_SHORT).show();
            }
        });

        viewBinding.add.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditLoginInfoActivity.class);
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelperCallback(viewModel, viewBinding.add, adapter))
                .attachToRecyclerView(viewBinding.loginInfoRecyclerView);

    }

    /**
     * The real usage of this method is in testing, to have the ability to inject mocked view model
     *
     * @param factory Factory to create view model with it
     *
     * @return the created view model
     */
    protected LoginInfoViewModel getViewModel(ViewModelProvider.Factory factory) {
        return new ViewModelProvider(this, factory).get(LoginInfoViewModel.class);
    }

    private static class ItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

        LoginInfoAdapter adapter;
        LoginInfoViewModel viewModel;
        View snackbarAnchorView;

        /**
         * It is used to store where the movement started, The end is not stored because we can get
         * it from {@link ViewHolder#getAdapterPosition()}
         */
        int startMovePosition = AdapterView.INVALID_POSITION;

        public ItemTouchHelperCallback(LoginInfoViewModel viewModel, View snackbarAnchorView,
                LoginInfoAdapter adapter) {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.viewModel = viewModel;
            this.snackbarAnchorView = snackbarAnchorView;
            this.adapter = adapter;
        }


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder,
                @NonNull ViewHolder target) {

            viewHolder.itemView.setElevation(25); // Draw shadow under the moving view

            int oldPosition = viewHolder.getAdapterPosition();
            int newPosition = target.getAdapterPosition();

            if (startMovePosition == AdapterView.INVALID_POSITION) {
                startMovePosition = oldPosition;
            }

            List<LoginInfoFull> loginInfoList = new ArrayList<>(adapter.getCurrentList());

            reorderListPositions(loginInfoList, oldPosition, newPosition);

            adapter.submitList(loginInfoList);

            return true;
        }


        private static void swapLoginInfoIds(@NonNull LoginInfoFull loginInfo_1,
                @NonNull LoginInfoFull loginInfo_2) {
            long temp = loginInfo_1.getId();
            loginInfo_1.setId(loginInfo_2.getId());
            loginInfo_2.setId(temp);
        }


        private void reorderListPositions(List<LoginInfoFull> loginInfoList, int oldPosition,
                int newPosition) {

            if (oldPosition < newPosition) {
                for (int i = oldPosition; i < newPosition; i++) {
                    Collections.swap(loginInfoList, i, i + 1);
                    // Reordering is done by replacing ids, smaller id is on the top
                    swapLoginInfoIds(loginInfoList.get(i), loginInfoList.get(i + 1));
                }
            } else {
                for (int i = oldPosition; i > newPosition; i--) {
                    Collections.swap(loginInfoList, i, i - 1);
                    // Reordering is done by replacing ids, smaller id is on the top
                    swapLoginInfoIds(loginInfoList.get(i), loginInfoList.get(i - 1));
                }
            }
        }


        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            int endMovePosition = viewHolder.getAdapterPosition();
            if (startMovePosition != AdapterView.INVALID_POSITION) {
                viewModel.reorderLoginInfoPositions(startMovePosition, endMovePosition);
                startMovePosition = AdapterView.INVALID_POSITION;
            }
            viewHolder.itemView.setElevation(0);
        }

        @Override
        public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {
            LoginInfoFull loginInfo = adapter.getCurrentList().get(viewHolder.getAdapterPosition());
            viewModel.deleteLoginInfo(loginInfo);

            Snackbar.make(snackbarAnchorView, R.string.item_deleted_successfully, 10_000)
                    .setAnchorView(snackbarAnchorView)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                    .setAction(R.string.undo, v -> viewModel.insertLoginInfo(loginInfo))
                    .show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                @NonNull ViewHolder viewHolder, float dX, float dY, int actionState,
                boolean isCurrentlyActive) {

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            int swipeDirection = 0; // 0 means view is unSwiped

            if (dX > 0) {
                swipeDirection = ItemTouchHelper.RIGHT;
            } else if (dX < 0) {
                swipeDirection = ItemTouchHelper.LEFT;
            }

            if (swipeDirection != 0) {
                viewHolder.itemView.setElevation(25); // Draw shadow under the swiped view
                drawBackground(c, viewHolder.itemView);
                drawDeleteIcon(c, viewHolder.itemView, swipeDirection);
            }

        }

        private void drawDeleteIcon(@NonNull Canvas c, @NonNull View itemView, int swipeDirection) {

            Drawable icon = itemView.getContext().getDrawable(R.drawable.action_delete);

            int iconMargin, iconTop, iconBottom, iconLeft = 0, iconRight = 0;

            iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            iconBottom = iconTop + icon.getIntrinsicHeight();

            if (swipeDirection == ItemTouchHelper.RIGHT) {
                iconLeft = itemView.getLeft() + iconMargin;
                iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            } else if (swipeDirection == ItemTouchHelper.LEFT) {
                iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                iconRight = itemView.getRight() - iconMargin;
            }

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.draw(c);
        }

        private void drawBackground(@NonNull Canvas c, @NonNull View itemView) {
            ColorDrawable background = new ColorDrawable(Color.RED);
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
            background.draw(c);
        }

        @Override
        public float getSwipeThreshold(@NonNull ViewHolder viewHolder) {
            return 0.75F;
        }

    }

}