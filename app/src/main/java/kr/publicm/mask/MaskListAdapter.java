package kr.publicm.mask;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MaskListAdapter extends RecyclerView.Adapter<MaskListAdapter.ViewHolder> {
    private MaskInfo maskInfo;
    private OnItemClickListener onItemClickListener = null;
    private DataLint dataLint;

    @Override
    public MaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_mask_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        View type = (View) holder.view.findViewById(R.id.list_item_type);
        TextView name = (TextView) holder.view.findViewById(R.id.list_item_name);
        TextView detail = (TextView) holder.view.findViewById(R.id.list_item_detail);
        TextView stat = (TextView) holder.view.findViewById(R.id.list_item_stat);
        TextView stock_at = (TextView) holder.view.findViewById(R.id.list_item_stock_at);
        MaskInfo.Store store = maskInfo.getStores()[position];
        name.setText(store.name);
        detail.setText(String.format("%sm", dataLint.getDistanceFromTo(maskInfo.getRefLocation(),
                new CustomLocation(store.lat, store.lng)).toString()));

        DataLint.Stock stock = dataLint.getStockText(store.remain_stat);
        stat.setTextColor(holder.view.getResources().getColor(stock.getColor(), null));
        stat.setText(stock.getStockText());
        stock_at.setText(dataLint.getTimeOffset(store.stock_at));

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
        if (maskInfo.getStores() == null) {

            return 0;
        }
        return maskInfo.getStores().length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public MaskListAdapter(Activity mContext, MaskInfo maskInfo) {
        this.maskInfo = maskInfo;
        this.dataLint = new DataLint(mContext);
    }

    public MaskListAdapter(Activity mContext, OnItemClickListener itemClickListener, MaskInfo maskInfo) {
        this.maskInfo = maskInfo;
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