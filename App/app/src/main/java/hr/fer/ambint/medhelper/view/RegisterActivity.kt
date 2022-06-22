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
import hr.fer.ambint.medhelper.R
import hr.fer.ambint.medhelper.viewModels.RegisterViewModel
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        sendRegistrationBtn.setOnClickListener {

            if (regName.text.toString().equals("") || regSurname.text.toString()
                    .equals("") || regEmail.text.toString()
                    .equals("") || regPassword.text.toString().equals("")
            ) {
                Toast.makeText(this, "Unesite sve podatke ", Toast.LENGTH_LONG).show()
            } else {
                registrationPB.visibility = View.VISIBLE
                sharedPref?.let { sharedPref ->
                    viewModel.register(
                        regPassword.text.toString(),
                        regName.text.toString(),
                        regSurname.text.toString(),
                        regEmail.text.toString(),
                        sharedPref
                    )
                }

                with(sharedPref?.edit()) {
                    this?.putString(
                        Constants.NAME_ID,
                        regName.text.toString()
                    )
                    this?.putBoolean(Constants.LOGIN_SUCC, false)
                    this?.apply()
                }

            }

        }

        viewModel.getRegistrationResultLiveData().observe(this) { registrationResponse ->

            registrationPB.visibility = View.GONE

            if (registrationResponse) {

                with(sharedPref?.edit()) {
                    Toast.makeText(applicationContext, "Uspješna registracija", Toast.LENGTH_SHORT)
                        .show()
                }
                setResult(RESULT_OK)

                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                //Navigiraj dalje
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Neuspješna registracija")
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