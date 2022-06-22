package hr.fer.ambint.medhelper.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.fer.ambint.medhelper.model.Lijek
import hr.fer.ambint.medhelper.model.LijekPost
import hr.fer.ambint.medhelper.model.LijekWrapper
import hr.fer.ambint.medhelper.model.ObavijestLijek
import hr.fer.ambint.medhelper.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LijekoviViewModel() : ViewModel() {

    private val KorisnikoviLijekoviLiveData: MutableLiveData<List<Lijek>> by lazy {
        MutableLiveData<List<Lijek>>()
    }

    private val ObavijestiLijekovaLiveData: MutableLiveData<List<ObavijestLijek>> by lazy {
        MutableLiveData<List<ObavijestLijek>>()
    }

    private val postLijekLiveData: MutableLiveData<Lijek> by lazy {
        MutableLiveData<Lijek>()
    }

    private val putObavijestLiveData: MutableLiveData<ObavijestLijek> by lazy {
        MutableLiveData<ObavijestLijek>()
    }

    private val postObavijestLiveData: MutableLiveData<ObavijestLijek> by lazy {
        MutableLiveData<ObavijestLijek>()
    }

    private val deleteObavijestLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val deleteLijekLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    private val errorMessageLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getDeleteObavijestLiveDataa(): MutableLiveData<String> {
        return deleteObavijestLiveData
    }

    fun getDeleteLijekLiveDataa(): MutableLiveData<String> {
        return deleteLijekLiveData
    }


    fun getObavijestiLijekoviLiveData(): MutableLiveData<List<ObavijestLijek>> {
        return ObavijestiLijekovaLiveData
    }

    fun getPutObavijestLiveDataa(): MutableLiveData<ObavijestLijek> {
        return putObavijestLiveData
    }

    fun getLijekoviLiveData(): MutableLiveData<List<Lijek>> {
        return KorisnikoviLijekoviLiveData
    }


    fun getPostLijekLiveData(): LiveData<Lijek> {
        return postLijekLiveData
    }

    fun getPostObavijestLiveData(): LiveData<ObavijestLijek> {
        return postObavijestLiveData
    }


    fun getKorisnikoviLijekovi(idKorisnik: Int) {
        ApiModule.retrofit.getKorisnikoveLijekove(idKorisnik)
            .enqueue(object : Callback<List<LijekWrapper>> {
                override fun onResponse(
                    call: Call<List<LijekWrapper>>,
                    response: Response<List<LijekWrapper>>
                ) {

                    KorisnikoviLijekoviLiveData.value = response.body()?.map {
                        Lijek(
                            it.lijek!!.idLijek,
                            it.lijek.naziv,
                            it.lijek.pocetniDatum,
                            it.lijek.trajanjeTerapije,
                            it.lijek.daniUzimanja
                        )
                    }
                }

                override fun onFailure(call: Call<List<LijekWrapper>>, t: Throwable) {
                    KorisnikoviLijekoviLiveData.value = null
                }
            })
    }

    fun getKorisnikoveObavijesti(idKorisnik: Int) {
        ApiModule.retrofit.getKorisnikoveObavijesti(idKorisnik)
            .enqueue(object : Callback<List<ObavijestLijek>> {
                override fun onResponse(
                    call: Call<List<ObavijestLijek>>,
                    response: Response<List<ObavijestLijek>>
                ) {
                    val test = response.body()
                    ObavijestiLijekovaLiveData.value = response.body()


                    /*val rezultat=response.body()?.map {
                        val lijek = Lijek(
                            it.lijek!!.idLijek,
                            it.lijek!!.naziv,
                            it.lijek!!.pocetniDatum,
                            it.lijek!!.trajanjeTerapije,
                            it.lijek!!.daniUzimanja)

                        ObavijestLijek(
                            it.idKorisnik,
                            it.idLijek,
                            it.idObavijest,
                            it.vrijemeObavijesti,
                            it.datumObavijesti,
                            it.kolicina,
                            it.uzetLijek,
                            lijek
                        )
                    }

                    ObavijestiLijekovaLiveData.value = response.body()?.map {
                        ObavijestLijek(
                            it.idKorisnik,
                            it.idLijek,
                            it.idObavijest,
                            it.vrijemeObavijesti,
                            it.datumObavijesti,
                            it.kolicina,
                            it.uzetLijek,
                            Lijek(
                                it.lijek!!.idLijek,
                                it.lijek!!.naziv,
                                it.lijek!!.pocetniDatum,
                                it.lijek!!.trajanjeTerapije,
                                it.lijek!!.daniUzimanja
                            )
                        )
                    }

                    println("a")*/
                }

                override fun onFailure(call: Call<List<ObavijestLijek>>, t: Throwable) {
                    ObavijestiLijekovaLiveData.value = null
                }
            })
    }


    fun postLijek(lijek: LijekPost) {
        ApiModule.retrofit.postNoviLijek(lijek)
            .enqueue(object : Callback<Lijek> {
                override fun onResponse(
                    call: Call<Lijek>,
                    response: Response<Lijek>
                ) {
                    postLijekLiveData.value = response.body()
                }

                override fun onFailure(call: Call<Lijek>, t: Throwable) {
                    postLijekLiveData.value = null
                }
            })
    }

    fun putObavijest(obavijest: ObavijestLijek) {
        ApiModule.retrofit.putObavijest(obavijest.idObavijest!!, obavijest)
            .enqueue(object : Callback<ObavijestLijek> {
                override fun onResponse(
                    call: Call<ObavijestLijek>,
                    response: Response<ObavijestLijek>
                ) {
                    putObavijestLiveData.value = response.body()
                }

                override fun onFailure(call: Call<ObavijestLijek>, t: Throwable) {
                    putObavijestLiveData.value = null
                }
            })
    }


    fun postObavijest(obavijest: ObavijestLijek) {
        ApiModule.retrofit.postNovaObavijest(obavijest)
            .enqueue(object : Callback<ObavijestLijek> {
                override fun onResponse(
                    call: Call<ObavijestLijek>,
                    response: Response<ObavijestLijek>
                ) {
                    postObavijestLiveData.value = response.body()
                }

                override fun onFailure(call: Call<ObavijestLijek>, t: Throwable) {
                    postObavijestLiveData.value = null
                }
            })
    }

    fun deleteLijek(idLijek: Int) {
        ApiModule.retrofit.deleteLijek(idLijek)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    deleteLijekLiveData.value = "Lijek uspješno izbrisan"
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    deleteLijekLiveData.value = "Greška kod brisanja lijeka"
                }
            })
    }

    fun deleteObavijest(idObavijest: Int) {
        ApiModule.retrofit.deleteObavijest(idObavijest)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    deleteObavijestLiveData.value = response.body()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    deleteObavijestLiveData.value = "Greška kod brisanja obavijesti"
                }
            })
    }

}