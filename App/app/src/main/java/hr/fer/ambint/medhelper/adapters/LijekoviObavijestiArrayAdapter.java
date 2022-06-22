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

import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hr.fer.ambint.medhelper.R;
import hr.fer.ambint.medhelper.model.Lijek;
import hr.fer.ambint.medhelper.model.ObavijestLijek;


public class LijekoviObavijestiArrayAdapter extends RecyclerView.Adapter<LijekoviObavijestiArrayAdapter.ViewHolder> {
    Context context;
    private List<Lijek> lijekovi = new ArrayList<>();
    private List<Lijek> lijekoviZaPrikazati = new ArrayList<>();
    private List<ObavijestLijek> obavijesti = new ArrayList<>();
    private List<ObavijestLijek> obavijestiZaPrikazati = new ArrayList<>();
    private LijekObavijestItemClickListener mItemClickListener;
    private SingleRowCalendar singleRowCalendar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LijekoviObavijestiArrayAdapter(Context context, List<Lijek> items, List<ObavijestLijek> obavijesti, SingleRowCalendar singleRowCalendar,
                                          LijekObavijestItemClickListener listener) throws ParseException {
        this.context = context;
        this.lijekovi.addAll(items);
        this.lijekoviZaPrikazati.addAll(items);
        this.obavijesti.addAll(obavijesti);
        this.obavijestiZaPrikazati.addAll(obavijesti);
        this.mItemClickListener = listener;
        this.singleRowCalendar = singleRowCalendar;

        refillObavijesti(obavijesti);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.obavijest_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (obavijestiZaPrikazati.size() > position) {
            ObavijestLijek obavijestLijek = obavijestiZaPrikazati.get(position);
            Optional<Lijek> l = lijekoviZaPrikazati.stream().filter(lijek -> (lijek.getIdLijek() == obavijestLijek.getIdLijek())).findFirst();

            holder.vrijemeObavijesti.setText(obavijestLijek.getVrijemeObavijesti());
            holder.kolicina.setText(obavijestLijek.getKolicina());

            l.ifPresent(lijek -> holder.naziv.setText(lijek.getNaziv()));

            if (obavijestLijek.getUzetLijek()) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
                holder.uzet.setVisibility(View.VISIBLE);
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
                holder.uzet.setVisibility(View.INVISIBLE);
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

    public void refillLijekovi(List<Lijek> lijekovi) {
        this.lijekovi = lijekovi;
        this.lijekoviZaPrikazati = lijekovi;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refillObavijesti(List<ObavijestLijek> obavijesti) throws ParseException {
        this.obavijesti.clear();
        this.obavijesti.addAll(obavijesti);
        this.obavijestiZaPrikazati.clear();
        this.obavijestiZaPrikazati.addAll(obavijesti);
        if (singleRowCalendar.getSelectedDates().get(0) != null) {
            datumPromijenjen(singleRowCalendar.getSelectedDates().get(0));
        } else {
            this.obavijestiZaPrikazati.clear();
        }
        notifyDataSetChanged();
    }

    public interface LijekObavijestItemClickListener {
        void onItemClick(ObavijestLijek obavijestLijek, Lijek lijek);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void datumPromijenjen(Date date) throws ParseException {
        obavijestiZaPrikazati.clear();

        for (Lijek lijek : lijekovi) {

            for (ObavijestLijek obavijest : obavijesti.stream().sorted(Comparator.comparing(ObavijestLijek::getVrijemeObavijesti)).filter(o -> o.getIdLijek() == lijek.getIdLijek()).collect(Collectors.toList())) {
                int brojDanaUzimanja = lijek.getTrajanjeTerapije();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate pocetniLD = LocalDate.parse(lijek.getPocetniDatum(), formatter);
                LocalDate trenutniLD = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

                String lijekGodina = lijek.getPocetniDatum().split("/")[2];
                int lijekMjesec = Integer.parseInt(lijek.getPocetniDatum().split("/")[0]);

                String mjesec = "";
                if (trenutniLD.getMonthValue() < 10)
                    mjesec = "0" + trenutniLD.getMonthValue();
                else mjesec = String.valueOf(trenutniLD.getMonthValue());
                String dan = "";
                if (trenutniLD.getDayOfMonth() < 10)
                    dan = "0" + trenutniLD.getDayOfMonth();
                else dan = String.valueOf(trenutniLD.getDayOfMonth());

                String datumStr = mjesec + "/" + dan + "/" + trenutniLD.getYear();

                if (datumStr.equals(obavijest.getDatumObavijesti())) {
                    obavijestiZaPrikazati.add(obavijest);
                }
                //slozit da mora imat isti datum

                //U istoj godini, i mjesec pocetka prije ili istog mjeseca
                /*if (year.equals(lijekGodina) && lijekMjesec <= month) {

                    //Iteracija po datumima izmedu pocetnog datuma uzimanja i sadasnjeg, broje se obavijesti
                    if (trenutniLD.isAfter(pocetniLD) || trenutniLD.equals(pocetniLD)) {
                        for (LocalDate itrDate = pocetniLD; itrDate.isBefore(trenutniLD) || itrDate.isEqual(trenutniLD); itrDate = itrDate.plusDays(1)) {
                            //Ako je dan u tjednu u kojem se uzima lijek
                            if (lijek.getDaniUzimanja().contains(itrDate.getDayOfWeek().getValue())) {
                                brojDanaUzimanja--;
                            }
                        }

                        if (brojDanaUzimanja >= 0 && lijek.getDaniUzimanja().contains(trenutniLD.getDayOfWeek().getValue())) {
                            obavijestiZaPrikazati.add(obavijest);
                        }
                    }
                }*/
            }
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView vrijemeObavijesti;
        private final TextView kolicina;
        private final TextView naziv;
        private final ImageView uzet;
        private final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            vrijemeObavijesti = view.findViewById(R.id.colVrijeme);
            kolicina = view.findViewById(R.id.colKolicina);
            naziv = view.findViewById(R.id.colNaziv);
            uzet = view.findViewById(R.id.picDone);
            cardView = view.findViewById(R.id.obavijestCV);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ObavijestLijek obavijestLijek = obavijestiZaPrikazati.get(getAdapterPosition());
            mItemClickListener.onItemClick(obavijestLijek,
                    lijekovi.stream().filter(l -> l.getIdLijek() == obavijestLijek.getIdLijek()).findFirst().get());
        }


    }
}
