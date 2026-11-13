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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudoryi.physec.data.PhySecRepository
import com.kudoryi.physec.model.Finding
import com.kudoryi.physec.model.FindingSeverity
import com.kudoryi.physec.model.FindingStatus
import com.kudoryi.physec.model.Report
import com.kudoryi.physec.ui.theme.*

@Composable
fun FindingsScreenCompose(
    onUpdateFinding: (Report, Finding) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Open, 2: High/Critical, 3: Resolved
    val reportsList by PhySecRepository.reports.observeAsState(emptyList())

    val allFindings = PhySecRepository.getAllFindings()
    val filtered = when (selectedFilter) {
        1 -> allFindings.filter { (_, f) -> f.status == FindingStatus.OPEN || f.status == FindingStatus.IN_PROGRESS }
        2 -> allFindings.filter { (_, f) -> f.severity == FindingSeverity.HIGH || f.severity == FindingSeverity.CRITICAL }
        3 -> allFindings.filter { (_, f) -> f.status == FindingStatus.RESOLVED || f.status == FindingStatus.CLOSED }
        else -> allFindings
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Findings & Action Tracker", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Track physical security and safety issues across Ghanaian telecom sites", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(selected = selectedFilter == 0, onClick = { selectedFilter = 0 }, label = { Text("All", fontSize = 11.sp) })
                FilterChip(selected = selectedFilter == 1, onClick = { selectedFilter = 1 }, label = { Text("Open", fontSize = 11.sp) })
                FilterChip(selected = selectedFilter == 2, onClick = { selectedFilter = 2 }, label = { Text("High/Critical", fontSize = 11.sp) })
                FilterChip(selected = selectedFilter == 3, onClick = { selectedFilter = 3 }, label = { Text("Resolved", fontSize = 11.sp) })
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Text("No findings match the selected filter.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(24.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered, key = { "${it.first.id}_${it.second.id}" }) { (report, finding) ->
                        FindingCardCompose(report, finding, onClick = { onUpdateFinding(report, finding) })
                    }
                }
            }
        }
    }
}

@Composable
fun FindingCardCompose(report: Report, finding: Finding, onClick: () -> Unit) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = if (finding.severity == FindingSeverity.HIGH || finding.severity == FindingSeverity.CRITICAL) IssueRedBg else WarningAmberBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "${finding.severity.label.uppercase()} SEVERITY",
                        color = if (finding.severity == FindingSeverity.HIGH || finding.severity == FindingSeverity.CRITICAL) IssueRed else WarningAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(finding.status.label.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(finding.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("${report.siteName} • Due: ${finding.dueDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(finding.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("RECOMMENDED ACTION:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Text(finding.recommendedAction, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
