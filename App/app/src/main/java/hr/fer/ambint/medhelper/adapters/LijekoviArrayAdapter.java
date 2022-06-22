package hr.fer.ambint.medhelper.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hr.fer.ambint.medhelper.R;
import hr.fer.ambint.medhelper.model.Lijek;


public class LijekoviArrayAdapter extends RecyclerView.Adapter<LijekoviArrayAdapter.ViewHolder> {
    Context context;
    private List<Lijek> lijekovi = new ArrayList<>();
    private LijekPopisItemClickListener mItemClickListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LijekoviArrayAdapter(Context context, List<Lijek> items, LijekPopisItemClickListener listener) {
        this.context = context;
        this.lijekovi.addAll(items);
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lijek_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lijek lijek = lijekovi.get(position);

        holder.naziv.setText(lijek.getNaziv());
        holder.trajanje.setText("Trajanje: " + String.valueOf(lijek.getTrajanjeTerapije()));
        holder.daniUzimanja.setText(daniUString(lijek.getDaniUzimanja()));
        holder.pocetak.setText("Početak: " + lijek.getPocetniDatum());
    }

    @Override
    public int getItemCount() {
        return lijekovi.size();
    }

    public void addItemClickListener(LijekPopisItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void refillLijekovi(List<Lijek> lijekovi) {
        this.lijekovi = lijekovi;
        notifyDataSetChanged();
    }


    public interface LijekPopisItemClickListener {
        void onItemClick(Lijek lijek);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView naziv;
        private final TextView trajanje;
        private final TextView pocetak;
        private final TextView daniUzimanja;


        public ViewHolder(View view) {
            super(view);
            trajanje = view.findViewById(R.id.colTrajanje);
            pocetak = view.findViewById(R.id.colPocetak);
            daniUzimanja = view.findViewById(R.id.colUzimanje);
            naziv = view.findViewById(R.id.colNaziv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Lijek lijek = lijekovi.get(getAdapterPosition());
            mItemClickListener.onItemClick(lijek);
        }
    }

    private String daniUString(List<Integer> daniInteger) {
        String dani = "";

        if (daniInteger.contains(1)) dani += "Pon ";
        if (daniInteger.contains(2)) dani += "Uto ";
        if (daniInteger.contains(3)) dani += "Sri ";
        if (daniInteger.contains(4)) dani += "Čet ";
        if (daniInteger.contains(5)) dani += "Pet ";
        if (daniInteger.contains(6)) dani += "Sub ";
        if (daniInteger.contains(7)) dani += "Ned";

        return dani;
    }
}
