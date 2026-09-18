package com.nidoham.aurafeed.features.auth.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aurafeed.shared.generated.resources.Res
import aurafeed.shared.generated.resources.ad_calm
import aurafeed.shared.generated.resources.ad_secure
import aurafeed.shared.generated.resources.ad_share
import aurafeed.shared.generated.resources.app_logo
import aurafeed.shared.generated.resources.google
import com.nidoham.aurafeed.ui.theme.AuraGlowSoft
import com.nidoham.aurafeed.ui.theme.AuraGlowStrong
import com.nidoham.aurafeed.ui.theme.AuraPrimary80
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Desktop-only left pane: first-party campaign ads.
 *
 * Story-style progress bars auto-advance every 5.6s.
 */
data class AuthCampaign(
    val id: String,
    val kicker: String,
    val headline: String,
    val body: String,
    val images: DrawableResource
)

val DefaultAuthCampaigns: List<AuthCampaign> = listOf(
    AuthCampaign(
        id = "share",
        kicker = "Stories",
        headline = "Glow a little louder.",
        body = "Share the quiet moments that actually matter — without the noise.",
        images = Res.drawable.ad_share
    ),
    AuthCampaign(
        id = "calm",
        kicker = "Feed",
        headline = "A calmer place to land.",
        body = "Less shouting. More of the people you actually chose to keep close.",
        images = Res.drawable.ad_calm
    ),
    AuthCampaign(
        id = "secure",
        kicker = "Safety",
        headline = "You’ll know first.",
        body = "Unknown devices are flagged the moment they appear — quietly, immediately.",
        images = Res.drawable.ad_secure
    ),
)

private const val AdDurationMs = 5600

@Composable
fun AuthAdPanel(
    modifier: Modifier = Modifier,
    campaigns: List<AuthCampaign> = DefaultAuthCampaigns,
) {
    if (campaigns.isEmpty()) return

    val tokens = AurafeedTheme.tokens
    var index by remember { mutableIntStateOf(0) }
    val safeIndex = index.coerceIn(0, campaigns.lastIndex)
    val fill = remember { Animatable(0f) }

    LaunchedEffect(safeIndex, campaigns.size) {
        fill.snapTo(0f)
        fill.animateTo(1f, tween(AdDurationMs, easing = LinearEasing))
        index = (safeIndex + 1) % campaigns.size
    }

    val campaign = campaigns[safeIndex]

    Box(modifier = modifier) {
        AnimatedContent(
            targetState = campaign.id,
            transitionSpec = {
                fadeIn(tween(AurafeedMotion.Long)) togetherWith fadeOut(tween(AurafeedMotion.MediumSlow))
            },
            label = "auth-ad-slide",
        ) { id ->
            val current = campaigns.firstOrNull { it.id == id } ?: campaign
            Box(Modifier.fillMaxSize()) {

                // 1. Image Background using Compose Multiplatform painterResource
                Image(
                    painter = painterResource(current.images),
                    contentDescription = current.headline,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 2. Base overlay to ensure readability
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Black.copy(alpha = 0.3f),
                                0.6f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.8f)
                            )
                        )
                )

                // 3. Soft brand orbs
                Box(
                    Modifier
                        .size(320.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 72.dp, y = (-48).dp)
                        .background(
                            Brush.radialGradient(listOf(AuraGlowStrong, AuraGlowSoft, Color.Transparent)),
                            CircleShape,
                        ),
                )
            }
        }

        // Header / Logo
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = tokens.spacing.xxl, vertical = tokens.spacing.xl),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                )
                Text(
                    text = "Aurafeed",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                )
            }
            Text(
                text = "Ad",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                ),
                color = Color.White.copy(alpha = 0.86f),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.38f))
                    .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }

        // Footer Text and Progress Bars
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = tokens.spacing.xxl, vertical = tokens.spacing.xxl),
        ) {
            AnimatedContent(targetState = campaign, label = "ad-copy") { item ->
                Column {
                    Text(
                        text = item.kicker.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp,
                        ),
                        color = AuraPrimary80,
                    )
                    Spacer(Modifier.height(tokens.spacing.sm))
                    Text(
                        text = item.headline,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Color.White,
                        modifier = Modifier.widthIn(max = 420.dp),
                    )
                    Spacer(Modifier.height(tokens.spacing.sm))
                    Text(
                        text = item.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.widthIn(max = 420.dp),
                    )
                }
            }

            Spacer(Modifier.height(tokens.spacing.xl))

            Row(
                modifier = Modifier.widthIn(max = 220.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                campaigns.forEachIndexed { i, _ ->
                    val fraction = when {
                        i < safeIndex -> 1f
                        i == safeIndex -> fill.value
                        else -> 0f
                    }
                    val tickInteraction = remember(i) { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.28f))
                            .clickable(
                                role = Role.Tab,
                                indication = null,
                                interactionSource = tickInteraction,
                            ) { index = i },
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(fraction)
                                .height(3.dp)
                                .background(Color.White, RoundedCornerShape(2.dp)),
                        )
                    }
                }
            }
        }
    }
}