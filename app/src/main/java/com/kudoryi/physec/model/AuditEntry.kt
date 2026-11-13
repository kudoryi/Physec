package com.kudoryi.physec.model

data class AuditEntry(
    val id: String,
    val reportId: String,
    val timestampFormatted: String,
    val userName: String,
    val userRole: UserRole,
    val actionText: String
)
