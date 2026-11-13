package com.kudoryi.physec.model

data class ReportChecklistItem(
    val id: String,
    val sectionName: String, // e.g. Access Control, CCTV, Fire Safety
    val title: String,
    val prompt: String,
    var status: ChecklistItemStatus = ChecklistItemStatus.UNCHECKED,
    var notes: String = "",
    var findingId: String? = null,
    val photoIds: MutableList<String> = mutableListOf()
)
