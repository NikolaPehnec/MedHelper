package hr.fer.ambint.medhelper.model

import com.google.gson.annotations.SerializedName

class ObavijestLijek(
    idKorisnik: Int?,
    idLijek: Int?,
    idObavijest: Int?,
    vrijemeObavijesti: String,
    datumObavijesti: String,
    kolicina: String,
    uzetLijek: Boolean,
    lijek: Lijek?,
) {
    @SerializedName("idKorisnik")
    var idKorisnik: Int? = idKorisnik

    @SerializedName("idLijek")
    var idLijek: Int? = idLijek

    @SerializedName("idObavijest")
    var idObavijest: Int? = idObavijest

    @SerializedName("vrijemeObavijesti")
    var vrijemeObavijesti: String = vrijemeObavijesti

    @SerializedName("datumObavijesti")
    var datumObavijesti: String = datumObavijesti

    @SerializedName("kolicina")
    var kolicina: String = kolicina

    @SerializedName("uzetLijek")
    var uzetLijek: Boolean = uzetLijek

    @SerializedName("lijek")
    var lijek: Lijek? = null
}

var mapaObavijesti: Map<Int, List<ObavijestLijek>> =
    mutableMapOf(
        1 to listOf(ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null)),
        2 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null)
        ),
        3 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "15:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null)
        ),
        4 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "12:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "17:40", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        5 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "11:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "15:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "19:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        6 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "10:10", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "13:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "16:35", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "19:45", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        7 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "09:40", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "12:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "15:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "17:40", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "20:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        8 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "09:15", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "11:30", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "13:50", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "16:05", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "18:25", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "20:40", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        9 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "09:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "11:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "13:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "15:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "17:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "19:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "21:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        10 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "08:45", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "10:30", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "12:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "14:05", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "15:50", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "17:40", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "19:25", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "21:10", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        11 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "08:35", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "10:10", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "11:45", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "13:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "15:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "16:35", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "18:10", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "19:45", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "21:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
        12 to listOf(
            ObavijestLijek(null, null, null, "07:00", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "08:25", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "09:55", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "11:20", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "12:45", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "14:15", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "15:40", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "17:10", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "18:35", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "20:05", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "21:30", "", "1 kom", false, null),
            ObavijestLijek(null, null, null, "23:00", "", "1 kom", false, null),
        ),
    )