package com.matthewparsons.hookline.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matthewparsons.hookline.domain.auth.AuthUser

/**
 * Placeholder home shown after sign-in until Phase 4 introduces the real
 * pattern history + new-pattern flow.
 */
@Composable
fun SignedInPlaceholderScreen(
    user: AuthUser,
    onSignOut: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Signed in as", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                text = user.displayName ?: user.email ?: user.id,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(48.dp))
            Text(
                text = "Pattern history and the new-pattern form land in Phase 4.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(48.dp))
            OutlinedButton(onClick = onSignOut) {
                Text("Sign out")
            }
        }
    }
}
