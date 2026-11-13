package com.kudoryi.physec.model

data class Finding(
    val id: String,
    val reportId: String,
    val checklistItemId: String? = null,
    val title: String,
    val description: String,
    val severity: FindingSeverity,
    val recommendedAction: String,
    val assignedTeam: String, // Facilities & Maintenance, IT Security, HSE, Vendor
    val assignedPerson: String = "Facilities Duty Officer",
    val dueDate: String, // Format "DD/MM/YYYY" e.g., "28/10/2024"
    val status: FindingStatus = FindingStatus.OPEN,
    val resolutionNotes: String = "",
    val resolutionPhotoUri: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
