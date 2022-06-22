package hr.fer.ambint.medhelper

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.children
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    @SuppressLint("SetTextI18n")
    fun checkUser(pref: SharedPreferences, layoutView: List<ViewGroup>, viewLists: List<List<View>>){
        val name = pref.getString(Constants.NAME_ID, null)
        val role = pref.getInt(Constants.ROLE_ID, 0)
        Log.d("role", role.toString())
        for(lv in layoutView) {
            for (v in lv.children) {
                v.visibility = if (v in viewLists[role]) View.VISIBLE else View.GONE
            }
        }

    }

}

fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDataSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat(format, Locale.UK)
            setText("Početni datum: "+sdf.format(myCalendar.time))
        }

    setOnClickListener {
        DatePickerDialog(
            context, datePickerOnDataSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            show()
        }
    }
}