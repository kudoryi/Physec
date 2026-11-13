package com.kudoryi.physec.ui.compose

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudoryi.physec.data.PhySecRepository
import com.kudoryi.physec.model.*
import com.kudoryi.physec.ui.theme.*
import com.kudoryi.physec.util.PdfExporter

sealed class ComposeScreen {
    object Home : ComposeScreen()
    object Reports : ComposeScreen()
    data class Checklist(val reportId: String) : ComposeScreen()
    data class ReportReview(val reportId: String) : ComposeScreen()
    object Findings : ComposeScreen()
    object Sites : ComposeScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhySecApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<ComposeScreen>(ComposeScreen.Home) }

    val currentRole by PhySecRepository.currentRole.observeAsState(UserRole.INSPECTOR)
    val isOfflineMode by PhySecRepository.isOfflineMode.observeAsState(false)
    val isDarkMode by PhySecRepository.isDarkMode.observeAsState(null)
    val reportsList: List<Report> by PhySecRepository.reports.observeAsState(emptyList())

    val pendingSyncCount = reportsList.count { it.syncStatus == SyncStatus.PENDING_SYNC }
    val openFindingsCount = PhySecRepository.getOpenOrHighPriorityFindings().size

    // Dialog state holders
    var activeDialog by remember { mutableStateOf<String?>(null) }
    var dialogPreselectedSiteId by remember { mutableStateOf<String?>(null) }
    var dialogReportId by remember { mutableStateOf("") }
    var dialogChecklistItemId by remember { mutableStateOf<String?>(null) }
    var dialogFindingId by remember { mutableStateOf<String?>(null) }
    var dialogItemTitle by remember { mutableStateOf("") }

    PhySecTheme(forceDarkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Text("🛡", fontSize = 16.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Column {
                                Text("PhySec", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Ghana Telecom Ops", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    },
                    navigationIcon = {
                        if (currentScreen is ComposeScreen.Checklist || currentScreen is ComposeScreen.ReportReview) {
                            IconButton(onClick = { currentScreen = ComposeScreen.Home }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        // Dark Mode Toggle Button
                        IconButton(onClick = { PhySecRepository.toggleDarkMode() }) {
                            Text(if (isDarkMode == true) "☀️" else "🌙", fontSize = 16.sp)
                        }

                        // Role Pill Toggle Button
                        Surface(
                            color = Navy800,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        val newRole = if (currentRole == UserRole.INSPECTOR) UserRole.SUPERVISOR else UserRole.INSPECTOR
                                        PhySecRepository.setRole(newRole)
                                        Toast.makeText(context, "Role switched to ${newRole.displayName}", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = if (currentRole == UserRole.INSPECTOR) "👤 Inspector" else "👮 Supervisor",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Sync Button
                        IconButton(onClick = { activeDialog = "SYNC_CENTER" }) {
                            BadgedBox(
                                badge = {
                                    if (pendingSyncCount > 0) {
                                        Badge(containerColor = WarningAmber) { Text(pendingSyncCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sync Queue",
                                    tint = if (isOfflineMode) WarningAmber else Color(0xFF38BDF8)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy900)
                )
            },
            bottomBar = {
                FloatingPillNavigationBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { newScreen -> currentScreen = newScreen },
                    pendingFindingsCount = openFindingsCount
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(
                    targetState = currentScreen,
                    label = "ScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        is ComposeScreen.Home -> HomeScreenCompose(
                            onStartNewReport = { activeDialog = "CREATE_REPORT" },
                            onViewAllReports = { currentScreen = ComposeScreen.Reports },
                            onOpenReport = { report ->
                                if (report.status == ReportStatus.DRAFT) {
                                    currentScreen = ComposeScreen.Checklist(report.id)
                                } else {
                                    currentScreen = ComposeScreen.ReportReview(report.id)
                                }
                            },
                            onOpenDraftsFilter = { currentScreen = ComposeScreen.Reports },
                            onOpenPendingFilter = { currentScreen = ComposeScreen.Reports },
                            onOpenFindingsTab = { currentScreen = ComposeScreen.Findings }
                        )

                        is ComposeScreen.Reports -> ReportsScreenCompose(
                            onOpenReport = { report ->
                                if (report.status == ReportStatus.DRAFT) {
                                    currentScreen = ComposeScreen.Checklist(report.id)
                                } else {
                                    currentScreen = ComposeScreen.ReportReview(report.id)
                                }
                            },
                            onStartNewReport = { activeDialog = "CREATE_REPORT" }
                        )

                        is ComposeScreen.Checklist -> ChecklistScreenCompose(
                            reportId = targetScreen.reportId,
                            onSaveDraft = {
                                Toast.makeText(context, "Draft saved!", Toast.LENGTH_SHORT).show()
                                currentScreen = ComposeScreen.Home
                            },
                            onReviewSubmit = {
                                currentScreen = ComposeScreen.ReportReview(targetScreen.reportId)
                            },
                            onAttachPhoto = { itemId, findingId ->
                                dialogReportId = targetScreen.reportId
                                dialogChecklistItemId = itemId
                                dialogFindingId = findingId
                                activeDialog = "CAPTURE_PHOTO"
                            },
                            onLogFinding = { itemId, title ->
                                dialogReportId = targetScreen.reportId
                                dialogChecklistItemId = itemId
                                dialogItemTitle = title
                                activeDialog = "ADD_FINDING"
                            }
                        )

                        is ComposeScreen.ReportReview -> ReportReviewScreenCompose(
                            reportId = targetScreen.reportId,
                            onExportPdf = {
                                val report = PhySecRepository.getReportById(targetScreen.reportId)
                                if (report != null) {
                                    PdfExporter.generatePdfAndShare(context, report)
                                }
                            },
                            onShareSummary = {
                                val report = PhySecRepository.getReportById(targetScreen.reportId)
                                if (report != null) {
                                    val text = PdfExporter.generateTextSummary(report)
                                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                                    }
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Summary"))
                                }
                            },
                            onSubmitReport = {
                                PhySecRepository.submitReportForApproval(targetScreen.reportId)
                                Toast.makeText(context, "Submitted for review!", Toast.LENGTH_SHORT).show()
                            }
                        )

                        is ComposeScreen.Findings -> FindingsScreenCompose(
                            onUpdateFinding = { report, finding ->
                                dialogReportId = report.id
                                dialogFindingId = finding.id
                                activeDialog = "FINDING_DETAIL"
                            }
                        )

                        is ComposeScreen.Sites -> SitesScreenCompose(
                            onStartAuditForSite = { site ->
                                dialogPreselectedSiteId = site.id
                                activeDialog = "CREATE_REPORT"
                            },
                            onAddSite = { activeDialog = "ADD_SITE" }
                        )
                    }
                }
            }

            // Active Dialog overlays
            when (activeDialog) {
                "CREATE_REPORT" -> CreateReportDialogCompose(
                    preselectedSiteId = dialogPreselectedSiteId,
                    onDismiss = { activeDialog = null },
                    onConfirm = { type, site, customDate ->
                        val report = PhySecRepository.createNewReport(type, site, customDate)
                        activeDialog = null
                        currentScreen = ComposeScreen.Checklist(report.id)
                    }
                )

                "ADD_FINDING" -> AddFindingDialogCompose(
                    itemTitle = dialogItemTitle,
                    onDismiss = { activeDialog = null },
                    onSave = { title, desc, severity, action, team, dueDate ->
                        PhySecRepository.addFindingToReport(
                            reportId = dialogReportId,
                            checklistItemId = dialogChecklistItemId,
                            title = title,
                            description = desc,
                            severity = severity,
                            recommendedAction = action,
                            assignedTeam = team,
                            assignedPerson = "Facilities Duty Officer",
                            dueDate = dueDate
                        )
                        activeDialog = null
                        Toast.makeText(context, "Finding logged!", Toast.LENGTH_SHORT).show()
                    }
                )

                "CAPTURE_PHOTO" -> CapturePhotoDialogCompose(
                    onDismiss = { activeDialog = null },
                    onSave = { caption, type ->
                        PhySecRepository.addPhotoToReport(
                            reportId = dialogReportId,
                            checklistItemId = dialogChecklistItemId,
                            findingId = dialogFindingId,
                            uriPath = "sample_field_photo",
                            caption = caption,
                            evidenceType = type
                        )
                        activeDialog = null
                        Toast.makeText(context, "Photo evidence attached!", Toast.LENGTH_SHORT).show()
                    }
                )

                "FINDING_DETAIL" -> {
                    val fid = dialogFindingId
                    if (fid != null) {
                        FindingDetailDialogCompose(
                            reportId = dialogReportId,
                            findingId = fid,
                            onDismiss = { activeDialog = null },
                            onAttachResolutionPhoto = {
                                activeDialog = "CAPTURE_PHOTO"
                            }
                        )
                    }
                }

                "ADD_SITE" -> AddSiteDialogCompose(
                    onDismiss = { activeDialog = null },
                    onSave = { name, region, type, address, contact, phone ->
                        PhySecRepository.addSite(name, region, type, address, contact, phone)
                        activeDialog = null
                        Toast.makeText(context, "Site facility added!", Toast.LENGTH_SHORT).show()
                    }
                )

                "SYNC_CENTER" -> SyncCenterDialogCompose(
                    onDismiss = { activeDialog = null }
                )
            }
        }
    }
}
