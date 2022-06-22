package hr.fer.ambint.medhelper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hr.fer.ambint.medhelper.R;
import hr.fer.ambint.medhelper.model.ObavijestLijek;


public class ObavijestiArrayAdapter extends RecyclerView.Adapter<ObavijestiArrayAdapter.ViewHolder> {
    Context context;
    private List<ObavijestLijek> obavijesti;
    private ItemClickListener mItemClickListener;

    public ObavijestiArrayAdapter(Context context, List<ObavijestLijek> items, ItemClickListener listener) {
        this.context = context;
        this.obavijesti = items;
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.obavijest_lijek_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObavijestLijek p = obavijesti.get(position);
        holder.vrijemeObavijesti.setText(p.getVrijemeObavijesti());
        holder.kolicina.setText(p.getKolicina());
    }

    @Override
    public int getItemCount() {
        return obavijesti.size();
    }

    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void refillObavijesti(List<ObavijestLijek> obavijestiList) {
        this.obavijesti = obavijestiList;
        notifyDataSetChanged();
    }


    public interface ItemClickListener {
        void onItemClick(ObavijestLijek obavijest);
    }

    public void addPaket(ObavijestLijek obavijest) {
        this.obavijesti.add(obavijest);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView vrijemeObavijesti;
        private final TextView kolicina;

        public ViewHolder(View view) {
            super(view);
            vrijemeObavijesti = view.findViewById(R.id.colVrijeme);
            kolicina = view.findViewById(R.id.colKol);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ObavijestLijek obavijest = obavijesti.get(getAdapterPosition());
            mItemClickListener.onItemClick(obavijest);
        }


    }
}
