package hr.fer.ambint.medhelper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.fer.ambint.medhelper.R
import hr.fer.ambint.medhelper.model.Korisnik
import kotlinx.android.synthetic.main.korisnik_view.view.*

class KorisnikAdapter(
    private var korisniciList: List<Korisnik>,
    private val onKorisnikClickListener: onClickListener,
) : RecyclerView.Adapter<KorisnikAdapter.KorisnikViewHolder>() {

    var filterKorisniciList = ArrayList<Korisnik>()

    init {
        filterKorisniciList.addAll(korisniciList)

    }

    class KorisnikViewHolder(val korisnikView: View) : RecyclerView.ViewHolder(korisnikView)

    override fun getItemCount(): Int {
        return filterKorisniciList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KorisnikViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.korisnik_view, parent, false)

        return KorisnikViewHolder(view)
    }


    override fun onBindViewHolder(holder: KorisnikViewHolder, position: Int) {
        with(holder.korisnikView) {
            ime.text = "Ime: " + filterKorisniciList[position].ime
            prezime.text = "Prezime: " + filterKorisniciList[position].prezime
            korisnickoime.text = "Korisnicko ime: " + filterKorisniciList[position].korisnickoIme
            lozinka.text = "Lozinka: " + filterKorisniciList[position].lozinka
            uloga.text = "Uloga: " + filterKorisniciList[position].uloga

            deleteKorisnikBtn.setOnClickListener {
                onKorisnikClickListener.onIzbrisiClicked(filterKorisniciList[position].userID!!.toInt())
                notifyDataSetChanged()
            }

            korisnikLayout.setOnClickListener {
                onKorisnikClickListener.onKorisnikOdabran(filterKorisniciList[position])
            }
        }
    }


    interface onClickListener {
        fun onIzbrisiClicked(userId: Int)
        fun onKorisnikOdabran(korisnikOdabran: Korisnik)
    }


    fun filter(uloga: String, ime: String, prezime: String) {
        filterKorisniciList.clear()

        var filtriranoPoUlozi = korisniciList.filter { it.uloga.toString().equals(uloga) }.toList()

        if (!ime.equals("")) {
            filtriranoPoUlozi = filtriranoPoUlozi.filter { it.ime!!.uppercase().startsWith(ime.uppercase()) }.toList()
        }

        if (!prezime.equals("")) {
            filtriranoPoUlozi =
                filtriranoPoUlozi.filter { it.prezime!!.uppercase().startsWith(prezime.uppercase()) }.toList()
        }

        filterKorisniciList.addAll(filtriranoPoUlozi)

        notifyDataSetChanged()
    }
}

