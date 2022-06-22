package hr.fer.ambint.medhelper.networking

import hr.fer.ambint.medhelper.model.*
import retrofit2.Call
import retrofit2.http.*


interface MedHelperApiService {

    @POST("korisnik/noviKorisnik")
    fun register(@Body user: RegisterRequest?): Call<Korisnik>

    @POST("korisnik/login")
    fun login(@Body user: LoginRequest?): Call<Korisnik>

    @GET("korisnik/korisnikLijekovi/{id_korisnik}")
    fun getKorisnikoveLijekove(@Path(value = "id_korisnik") idKorisnik: Int): Call<List<LijekWrapper>>

    @GET("obavijest/{id_korisnik}")
    fun getKorisnikoveObavijesti(@Path(value = "id_korisnik") idKorisnik: Int): Call<List<ObavijestLijek>>

    @POST("lijek/noviLijek")
    fun postNoviLijek(@Body lijek: LijekPost): Call<Lijek>

    @POST("obavijest/dodajObavijest")
    fun postNovaObavijest(@Body obavijest: ObavijestLijek): Call<ObavijestLijek>

    @PUT("obavijest/urediObavijest/{id_obavijest}")
    fun putObavijest(
        @Path(value = "id_obavijest") idObavijest: Int,
        @Body obavijest: ObavijestLijek
    ): Call<ObavijestLijek>

    @DELETE("lijek/{id_lijek}")
    fun deleteLijek(@Path(value = "id_lijek") idLijek: Int): Call<String>


    @DELETE("obavijest/{id_obavijest}")
    fun deleteObavijest(@Path(value = "id_obavijest") idObavijest: Int): Call<String>
}