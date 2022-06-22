package hr.fer.ambint.medhelper.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.fer.ambint.medhelper.Constants
import hr.fer.ambint.medhelper.R
import hr.fer.ambint.medhelper.adapters.LijekoviArrayAdapter
import hr.fer.ambint.medhelper.alarm.Alarm
import hr.fer.ambint.medhelper.alarm.AlarmsListViewModel
import hr.fer.ambint.medhelper.alarm.CreateAlarmViewModel
import hr.fer.ambint.medhelper.model.Lijek
import hr.fer.ambint.medhelper.viewModels.LijekoviViewModel
import kotlinx.android.synthetic.main.activity_meds.*

class MedicationsActivity : AppCompatActivity(), LijekoviArrayAdapter.LijekPopisItemClickListener {
    private lateinit var pref: SharedPreferences
    private var userId = 0;

    private var lijekoviList: ArrayList<Lijek> = ArrayList()
    private val viewModelLijekovi: LijekoviViewModel by viewModels()
    private var lijekoviArrayAdapter: LijekoviArrayAdapter? = null
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var lijekZaIzbrisati: Lijek? = null
    private var listaAlarma: MutableList<Alarm> = mutableListOf()
    private var createAlarmViewModel: CreateAlarmViewModel? = null
    private var alarmsListViewModel: AlarmsListViewModel? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meds)
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        userId = pref.getInt(Constants.USER_ID, 0)

        createAlarmViewModel = ViewModelProviders.of(this)[CreateAlarmViewModel::class.java]
        alarmsListViewModel = ViewModelProviders.of(this)[AlarmsListViewModel::class.java]
        alarmsListViewModel!!.alarmsLiveData.observe(this) { alarmi ->
            listaAlarma.clear()
            listaAlarma.addAll(alarmi)
        }

        if (userId != 0) {
            storePB.visibility = View.VISIBLE
            viewModelLijekovi.getKorisnikoviLijekovi(userId)
        } else {
            Toast.makeText(
                this,
                "Pogrešan ID korisnika",
                Toast.LENGTH_LONG
            ).show()
        }

        viewModelLijekovi.getLijekoviLiveData().observe(this) { lijekovi ->
            storePB.visibility = View.GONE

            if (lijekovi != null) {
                lijekoviList.clear()
                lijekoviList.addAll(lijekovi)

                Toast.makeText(
                    this,
                    "Učitani svi lijekovi",
                    Toast.LENGTH_LONG
                ).show()

                initLijekoviRecycler()

            } else {
                Toast.makeText(
                    this,
                    "Pogreška kod učitavanja lijekova sa servera",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModelLijekovi.getDeleteLijekLiveDataa().observe(this) {
            Toast.makeText(
                this,
                it,
                Toast.LENGTH_LONG
            ).show()
        }

        viewModelLijekovi.getObavijestiLijekoviLiveData().observe(this) { obavijesti ->
            storePB.visibility = View.GONE

            if (obavijesti != null) {
                if (obavijesti.isNotEmpty()) {
                    val obavijestiZaBrisati =
                        obavijesti.filter { o -> o.idLijek == lijekZaIzbrisati!!.idLijek }.toList()

                    for (obavijest in obavijestiZaBrisati) {
                        viewModelLijekovi.deleteObavijest(obavijest.idObavijest!!)

                        try {
                            val alarmZaIzbrisat =
                                listaAlarma.filter { alarm -> alarm.alarmId == obavijest.idObavijest }
                                    .first()
                            alarmZaIzbrisat.cancelAlarm(this)

                        } catch (e: NoSuchElementException) {
                        }
                    }
                }
            }

            viewModelLijekovi.getKorisnikoviLijekovi(userId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initLijekoviRecycler() {
        val lijekoviRecycler = findViewById<RecyclerView>(R.id.lijekoviRecycler)
        lijekoviRecycler.layoutManager = LinearLayoutManager(this)

        lijekoviArrayAdapter =
            LijekoviArrayAdapter(
                this,
                lijekoviList,
                this
            )

        lijekoviRecycler.adapter = lijekoviArrayAdapter
    }

    override fun onItemClick(lijek: Lijek?) {

        AlertDialog.Builder(this)
            .setMessage("Želite li izbrisati lijek i njegove obavijesti?")
            .setPositiveButton("DA") { dialog, which ->
                izbrisiLijekIObavijesti(lijek)
            }.setNegativeButton("NE") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun izbrisiLijekIObavijesti(lijek: Lijek?) {
        viewModelLijekovi.deleteLijek(lijek!!.idLijek!!)
        viewModelLijekovi.getKorisnikoveObavijesti(userId)
        lijekZaIzbrisati = lijek
    }
}