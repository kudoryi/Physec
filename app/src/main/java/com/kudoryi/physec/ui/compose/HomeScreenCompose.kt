package com.kudoryi.physec.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudoryi.physec.data.PhySecRepository
import com.kudoryi.physec.model.*
import com.kudoryi.physec.ui.theme.*

@Composable
fun HomeScreenCompose(
    onStartNewReport: () -> Unit,
    onViewAllReports: () -> Unit,
    onOpenReport: (Report) -> Unit,
    onOpenDraftsFilter: () -> Unit,
    onOpenPendingFilter: () -> Unit,
    onOpenFindingsTab: () -> Unit
) {
    val currentRole by PhySecRepository.currentRole.observeAsState(UserRole.INSPECTOR)
    val isOfflineMode by PhySecRepository.isOfflineMode.observeAsState(false)
    val reportsList: List<Report> by PhySecRepository.reports.observeAsState(emptyList())

    val drafts = reportsList.filter { it.status == ReportStatus.DRAFT }
    val pending = reportsList.filter { it.status == ReportStatus.PENDING_APPROVAL }
    val completed = reportsList.filter { it.status == ReportStatus.APPROVED || it.status == ReportStatus.REVISION_REQUESTED }
    val openFindings = PhySecRepository.getOpenOrHighPriorityFindings()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Welcome, ${currentRole.displayName}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("${currentRole.title} • Telecom Ops Ghana", color = TextMuted, fontSize = 12.sp)
                        }

                        Surface(
                            color = if (isOfflineMode) WarningAmberBg else PassGreenBg,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (isOfflineMode) "Offline Mode" else "Online & Synced",
                                color = if (isOfflineMode) WarningAmber else PassGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Navy700)
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onStartNewReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Start Field Report", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Text("Field Operations Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard("Active Drafts", drafts.size.toString(), MaterialTheme.colorScheme.primary, Modifier.weight(1f), onOpenDraftsFilter)
                MetricCard("Pending Review", pending.size.toString(), WarningAmber, Modifier.weight(1f), onOpenPendingFilter)
                MetricCard("Open Issues", openFindings.size.toString(), IssueRed, Modifier.weight(1f), onOpenFindingsTab)
            }
        }

        // Resume Drafts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Resume Active Drafts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                TextButton(onClick = onViewAllReports) {
                    Text("View All ›", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (drafts.isEmpty()) {
            item {
                Text("No active drafts. Click 'Start Field Report' to begin a sweep.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
        } else {
            items(drafts, key = { it.id }) { draft ->
                ReportCardCompose(draft, onClick = { onOpenReport(draft) })
            }
        }

        // Recent Completed Audits
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Recent Completed Audits", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }

        items(completed.take(3), key = { it.id }) { report ->
            ReportCardCompose(report, onClick = { onOpenReport(report) })
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ReportCardCompose(report: Report, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Navy900,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (report.type == ReportType.SECURITY_SWEEP) "SEC SWEEP" else "HSE CHECK",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(report.reportNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                val statusColor = when (report.status) {
                    ReportStatus.APPROVED -> PassGreen
                    ReportStatus.PENDING_APPROVAL -> Blue600
                    ReportStatus.DRAFT -> WarningAmber
                    ReportStatus.REVISION_REQUESTED -> IssueRed
                }
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = report.status.label,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(report.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("${report.siteName} • ${report.dateRecordedFormatted}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("✓ ${report.getPassCount()} Pass", color = PassGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("⚠ ${report.getIssueCount()} Issue", color = IssueRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("📷 ${report.photos.size} Photos", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        }
    }
}
