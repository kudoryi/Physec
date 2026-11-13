package com.kudoryi.physec.model

data class PhotoEvidence(
    val id: String,
    val reportId: String,
    val checklistItemId: String? = null,
    val findingId: String? = null,
    val uriPath: String, // local file URI or drawable resource name / sample image identifier
    val caption: String,
    val evidenceType: EvidenceType,
    val capturedAtFormatted: String, // e.g., "24 Oct 2024, 14:15 GMT"
    val timestampMillis: Long = System.currentTimeMillis()
)
