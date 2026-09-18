package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        TemplateAppScreen()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateAppScreen() {
  var verificationCount by remember { mutableIntStateOf(1) }
  var isAgentHelpVisible by remember { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = "Template Logo",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = stringResource(R.string.app_name),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
          }
        },
        actions = {
          IconButton(
            onClick = { isAgentHelpVisible = !isAgentHelpVisible },
            modifier = Modifier.testTag("action_info_button")
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Toggle Agent Guide"
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        HeroHeaderCard(
          verificationCount = verificationCount,
          onVerifyClick = { verificationCount++ }
        )
      }

      if (isAgentHelpVisible) {
        item {
          AgentQuickRefCard()
        }
      }

      item {
        Text(
          text = "Production CI/CD Capabilities",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      item {
        CiFeatureCard(
          icon = Icons.Default.Build,
          title = "GitHub Actions CI Pipeline",
          description = "Automated testing matrix executing Android Lint, JUnit tests, Robolectric JVM suites, and Roborazzi visual regression on every push & PR.",
          badge = "ci.yml"
        )
      }

      item {
        CiFeatureCard(
          icon = Icons.Default.VpnKey,
          title = "Keystore Signing & Release Flow",
          description = "Dual-mode signing supporting base64-decoded release keystores with seamless fallback to debug keys for local agent development.",
          badge = "release.yml"
        )
      }

      item {
        CiFeatureCard(
          icon = Icons.Default.Security,
          title = "Automated Keystore Generator",
          description = "On-demand GitHub workflow & helper scripts for Linux, macOS, and Windows to create RSA 2048 production keystores with a single command.",
          badge = "generate-keystore.yml"
        )
      }

      item {
        Text(
          text = "Environment Diagnostics",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      item {
        DiagnosticsCard(verificationCount = verificationCount)
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroHeaderCard(
  verificationCount: Int,
  onVerifyClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("hero_header_card"),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(Color(0xFF2E7D32))
          )
          Text(
            text = "TEMPLATE READY",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ) {
          Text(
            text = "API 36",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Text(
        text = "Android AI Agent Starter",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer
      )

      Text(
        text = "Pre-configured with full GitHub Actions testing & release pipelines, automated keystore workflows, Roborazzi screenshots, and Material 3 Compose architecture.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
      )

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TechChip(label = "Compose M3")
        TechChip(label = "Robolectric")
        TechChip(label = "Roborazzi")
        TechChip(label = "GitHub CI/CD")
        TechChip(label = "Keytool Scripts")
      }

      Spacer(modifier = Modifier.height(4.dp))

      Button(
        onClick = onVerifyClick,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("btn_verify_template")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Verify System Health (Check #$verificationCount)")
      }
    }
  }
}

@Composable
fun TechChip(label: String) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
    )
  }
}

@Composable
fun CiFeatureCard(
  icon: ImageVector,
  title: String,
  description: String,
  badge: String
) {
  OutlinedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.outlinedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.size(24.dp)
        )
      }

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer
          ) {
            Text(
              text = badge,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onTertiaryContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun DiagnosticsCard(verificationCount: Int) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("diagnostics_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      DiagnosticRow(label = "Application ID", value = "com.aistudio.template.kpxmqz")
      DiagnosticRow(label = "Target SDK", value = "36 (Android 16)")
      DiagnosticRow(label = "Secrets Gradle Plugin", value = ".env / .env.example enabled")
      DiagnosticRow(label = "Signing Mode", value = "Dual (Release key + Debug fallback)")
      DiagnosticRow(label = "Health Check Status", value = "Pass ($verificationCount cycles verified)")
    }
  }
}

@Composable
fun DiagnosticRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface,
      fontFamily = FontFamily.Monospace
    )
  }
}

@Composable
fun AgentQuickRefCard() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("agent_guide_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.tertiaryContainer
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Agent Instructions",
          tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
          text = "Instructions for AI Agents",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onTertiaryContainer
        )
      }

      Text(
        text = "1. Consult AI_AGENT_GUIDE.md for detailed recipes.\n" +
               "2. Keep namespace unchanged while customizing applicationId.\n" +
               "3. Use GitHub Secrets (KEYSTORE_BASE64) to enable Google Play signed releases.\n" +
               "4. Use .env for secrets injected into BuildConfig.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onTertiaryContainer
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}

@Preview(showBackground = true)
@Composable
fun TemplateAppScreenPreview() {
  MyApplicationTheme { TemplateAppScreen() }
}

