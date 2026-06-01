package com.paid.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paid.myapplication.data.JaapPrefs
import com.paid.myapplication.data.JaapSession
import com.paid.myapplication.data.JaapStats
import com.paid.myapplication.data.PrefsStore
import com.paid.myapplication.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsStore(application)
    private val gson  = Gson()

    // ── Auth / Onboarding ────────────────────────────────────────────────────
    private val _isAuthenticated = MutableStateFlow(
        runBlocking { prefs.isAuthenticated.first() }
    )
    private val _hasOnboarded = MutableStateFlow(
        runBlocking { prefs.hasOnboarded.first() }
    )

    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated.asStateFlow()
    val hasOnboarded: StateFlow<Boolean?> = _hasOnboarded.asStateFlow()

    init {
        viewModelScope.launch { prefs.isAuthenticated.collect { _isAuthenticated.value = it } }
        viewModelScope.launch { prefs.hasOnboarded.collect { _hasOnboarded.value = it } }
    }

    fun login() = viewModelScope.launch {
        prefs.setAuthenticated(true)
        _isAuthenticated.value = true
    }
    fun logout() = viewModelScope.launch {
        prefs.setAuthenticated(false)
        _isAuthenticated.value = false
    }
    fun completeOnboarding() = viewModelScope.launch {
        prefs.setOnboarded(true)
        _hasOnboarded.value = true
    }

    // ── Theme ────────────────────────────────────────────────────────────────
    val theme: StateFlow<String> = prefs.theme
        .stateIn(viewModelScope, SharingStarted.Eagerly, "dark")

    fun toggleTheme() = viewModelScope.launch {
        val current = prefs.theme.first()
        prefs.setTheme(if (current == "dark") "light" else "dark")
    }

    // ── User Profile ─────────────────────────────────────────────────────────
    val userProfile: StateFlow<UserProfile> = combine(
        prefs.userName, prefs.userCity, prefs.userState, prefs.userDeity
    ) { name, city, state, deity ->
        UserProfile(name = name, city = city, state = state, deity = deity)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UserProfile())

    fun saveProfile(name: String, city: String, state: String, deity: String) =
        viewModelScope.launch { prefs.saveUserProfile(name, city, state, deity) }

    // ── Jaap Stats ───────────────────────────────────────────────────────────
    private val _jaapStats = MutableStateFlow(JaapStats())
    val jaapStats: StateFlow<JaapStats> = _jaapStats.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.jaapStats.collect { json ->
                if (json.isNotBlank()) {
                    runCatching { gson.fromJson(json, JaapStats::class.java) }
                        .onSuccess { _jaapStats.value = it }
                }
            }
        }
    }

    fun recordJaapSession(count: Int, mantra: String, goal: Int) {
        if (count <= 0) return
        val today = LocalDate.now().toString()
        val prev  = _jaapStats.value
        val sessions = buildList {
            add(JaapSession(today, count, mantra, goal))
            addAll(prev.sessions.filter { it.date != today })
        }.take(30)

        val streak = when {
            prev.lastSessionDate == today -> prev.currentStreak
            prev.lastSessionDate == null  -> 1
            else -> {
                val days = ChronoUnit.DAYS.between(
                    LocalDate.parse(prev.lastSessionDate), LocalDate.parse(today)
                )
                if (days == 1L) prev.currentStreak + 1 else 1
            }
        }

        val updated = JaapStats(
            totalJaap       = prev.totalJaap + count,
            currentStreak   = streak,
            longestStreak   = maxOf(prev.longestStreak, streak),
            sessions        = sessions,
            lastSessionDate = today,
        )
        _jaapStats.value = updated
        viewModelScope.launch { prefs.saveJaapStats(gson.toJson(updated)) }
    }

    // ── Jaap Prefs ───────────────────────────────────────────────────────────
    private val _jaapPrefs = MutableStateFlow(JaapPrefs())
    val jaapPrefs: StateFlow<JaapPrefs> = _jaapPrefs.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.jaapPrefs.collect { json ->
                if (json.isNotBlank()) {
                    runCatching { gson.fromJson(json, JaapPrefs::class.java) }
                        .onSuccess { _jaapPrefs.value = it }
                }
            }
        }
    }

    private fun saveJaapPrefs(p: JaapPrefs) {
        _jaapPrefs.value = p
        viewModelScope.launch { prefs.saveJaapPrefs(gson.toJson(p)) }
    }

    fun toggleReminder() = saveJaapPrefs(_jaapPrefs.value.copy(reminderEnabled = !_jaapPrefs.value.reminderEnabled))
    fun setReminderTime(time: String) = saveJaapPrefs(_jaapPrefs.value.copy(reminderTime = time))
    fun addMantra(mantra: String) {
        val p = _jaapPrefs.value
        if (!p.savedMantras.contains(mantra))
            saveJaapPrefs(p.copy(savedMantras = p.savedMantras + mantra))
    }
}
