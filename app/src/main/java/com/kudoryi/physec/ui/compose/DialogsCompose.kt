package com.kudoryi.physec.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kudoryi.physec.data.PhySecRepository
import com.kudoryi.physec.data.SampleData
import com.kudoryi.physec.model.*
import com.kudoryi.physec.ui.theme.*

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    minLines: Int = 1,
    singleLine: Boolean = minLines == 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        minLines = minLines,
        singleLine = singleLine,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun CreateReportDialogCompose(
    preselectedSiteId: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (ReportType, Site, String?) -> Unit
) {
    val sites = PhySecRepository.sites.value ?: SampleData.SITES
    var selectedReportType by remember { mutableStateOf(ReportType.SECURITY_SWEEP) }
    var selectedSite by remember {
        mutableStateOf<Site>(sites.find { it.id == preselectedSiteId } ?: sites.firstOrNull() ?: SampleData.SITES[0])
    }
    var dateTimeText by remember { mutableStateOf(PhySecRepository.getCurrentGhanaTimeString()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Start Field Report", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Select inspection type and Ghanaian telecom facility", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(14.dp))

                Text("Report Type", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedReportType == ReportType.SECURITY_SWEEP,
                        onClick = { selectedReportType = ReportType.SECURITY_SWEEP }
                    )
                    Text("Physical Security Sweep", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedReportType == ReportType.SAFETY_INSPECTION,
                        onClick = { selectedReportType = ReportType.SAFETY_INSPECTION }
                    )
                    Text("Workplace Health & Safety Check", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Select Site / Location", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("${selectedSite.name} (${selectedSite.region})", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        sites.forEach { site ->
                            DropdownMenuItem(
                                text = { Text("${site.name} (${site.region})") },
                                onClick = {
                                    selectedSite = site
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                AppOutlinedTextField(
                    value = dateTimeText,
                    onValueChange = { dateTimeText = it },
                    label = "Date & Time (GMT)"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(selectedReportType, selectedSite, dateTimeText) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Begin Checklist", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddFindingDialogCompose(
    itemTitle: String,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, severity: FindingSeverity, action: String, team: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("28/10/2024") }
    var severity by remember { mutableStateOf(FindingSeverity.MEDIUM) }
    var team by remember { mutableStateOf("Facilities & Maintenance") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Log Security / Safety Finding", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Linked to: $itemTitle", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(12.dp))

                AppOutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Finding Title*"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AppOutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = "Detailed Description*",
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))

                AppOutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = "Recommended Corrective Action*",
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))

                AppOutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = "Target Due Date (DD/MM/YYYY)"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(title, desc, severity, action, team, dueDate) },
                        colors = ButtonDefaults.buttonColors(containerColor = IssueRed)
                    ) {
                        Text("Save Finding", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CapturePhotoDialogCompose(
    onDismiss: () -> Unit,
    onSave: (caption: String, type: EvidenceType) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var evidenceType by remember { mutableStateOf(EvidenceType.SUPPORTS_ISSUE) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Capture Photo Evidence", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Linked to field audit item", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Navy800, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷 Field Camera Preview", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Evidence Purpose", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = evidenceType == EvidenceType.SUPPORTS_ISSUE,
                        onClick = { evidenceType = EvidenceType.SUPPORTS_ISSUE }
                    )
                    Text("Supports Logged Issue", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = evidenceType == EvidenceType.SHOWS_RESOLVED_ISSUE,
                        onClick = { evidenceType = EvidenceType.SHOWS_RESOLVED_ISSUE }
                    )
                    Text("Shows Resolved Issue", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.height(8.dp))
                AppOutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = "Photo Caption*"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalCaption = if (caption.isBlank()) "Field Inspection Evidence" else caption
                            onSave(finalCaption, evidenceType)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Attach Photo", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FindingDetailDialogCompose(
    reportId: String,
    findingId: String,
    onDismiss: () -> Unit,
    onAttachResolutionPhoto: () -> Unit
) {
    val report = PhySecRepository.getReportById(reportId)
    val finding = report?.findings?.find { it.id == findingId } ?: return

    var selectedStatus by remember { mutableStateOf(finding.status) }
    var resolutionNotesInput by remember { mutableStateOf(finding.resolutionNotes) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(finding.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("${report.siteName} • Due: ${finding.dueDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = if (finding.severity == FindingSeverity.HIGH || finding.severity == FindingSeverity.CRITICAL) IssueRedBg else WarningAmberBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${finding.severity.label.uppercase()} SEVERITY • Team: ${finding.assignedTeam}",
                        color = if (finding.severity == FindingSeverity.HIGH || finding.severity == FindingSeverity.CRITICAL) IssueRed else WarningAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Description:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(finding.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Recommended Action:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(finding.recommendedAction, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)

                Spacer(modifier = Modifier.height(14.dp))
                Text("Update Finding Status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                Column {
                    FindingStatus.entries.forEach { st ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedStatus == st,
                                onClick = { selectedStatus = st }
                            )
                            Text(st.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                AppOutlinedTextField(
                    value = resolutionNotesInput,
                    onValueChange = { resolutionNotesInput = it },
                    label = "Resolution Notes & Actions Taken",
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onAttachResolutionPhoto,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📷 Attach Resolution Photo", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            PhySecRepository.updateFindingStatus(reportId, findingId, selectedStatus, resolutionNotesInput)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Status", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddSiteDialogCompose(
    onDismiss: () -> Unit,
    onSave: (name: String, region: String, type: String, address: String, contact: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("Greater Accra") }
    var type by remember { mutableStateOf("Tier-3 Data Center") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val regions = listOf("Greater Accra", "Ashanti", "Western", "Northern", "Central", "Volta", "Eastern", "Upper East")
    var isRegionExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Add New Telecom Facility", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(12.dp))

                AppOutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Site / Facility Name*"
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { isRegionExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Region: $region", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                    }
                    DropdownMenu(
                        expanded = isRegionExpanded,
                        onDismissRequest = { isRegionExpanded = false }
                    ) {
                        regions.forEach { reg ->
                            DropdownMenuItem(
                                text = { Text(reg) },
                                onClick = {
                                    region = reg
                                    isRegionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                AppOutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = "Facility Type (Hub, Tower, Data Center)*"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AppOutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address / Location Notes*"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AppOutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = "Site Contact Person*"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AppOutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Contact Phone (+233...)*"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val finalType = if (type.isBlank()) "Telecom Site" else type
                                onSave(name, region, finalType, address, contact, phone)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Facility", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SyncCenterDialogCompose(
    onDismiss: () -> Unit
) {
    val isOfflineMode by PhySecRepository.isOfflineMode.observeAsState(false)
    val reports = PhySecRepository.getAllReports()
    val pending = reports.filter { it.syncStatus == SyncStatus.PENDING_SYNC }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Offline Sync Queue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("PhySec preserves all field reports locally first so offline audits are 100% reliable.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(if (isOfflineMode) "Mode: Offline" else "Mode: Online", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("Simulate network connectivity state", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isOfflineMode,
                            onCheckedChange = { PhySecRepository.toggleOfflineMode() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Pending Sync Reports (${pending.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                if (pending.isEmpty()) {
                    Text("All local report data is synced with cloud server.", fontSize = 12.sp, color = PassGreen, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    pending.forEach { rep ->
                        Text("• ${rep.reportNumber}: ${rep.title}", fontSize = 12.sp, color = WarningAmber)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            PhySecRepository.syncPendingReports()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Sync All Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
