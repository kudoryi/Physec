package com.kudoryi.physec.model

enum class ReportType(val displayName: String, val shortCode: String) {
    SECURITY_SWEEP("Physical Security Sweep", "SEC"),
    SAFETY_INSPECTION("Workplace Health & Safety Check", "HSE")
}

enum class ReportStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    REVISION_REQUESTED("Revision Requested")
}

enum class ChecklistItemStatus(val label: String) {
    UNCHECKED("Not Checked"),
    PASS("Pass"),
    ISSUE("Issue Identified"),
    NOT_APPLICABLE("N/A")
}

enum class FindingSeverity(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical")
}

enum class FindingStatus(val label: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CLOSED("Closed")
}

enum class EvidenceType(val label: String) {
    SUPPORTS_ISSUE("Supports Issue"),
    SHOWS_RESOLVED_ISSUE("Shows Resolved Issue"),
    GENERAL_EVIDENCE("General Field Evidence")
}

enum class SyncStatus(val label: String) {
    SYNCED("Synced with Server"),
    PENDING_SYNC("Pending Offline Sync"),
    SYNC_ERROR("Sync Warning")
}
