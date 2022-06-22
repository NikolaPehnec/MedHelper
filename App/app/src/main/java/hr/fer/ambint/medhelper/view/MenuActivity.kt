package hr.fer.ambint.medhelper.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import hr.fer.ambint.medhelper.Constants
import hr.fer.ambint.medhelper.R
import hr.fer.ambint.medhelper.Utils
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : Activity() {

    private var viewLists: List<List<View>> = mutableListOf()
    private var notLoggedViewList: List<View> = mutableListOf()
    private var bolesnikViewList: List<View> = mutableListOf()
    private var odgovorniViewList: List<View> = mutableListOf()

    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        addViews()

        Utils.checkUser(
            pref,
            listOf(menuLayoutView, bottomLayout, menuScrollView, menuLinearLayout),
            viewLists
        )


        homeBtn.setOnClickListener {
            startActivity(
                generateIntent(HomeActivity::class.java)
            )
        }

        loginBtn.setOnClickListener {
            startActivity(
                generateIntent(LoginActivity::class.java)
            )
        }

        registerBtn.setOnClickListener {
            startActivity(
                generateIntent(RegisterActivity::class.java)
            )
        }

        medicationsBtn.setOnClickListener {
            startActivity(
                generateIntent(MedicationsActivity::class.java)
            )
        }

        historyBtn.setOnClickListener {
            startActivity(
                generateIntent(HistoryActivity::class.java)
            )
        }



        logoutBtn.setOnClickListener {
            pref.edit().clear().apply()
            Utils.checkUser(
                pref,
                listOf(menuLayoutView, menuScrollView, menuLinearLayout, bottomLayout),
                viewLists
            )
        }


    }

    private fun generateIntent(cls: Class<out Activity>): Intent {
        val intent = Intent(this, cls)
        // intent.putExtra(Constants.TOGGLE_STATE, businessSwitch.isChecked)

        return intent
    }

    private fun addViews() {

        notLoggedViewList = mutableListOf(
            menuScrollView, menuLinearLayout,
            bottomLayout, registerBtn, loginBtn, textTitleView
        )

        bolesnikViewList = mutableListOf(
            menuScrollView, menuLinearLayout,
            homeBtn, medicationsBtn, historyBtn,bottomLayout, logoutBtn, textTitleView
        )

        odgovorniViewList = mutableListOf(
            homeBtn,
            menuScrollView,
            menuLinearLayout,
            bottomLayout,
            historyBtn,
            logoutBtn,
            textTitleView
        )

        viewLists =
            mutableListOf(notLoggedViewList, bolesnikViewList, odgovorniViewList)
    }

}