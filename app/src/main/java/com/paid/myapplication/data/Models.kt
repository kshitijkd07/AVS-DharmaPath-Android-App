package com.paid.myapplication.data

data class JaapSession(
    val date: String,
    val count: Int,
    val mantra: String,
    val goal: Int,
)

data class JaapStats(
    val totalJaap: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val sessions: List<JaapSession> = emptyList(),
    val lastSessionDate: String? = null,
)

data class JaapPrefs(
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "06:00",
    val savedMantras: List<String> = listOf(
        "Om Namah Shivaya",
        "Om Bhur Bhuva Swaha (Gayatri)",
        "Hare Krishna Hare Rama",
        "Om Gan Ganapataye Namo Namah",
    ),
)

data class UserProfile(
    val name: String = "Kshitij",
    val city: String = "Agra",
    val state: String = "Uttar Pradesh",
    val deity: String = "Shiva",
) {
    val location: String get() = if (state.isNotBlank()) "$city, $state" else city
}

data class PujaItem(
    val id: Int,
    val title: String,
    val category: String,
    val deity: String,
    val duration: String,
    val rating: Double,
    val verified: Boolean,
)

data class PujaDetail(
    val title: String,
    val deity: String,
    val duration: String,
    val rating: Double,
    val verified: Boolean,
    val overview: String,
    val samagri: List<SamagriItem>,
    val steps: List<PujaStep>,
)

data class SamagriItem(val id: Int, val name: String, val checked: Boolean = false)

data class PujaStep(
    val step: Int,
    val title: String,
    val instruction: String,
    val mantra: String? = null,
)

val PUJA_LIST = listOf(
    PujaItem(1, "Daily Shiva Puja",          "Daily",    "Shiva",   "15 mins", 4.8, true),
    PujaItem(2, "Satyanarayan Katha",         "Occasion", "Vishnu",  "90 mins", 4.9, true),
    PujaItem(3, "Ganesh Chaturthi Sthapana",  "Festival", "Ganesha", "45 mins", 5.0, true),
    PujaItem(4, "Navratri Ghatasthapana",     "Festival", "Durga",   "60 mins", 4.7, true),
    PujaItem(5, "Hanuman Chalisa Path",       "Daily",    "Hanuman", "10 mins", 4.9, false),
)

val PUJA_DETAILS: Map<Int, PujaDetail> = mapOf(
    1 to PujaDetail(
        title = "Daily Shiva Puja", deity = "Shiva", duration = "15 mins", rating = 4.8, verified = true,
        overview = "A simple daily puja for peace and spiritual growth. Best after morning bath.",
        samagri = listOf(
            SamagriItem(1, "Shiva Linga or Idol"),
            SamagriItem(2, "Fresh Water (Jal)"),
            SamagriItem(3, "Raw Milk"),
            SamagriItem(4, "Bilva Patra"),
            SamagriItem(5, "Chandan & Diya"),
        ),
        steps = listOf(
            PujaStep(1, "Achamana",   "Three sips of water:",          "Om Keshavaya Namah..."),
            PujaStep(2, "Sankalpa",   "State your intent with water and rice."),
            PujaStep(3, "Abhishekam", "Bathe the Linga:",              "Om Namah Shivaya"),
            PujaStep(4, "Offering",   "Chandan, Bilva, Diya and incense."),
            PujaStep(5, "Aarti",      "Perform Aarti and seek forgiveness."),
        ),
    ),
)

val PANCHANG = mapOf(
    "tithi"     to "Shukla Paksha Ekadashi",
    "tithiShort" to "Ekadashi",
    "paksha"    to "Shukla Paksha",
    "nakshatra" to "Rohini",
    "sunrise"   to "06:14 AM",
    "sunset"    to "06:28 PM",
    "abhijit"   to "11:58 AM – 12:46 PM",
    "rahu"      to "04:30 PM – 06:00 PM",
)
