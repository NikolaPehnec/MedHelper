package hr.fer.ambint.medhelper.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.fer.ambint.medhelper.Constants
import hr.fer.ambint.medhelper.model.Korisnik
import hr.fer.ambint.medhelper.model.LoginRequest
import hr.fer.ambint.medhelper.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private var message: String? = ""

    private val loginResultLiveData: MutableLiveData<Korisnik> by lazy { MutableLiveData<Korisnik>() }

    fun getloginResultLiveData(): LiveData<Korisnik> {
        return loginResultLiveData
    }

    fun getMessage(): String? {
        return message
    }

    fun login(korisnickoIme: String, password: String, sharedPref: SharedPreferences) {
        ApiModule.retrofit.login(LoginRequest(korisnickoIme, password))
            .enqueue(object : Callback<Korisnik> {
                override fun onResponse(
                    call: Call<Korisnik>,
                    response: Response<Korisnik>
                ) {

                    if (response.isSuccessful) {
                        with(sharedPref.edit()) {
                            var uloga = 0;
                            var id = 0;
                            try {
                                if (response.body()?.uloga!!.equals("Bolesnik")) {
                                    uloga = 1
                                } else uloga = 2
                                id = response.body()?.userID!!
                            } catch (e: Exception) {
                            }

                            this.putInt(Constants.ROLE_ID, uloga)
                            this.putInt(Constants.USER_ID, response.body()?.userID!!)
                            this.apply()
                        }

                        loginResultLiveData.value = Korisnik(
                            response.body()!!.userID,
                            response.body()!!.ime,
                            response.body()!!.prezime,
                            response.body()!!.korisnickoIme,
                            response.body()!!.lozinka,
                            response.body()!!.uloga!!
                        )
                    } else {
                        message = response.errorBody()?.string()?.substringAfter("detail\":\"")
                            ?.substringBeforeLast("\"}")
                        loginResultLiveData.value=null
                    }


                }

                override fun onFailure(call: Call<Korisnik>, t: Throwable) {
                    loginResultLiveData.value = null
                }
            })
    }


}