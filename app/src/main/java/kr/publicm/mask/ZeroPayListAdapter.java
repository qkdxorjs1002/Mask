package kr.publicm.mask;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ZeroPayListAdapter extends RecyclerView.Adapter<ZeroPayListAdapter.ViewHolder> {
    private ZeroPayInfo zeroPayInfo;
    private OnItemClickListener onItemClickListener = null;
    private DataLint dataLint;

    @Override
    public ZeroPayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_zero_pay_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TextView name = (TextView) holder.view.findViewById(R.id.zlist_item_name);
        TextView detail = (TextView) holder.view.findViewById(R.id.zlist_item_detail);
        TextView type = (TextView) holder.view.findViewById(R.id.zlist_item_type);
        ZeroPayInfo.Store store = zeroPayInfo.getStores()[position];
        name.setText(store.pobsAfstrName);
        detail.setText(String.format("%s %s", store.pobsBaseAddr, store.pobsDtlAddr));
        type.setText(store.bztypName);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (zeroPayInfo.getStores() == null) {

            return 0;
        }
        return zeroPayInfo.getStores().length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ZeroPayListAdapter(Activity mContext, ZeroPayInfo zeroPayInfo) {
        this.zeroPayInfo = zeroPayInfo;
        this.dataLint = new DataLint(mContext);
    }

    public ZeroPayListAdapter(Activity mContext, OnItemClickListener itemClickListener, ZeroPayInfo zeroPayInfo) {
        this.zeroPayInfo = zeroPayInfo;
        this.onItemClickListener = itemClickListener;
        this.dataLint = new DataLint(mContext);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener i) {
        this.onItemClickListener = i;
    }
}