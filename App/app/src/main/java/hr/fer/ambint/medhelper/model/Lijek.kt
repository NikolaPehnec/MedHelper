package hr.fer.ambint.medhelper.model

import com.google.gson.annotations.SerializedName

class LijekWrapper(

) {
    @SerializedName("lijek")
    val lijek: Lijek? = null
}

class Lijek(
    idLijek: Int?,
    naziv: String,
    pocetniDatum: String,
    trajanjeTerapije: Int,
    daniUzimanja: List<Int>,
) {
    @SerializedName("idLijek")
    val idLijek: Int? = idLijek

    @SerializedName("naziv")
    var naziv: String = naziv

    @SerializedName("pocetniDatum")
    var pocetniDatum: String = pocetniDatum

    @SerializedName("trajanjeTerapije")
    var trajanjeTerapije: Int = trajanjeTerapije

    @SerializedName("daniUzimanja")
    var daniUzimanja: List<Int> = daniUzimanja


    override fun toString(): String {
        return "NAZIV: " + naziv + "\n Pocet. datum: " + pocetniDatum + "\n" + " Trajanje terapije: " + trajanjeTerapije + "\n" + " Dani uzimanja: " + daniUzimanja.toString()
    }
}

class LijekPost(
    naziv: String,
    pocetniDatum: String,
    trajanjeTerapije: Int,
    daniUzimanja: String,
) {
    @SerializedName("naziv")
    var naziv: String = naziv

    @SerializedName("pocetniDatum")
    var pocetniDatum: String = pocetniDatum

    @SerializedName("trajanjeTerapije")
    var trajanjeTerapije: Int = trajanjeTerapije

    @SerializedName("daniUzimanja")
    var daniUzimanja: String = daniUzimanja
}

