package com.sanron.yidumusic.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sanron on 16-8-3.
 */
public abstract class ItemClickHelper {

    public static void setOnItemClickListener(final RecyclerView recyclerView, final OnItemClickListener onItemClickListener, final int... ids) {

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(final View view) {
                final RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                final View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v, v == view, viewHolder.getAdapterPosition());
                    }
                };
                if (ids != null) {
                    for (int id : ids) {
                        View child = view.findViewById(id);
                        if (child != null) {
                            child.setOnClickListener(onClickListener);
                        }
                    }
                }
                view.setOnClickListener(onClickListener);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
            }
        });
    }


    public interface OnItemClickListener {
        void onItemClick(View view, boolean isItemView, int position);
    }
}
