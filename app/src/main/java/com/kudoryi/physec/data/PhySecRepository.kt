package com.kudoryi.physec.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kudoryi.physec.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object PhySecRepository {

    private val _currentRole = MutableLiveData<UserRole>(UserRole.INSPECTOR)
    val currentRole: LiveData<UserRole> get() = _currentRole

    private val _reports = MutableLiveData<List<Report>>(emptyList())
    val reports: LiveData<List<Report>> get() = _reports

    private val _sites = MutableLiveData<List<Site>>(SampleData.SITES)
    val sites: LiveData<List<Site>> get() = _sites

    private val _isOfflineMode = MutableLiveData<Boolean>(false)
    val isOfflineMode: LiveData<Boolean> get() = _isOfflineMode

    private val _isDarkMode = MutableLiveData<Boolean?>(null) // null = system, true = force dark, false = force light
    val isDarkMode: LiveData<Boolean?> get() = _isDarkMode

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        _reports.postValue(SampleData.createInitialReports())
    }

    fun setRole(role: UserRole) {
        _currentRole.postValue(role)
    }

    fun toggleOfflineMode(): Boolean {
        val newMode = !(_isOfflineMode.value ?: false)
        _isOfflineMode.postValue(newMode)
        return newMode
    }

    fun toggleDarkMode() {
        val current = _isDarkMode.value
        _isDarkMode.postValue(when (current) {
            null, false -> true
            true -> false
        })
    }

    fun getAllReports(): List<Report> {
        return _reports.value ?: emptyList()
    }

    fun getReportById(reportId: String): Report? {
        return _reports.value?.find { it.id == reportId }
    }

    fun getDrafts(): List<Report> {
        return getAllReports().filter { it.status == ReportStatus.DRAFT }
    }

    fun getPendingApprovalReports(): List<Report> {
        return getAllReports().filter { it.status == ReportStatus.PENDING_APPROVAL }
    }

    fun getAllFindings(): List<Pair<Report, Finding>> {
        val result = mutableListOf<Pair<Report, Finding>>()
        getAllReports().forEach { report ->
            report.findings.forEach { finding ->
                result.add(Pair(report, finding))
            }
        }
        return result
    }

    fun getOpenOrHighPriorityFindings(): List<Pair<Report, Finding>> {
        return getAllFindings().filter { (_, finding) ->
            finding.status == FindingStatus.OPEN || finding.status == FindingStatus.IN_PROGRESS || finding.severity == FindingSeverity.HIGH || finding.severity == FindingSeverity.CRITICAL
        }
    }

    fun createNewReport(
        type: ReportType,
        site: Site,
        customDateString: String? = null
    ): Report {
        val userRole = _currentRole.value ?: UserRole.INSPECTOR
        val timeStr = customDateString ?: getCurrentGhanaTimeString()
        val seqNum = (getAllReports().size + 1).toString().padStart(3, '0')
        val repNum = "REP-2024-${site.id.takeLast(3)}-$seqNum"

        val newReport = Report(
            id = UUID.randomUUID().toString(),
            reportNumber = repNum,
            title = "${type.displayName} - ${site.name}",
            type = type,
            siteId = site.id,
            siteName = site.name,
            siteRegion = site.region,
            inspectorName = userRole.displayName,
            inspectorRoleTitle = userRole.title,
            dateRecordedFormatted = timeStr,
            timestampMillis = System.currentTimeMillis(),
            status = ReportStatus.DRAFT,
            checklistItems = ChecklistTemplates.createChecklistForType(type).toMutableList(),
            syncStatus = if (_isOfflineMode.value == true) SyncStatus.PENDING_SYNC else SyncStatus.SYNCED
        )

        val audit = AuditEntry(
            id = UUID.randomUUID().toString(),
            reportId = newReport.id,
            timestampFormatted = timeStr,
            userName = userRole.displayName,
            userRole = userRole,
            actionText = "Report created and saved as draft"
        )
        newReport.auditTrail.add(audit)

        val currentList = getAllReports().toMutableList()
        currentList.add(0, newReport)
        _reports.postValue(currentList)
        return newReport
    }

    fun updateChecklistItemStatus(
        reportId: String,
        itemId: String,
        newStatus: ChecklistItemStatus,
        notes: String
    ) {
        val report = getReportById(reportId) ?: return
        val item = report.checklistItems.find { it.id == itemId } ?: return
        item.status = newStatus
        item.notes = notes

        addAuditLog(report, "Updated item '${item.title}' status to ${newStatus.label}")
        notifyDataChanged()
    }

    fun addFindingToReport(
        reportId: String,
        checklistItemId: String?,
        title: String,
        description: String,
        severity: FindingSeverity,
        recommendedAction: String,
        assignedTeam: String,
        assignedPerson: String,
        dueDate: String
    ): Finding {
        val report = getReportById(reportId) ?: throw IllegalArgumentException("Report not found")
        val findingId = "FIND-${UUID.randomUUID().toString().take(6).uppercase()}"

        val finding = Finding(
            id = findingId,
            reportId = reportId,
            checklistItemId = checklistItemId,
            title = title,
            description = description,
            severity = severity,
            recommendedAction = recommendedAction,
            assignedTeam = assignedTeam,
            assignedPerson = assignedPerson,
            dueDate = dueDate,
            status = FindingStatus.OPEN
        )

        report.findings.add(0, finding)

        if (checklistItemId != null) {
            val item = report.checklistItems.find { it.id == checklistItemId }
            if (item != null) {
                item.status = ChecklistItemStatus.ISSUE
                item.findingId = finding.id
            }
        }

        addAuditLog(report, "Logged finding: '${title}' (${severity.label} Severity)")
        notifyDataChanged()
        return finding
    }

    fun addPhotoToReport(
        reportId: String,
        checklistItemId: String?,
        findingId: String?,
        uriPath: String,
        caption: String,
        evidenceType: EvidenceType
    ): PhotoEvidence {
        val report = getReportById(reportId) ?: throw IllegalArgumentException("Report not found")
        val photo = PhotoEvidence(
            id = "PHOTO-${UUID.randomUUID().toString().take(6).uppercase()}",
            reportId = reportId,
            checklistItemId = checklistItemId,
            findingId = findingId,
            uriPath = uriPath,
            caption = caption,
            evidenceType = evidenceType,
            capturedAtFormatted = getCurrentGhanaTimeString()
        )

        report.photos.add(0, photo)

        if (checklistItemId != null) {
            val item = report.checklistItems.find { it.id == checklistItemId }
            item?.photoIds?.add(photo.id)
        }

        addAuditLog(report, "Added photo evidence: '${caption}' (${evidenceType.label})")
        notifyDataChanged()
        return photo
    }

    fun removePhotoFromReport(reportId: String, photoId: String) {
        val report = getReportById(reportId) ?: return
        val photo = report.photos.find { it.id == photoId } ?: return
        report.photos.remove(photo)

        report.checklistItems.forEach { item ->
            item.photoIds.remove(photoId)
        }

        addAuditLog(report, "Removed photo evidence: '${photo.caption}'")
        notifyDataChanged()
    }

    fun updateFindingStatus(
        reportId: String,
        findingId: String,
        newStatus: FindingStatus,
        resolutionNotes: String,
        resolutionPhotoUri: String? = null
    ) {
        val report = getReportById(reportId) ?: return
        val finding = report.findings.find { it.id == findingId } ?: return

        val updatedFinding = finding.copy(
            status = newStatus,
            resolutionNotes = resolutionNotes,
            resolutionPhotoUri = resolutionPhotoUri ?: finding.resolutionPhotoUri,
            updatedAtMillis = System.currentTimeMillis()
        )

        val idx = report.findings.indexOf(finding)
        if (idx >= 0) {
            report.findings[idx] = updatedFinding
        }

        val role = _currentRole.value ?: UserRole.INSPECTOR
        addAuditLog(report, "${role.displayName} updated finding '${finding.title}' status to ${newStatus.label}")
        notifyDataChanged()
    }

    fun submitReportForApproval(reportId: String) {
        val report = getReportById(reportId) ?: return
        report.status = ReportStatus.PENDING_APPROVAL
        if (_isOfflineMode.value == true) {
            report.syncStatus = SyncStatus.PENDING_SYNC
        } else {
            report.syncStatus = SyncStatus.SYNCED
        }

        val role = _currentRole.value ?: UserRole.INSPECTOR
        addAuditLog(report, "${role.displayName} submitted report for Supervisor Review")
        notifyDataChanged()
    }

    fun supervisorReviewReport(reportId: String, isApproved: Boolean, comment: String) {
        val report = getReportById(reportId) ?: return
        val role = _currentRole.value ?: UserRole.SUPERVISOR

        report.supervisorName = role.displayName
        report.supervisorComment = comment
        report.status = if (isApproved) ReportStatus.APPROVED else ReportStatus.REVISION_REQUESTED

        val action = if (isApproved) "Approved report with comment: '$comment'" else "Requested revision with comment: '$comment'"
        addAuditLog(report, "${role.displayName} (Supervisor) $action")
        notifyDataChanged()
    }

    fun syncPendingReports(): Int {
        val pending = getAllReports().filter { it.syncStatus == SyncStatus.PENDING_SYNC }
        val count = pending.size
        pending.forEach { report ->
            report.syncStatus = SyncStatus.SYNCED
            addAuditLog(report, "System synced report to cloud server successfully")
        }
        notifyDataChanged()
        return count
    }

    fun addSite(name: String, region: String, type: String, address: String, contactPerson: String, contactPhone: String): Site {
        val newSite = Site(
            id = "SITE-${UUID.randomUUID().toString().take(4).uppercase()}",
            name = name,
            region = region,
            siteType = type,
            address = address,
            contactPerson = contactPerson,
            contactPhone = contactPhone,
            totalSweepsCompleted = 0
        )
        val currentSites = (_sites.value ?: emptyList()).toMutableList()
        currentSites.add(newSite)
        _sites.postValue(currentSites)
        return newSite
    }

    private fun addAuditLog(report: Report, actionText: String) {
        val role = _currentRole.value ?: UserRole.INSPECTOR
        val entry = AuditEntry(
            id = UUID.randomUUID().toString(),
            reportId = report.id,
            timestampFormatted = getCurrentGhanaTimeString(),
            userName = role.displayName,
            userRole = role,
            actionText = actionText
        )
        report.auditTrail.add(0, entry)
    }

    fun getCurrentGhanaTimeString(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm 'GMT'", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return sdf.format(Date())
    }

    private fun notifyDataChanged() {
        _reports.postValue(_reports.value)
    }
}
