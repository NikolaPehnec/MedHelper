package hr.fer.ambint.medhelper.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hr.fer.ambint.medhelper.R;
import hr.fer.ambint.medhelper.model.Lijek;
import hr.fer.ambint.medhelper.model.ObavijestLijek;


public class ObavijestiPopisArrayAdapter extends RecyclerView.Adapter<ObavijestiPopisArrayAdapter.ViewHolder> {
    Context context;
    private List<ObavijestLijek> obavijesti = new ArrayList<>();
    private List<ObavijestLijek> obavijestiZaPrikazati = new ArrayList<>();
    private LijekObavijestItemClickListener mItemClickListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ObavijestiPopisArrayAdapter(Context context, List<ObavijestLijek> obavijesti) throws ParseException {
        this.context = context;
        this.obavijesti.addAll(obavijesti);
        this.obavijestiZaPrikazati.addAll(obavijesti);

        refillObavijesti(obavijesti);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.obavijest_popis_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filterPromijenjen(int position) {
        //0-svi
        //1-uzeti
        //2-zaboravljeni
        obavijestiZaPrikazati.clear();
        if (position == 0) {
            obavijestiZaPrikazati.addAll(obavijesti);
        } else {
            for (ObavijestLijek obavijest : obavijesti) {
                if (position == 1 && obavijest.getUzetLijek()) {
                    this.obavijestiZaPrikazati.add(obavijest);
                } else if (position == 2 && !obavijest.getUzetLijek()) {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate datumObavijesti = LocalDate.parse(obavijest.getDatumObavijesti(), formatter);
                    LocalDate trenutniDatum = LocalDate.now();

                    if (datumObavijesti.isBefore(trenutniDatum))
                        this.obavijestiZaPrikazati.add(obavijest);
                }
            }
        }
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (obavijestiZaPrikazati.size() > position) {
            ObavijestLijek obavijestLijek = obavijestiZaPrikazati.get(position);

            holder.vrijemeObavijesti.setText(obavijestLijek.getVrijemeObavijesti());
            holder.kolicina.setText(obavijestLijek.getKolicina());
            holder.naziv.setText(Objects.requireNonNull(obavijestLijek.getLijek()).getNaziv());
            holder.datum.setText(obavijestLijek.getDatumObavijesti());

            if (obavijestLijek.getUzetLijek()) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
                holder.uzet.setVisibility(View.VISIBLE);
                holder.neuzet.setVisibility(View.GONE);

            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));

                holder.uzet.setVisibility(View.GONE);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate datumObavijesti = LocalDate.parse(obavijestLijek.getDatumObavijesti(), formatter);
                LocalDate trenutniDatum = LocalDate.now();

                //Ako je datum obavijesti prosao a lijek nije uzet
                if (datumObavijesti.isBefore(trenutniDatum)) {
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
                    holder.neuzet.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return obavijestiZaPrikazati.size();
    }

    public void addItemClickListener(LijekObavijestItemClickListener listener) {
        mItemClickListener = listener;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refillObavijesti(List<ObavijestLijek> obavijesti) throws ParseException {
        this.obavijesti.clear();
        this.obavijestiZaPrikazati.clear();

        for (ObavijestLijek obavijest : obavijesti) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate datumObavijesti = LocalDate.parse(obavijest.getDatumObavijesti(), formatter);
            LocalDate trenutniDatum = LocalDate.now();

            if (datumObavijesti.isBefore(trenutniDatum) || datumObavijesti.equals(trenutniDatum)) {
                this.obavijestiZaPrikazati.add(obavijest);
                this.obavijesti.add(obavijest);
            }

        }

        notifyDataSetChanged();
    }

    public interface LijekObavijestItemClickListener {
        void onItemClick(ObavijestLijek obavijestLijek, Lijek lijek);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView vrijemeObavijesti;
        private final TextView kolicina;
        private final TextView naziv;
        private final TextView datum;
        private final ImageView uzet;
        private final ImageView neuzet;
        private final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            vrijemeObavijesti = view.findViewById(R.id.colVrijeme);
            kolicina = view.findViewById(R.id.colKolicina);
            datum = view.findViewById(R.id.colDatum);
            naziv = view.findViewById(R.id.colNaziv);
            uzet = view.findViewById(R.id.picDone);
            neuzet = view.findViewById(R.id.picNotDone);
            cardView = view.findViewById(R.id.obavijestCV);
        }

    }
}
