package com.kudoryi.physec

import com.kudoryi.physec.data.ChecklistTemplates
import com.kudoryi.physec.data.SampleData
import com.kudoryi.physec.model.*
import org.junit.Assert.*
import org.junit.Test

class PhySecRepositoryTest {

    @Test
    fun testChecklistTemplateGeneration() {
        val securityItems = ChecklistTemplates.createChecklistForType(ReportType.SECURITY_SWEEP)
        assertTrue(securityItems.isNotEmpty())
        assertTrue(securityItems.any { it.sectionName.contains("Access Control", ignoreCase = true) })

        val safetyItems = ChecklistTemplates.createChecklistForType(ReportType.SAFETY_INSPECTION)
        assertTrue(safetyItems.isNotEmpty())
        assertTrue(safetyItems.any { it.sectionName.contains("Fire", ignoreCase = true) })
    }

    @Test
    fun testSampleDataInitialReports() {
        val initialReports = SampleData.createInitialReports()
        assertTrue(initialReports.isNotEmpty())

        val accraReport = initialReports.find { it.siteId == "SITE-ACC-01" }
        assertNotNull(accraReport)
        assertEquals(ReportStatus.APPROVED, accraReport?.status)
        assertEquals(1, accraReport?.getIssueCount())
        assertTrue(accraReport?.getPassCount() ?: 0 > 0)

        val kumasiReport = initialReports.find { it.siteId == "SITE-KMS-02" }
        assertNotNull(kumasiReport)
        assertEquals(ReportStatus.PENDING_APPROVAL, kumasiReport?.status)
        assertEquals(2, kumasiReport?.findings?.size)
    }

    @Test
    fun testReportCounters() {
        val items = mutableListOf(
            ReportChecklistItem("1", "Sec", "Item 1", "Prompt 1", ChecklistItemStatus.PASS),
            ReportChecklistItem("2", "Sec", "Item 2", "Prompt 2", ChecklistItemStatus.ISSUE),
            ReportChecklistItem("3", "Sec", "Item 3", "Prompt 3", ChecklistItemStatus.NOT_APPLICABLE),
            ReportChecklistItem("4", "Sec", "Item 4", "Prompt 4", ChecklistItemStatus.UNCHECKED)
        )

        val report = Report(
            id = "R1",
            reportNumber = "REP-001",
            title = "Test Audit",
            type = ReportType.SECURITY_SWEEP,
            siteId = "SITE-1",
            siteName = "Accra Center",
            siteRegion = "Greater Accra",
            inspectorName = "Kofi Mensah",
            inspectorRoleTitle = "Inspector",
            dateRecordedFormatted = "24/10/2024 10:00 GMT",
            checklistItems = items
        )

        assertEquals(1, report.getPassCount())
        assertEquals(1, report.getIssueCount())
        assertEquals(1, report.getNaCount())
        assertEquals(1, report.getUncheckedCount())
    }
}
