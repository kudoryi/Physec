package com.kudoryi.physec.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.kudoryi.physec.model.ChecklistItemStatus
import com.kudoryi.physec.model.ReportChecklistItem
import com.kudoryi.physec.ui.theme.*

@Composable
fun ChecklistScreenCompose(
    reportId: String,
    onSaveDraft: () -> Unit,
    onReviewSubmit: () -> Unit,
    onAttachPhoto: (checklistItemId: String, findingId: String?) -> Unit,
    onLogFinding: (checklistItemId: String, itemTitle: String) -> Unit
) {
    val reports by PhySecRepository.reports.observeAsState(emptyList())
    val report = reports.find { it.id == reportId } ?: return

    var selectedSection by remember { mutableStateOf("All Sections") }
    val sections = listOf("All Sections") + report.checklistItems.map { it.sectionName }.distinct()

    val itemsToDisplay = if (selectedSection == "All Sections") {
        report.checklistItems
    } else {
        report.checklistItems.filter { it.sectionName == selectedSection }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Sticky Report Header Card
        Surface(
            color = Navy900,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(report.reportNumber, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = WarningAmberBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = report.status.label,
                            color = WarningAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(report.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))
                val total = report.checklistItems.size
                val completed = report.checklistItems.count { it.status != ChecklistItemStatus.UNCHECKED }
                val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Blue600,
                    trackColor = Navy700
                )
                Text(
                    text = "$completed/$total Answered (${report.getIssueCount()} Issues Identified)",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Section Tabs Filter Bar
        ScrollableTabRow(
            selectedTabIndex = sections.indexOf(selectedSection).coerceAtLeast(0),
            containerColor = SurfaceCard,
            contentColor = Blue600,
            edgePadding = 12.dp
        ) {
            sections.forEach { sectionName ->
                Tab(
                    selected = selectedSection == sectionName,
                    onClick = { selectedSection = sectionName },
                    text = {
                        Text(
                            text = sectionName,
                            fontSize = 12.sp,
                            fontWeight = if (selectedSection == sectionName) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSection == sectionName) Blue600 else TextSecondary
                        )
                    }
                )
            }
        }

        // Checklist Items List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(itemsToDisplay, key = { it.id }) { item ->
                ChecklistItemCardCompose(
                    item = item,
                    onStatusChange = { newStatus ->
                        PhySecRepository.updateChecklistItemStatus(report.id, item.id, newStatus, item.notes)
                    },
                    onNotesChange = { newNotes ->
                        PhySecRepository.updateChecklistItemStatus(report.id, item.id, item.status, newNotes)
                    },
                    onAttachPhotoClick = { onAttachPhoto(item.id, item.findingId) },
                    onLogFindingClick = { onLogFinding(item.id, item.title) }
                )
            }
        }

        // Fixed Bottom Action Bar
        Surface(
            tonalElevation = 8.dp,
            color = SurfaceCard
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSaveDraft,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Draft", color = TextPrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onReviewSubmit,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Review & Submit", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChecklistItemCardCompose(
    item: ReportChecklistItem,
    onStatusChange: (ChecklistItemStatus) -> Unit,
    onNotesChange: (String) -> Unit,
    onAttachPhotoClick: () -> Unit,
    onLogFindingClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(item.sectionName.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Teal600)
            Spacer(modifier = Modifier.height(2.dp))
            Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(item.prompt, fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Status Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Pass Button
                val isPass = item.status == ChecklistItemStatus.PASS
                Button(
                    onClick = { onStatusChange(ChecklistItemStatus.PASS) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPass) PassGreen else Color.Transparent,
                        contentColor = if (isPass) Color.White else PassGreen
                    ),
                    border = BorderStroke(1.dp, PassGreen),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("✓ Pass", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Issue Button
                val isIssue = item.status == ChecklistItemStatus.ISSUE
                Button(
                    onClick = {
                        onStatusChange(ChecklistItemStatus.ISSUE)
                        if (item.findingId.isNullOrEmpty()) onLogFindingClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isIssue) IssueRed else Color.Transparent,
                        contentColor = if (isIssue) Color.White else IssueRed
                    ),
                    border = BorderStroke(1.dp, IssueRed),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("⚠ Issue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // N/A Button
                val isNA = item.status == ChecklistItemStatus.NOT_APPLICABLE
                Button(
                    onClick = { onStatusChange(ChecklistItemStatus.NOT_APPLICABLE) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNA) NeutralSlate else Color.Transparent,
                        contentColor = if (isNA) Color.White else NeutralSlate
                    ),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("N/A", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field Notes Field
            OutlinedTextField(
                value = item.notes,
                onValueChange = onNotesChange,
                label = { Text("Field Notes & Observations...", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAttachPhotoClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("📷 Photos (${item.photoIds.size})", fontSize = 11.sp, color = TextPrimary)
                }

                OutlinedButton(
                    onClick = onLogFindingClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, IssueRed),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("⚠ Log Finding", fontSize = 11.sp, color = IssueRed, fontWeight = FontWeight.Bold)
                }
            }

            if (!item.findingId.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = IssueRedBg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "⚠ Finding Linked to Item",
                        color = IssueRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
