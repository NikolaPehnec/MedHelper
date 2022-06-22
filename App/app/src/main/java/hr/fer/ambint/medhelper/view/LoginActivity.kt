package hr.fer.ambint.medhelper.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import hr.fer.ambint.medhelper.Constants
import hr.fer.ambint.medhelper.NetworkChecker
import hr.fer.ambint.medhelper.R
import hr.fer.ambint.medhelper.viewModels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        ////////////////////TEST--///////////////////////
        ////////////////////TEST--///////////////////////

        /* test1.setOnClickListener {
             with(pref.edit()) {
                 this.putInt(Constants.ROLE_ID, 1)
                 this.apply()
             }

             val intent = Intent(this, StoreActivity::class.java)
             startActivity(intent)
         }*/
        ////////////////////TEST--///////////////////////
        ////////////////////TEST--///////////////////////


        loginButton.setOnClickListener {

            val networkChecker = NetworkChecker(this)
            if (!networkChecker.isOnline()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Greška")
                builder.setMessage("Nemoguća prijava, nemate internet konekciju")
                builder.setPositiveButton("Ok") { _, _ ->
                }

                builder.show()
            } else {

                loginPB.visibility = View.VISIBLE

                pref?.let { sharedPref ->
                    viewModel.login(
                        textLoginName.text.toString(),
                        textPassword.text.toString(),
                        sharedPref
                    )
                }

                with(pref?.edit()) {
                    this?.putString(
                        Constants.NAME_ID,
                        textLoginName.text.toString()
                    )
                    this?.putBoolean(Constants.LOGIN_SUCC, false)
                    this?.apply()
                }
            }
        }

        viewModel.getloginResultLiveData().observe(this) { loginResponse ->

            loginPB.visibility = View.GONE

            if (loginResponse != null) {

                with(pref?.edit()) {
                    Toast.makeText(applicationContext, "Uspješan login", Toast.LENGTH_SHORT).show()
                    this.putInt(Constants.USER_ID, loginResponse.userID!!)
                }
                setResult(RESULT_OK)

                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                //Navigiraj dalje
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Neuspješan login")
                if (viewModel.getMessage() != null) {
                    builder.setMessage(viewModel.getMessage())
                } else {
                    builder.setMessage("Neispravni podaci")
                }
                builder.setPositiveButton("Ok") { _, _ ->
                }

                builder.show()
            }
        }
    }
}