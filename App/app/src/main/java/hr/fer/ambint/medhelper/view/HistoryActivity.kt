package hr.fer.ambint.medhelper.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.fer.ambint.medhelper.Constants
import hr.fer.ambint.medhelper.R
import hr.fer.ambint.medhelper.adapters.ObavijestiPopisArrayAdapter
import hr.fer.ambint.medhelper.adapters.SpinnerItem
import hr.fer.ambint.medhelper.model.ObavijestLijek
import hr.fer.ambint.medhelper.viewModels.LijekoviViewModel
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_meds.storePB

class HistoryActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private var userId = 0;

    private var obavijestiList: ArrayList<ObavijestLijek> = ArrayList()
    private val viewModelLijekovi: LijekoviViewModel by viewModels()
    private var obavijestiArrayAdapter: ObavijestiPopisArrayAdapter? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        userId = pref.getInt(Constants.USER_ID, 0)


        val filteri = mutableListOf<SpinnerItem>()
        filteri.add(SpinnerItem("1", "Svi"))
        filteri.add(SpinnerItem("2", "Uzeto"))
        filteri.add(SpinnerItem("3", "Zaboravljeno"))

        val filterArrayAdapter = ArrayAdapter<SpinnerItem>(
            this,
            R.layout.custom_simple_spinner_item, filteri.toList()
        )
        filterArrayAdapter.setDropDownViewResource(R.layout.custom_simple_spinner_dropdown_item)

        spinner.adapter = filterArrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (obavijestiArrayAdapter != null)
                    obavijestiArrayAdapter!!.filterPromijenjen(position)
            }

        }

        if (userId != 0) {
            storePB.visibility = View.VISIBLE
            viewModelLijekovi.getKorisnikoveObavijesti(userId)
        } else {
            Toast.makeText(
                this,
                "Pogrešan ID korisnika",
                Toast.LENGTH_LONG
            ).show()
        }

        viewModelLijekovi.getObavijestiLijekoviLiveData().observe(this) { obavijesti ->
            storePB.visibility = View.GONE

            if (obavijesti != null) {
                obavijestiList.clear()
                obavijestiList.addAll(obavijesti)

                Toast.makeText(
                    this,
                    "Učitani svi lijekovi",
                    Toast.LENGTH_LONG
                ).show()

                initObavijestiRecycler()

            } else {
                Toast.makeText(
                    this,
                    "Pogreška kod učitavanja lijekova sa servera",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObavijestiRecycler() {
        val obavijestiRecycler = findViewById<RecyclerView>(R.id.obavijestiRecycler)
        obavijestiRecycler.layoutManager = LinearLayoutManager(this)

        obavijestiArrayAdapter =
            ObavijestiPopisArrayAdapter(
                this,
                obavijestiList,
            )

        obavijestiRecycler.adapter = obavijestiArrayAdapter
    }

}