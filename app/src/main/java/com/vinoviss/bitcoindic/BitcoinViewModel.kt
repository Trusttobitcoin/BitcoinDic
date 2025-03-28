package com.vinoviss.bitcoindic.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vinoviss.bitcoindic.data.BitcoinRepository
import com.vinoviss.bitcoindic.data.BitcoinTerm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BitcoinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BitcoinRepository(application)

    private val _terms = MutableStateFlow<List<BitcoinTerm>>(emptyList())
    val terms: StateFlow<List<BitcoinTerm>> = _terms

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Current tab selection (0 for English, 1 for Farsi)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Search suggestions
    private val _searchSuggestions = MutableStateFlow<List<BitcoinTerm>>(emptyList())
    val searchSuggestions: StateFlow<List<BitcoinTerm>> = _searchSuggestions

    // Favorites
    private val _favorites = MutableStateFlow<Set<Int>>(loadFavorites())
    val favorites: StateFlow<Set<Int>> = _favorites

    // Add a dedicated StateFlow for favorite terms list
    private val _favoriteTermsList = MutableStateFlow<List<BitcoinTerm>>(emptyList())
    val favoriteTermsList: StateFlow<List<BitcoinTerm>> = _favoriteTermsList

    // Theme preference (false for light, true for dark)
    private val _isDarkTheme = MutableStateFlow(loadThemePreference())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    // All terms cache for suggestions and favorites
    private val _allTerms = MutableStateFlow<List<BitcoinTerm>>(emptyList())

    init {
        loadAllTerms()
        loadTermsOrderedByEngName()
    }

    private fun loadAllTerms() {
        viewModelScope.launch {
            repository.getAllTermsOrderedByEngName()
                .onEach { terms ->
                    _allTerms.value = terms
                    // Update favorite terms list whenever all terms are loaded
                    updateFavoriteTermsList()
                }
                .catch { e ->
                    _error.value = e.message
                }
                .launchIn(this)
        }
    }

    fun updateSearchSuggestions(query: String) {
        if (query.isBlank()) {
            _searchSuggestions.value = emptyList()
            return
        }

        val suggestions = if (_selectedTab.value == 0) {
            // English search - search in engname and description
            _allTerms.value.filter { term ->
                term.engname.contains(query, ignoreCase = true) ||
                        term.description.contains(query, ignoreCase = true)
            }
        } else {
            // Farsi search - search in faname and description
            _allTerms.value.filter { term ->
                term.faname.contains(query, ignoreCase = true) ||
                        term.description.contains(query, ignoreCase = true)
            }
        }

        // Limit suggestions to top 5
        _searchSuggestions.value = suggestions.take(5)
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
        if (tab == 0) {
            if (_searchQuery.value.isEmpty()) {
                loadTermsOrderedByEngName()
            } else {
                searchInEngName(_searchQuery.value)
            }
        } else {
            if (_searchQuery.value.isEmpty()) {
                loadTermsOrderedByFaName()
            } else {
                searchInFaName(_searchQuery.value)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        updateSearchResults()
    }

    private fun updateSearchResults() {
        if (_searchQuery.value.isEmpty()) {
            if (_selectedTab.value == 0) {
                loadTermsOrderedByEngName()
            } else {
                loadTermsOrderedByFaName()
            }
            _searchSuggestions.value = emptyList()
        } else {
            if (_selectedTab.value == 0) {
                searchInAllFields(_searchQuery.value)
            } else {
                searchInAllFields(_searchQuery.value)
            }
        }
    }

    fun loadTermsOrderedByEngName() {
        _isLoading.value = true
        _error.value = null

        repository.getAllTermsOrderedByEngName()
            .onEach { terms ->
                _terms.value = terms
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    fun loadTermsOrderedByFaName() {
        _isLoading.value = true
        _error.value = null

        repository.getAllTermsOrderedByFaName()
            .onEach { terms ->
                _terms.value = terms
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    fun searchInEngName(query: String) {
        _isLoading.value = true
        _error.value = null

        repository.searchByEngName(query)
            .onEach { terms ->
                _terms.value = terms
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    fun searchInFaName(query: String) {
        _isLoading.value = true
        _error.value = null

        repository.searchByFaName(query)
            .onEach { terms ->
                _terms.value = terms
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    // Search in all fields including description
    private fun searchInAllFields(query: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Use cached data for faster search
                val results = if (_selectedTab.value == 0) {
                    // English search
                    _allTerms.value.filter { term ->
                        term.engname.contains(query, ignoreCase = true) ||
                                term.description.contains(query, ignoreCase = true)
                    }
                } else {
                    // Farsi search
                    _allTerms.value.filter { term ->
                        term.faname.contains(query, ignoreCase = true) ||
                                term.description.contains(query, ignoreCase = true)
                    }
                }

                _terms.value = results
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    // Favorites functionality
    fun toggleFavorite(termId: Int) {
        val currentFavorites = _favorites.value.toMutableSet()
        if (currentFavorites.contains(termId)) {
            currentFavorites.remove(termId)
        } else {
            currentFavorites.add(termId)
        }
        _favorites.value = currentFavorites
        saveFavorites(currentFavorites)

        // Update the favorite terms list immediately after toggling
        updateFavoriteTermsList()
    }

    // Update the favorite terms list based on current favorites
    private fun updateFavoriteTermsList() {
        val favoriteIds = _favorites.value
        _favoriteTermsList.value = _allTerms.value.filter { term -> favoriteIds.contains(term.id) }
    }

    // Theme functionality
    fun toggleTheme() {
        val newThemeValue = !_isDarkTheme.value
        _isDarkTheme.value = newThemeValue
        saveThemePreference(newThemeValue)
    }

    // Original function kept for backward compatibility
    fun getFavoriteTerms(): List<BitcoinTerm> {
        // Update favorites list first to ensure it's current
        updateFavoriteTermsList()
        return _favoriteTermsList.value
    }

    // Persistence for favorites using SharedPreferences
    private fun saveFavorites(favorites: Set<Int>) {
        val prefs = getApplication<Application>().getSharedPreferences("bitcoin_dictionary_prefs", Application.MODE_PRIVATE)
        prefs.edit()
            .putStringSet("favorites", favorites.map { it.toString() }.toSet())
            .apply()
    }

    private fun loadFavorites(): Set<Int> {
        val prefs = getApplication<Application>().getSharedPreferences("bitcoin_dictionary_prefs", Application.MODE_PRIVATE)
        val stringSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    // Persistence for theme preference
    private fun saveThemePreference(isDark: Boolean) {
        val prefs = getApplication<Application>().getSharedPreferences("bitcoin_dictionary_prefs", Application.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("dark_theme", isDark)
            .apply()
    }

    private fun loadThemePreference(): Boolean {
        val prefs = getApplication<Application>().getSharedPreferences("bitcoin_dictionary_prefs", Application.MODE_PRIVATE)
        return prefs.getBoolean("dark_theme", false) // Default to light theme
    }
}