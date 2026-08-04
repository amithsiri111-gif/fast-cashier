package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.ErrorBoundary
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CustomerSupportScreen
import com.example.ui.screens.DepositScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivacyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.UserGuideScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.theme.FastXbetTheme
import com.example.ui.theme.PrimaryGreen
import com.example.ui.util.LocaleHelper
import com.example.ui.viewmodel.CashierViewModel
import com.example.ui.viewmodel.UiMessage

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

sealed class NavRoutes(val route: String, val titleRes: Int, val icon: ImageVector) {
    object Home : NavRoutes("home", R.string.nav_home, Icons.Default.Home)
    object Deposit : NavRoutes("deposit", R.string.nav_deposit, Icons.Default.Payment)
    object Withdraw : NavRoutes("withdraw", R.string.nav_withdraw, Icons.Default.TrendingUp)
    object History : NavRoutes("history", R.string.nav_history, Icons.Default.History)
    object Profile : NavRoutes("profile", R.string.nav_profile, Icons.Default.AccountCircle)
    object Admin : NavRoutes("admin", R.string.nav_admin, Icons.Default.AdminPanelSettings)
    object Privacy : NavRoutes("privacy", R.string.privacy_title, Icons.Default.AccountCircle)
    object Guide : NavRoutes("guide", R.string.menu_guide, Icons.Default.Home)
    object Support : NavRoutes("support", R.string.welcome_title, Icons.Default.HeadsetMic)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: CashierViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, "si"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userState by viewModel.userState.collectAsState()
            val context = LocalContext.current
            val currentLang = userState?.language ?: "si"

            val localeContext = remember(currentLang) {
                LocaleHelper.setLocale(context, currentLang)
            }

            key(currentLang) {
                CompositionLocalProvider(
                    LocalContext provides localeContext,
                    LocalConfiguration provides localeContext.resources.configuration,
                    LocalActivityResultRegistryOwner provides this
                ) {
                    FastXbetTheme {
                        ErrorBoundary {
                            CashierAppContent(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierAppContent(viewModel: CashierViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val uiMessage by viewModel.uiMessage.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.Home.route

    val bottomNavItems = listOf(
        NavRoutes.Home,
        NavRoutes.Deposit,
        NavRoutes.Withdraw,
        NavRoutes.History,
        NavRoutes.Profile
    )

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            val text = when (msg) {
                is UiMessage.SuccessRes -> "✅ " + context.getString(msg.resId, *msg.args.toTypedArray())
                is UiMessage.ErrorRes -> "⚠️ " + context.getString(msg.resId, *msg.args.toTypedArray())
                is UiMessage.Success -> "✅ ${msg.message}"
                is UiMessage.Error -> "⚠️ ${msg.message}"
            }
            snackbarHostState.showSnackbar(text)
            viewModel.clearUiMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val userState by viewModel.userState.collectAsState()
            val currentLang = userState?.language ?: "si"

            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(NavRoutes.Support.route) },
                        modifier = Modifier.testTag("top_app_bar_support_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = "Customer Support",
                            tint = PrimaryGreen
                        )
                    }
                    SingleLanguageButton(
                        currentLang = currentLang,
                        onLanguageSelected = { viewModel.setLanguage(it) }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            if (bottomNavItems.any { it.route == currentRoute }) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.titleRes)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.titleRes),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryGreen,
                                selectedTextColor = PrimaryGreen,
                                indicatorColor = PrimaryGreen.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Home.route
            ) {
                composable(NavRoutes.Home.route) {
                    HomeScreen(
                        onNavigateDeposit = { navController.navigate(NavRoutes.Deposit.route) },
                        onNavigateWithdraw = { navController.navigate(NavRoutes.Withdraw.route) },
                        onNavigateGuide = { navController.navigate(NavRoutes.Guide.route) },
                        onNavigatePrivacy = { navController.navigate(NavRoutes.Privacy.route) },
                        onNavigateSupport = { navController.navigate(NavRoutes.Support.route) }
                    )
                }

                composable(NavRoutes.Deposit.route) {
                    DepositScreen(viewModel = viewModel)
                }

                composable(NavRoutes.Withdraw.route) {
                    WithdrawScreen(viewModel = viewModel)
                }

                composable(NavRoutes.History.route) {
                    HistoryScreen(viewModel = viewModel)
                }

                composable(NavRoutes.Profile.route) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateAdmin = { navController.navigate(NavRoutes.Admin.route) },
                        onNavigateGuide = { navController.navigate(NavRoutes.Guide.route) },
                        onNavigateSupport = { navController.navigate(NavRoutes.Support.route) }
                    )
                }

                composable(NavRoutes.Admin.route) {
                    AdminScreen(viewModel = viewModel)
                }

                composable(NavRoutes.Privacy.route) {
                    PrivacyScreen()
                }

                composable(NavRoutes.Guide.route) {
                    UserGuideScreen()
                }

                composable(NavRoutes.Support.route) {
                    CustomerSupportScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun SingleLanguageButton(
    currentLang: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val (currentLabel, flag) = when (currentLang) {
        "si" -> "සිංහල" to "🇱🇰"
        "ta" -> "தமிழ்" to "🇱🇰"
        else -> "English" to "🇬🇧"
    }

    Box(modifier = Modifier.padding(end = 12.dp)) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(20.dp),
            color = PrimaryGreen.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.5f)),
            modifier = Modifier.testTag("single_language_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$flag $currentLabel",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "🇱🇰 සිංහල (Sinhala)",
                        fontWeight = if (currentLang == "si") FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (currentLang == "si") PrimaryGreen else MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                },
                onClick = {
                    expanded = false
                    onLanguageSelected("si")
                },
                modifier = Modifier.testTag("lang_select_si")
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "🇱🇰 தமிழ் (Tamil)",
                        fontWeight = if (currentLang == "ta") FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (currentLang == "ta") PrimaryGreen else MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                },
                onClick = {
                    expanded = false
                    onLanguageSelected("ta")
                },
                modifier = Modifier.testTag("lang_select_ta")
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "🇬🇧 English",
                        fontWeight = if (currentLang == "en") FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (currentLang == "en") PrimaryGreen else MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                },
                onClick = {
                    expanded = false
                    onLanguageSelected("en")
                },
                modifier = Modifier.testTag("lang_select_en")
            )
        }
    }
}
