package com.kudoryi.physec.model

data class Report(
    val id: String,
    val reportNumber: String, // e.g., REP-ACC-2024-001
    val title: String,
    val type: ReportType,
    val siteId: String,
    val siteName: String,
    val siteRegion: String,
    val inspectorName: String,
    val inspectorRoleTitle: String,
    val dateRecordedFormatted: String, // e.g. "24/10/2024 09:30 GMT"
    val timestampMillis: Long = System.currentTimeMillis(),
    var status: ReportStatus = ReportStatus.DRAFT,
    var supervisorComment: String? = null,
    var supervisorName: String? = null,
    val checklistItems: MutableList<ReportChecklistItem> = mutableListOf(),
    val findings: MutableList<Finding> = mutableListOf(),
    val photos: MutableList<PhotoEvidence> = mutableListOf(),
    val auditTrail: MutableList<AuditEntry> = mutableListOf(),
    var syncStatus: SyncStatus = SyncStatus.PENDING_SYNC
) {
    fun getPassCount(): Int = checklistItems.count { it.status == ChecklistItemStatus.PASS }
    fun getIssueCount(): Int = checklistItems.count { it.status == ChecklistItemStatus.ISSUE }
    fun getNaCount(): Int = checklistItems.count { it.status == ChecklistItemStatus.NOT_APPLICABLE }
    fun getUncheckedCount(): Int = checklistItems.count { it.status == ChecklistItemStatus.UNCHECKED }
}
