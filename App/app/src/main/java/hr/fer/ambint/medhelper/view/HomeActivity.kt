package hr.fer.ambint.medhelper.view

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import hr.fer.ambint.medhelper.Constants
import hr.fer.ambint.medhelper.R
import hr.fer.ambint.medhelper.adapters.LijekoviObavijestiArrayAdapter
import hr.fer.ambint.medhelper.adapters.ObavijestiArrayAdapter
import hr.fer.ambint.medhelper.alarm.Alarm
import hr.fer.ambint.medhelper.alarm.AlarmsListViewModel
import hr.fer.ambint.medhelper.alarm.CreateAlarmViewModel
import hr.fer.ambint.medhelper.model.Lijek
import hr.fer.ambint.medhelper.model.LijekPost
import hr.fer.ambint.medhelper.model.ObavijestLijek
import hr.fer.ambint.medhelper.model.mapaObavijesti
import hr.fer.ambint.medhelper.transformIntoDatePicker
import hr.fer.ambint.medhelper.viewModels.LijekoviViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors


class HomeActivity : AppCompatActivity(), ObavijestiArrayAdapter.ItemClickListener,
    LijekoviObavijestiArrayAdapter.LijekObavijestItemClickListener {
    private lateinit var pref: SharedPreferences

    private var viewLists: List<List<View>> = mutableListOf()
    private var kupacViewList: List<View> = mutableListOf()
    private var prazanViewList: List<View> = mutableListOf()
    private var adminViewList: List<View> = mutableListOf()

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    private var id = 1;
    private var userId = 0;
    private var obavijestiUkljucene = false

    private var dialog: Dialog? = null
    private var obavijestiList: ArrayList<ObavijestLijek> = ArrayList()
    private var lijekoviList: ArrayList<Lijek> = ArrayList()
    private var lijekoviZaPoslati: ArrayList<Lijek> = ArrayList()
    private var odabraniDaniUzimanja: MutableList<Int>? = null
    private var zapisaneObavijesti: ArrayList<ObavijestLijek> = ArrayList()
    private var obavijestiZaPoslati: ArrayList<ObavijestLijek> = ArrayList()
    private var uredajObavijesti: ArrayList<ObavijestLijek> = ArrayList()
    private var obavijestiArrayAdapter: ObavijestiArrayAdapter? = null
    private var singleRowCalendar: SingleRowCalendar? = null
    private var lijekoviObavijestiArrayAdapter: LijekoviObavijestiArrayAdapter? = null
    private var alarmsListViewModel: AlarmsListViewModel? = null
    private var listaAlarma: MutableList<Alarm> = mutableListOf()
    private var createAlarmViewModel: CreateAlarmViewModel? = null

    private val viewModelLijekovi: LijekoviViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        userId = pref.getInt(Constants.USER_ID, 0)
        createAlarmViewModel = ViewModelProviders.of(this)[CreateAlarmViewModel::class.java]
        alarmsListViewModel = ViewModelProviders.of(this)[AlarmsListViewModel::class.java]

        alarmsListViewModel!!.alarmsLiveData.observe(this) { alarmi ->
            listaAlarma.clear()
            listaAlarma.addAll(alarmi)
        }

        // set current date to calendar and current month to currentMonth variable
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        // enable white status bar with black icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        initCalendar()
        addViews()
        addListeners()

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

        viewModelLijekovi.getPutObavijestLiveDataa().observe(this) { obavijest ->
            if (obavijest != null) {
                viewModelLijekovi.getKorisnikoveObavijesti(userId)
            } else {
                Toast.makeText(
                    this,
                    "Pogrešan kod uzimanja lijeka, pokušajte ponovo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModelLijekovi.getObavijestiLijekoviLiveData().observe(this) { obavijesti ->
            storePB.visibility = View.GONE

            if (obavijesti != null) {
                obavijestiList.clear()
                obavijestiList.addAll(obavijesti)

                val sviLijekovi =
                    obavijesti.stream().map { obav -> obav.lijek }.collect(Collectors.toList())

                val myMap: MutableMap<Int, Lijek> = HashMap()
                for (lijek in sviLijekovi) {
                    if (lijek != null) {
                        myMap.put(lijek.idLijek!!, lijek)
                    }
                }

                lijekoviList.clear()
                lijekoviList.addAll(myMap.values.toList())

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

        viewModelLijekovi.getPostLijekLiveData().observe(this) { lijek ->
            storePB.visibility = View.GONE

            if (lijek != null) {

                Toast.makeText(
                    this,
                    "Uspješno zapisan lijek na server",
                    Toast.LENGTH_LONG
                ).show()

                for (obavijest in obavijestiZaPoslati) {
                    obavijest.idLijek = lijek.idLijek
                    obavijest.idKorisnik = userId
                    viewModelLijekovi.postObavijest(obavijest)
                }

                if (obavijestiUkljucene) {
                    postaviObavijesti(lijek)
                }

                obavijestiZaPoslati.clear()
                viewModelLijekovi.getKorisnikoveObavijesti(userId)
            } else {
                Toast.makeText(
                    this,
                    "Pogreška kod slanja lijeka na server",
                    Toast.LENGTH_LONG
                ).show()

                obavijestiZaPoslati.clear()
            }
        }

        viewModelLijekovi.getPostObavijestLiveData().observe(this) { obavijest ->
            if (obavijest != null) {

            } else {
                Toast.makeText(
                    this,
                    "Pogreška kod slanja obavijesti lijeka na server",
                    Toast.LENGTH_LONG
                ).show()

                obavijestiZaPoslati.clear()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initLijekoviRecycler() {
        val lijekoviRecycler = findViewById<RecyclerView>(R.id.lijekoviRecycler)
        lijekoviRecycler.layoutManager = LinearLayoutManager(this)

        lijekoviObavijestiArrayAdapter =
            LijekoviObavijestiArrayAdapter(
                this,
                lijekoviList,
                obavijestiList,
                singleRowCalendar,
                this
            )
        lijekoviRecycler.adapter = lijekoviObavijestiArrayAdapter
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCalendar() {
        // calendar view manager is responsible for our displaying logic
        val myCalendarViewManager = object : CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                // if item is selected we return this layout items
                return if (isSelected) R.layout.selected_calendar_item
                else R.layout.calendar_item
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)
            }
        }

        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object : CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                tvDay.text = DateUtils.getDayName(date)
                if (lijekoviObavijestiArrayAdapter != null) {
                    lijekoviObavijestiArrayAdapter!!.datumPromijenjen(date)
                    lijekoviObavijestiArrayAdapter!!.notifyDataSetChanged();
                }
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                /*val cal = Calendar.getInstance()
                cal.time = date*/
                //in this example sunday and saturday can't be selected, other item can be selected
                /*return when (cal[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> false
                    Calendar.SUNDAY -> false
                    else -> true
                }*/
                return true
            }
        }

        val indexOfDay =
            (Calendar.getInstance() as GregorianCalendar).toZonedDateTime().dayOfMonth - 1

        // here we init our calendar, also you can set more properties if you need them
        singleRowCalendar = main_single_row_calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonth())
            includeCurrentDate = true
            initialPositionIndex = indexOfDay
            init()
            select(indexOfDay)
        }

        btnRight.setOnClickListener {
            singleRowCalendar!!.setDates(getDatesOfNextMonth())
            singleRowCalendar!!.initialPositionIndex = 0
            singleRowCalendar!!.init()

        }

        btnLeft.setOnClickListener {
            singleRowCalendar!!.setDates(getDatesOfPreviousMonth())
            singleRowCalendar!!.initialPositionIndex = 0
            singleRowCalendar!!.init()
        }

        btnToday.setOnClickListener {
            singleRowCalendar!!.select(indexOfDay)
            singleRowCalendar!!.scrollToPosition(indexOfDay)
        }
    }

    private fun getDatesOfNextMonth(): List<Date> {
        currentMonth++ // + because we want next month
        if (currentMonth == 12) {
            // we will switch to january of next year, when we reach last month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0 // 0 == january
        }
        return getDates(mutableListOf())
    }

    private fun getDatesOfPreviousMonth(): List<Date> {
        currentMonth-- // - because we want previous month
        if (currentMonth == -1) {
            // we will switch to december of previous year, when we reach first month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11 // 11 == december
        }
        return getDates(mutableListOf())
    }

    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun addViews() {
        kupacViewList = mutableListOf()
        adminViewList = mutableListOf()

        viewLists = mutableListOf(prazanViewList, kupacViewList, adminViewList)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addListeners() {

        pilFabZapisi.setOnClickListener {
            dodajNoviLijek()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dodajNoviLijek() {
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.novi_lijek_dialog)
        dialog!!.setTitle("Novi lijek")
        dialog!!.setCanceledOnTouchOutside(false)

        dialog!!.setOnKeyListener { arg0, arg1, arg2 ->
            if (arg1 == KeyEvent.KEYCODE_BACK) {
                dialog!!.dismiss()
            }
            false
        }

        val switchObavijesti = dialog!!.findViewById<View>(R.id.switchObavijesti) as SwitchCompat

        val spinnerObavijesti =
            dialog!!.findViewById<View>(R.id.spinnerObavijesti) as Spinner
        spinnerObavijesti.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                napuniListuObavijesti()
            }
        }

        val radioBtnNeprekidno =
            dialog!!.findViewById<View>(R.id.neprekidno_rb) as RadioButton
        val radioBtnOdredeno =
            dialog!!.findViewById<View>(R.id.odredeno_rb) as RadioButton

        radioBtnNeprekidno.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                radioBtnOdredeno.text = "Određeni broj dana: "
            }
        }
        radioBtnOdredeno.setOnCheckedChangeListener { compoundButton, b ->
            if (b) ucitajBrojDanaDialog()
        }

        val radioBtnSvakiDan =
            dialog!!.findViewById<View>(R.id.svaki_dan_rb) as RadioButton
        val radioBtnSpecDani =
            dialog!!.findViewById<View>(R.id.odredeni_dani_rb) as RadioButton

        radioBtnSvakiDan.setOnCheckedChangeListener { compoundButton, b ->
            if (b) radioBtnSpecDani.text = "Određeni dani: "
        }

        radioBtnSpecDani.setOnCheckedChangeListener { compoundButton, b ->
            if (b) ucitajSpecDaniDialog()
        }

        val pocetniDatum =
            dialog!!.findViewById<View>(R.id.pocetni_datum_et) as EditText
        pocetniDatum.transformIntoDatePicker(this, "MM/dd/yyyy")

        val btnZapisi =
            dialog!!.findViewById<View>(R.id.btnZapisi) as Button

        btnZapisi.setOnClickListener {
            zapisiNoviLijek()
        }

        napuniListuObavijesti()

        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun zapisiNoviLijek() {
        //zapisaneObavijesti = ArrayList()
        var pocetniDatumVrijednost = ""
        var trajanjeTerapije = 365

        val nazivLijeka =
            (dialog!!.findViewById<View>(R.id.ime_lijeka_et) as EditText).text.toString()
        val pocetniDatum =
            (dialog!!.findViewById<View>(R.id.pocetni_datum_et) as EditText).text.toString()
        val odredeniBrojDana = dialog!!.findViewById<View>(R.id.odredeno_rb) as RadioButton
        val svakiDanUzimanja = dialog!!.findViewById<View>(R.id.svaki_dan_rb) as RadioButton

        if (nazivLijeka.equals("")) {
            MaterialAlertDialogBuilder(this)
                .setMessage("Unesite ime lijeka!")
                .setPositiveButton("OK") { dialog, which ->
                }
                .show()
        } else if (pocetniDatum.equals("Početni datum: ")) {
            MaterialAlertDialogBuilder(this)
                .setMessage("Unesite početni datum uzimanja lijeka!")
                .setPositiveButton("OK") { dialog, which ->
                }
                .show()
        } else {
            pocetniDatumVrijednost = pocetniDatum.split(":")[1].trimStart()
            if (odredeniBrojDana.isChecked)
                trajanjeTerapije = Integer.parseInt(odredeniBrojDana.text.split(":")[1].trimStart())

            if (svakiDanUzimanja.isChecked) {
                odabraniDaniUzimanja = mutableListOf()
                odabraniDaniUzimanja!!.clear()
                odabraniDaniUzimanja!!.addAll(listOf(1, 2, 3, 4, 5, 6, 7))
            }

            uredajObavijesti.clear()
            obavijestiZaPoslati.clear()
            lijekoviZaPoslati.clear()

            for (obavijestLijek in obavijestiList) {
                //Zapisat sve obavijesti po svakom danu kad je obavijest
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val pocetniLD = LocalDate.parse(pocetniDatumVrijednost, formatter)

                var itrDate: LocalDate = pocetniLD
                var _trajanjeTerapije = trajanjeTerapije
                while (_trajanjeTerapije > 0) {

                    //Ako je dan u tjednu u kojem se uzima lijek
                    if (odabraniDaniUzimanja!!.contains(itrDate.dayOfWeek.value)) {
                        val novaObavijest = ObavijestLijek(
                            null,
                            null,
                            null,
                            obavijestLijek.vrijemeObavijesti,
                            obavijestLijek.datumObavijesti,
                            obavijestLijek.kolicina,
                            obavijestLijek.uzetLijek,
                            null
                        )

                        novaObavijest.idObavijest = Random().nextInt(Int.MAX_VALUE)
                        _trajanjeTerapije--
                        //novaObavijest.idLijek = id
                        var month = ""
                        if (itrDate.monthValue < 10)
                            month = "0" + itrDate.monthValue.toString()
                        else month = itrDate.monthValue.toString()
                        var day = ""
                        if (itrDate.dayOfMonth < 10)
                            day = "0" + itrDate.dayOfMonth.toString()
                        else day = itrDate.dayOfMonth.toString()

                        novaObavijest.datumObavijesti =
                            month + "/" + day + "/" + itrDate.year.toString()
                        obavijestiZaPoslati.add(novaObavijest)

                        if (itrDate.isAfter(LocalDate.now()) || itrDate.equals(LocalDate.now()))
                            uredajObavijesti.add(novaObavijest)

                    }

                    itrDate = itrDate.plusDays(1)
                }
            }


            lijekoviZaPoslati.add(
                Lijek(
                    null,
                    nazivLijeka,
                    pocetniDatumVrijednost,
                    trajanjeTerapije,
                    odabraniDaniUzimanja!!.toList(),
                )
            )

            obavijestiUkljucene =
                (dialog!!.findViewById<View>(R.id.switchObavijesti) as SwitchCompat).isChecked
            posaljiLijekove()

            //id++
            dialog!!.dismiss()
        }
    }

    private fun posaljiLijekove() {
        for (lijek in lijekoviZaPoslati) {

            val dani = lijek.daniUzimanja.joinToString(";")
            val lijekPost = LijekPost(lijek.naziv, lijek.pocetniDatum, lijek.trajanjeTerapije, dani)
            storePB.visibility = View.VISIBLE

            viewModelLijekovi.postLijek(lijekPost)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postaviObavijesti(lijek: Lijek) {
        for (obavijest in uredajObavijesti) {

            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
            val vrijeme=obavijest.datumObavijesti+" "+obavijest.vrijemeObavijesti
            val datumObavijesti = LocalDateTime.parse(vrijeme, formatter)
            val dateNow=LocalDateTime.now()

            if(datumObavijesti.isAfter(dateNow)) {
                val alarm = Alarm(
                    obavijest.idObavijest!!,
                    obavijest.vrijemeObavijesti.split(":")[0].toInt(),
                    obavijest.vrijemeObavijesti.split(":")[1].toInt(),
                    Integer.parseInt(obavijest.datumObavijesti.split("/")[2]),
                    Integer.parseInt(obavijest.datumObavijesti.split("/")[0]),
                    Integer.parseInt(obavijest.datumObavijesti.split("/")[1]),
                    lijek.naziv,
                    System.currentTimeMillis(),
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
                )

                createAlarmViewModel!!.insert(alarm)

                alarm.schedule(this, obavijest.datumObavijesti)
            }
        }

        uredajObavijesti.clear()
    }

    private fun ucitajBrojDanaDialog() {
        val dialog3 = Dialog(this)
        dialog3.setContentView(R.layout.numofday_picker_dialog)
        dialog3.setCanceledOnTouchOutside(false)

        val picker = dialog3.findViewById<View>(R.id.numPicker) as NumberPicker
        picker.minValue = 1
        picker.maxValue = 100

        val btnZapisi = dialog3.findViewById<View>(R.id.btnZapisi) as Button
        btnZapisi.setOnClickListener {
            val value = picker.value
            dialog3.dismiss()
            val radioBtnOdredeno =
                dialog!!.findViewById<RecyclerView>(R.id.odredeno_rb) as RadioButton
            radioBtnOdredeno.text = "Određeni broj dana: " + value.toString()
        }

        //dialog2.setTitle("Novi lijek")
        dialog3.show()
    }

    private fun ucitajSpecDaniDialog() {
        val dialog4 = Dialog(this)
        dialog4.setContentView(R.layout.spec_dani_dialog)
        dialog4.setCanceledOnTouchOutside(false)

        val btnZapisi = dialog4.findViewById<View>(R.id.btnZapisi) as Button
        btnZapisi.setOnClickListener {
            val odabraniDani = ucitajOdabraneDane(dialog4)
            odabraniDaniUzimanja = ucitajOdabraneDaneInt(dialog4)

            if (odabraniDani.equals("")) {
                MaterialAlertDialogBuilder(this)
                    .setMessage("Odaberite bar jedan dan!")
                    .setPositiveButton("OK") { dialog, which ->
                        // Respond to positive button press
                    }
                    .show()
            } else {
                dialog4.dismiss()
                val radioBtnSpecDani =
                    dialog!!.findViewById<RecyclerView>(R.id.odredeni_dani_rb) as RadioButton
                radioBtnSpecDani.text = "Određeni dani: " + odabraniDani
            }
        }

        //dialog2.setTitle("Novi lijek")
        dialog4.show()
    }

    private fun ucitajOdabraneDane(dialog: Dialog): String {
        val pon = dialog.findViewById<View>(R.id.pon) as CheckBox
        val uto = dialog.findViewById<View>(R.id.uto) as CheckBox
        val sri = dialog.findViewById<View>(R.id.sri) as CheckBox
        val cet = dialog.findViewById<View>(R.id.cet) as CheckBox
        val pet = dialog.findViewById<View>(R.id.pet) as CheckBox
        val sub = dialog.findViewById<View>(R.id.sub) as CheckBox
        val ned = dialog.findViewById<View>(R.id.ned) as CheckBox
        var str = ""
        if (pon.isChecked) str += " Pon"
        if (uto.isChecked) str += " Uto"
        if (sri.isChecked) str += " Sri"
        if (cet.isChecked) str += " Čet"
        if (pet.isChecked) str += " Pet"
        if (sub.isChecked) str += " Sub"
        if (ned.isChecked) str += " Ned"
        return str
    }

    private fun ucitajOdabraneDaneInt(dialog: Dialog): MutableList<Int> {
        val pon = dialog.findViewById<View>(R.id.pon) as CheckBox
        val uto = dialog.findViewById<View>(R.id.uto) as CheckBox
        val sri = dialog.findViewById<View>(R.id.sri) as CheckBox
        val cet = dialog.findViewById<View>(R.id.cet) as CheckBox
        val pet = dialog.findViewById<View>(R.id.pet) as CheckBox
        val sub = dialog.findViewById<View>(R.id.sub) as CheckBox
        val ned = dialog.findViewById<View>(R.id.ned) as CheckBox
        var dani = mutableListOf<Int>()
        if (pon.isChecked) dani.add(1)
        if (uto.isChecked) dani.add(2)
        if (sri.isChecked) dani.add(3)
        if (cet.isChecked) dani.add(4)
        if (pet.isChecked) dani.add(5)
        if (sub.isChecked) dani.add(6)
        if (ned.isChecked) dani.add(7)
        return dani
    }

    private fun napuniListuObavijesti() {
        obavijestiList = ArrayList()

        val spinnerObavijesti =
            dialog!!.findViewById<View>(R.id.spinnerObavijesti) as Spinner
        val kol = spinnerObavijesti.selectedItemPosition

        obavijestiList.clear()
        obavijestiList.addAll(mapaObavijesti[kol + 1]!!.toList())

        val obavijestiRecycler = dialog!!.findViewById<RecyclerView>(R.id.recyclerObavijesti)
        obavijestiRecycler.layoutManager = LinearLayoutManager(this)
        obavijestiArrayAdapter = ObavijestiArrayAdapter(this, obavijestiList, this)
        obavijestiRecycler.adapter = obavijestiArrayAdapter
    }

    override fun onItemClick(obavijest: ObavijestLijek) {
        val dialog2 = Dialog(this)
        dialog2.setContentView(R.layout.time_picker_dialog)
        dialog2.setCanceledOnTouchOutside(true)

        val picker = dialog2.findViewById<View>(R.id.timePicker1) as TimePicker
        val kol = dialog2.findViewById<View>(R.id.kolicina_lijek_et) as EditText
        val jed = dialog2.findViewById<View>(R.id.jedinica_lijek_et) as EditText
        picker.setIs24HourView(true)
        picker.hour = Integer.parseInt(obavijest!!.vrijemeObavijesti.split(":")[0])
        picker.minute = Integer.parseInt(obavijest.vrijemeObavijesti.split(":")[1])
        try {
            kol.setText(obavijest.kolicina.split(" ")[0])
            jed.setText(obavijest.kolicina.split(" ")[1])
        } catch (e: Exception) {

        }

        val btnZapisi = dialog2.findViewById<View>(R.id.btnZapisi) as Button
        btnZapisi.setOnClickListener {
            var hourStr = ""
            var minuteStr = ""

            hourStr = if (picker.hour < 10)
                "0" + picker.hour.toString()
            else
                picker.hour.toString()

            minuteStr = if (picker.minute < 10)
                "0" + picker.minute.toString()
            else
                picker.minute.toString()

            obavijest.vrijemeObavijesti = "$hourStr:$minuteStr"
            obavijest.kolicina = kol.text.toString() + " " + jed.text.toString()

            obavijestiArrayAdapter!!.notifyDataSetChanged()
            dialog2.dismiss()
        }

        dialog2.setTitle("Novi lijek")
        dialog2.show()
    }

    override fun onItemClick(obavijestLijek: ObavijestLijek?, lijek: Lijek?) {
        val dialog2 = Dialog(this)
        dialog2.setContentView(R.layout.uzimanje_lijeka_dialog)
        dialog2.setCanceledOnTouchOutside(true)

        val naziv = dialog2.findViewById<View>(R.id.naziv_lijeka) as TextView
        val kol = dialog2.findViewById<View>(R.id.kolicina_lijek_et) as EditText
        val jed = dialog2.findViewById<View>(R.id.jedinica_lijek_et) as EditText

        naziv.setText(lijek!!.naziv)
        try {
            kol.setText(obavijestLijek!!.kolicina.split(" ")[0])
            jed.setText(obavijestLijek!!.kolicina.split(" ")[1])
        } catch (e: Exception) {
        }

        val btnZapisi = dialog2.findViewById<View>(R.id.btnZapisi) as Button
        val btnOdustani = dialog2.findViewById<View>(R.id.btnOdustani) as Button

        btnZapisi.setOnClickListener {
            obavijestLijek!!.uzetLijek = true
            //lijekoviObavijestiArrayAdapter!!.notifyDataSetChanged()

            val kol = dialog2.findViewById<View>(R.id.kolicina_lijek_et) as EditText
            val jed = dialog2.findViewById<View>(R.id.jedinica_lijek_et) as EditText
            val uzetaKolicina = kol.text.toString() + " " + jed.text.toString()

            obavijestLijek.kolicina = uzetaKolicina

            Log.e("ALARM", listaAlarma.toString())
            Log.e("obav", obavijestLijek.idObavijest.toString())
            try {
                val alarmZaIzbrisat =
                    listaAlarma.filter { alarm -> alarm.alarmId == obavijestLijek.idObavijest }
                        .first()
                alarmZaIzbrisat.cancelAlarm(this)
                Toast.makeText(
                    this,
                    "Izbrisan alarm " + obavijestLijek.vrijemeObavijesti,
                    LENGTH_LONG
                ).show()

            } catch (e: NoSuchElementException) {
            }

            viewModelLijekovi.putObavijest(obavijestLijek)

            dialog2.dismiss()
        }

        btnOdustani.setOnClickListener {
            dialog2.dismiss()
        }

        dialog2.setTitle("Uzimanje lijeka")
        dialog2.show()
    }
}