package com.vinoviss.bitcoindic
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import com.vinoviss.bitcoindic.ui.theme.BitcoinOrange
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vinoviss.bitcoindic.data.BitcoinTerm
import com.vinoviss.bitcoindic.ui.BitcoinViewModel
import java.lang.Math.max
import java.lang.Math.min

enum class Screen {
    HOME, FAVORITES, ABOUT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitcoinDictionaryScreen() {
    val viewModel: BitcoinViewModel = viewModel()
    val terms by viewModel.terms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favoriteTermsList by viewModel.favoriteTermsList.collectAsState()

    var activeSearch by rememberSaveable { mutableStateOf(false) }
    var selectedTerm by remember { mutableStateOf<BitcoinTerm?>(null) }
    var currentScreen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var showExitDialog by remember { mutableStateOf(false) }

    val activity = LocalContext.current as? Activity

    // Handle back press
    BackHandler {
        if (selectedTerm != null) {
            selectedTerm = null
        } else if (currentScreen != Screen.HOME) {
            currentScreen = Screen.HOME
        } else {
            showExitDialog = true
        }
    }

    // Exit confirmation dialog
    if (showExitDialog) {
        AlertDialog(
            title = { Text("خروج از برنامه", textAlign = TextAlign.Right) },
            text = { Text("آیا می‌خواهید از برنامه خارج شوید؟", textAlign = TextAlign.Right) },
            onDismissRequest = { showExitDialog = false },
            confirmButton = {
                Button(onClick = { activity?.finish() }) {
                    Text("بله")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("خیر")
                }
            }
        )
    }

    // Set layout direction based on selected tab
    CompositionLocalProvider(
        LocalLayoutDirection provides if (selectedTab == 1) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomAppBar {
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = {
                            currentScreen = Screen.HOME
                            selectedTerm = null
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(if (selectedTab == 1) "خانه" else "Home") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.FAVORITES,
                        onClick = {
                            currentScreen = Screen.FAVORITES
                            selectedTerm = null
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                        label = { Text(if (selectedTab == 1) "علاقه‌مندی‌ها" else "Favorites") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.ABOUT,
                        onClick = { currentScreen = Screen.ABOUT },
                        icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                        label = { Text(if (selectedTab == 1) "درباره" else "About") }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Tabs for selecting language - only show in dictionary screens
                if (currentScreen != Screen.ABOUT) {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { viewModel.selectTab(0) },
                            text = { Text("English") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { viewModel.selectTab(1) },
                            text = { Text("فارسی") }
                        )
                    }
                }

                // Search - only show in dictionary screens
                if (currentScreen != Screen.ABOUT) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            viewModel.setSearchQuery(it)
                            viewModel.updateSearchSuggestions(it)
                        },
                        onSearch = {
                            activeSearch = false
                            viewModel.setSearchQuery(it)
                        },
                        active = activeSearch,
                        onActiveChange = { activeSearch = it },
                        placeholder = { Text(if (selectedTab == 1) "جستجو" else "Search") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Search suggestions
                        if (searchSuggestions.isNotEmpty()) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 160.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(searchSuggestions) { suggestion ->
                                    TermGridItem(
                                        term = suggestion,
                                        isRtl = selectedTab == 1,
                                        onClick = {
                                            viewModel.setSearchQuery("")
                                            activeSearch = false
                                            selectedTerm = suggestion
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Content area
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        Screen.HOME -> {
                            if (selectedTerm != null) {
                                // Term detail with scrollable description
                                TermDetailScreen(
                                    term = selectedTerm!!,
                                    isRtl = selectedTab == 1,
                                    isFavorite = favorites.contains(selectedTerm!!.id),
                                    onBack = { selectedTerm = null },
                                    onToggleFavorite = { viewModel.toggleFavorite(selectedTerm!!.id) }
                                )
                            } else {
                                // Dictionary content with grid layout
                                DictionaryContent(
                                    terms = terms,
                                    isLoading = isLoading,
                                    error = error,
                                    isRtl = selectedTab == 1,
                                    onTermClick = { selectedTerm = it }
                                )
                            }
                        }
                        Screen.FAVORITES -> {
                            if (selectedTerm != null) {
                                // Term detail with scrollable description
                                TermDetailScreen(
                                    term = selectedTerm!!,
                                    isRtl = selectedTab == 1,
                                    isFavorite = favorites.contains(selectedTerm!!.id),
                                    onBack = { selectedTerm = null },
                                    onToggleFavorite = { viewModel.toggleFavorite(selectedTerm!!.id) }
                                )
                            } else {
                                // Favorites content with grid layout and remove option
                                FavoritesScreen(
                                    favorites = favoriteTermsList, // Use the StateFlow directly instead of calling the function
                                    isRtl = selectedTab == 1,
                                    onTermClick = { selectedTerm = it },
                                    onRemoveFavorite = { termId -> viewModel.toggleFavorite(termId) }
                                )
                            }
                        }
                        Screen.ABOUT -> {
                            AboutScreen(isRtl = selectedTab == 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DictionaryContent(
    terms: List<BitcoinTerm>,
    isLoading: Boolean,
    error: String?,
    isRtl: Boolean,
    onTermClick: (BitcoinTerm) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (error != null) {
            Text(
                text = "Error: ${error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else if (terms.isEmpty()) {
            Text(
                text = if (isRtl) "هیچ موردی یافت نشد" else "No terms found",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(terms) { term ->
                    TermGridItem(
                        term = term,
                        isRtl = isRtl,
                        onClick = { onTermClick(term) }
                    )
                }
            }
        }
    }
}
@Composable
fun TermGridItem(
    term: BitcoinTerm,
    isRtl: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MetallicSilver,  // Metallic silver background
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BitcoinOrange)  // Orange border
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isRtl) {
                // Bitcoin logo
                Image(
                    painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = if (isRtl) Alignment.End else Alignment.Start
            ) {
                // Primary term
                Text(
                    text = if (isRtl) term.faname else term.engname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                )

                // Secondary term
                Text(
                    text = if (isRtl) term.engname else term.faname,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                )
            }

            if (isRtl) {
                Spacer(modifier = Modifier.width(12.dp))

                // Bitcoin logo for RTL layout
                Image(
                    painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}


@Composable
fun FavoritesScreen(
    favorites: List<BitcoinTerm>,
    isRtl: Boolean,
    onTermClick: (BitcoinTerm) -> Unit,
    onRemoveFavorite: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp),
                    alpha = 0.5f
                )

                Text(
                    text = if (isRtl) "هیچ علاقه‌مندی ثبت نشده است" else "No favorites saved",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.DarkGray
                )

                Text(
                    text = if (isRtl)
                        "با کلیک بر روی نماد قلب، اصطلاحات را به علاقه‌مندی‌ها اضافه کنید"
                    else
                        "Tap the heart icon to add terms to your favorites",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = favorites,
                    key = { it.id } // Use ID as key for stable identities
                ) { term ->
                    FavoriteGridItem(
                        term = term,
                        isRtl = isRtl,
                        onClick = { onTermClick(term) },
                        onRemove = { onRemoveFavorite(term.id) }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteGridItem(
    term: BitcoinTerm,
    isRtl: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MetallicSilver,
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BitcoinOrange),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isRtl) {
                    // Bitcoin logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = if (isRtl) Alignment.End else Alignment.Start
                ) {
                    // Primary term
                    Text(
                        text = if (isRtl) term.faname else term.engname,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                    )

                    // Secondary term
                    Text(
                        text = if (isRtl) term.engname else term.faname,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                    )
                }

                if (isRtl) {
                    Spacer(modifier = Modifier.width(12.dp))

                    // Bitcoin logo for RTL layout
                    Image(
                        painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Improved remove button with larger clickable area
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(if (isRtl) Alignment.TopStart else Alignment.TopEnd)
                    .padding(4.dp)
                    .clickable { showDeleteConfirm = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = if (isRtl) "حذف از علاقه‌مندی‌ها" else "Remove from favorites",
                    tint = BitcoinOrange,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

    // Show confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            containerColor = MetallicSilver,
            title = {
                Text(
                    text = if (isRtl) "حذف از علاقه‌مندی‌ها" else "Remove from favorites",
                    textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isRtl)
                        "آیا مطمئن هستید که می‌خواهید این مورد را از علاقه‌مندی‌ها حذف کنید؟"
                    else
                        "Are you sure you want to remove this item from your favorites?",
                    textAlign = if (isRtl) TextAlign.Right else TextAlign.Left
                )
            },
            onDismissRequest = { showDeleteConfirm = false },
            confirmButton = {
                Button(
                    onClick = {
                        // Call onRemove and close dialog
                        onRemove()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BitcoinOrange
                    )
                ) {
                    Text(if (isRtl) "حذف" else "Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(if (isRtl) "انصراف" else "Cancel")
                }
            }
        )
    }
}
// Define the silver metallic color
private val MetallicSilver = Color(0xFFE8E8E8).copy(alpha = 0.9f)
@Composable
fun AboutScreen(isRtl: Boolean) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = if (isRtl) "درباره دیکشنری بیت‌کوین" else "About Bitcoin Dictionary",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bitcoin Logo
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(vertical = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App description
        Text(
            text = if (isRtl)
                "این اپلیکیشن یک دیکشنری دوزبانه برای اصطلاحات بیت‌کوین است. شما می‌توانید اصطلاحات را به انگلیسی و فارسی جستجو کنید و موارد مورد علاقه خود را ذخیره نمایید. تمام داده های این نرم افزار از وبسایت bitcoind.me جمع آوری شده است."
            else
                "This application is a bilingual dictionary for Bitcoin terminology. You can search terms in both English and Persian, save favorites, and adjust text size for better readability. All data for this application has been collected from bitcoind.me website.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Developer info
        Text(
            text = if (isRtl) "توسعه‌دهنده: Zero" else "Developer: Zero",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Data source
        Text(
            text = if (isRtl) "منبع داده‌ها: bitcoind.me" else "Data source: bitcoind.me",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Version info
        Text(
            text = if (isRtl) "نسخه: 1.0" else "Version: 1.0",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TermDetailScreen(
    term: BitcoinTerm,
    isRtl: Boolean,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    // Use ScrollState for scrollable content
    val scrollState = rememberScrollState()

    // Font size state to control text scaling
    var fontSizeMultiplier by remember { mutableStateOf(1f) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // Add scrolling capability
            .padding(16.dp)
    ) {
        // Back and action buttons row
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isRtl) "← فهرست" else "← Back to list",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(bottom = 16.dp)
                    .align(if (isRtl) Alignment.CenterEnd else Alignment.CenterStart),
                textAlign = if (isRtl) TextAlign.Right else TextAlign.Left
            )

            // Action buttons row
            Row(
                modifier = Modifier.align(if (isRtl) Alignment.CenterStart else Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Decrease font size button
                IconButton(onClick = { fontSizeMultiplier = max(0.7f, fontSizeMultiplier - 0.1f) }) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = if (isRtl) "کاهش اندازه فونت" else "Decrease font size"
                    )
                }

                // Increase font size button
                IconButton(onClick = { fontSizeMultiplier = min(1.5f, fontSizeMultiplier + 0.1f) }) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = if (isRtl) "افزایش اندازه فونت" else "Increase font size"
                    )
                }

                // Share button
                IconButton(
                    onClick = {
                        // Create share intent
                        val shareText = buildString {
                            append(term.engname)
                            append("\n")
                            append(term.faname)
                            append("\n\n")
                            append(term.description)
                        }

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(
                            sendIntent,
                            if (isRtl) "اشتراک‌گذاری محتوا" else "Share content"
                        )
                        context.startActivity(shareIntent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = if (isRtl) "اشتراک‌گذاری" else "Share"
                    )
                }

                // Favorite button
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Primary term in appropriate language - with font size scaling
        Text(
            text = if (isRtl) term.faname else term.engname,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * fontSizeMultiplier
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        // Secondary term in other language - with font size scaling
        Text(
            text = if (isRtl) term.engname else term.faname,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = MaterialTheme.typography.titleMedium.fontSize * fontSizeMultiplier
            ),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Right
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = if (isRtl) "توضیحات" else "Description",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = MaterialTheme.typography.titleMedium.fontSize * fontSizeMultiplier
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        // Scrollable description with right-to-left alignment and font size scaling
        Text(
            text = term.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontSizeMultiplier
            ),
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Right  // Always RTL for descriptions
        )
    }
}