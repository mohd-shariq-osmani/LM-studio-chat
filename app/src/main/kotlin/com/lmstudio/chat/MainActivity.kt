package com.lmstudio.chat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.lmstudio.chat.data.local.datastore.SettingsDataStore
import com.lmstudio.chat.navigation.AppNavigation
import com.lmstudio.chat.theme.AccentPrimary
import com.lmstudio.chat.theme.Background
import com.lmstudio.chat.theme.LmStudioTheme
import com.lmstudio.chat.theme.TextPrimary
import com.lmstudio.chat.util.BiometricAuthenticator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authenticator = BiometricAuthenticator(this)

        setContent {
            LmStudioTheme {
                var isUnlocked by remember { mutableStateOf(false) }
                var lockEnabled by remember { mutableStateOf(true) }
                var authError by remember { mutableStateOf("") }
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    val enabled = settingsDataStore.appLockEnabled.first()
                    lockEnabled = enabled
                    if (!enabled) {
                        isUnlocked = true
                    } else {
                        // Trigger authentication automatically if available
                        if (authenticator.isBiometricAvailable()) {
                            authenticator.authenticate(
                                activity = this@MainActivity,
                                onSuccess = { isUnlocked = true },
                                onError = { authError = it }
                            )
                        } else {
                            // Fallback if no biometric hardware or credentials setup
                            isUnlocked = true
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isUnlocked) {
                        AppNavigation()
                    } else {
                        // Premium dark Lock Screen layout
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Background),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint Lock",
                                tint = AccentPrimary,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "App Locked",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Use fingerprint to access chats",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (authError.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = authError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    authenticator.authenticate(
                                        activity = this@MainActivity,
                                        onSuccess = { isUnlocked = true },
                                        onError = { authError = it }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                            ) {
                                Text("Unlock", color = Background)
                            }
                        }
                    }
                }
            }
        }
    }
}
