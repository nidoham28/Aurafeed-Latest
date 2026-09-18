package com.nidoham.aurafeed.features.auth.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.nidoham.aurafeed.ui.theme.AurafeedMotion
import com.nidoham.aurafeed.ui.theme.AurafeedTheme
import com.nidoham.aurafeed.ui.theme.AuraGlowSoft
import com.nidoham.aurafeed.ui.theme.AuraGlowStrong
import com.nidoham.aurafeed.ui.theme.AuraPrimary80
import com.nidoham.aurafeed.ui.theme.DuskGradientEnd
import com.nidoham.aurafeed.ui.theme.DuskGradientStart

/**
 * Desktop-only left pane: first-party campaign ads.
 *
 * Photography is optional — pass [art] to paint a full-bleed image per
 * campaign (Coil / composeResources). Without it, a cinematic dusk
 * gradient matching the campaign's color grade is used so the pane
 * never looks empty.
 *
 * Story-style progress bars auto-advance every 5.6s; hover/focus does
 * not exist as a pointer lock here (the HTML preview pauses on hover).
 */
data class AuthCampaign(
    val id: String,
    val kicker: String,
    val headline: String,
    val body: String,
    val gradient: List<Color>,
)

val DefaultAuthCampaigns: List<AuthCampaign> = listOf(
    AuthCampaign(
        id = "share",
        kicker = "Stories",
        headline = "Glow a little louder.",
        body = "Share the quiet moments that actually matter — without the noise.",
        gradient = listOf(Color(0xFF1A1020), DuskGradientStart, Color(0xFF5B2A8C)),
    ),
    AuthCampaign(
        id = "calm",
        kicker = "Feed",
        headline = "A calmer place to land.",
        body = "Less shouting. More of the people you actually chose to keep close.",
        gradient = listOf(Color(0xFF140C1C), Color(0xFF3D1B5C), Color(0xFF6E162E)),
    ),
    AuthCampaign(
        id = "secure",
        kicker = "Safety",
        headline = "You’ll know first.",
        body = "Unknown devices are flagged the moment they appear — quietly, immediately.",
        gradient = listOf(Color(0xFF0B0B12), DuskGradientEnd, Color(0xFF2B1245)),
    ),
)

private const val AdDurationMs = 5600

@Composable
fun AuthAdPanel(
    modifier: Modifier = Modifier,
    campaigns: List<AuthCampaign> = DefaultAuthCampaigns,
    art: @Composable (AuthCampaign) -> Unit = {},
) {
    val tokens = AurafeedTheme.tokens
    var index by remember { mutableIntStateOf(0) }
    val fill = remember { Animatable(0f) }

    LaunchedEffect(index, campaigns.size) {
        fill.snapTo(0f)
        fill.animateTo(1f, tween(AdDurationMs, easing = LinearEasing))
        index = (index + 1) % campaigns.size
    }

    val campaign = campaigns[index]

    Box(modifier = modifier.clip(RoundedCornerShape(0.dp))) {
        AnimatedContent(
            targetState = campaign.id,
            transitionSpec = {
                fadeIn(tween(AurafeedMotion.Long)) togetherWith fadeOut(tween(AurafeedMotion.MediumSlow))
            },
            label = "auth-ad-slide",
        ) { id ->
            val current = campaigns.first { it.id == id }
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(current.gradient)),
                )
                art(current)
                // Soft brand orbs so a missing photo still feels designed.
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
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color(0x480A0612),
                                0.45f to Color(0x140A0612),
                                1f to Color(0xE0080410),
                            ),
                        ),
                )
            }
        }

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
                AuthLogoMark(size = 32.dp)
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
                            letterSpacing = androidx.compose.ui.unit.TextUnit(
                                1.6f,
                                androidx.compose.ui.unit.TextUnitType.Sp,
                            ),
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
                        color = Color.White.copy(alpha = 0.78f),
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
                        i < index -> 1f
                        i == index -> fill.value
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

/** Optional full-bleed painter slot used by [AuthAdPanel.art]. */
@Composable
fun AuthCampaignArt(painter: androidx.compose.ui.graphics.painter.Painter?) {
    if (painter != null) {
        androidx.compose.foundation.Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}
