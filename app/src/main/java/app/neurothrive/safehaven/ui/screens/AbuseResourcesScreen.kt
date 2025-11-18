package app.neurothrive.safehaven.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Abuse Resources & Education Screen
 *
 * Comprehensive educational library covering:
 * - All 6 types of abuse
 * - Emotional abuse identification (30+ tactics)
 * - DARVO tactics and responses
 * - Documentation strategies
 * - Safety planning
 *
 * Based on: SafeHaven Abuse Resources and Self-Help Guide.md
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbuseResourcesScreen(
    onBack: () -> Unit,
    onNavigateToAbuseType: (String) -> Unit = {},
    onNavigateToDARVO: () -> Unit = {},
    onNavigateToSafetyPlanning: () -> Unit = {}
) {
    var selectedSection by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Understanding Abuse") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (selectedSection == null) {
            // Main menu
            AbuseResourcesMainMenu(
                modifier = Modifier.padding(padding),
                onSelectSection = { selectedSection = it }
            )
        } else {
            // Show selected section
            AbuseResourceDetail(
                section = selectedSection!!,
                onBack = { selectedSection = null },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun AbuseResourcesMainMenu(
    modifier: Modifier = Modifier,
    onSelectSection: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Text(
            text = "Education & Support",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Learn about abuse types, recognize tactics, and understand your options. All information is stored offline for privacy.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Types of Abuse Section
        SectionHeader("Types of Abuse")

        AbuseTypeCard(
            title = "Physical Abuse",
            description = "Hitting, choking, restraining, denying medical care",
            icon = Icons.Default.Warning,
            onClick = { onSelectSection("physical") }
        )

        AbuseTypeCard(
            title = "Verbal Abuse",
            description = "Name-calling, yelling, threats, constant criticism",
            icon = Icons.Default.RecordVoiceOver,
            onClick = { onSelectSection("verbal") }
        )

        AbuseTypeCard(
            title = "Emotional/Psychological Abuse",
            description = "Gaslighting, isolation, intimidation, control (30+ tactics)",
            icon = Icons.Default.Psychology,
            onClick = { onSelectSection("emotional") },
            highlighted = true
        )

        AbuseTypeCard(
            title = "Financial Abuse",
            description = "Controlling money, sabotaging employment, economic control",
            icon = Icons.Default.AccountBalance,
            onClick = { onSelectSection("financial") }
        )

        AbuseTypeCard(
            title = "Sexual Abuse",
            description = "Marital rape, reproductive coercion, sexual assault",
            icon = Icons.Default.Shield,
            onClick = { onSelectSection("sexual") }
        )

        AbuseTypeCard(
            title = "Stalking",
            description = "Following, surveillance, unwanted contact, digital tracking",
            icon = Icons.Default.Visibility,
            onClick = { onSelectSection("stalking") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // DARVO Section
        SectionHeader("Understanding Manipulation Tactics")

        Card(
            onClick = { onSelectSection("darvo") },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Report,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DARVO Tactics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Deny, Attack, Reverse Victim & Offender - How abusers deflect and how to respond",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Documentation & Legal Section
        SectionHeader("Documentation & Legal")

        ResourceCard(
            title = "Documenting Abuse for Court",
            description = "Timeline templates, pattern collection, legal language",
            icon = Icons.Default.Gavel,
            onClick = { onSelectSection("documentation") }
        )

        ResourceCard(
            title = "DARVO Court Strategies",
            description = "Counter abuser's deflection tactics in legal settings",
            icon = Icons.Default.Balance,
            onClick = { onSelectSection("darvo_court") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Safety Planning
        SectionHeader("Safety & Support")

        ResourceCard(
            title = "Safety Planning",
            description = "Create a personalized safety plan for leaving safely",
            icon = Icons.Default.Security,
            onClick = { onSelectSection("safety_planning") }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AbuseTypeCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    highlighted: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = if (highlighted) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (highlighted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResourceCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

/**
 * Abuse Resource Detail Screen
 * Shows detailed content for selected abuse type or topic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AbuseResourceDetail(
    section: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getSectionTitle(section)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (section) {
                "physical" -> PhysicalAbuseContent()
                "verbal" -> VerbalAbuseContent()
                "emotional" -> EmotionalAbuseContent()
                "financial" -> FinancialAbuseContent()
                "sexual" -> SexualAbuseContent()
                "stalking" -> StalkingContent()
                "darvo" -> DARVOContent()
                "documentation" -> DocumentationContent()
                "darvo_court" -> DARVOCourtContent()
                "safety_planning" -> SafetyPlanningContent()
                else -> Text("Content not found")
            }
        }
    }
}

private fun getSectionTitle(section: String): String = when (section) {
    "physical" -> "Physical Abuse"
    "verbal" -> "Verbal Abuse"
    "emotional" -> "Emotional Abuse"
    "financial" -> "Financial Abuse"
    "sexual" -> "Sexual Abuse"
    "stalking" -> "Stalking"
    "darvo" -> "DARVO Tactics"
    "documentation" -> "Documenting Abuse"
    "darvo_court" -> "DARVO in Court"
    "safety_planning" -> "Safety Planning"
    else -> "Resources"
}

// Content composables for each section
// These will be expanded in separate files for better organization

@Composable
private fun PhysicalAbuseContent() {
    ContentSection(
        title = "What is Physical Abuse?",
        content = "The use of physical force that causes or could cause harm, injury, or pain."
    )

    BulletList(
        title = "Examples:",
        items = listOf(
            "Hitting, slapping, punching, kicking",
            "Choking, strangling, or suffocation",
            "Burning or scalding",
            "Using weapons or objects to threaten or harm",
            "Restraining or confining",
            "Denying medical care or medication",
            "Forcing alcohol or drug use"
        )
    )

    BulletList(
        title = "Warning Signs:",
        items = listOf(
            "Unexplained injuries (bruises, burns, fractures)",
            "Injuries in various stages of healing",
            "Frequent 'accidents'",
            "Wearing inappropriate clothing to cover injuries",
            "Flinching at sudden movements",
            "Reluctance to seek medical attention"
        )
    )

    TipCard(
        title = "Documentation",
        content = "Photograph all injuries with date stamps. Seek medical attention to create medical records. Report to police if safe. Use SafeHaven's silent camera to document evidence."
    )
}

@Composable
private fun VerbalAbuseContent() {
    ContentSection(
        title = "What is Verbal Abuse?",
        content = "The use of words to cause harm, intimidate, or control another person."
    )

    BulletList(
        title = "Examples:",
        items = listOf(
            "Name-calling, insults, put-downs",
            "Yelling, screaming, constant criticism",
            "Belittling accomplishments or opinions",
            "Public humiliation",
            "Blaming for all problems",
            "Threatening statements",
            "Silent treatment as punishment",
            "Constant interrupting or invalidating"
        )
    )

    TipCard(
        title = "Documentation",
        content = "Keep a detailed journal with dates, times, and exact quotes. Save text messages, emails, voicemails. Record conversations if legally permitted in your state."
    )
}

@Composable
private fun EmotionalAbuseContent() {
    AlertCard(
        title = "Important",
        content = "Emotional abuse is one of the most dangerous and underrecognized forms of abuse. It often escalates to physical violence and causes severe psychological harm."
    )

    ContentSection(
        title = "What is Emotional/Psychological Abuse?",
        content = "A pattern of behavior that damages self-worth, sense of reality, and emotional well-being through manipulation, intimidation, and control."
    )

    ExpandableSection(
        title = "Gaslighting (Making you question reality)",
        items = listOf(
            "Denying events that clearly happened",
            "Minimizing your feelings ('You're too sensitive')",
            "Shifting blame to you",
            "Rewriting history",
            "Accusing you of being 'crazy' or 'unstable'",
            "Hiding or moving your belongings"
        )
    )

    ExpandableSection(
        title = "Isolation",
        items = listOf(
            "Cutting you off from friends and family",
            "Monitoring phone/email/social media",
            "Preventing work or school attendance",
            "Requiring constant check-ins",
            "Criticizing everyone you care about",
            "Making it difficult to maintain relationships"
        )
    )

    ExpandableSection(
        title = "Intimidation",
        items = listOf(
            "Using looks, gestures to create fear",
            "Smashing objects, punching walls",
            "Destroying your property",
            "Harming or threatening pets",
            "Displaying weapons",
            "Driving recklessly with you in car",
            "Threatening suicide if you leave"
        )
    )

    TipCard(
        title = "Why It's Dangerous",
        content = "Emotional abuse is invisible (no physical scars), often precedes physical violence, causes C-PTSD and depression, rewires the brain creating trauma bonding, and is hard to prove in court."
    )
}

@Composable
private fun FinancialAbuseContent() {
    ContentSection(
        title = "What is Financial Abuse?",
        content = "Controlling a person's ability to acquire, use, or maintain financial resources."
    )

    BulletList(
        title = "Examples:",
        items = listOf(
            "Preventing access to bank accounts",
            "Forcing you to account for every penny",
            "Sabotaging employment",
            "Hiding assets or money",
            "Running up debt in your name",
            "Refusing to work or contribute",
            "Giving you an 'allowance'",
            "Preventing you from working"
        )
    )
}

@Composable
private fun SexualAbuseContent() {
    ContentSection(
        title = "What is Sexual Abuse?",
        content = "Any sexual activity without full, freely given consent. This includes marital rape."
    )

    AlertCard(
        title = "Important",
        content = "Marital rape is illegal in all 50 states. Consent can be withdrawn at any time."
    )

    BulletList(
        title = "Examples:",
        items = listOf(
            "Marital rape or coerced sex",
            "Reproductive coercion (sabotaging birth control)",
            "Forced pregnancy or abortion",
            "Sexual acts without consent",
            "Using sex as a weapon",
            "Sharing intimate images without consent"
        )
    )
}

@Composable
private fun StalkingContent() {
    ContentSection(
        title = "What is Stalking?",
        content = "A pattern of unwanted contact, surveillance, or harassment that causes fear."
    )

    BulletList(
        title = "Physical Stalking:",
        items = listOf(
            "Following you",
            "Showing up at your work/home/school",
            "Watching your house",
            "Leaving unwanted gifts",
            "Contacting your friends/family"
        )
    )

    BulletList(
        title = "Digital Stalking:",
        items = listOf(
            "GPS tracking on phone/car",
            "Monitoring social media",
            "Hacking email/accounts",
            "Spyware on devices",
            "Excessive calls/texts"
        )
    )
}

@Composable
private fun DARVOContent() {
    AlertCard(
        title = "DARVO Definition",
        content = "Deny, Attack, Reverse Victim and Offender - A manipulation tactic used by abusers when confronted."
    )

    ExpandableSection(
        title = "1. DENY - The abuser denies the abuse",
        items = listOf(
            "'That never happened'",
            "'You're making this up'",
            "'I don't remember it that way'",
            "'It wasn't that bad'",
            "'I was just joking'"
        )
    )

    ExpandableSection(
        title = "2. ATTACK - Attacks your credibility",
        items = listOf(
            "'You're crazy/unstable'",
            "'You're trying to ruin my life'",
            "'You're doing this for attention'",
            "'You're a liar'",
            "'Everyone knows you're vindictive'"
        )
    )

    ExpandableSection(
        title = "3. REVERSE - Claims to be the victim",
        items = listOf(
            "'You're abusing ME'",
            "'I'm the victim here'",
            "'You hit me first' (reactive abuse)",
            "'You're controlling me'",
            "'This is a witch hunt'"
        )
    )

    TipCard(
        title = "Why DARVO Works",
        content = "It mimics real victim behavior, exploits empathy, confuses the actual victim, works in court, and isolates you when others believe the abuser."
    )
}

@Composable
private fun DocumentationContent() {
    ContentSection(
        title = "Why Documentation Matters",
        content = "Courts require evidence. Emotional abuse leaves no physical scars. Detailed documentation builds a pattern that proves ongoing abuse."
    )

    BulletList(
        title = "What to Document:",
        items = listOf(
            "Date, time, location of each incident",
            "Exact words spoken (use quotes)",
            "Your emotional and physical state",
            "Witnesses present",
            "Photos of injuries or destroyed property",
            "Text messages, emails, voicemails",
            "Medical records",
            "Police reports"
        )
    )

    TipCard(
        title = "SafeHaven Features",
        content = "Use Incident Reports for legal-formatted documentation. Use Silent Camera for photos. Use Document Verification for cryptographic proof."
    )
}

@Composable
private fun DARVOCourtContent() {
    ContentSection(
        title = "DARVO in Legal Settings",
        content = "Abusers use DARVO tactics in court to avoid accountability. Judges and lawyers can be fooled. You need to prepare."
    )

    BulletList(
        title = "How to Counter DARVO in Court:",
        items = listOf(
            "Warn your attorney DARVO will be used",
            "Bring documentation (timeline, evidence, witnesses)",
            "Stay factual (not emotional)",
            "Highlight the pattern",
            "Use expert testimony (DV-informed therapist)",
            "Request DARVO-aware judge/mediator if possible"
        )
    )

    TipCard(
        title = "Language for Court Filings",
        content = "\"The respondent is employing DARVO tactics (Deny, Attack, Reverse Victim and Offender) to deflect from their documented pattern of abuse.\""
    )
}

@Composable
private fun SafetyPlanningContent() {
    ContentSection(
        title = "Creating a Safety Plan",
        content = "A personalized plan for leaving safely and staying safe after leaving."
    )

    BulletList(
        title = "Emergency Plan:",
        items = listOf(
            "Identify safe places to go",
            "Pack an emergency bag (hide it)",
            "Save money secretly if possible",
            "Memorize important phone numbers",
            "Have copies of important documents",
            "Plan an escape route",
            "Tell someone you trust"
        )
    )

    BulletList(
        title = "After Leaving:",
        items = listOf(
            "Change locks if staying home",
            "Get restraining order",
            "Inform work/school of situation",
            "Vary your routines",
            "Check devices for spyware",
            "Change passwords",
            "Block abuser on all platforms"
        )
    )
}

// Helper composables for content layout

@Composable
private fun ContentSection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun BulletList(title: String, items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        items.forEach { item ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("•", style = MaterialTheme.typography.bodyMedium)
                Text(item, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun TipCard(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AlertCard(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ExpandableSection(title: String, items: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                items.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("•", style = MaterialTheme.typography.bodyMedium)
                        Text(item, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
