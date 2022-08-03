package com.fir.module.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.fir.module.R;
import com.fir.module.models.PList;

public class PListAdapter extends RecyclerView.Adapter<PListAdapter.ViewHolder>{
        private final PList[] listdata;
        private ItemClickListener mItemClickListener;

        // RecyclerView recyclerView;
        public PListAdapter(PList[] listdata) {
            this.listdata = listdata;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.payment_item, parent, false);
            return new ViewHolder(listItem);
        }

        public void addItemClickListener(ItemClickListener listener) {
            mItemClickListener = listener;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PList myListData = listdata[position];


            Resources res = holder.itemView.getContext().getResources();
            int pay_card_selected_text_color = res.getColor(R.color.pay_card_selected_text_color);

            holder.textView.setText(myListData.getDesc());
            holder.textView.setTextColor(pay_card_selected_text_color);
            holder.textView.setTypeface(null, Typeface.BOLD);

            holder.cmaterialCardView.setCardElevation(15);

            switch ((int) myListData.getID()){
                case 1000:
                    holder.imageView.setImageResource(R.drawable.ic_operator_billing_clicked);
                    break;
                case 1001:
                    holder.imageView.setImageResource(R.drawable.ic_card_clicked);
                    break;
                case 1002:
                    holder.imageView.setImageResource(R.drawable.ic_store_billing_clicked);
                    break;
                default: break;

            }
            holder.arrowImageView.setColorFilter(pay_card_selected_text_color, android.graphics.PorterDuff.Mode.SRC_IN);
            holder.imageView.setColorFilter(pay_card_selected_text_color, android.graphics.PorterDuff.Mode.SRC_IN);

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick((int) myListData.getID());
                    }
                 }
            });
        }

        //Define your Interface method here
        public interface ItemClickListener {
            void onItemClick(int position);
        }

        @Override
        public int getItemCount() {
            return listdata.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public ImageView arrowImageView;
            public TextView textView;
            public MaterialCardView cmaterialCardView;
            public ConstraintLayout relativeLayout;
            public ViewHolder(View itemView) {
                super(itemView);

                this.cmaterialCardView =  itemView.findViewById(R.id.cmaterialCardView);
                this.imageView = itemView.findViewById(R.id.cimageView);
                this.arrowImageView = itemView.findViewById(R.id.arrowImageView);
                this.textView = itemView.findViewById(R.id.ctextView);
                relativeLayout = itemView.findViewById(R.id.linearLayout);
            }
        }
    }