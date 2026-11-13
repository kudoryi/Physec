package com.kudoryi.physec.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudoryi.physec.data.PhySecRepository
import com.kudoryi.physec.model.Report
import com.kudoryi.physec.model.ReportStatus
import com.kudoryi.physec.ui.theme.*

@Composable
fun ReportsScreenCompose(
    onOpenReport: (Report) -> Unit,
    onStartNewReport: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<ReportStatus?>(null) }
    val reportsList by PhySecRepository.reports.observeAsState(emptyList())

    val filtered = reportsList.filter { rep ->
        val matchesStatus = selectedStatus == null || rep.status == selectedStatus
        val q = searchQuery.trim().lowercase()
        val matchesSearch = q.isEmpty() ||
                rep.title.lowercase().contains(q) ||
                rep.siteName.lowercase().contains(q) ||
                rep.reportNumber.lowercase().contains(q) ||
                rep.inspectorName.lowercase().contains(q)
        matchesStatus && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search reports by site, number, title...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null },
                    label = { Text("All", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedStatus == ReportStatus.DRAFT,
                    onClick = { selectedStatus = ReportStatus.DRAFT },
                    label = { Text("Drafts", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedStatus == ReportStatus.PENDING_APPROVAL,
                    onClick = { selectedStatus = ReportStatus.PENDING_APPROVAL },
                    label = { Text("Pending Review", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedStatus == ReportStatus.APPROVED,
                    onClick = { selectedStatus = ReportStatus.APPROVED },
                    label = { Text("Approved", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Text(
                    "No reports matching search filter.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { report ->
                        ReportCardCompose(report, onClick = { onOpenReport(report) })
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onStartNewReport,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("+ New Report", fontWeight = FontWeight.Bold)
        }
    }
}
