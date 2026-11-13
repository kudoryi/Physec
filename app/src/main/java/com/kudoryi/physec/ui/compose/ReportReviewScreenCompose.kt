package com.kudoryi.physec.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.kudoryi.physec.model.ReportStatus
import com.kudoryi.physec.model.UserRole
import com.kudoryi.physec.ui.theme.*

@Composable
fun ReportReviewScreenCompose(
    reportId: String,
    onExportPdf: () -> Unit,
    onShareSummary: () -> Unit,
    onSubmitReport: () -> Unit
) {
    val reportsList by PhySecRepository.reports.observeAsState(emptyList())
    val currentRole by PhySecRepository.currentRole.observeAsState(UserRole.INSPECTOR)
    val report = reportsList.find { it.id == reportId } ?: return

    var supervisorCommentInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(color = Navy900) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(report.reportNumber, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Surface(color = PassGreenBg, shape = RoundedCornerShape(12.dp)) {
                        Text(report.status.label.uppercase(), color = PassGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(report.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${report.siteName} (${report.siteRegion}) • ${report.dateRecordedFormatted}", color = TextMuted, fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Row
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(report.getPassCount().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PassGreen)
                            Text("Pass", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(report.getIssueCount().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = IssueRed)
                            Text("Issues", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(report.getNaCount().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeutralSlate)
                            Text("N/A", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(report.photos.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Photos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Supervisor Box
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("SUPERVISOR REVIEW & APPROVAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (!report.supervisorComment.isNullOrEmpty()) "Supervisor ${report.supervisorName}: ${report.supervisorComment}" else "No supervisor review notes recorded.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (currentRole == UserRole.SUPERVISOR) {
                            Spacer(modifier = Modifier.height(10.dp))
                            AppOutlinedTextField(
                                value = supervisorCommentInput,
                                onValueChange = { supervisorCommentInput = it },
                                label = "Supervisor Comments..."
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        PhySecRepository.supervisorReviewReport(report.id, true, supervisorCommentInput.ifBlank { "Approved" })
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PassGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Approve", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        PhySecRepository.supervisorReviewReport(report.id, false, supervisorCommentInput.ifBlank { "Revision required" })
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Request Revision", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Findings list
            item {
                Text("Identified Findings (${report.findings.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                report.findings.forEach { finding ->
                    Text("• [${finding.severity.label}] ${finding.title}", color = IssueRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("  Action: ${finding.recommendedAction} (Team: ${finding.assignedTeam})", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Audit Trail
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Audit Log Timeline", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                report.auditTrail.forEach { audit ->
                    Text("⏱ ${audit.timestampFormatted} • ${audit.userName} (${audit.userRole.name}): ${audit.actionText}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
            }
        }

        // Bottom CTAs
        Surface(
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (report.status == ReportStatus.DRAFT || report.status == ReportStatus.REVISION_REQUESTED) {
                    Button(
                        onClick = onSubmitReport,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onExportPdf,
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Export PDF", color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onShareSummary,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Share", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
