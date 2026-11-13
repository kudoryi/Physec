package com.kudoryi.physec.data

import com.kudoryi.physec.model.*

object SampleData {

    val SITES = listOf(
        Site(
            id = "SITE-ACC-01",
            name = "Accra Central Data Center",
            region = "Greater Accra",
            siteType = "Tier-3 Data Center",
            address = "Plot 14, Ring Road Central, Accra",
            contactPerson = "Kwame Addo (Site Supervisor)",
            contactPhone = "+233 24 412 3456",
            totalSweepsCompleted = 42
        ),
        Site(
            id = "SITE-KMS-02",
            name = "Kumasi Main Switch Hub",
            region = "Ashanti",
            siteType = "Regional Switching Hub",
            address = "Adum Commercial District, Kumasi",
            contactPerson = "Emmanuel Osei (Facility Lead)",
            contactPhone = "+233 20 811 9876",
            totalSweepsCompleted = 28
        ),
        Site(
            id = "SITE-TKD-03",
            name = "Takoradi Port Landing Facility",
            region = "Western",
            siteType = "Subsea Cable Station & Tower",
            address = "Harbour Road, Takoradi",
            contactPerson = "Esi Nyarko (Security Officer)",
            contactPhone = "+233 55 334 1122",
            totalSweepsCompleted = 19
        ),
        Site(
            id = "SITE-TMA-04",
            name = "Tema Subsea Gate Station",
            region = "Greater Accra",
            siteType = "Subsea Cable Landing Site",
            address = "Community 2 Industrial Zone, Tema",
            contactPerson = "Joseph Baah (HSE Inspector)",
            contactPhone = "+233 27 765 4321",
            totalSweepsCompleted = 35
        ),
        Site(
            id = "SITE-TML-05",
            name = "Tamale Regional Office & Tower",
            region = "Northern",
            siteType = "Microwave Hub & Office",
            address = "Hospital Road, Tamale",
            contactPerson = "Abubakari Alhassan (Site Admin)",
            contactPhone = "+233 26 998 7766",
            totalSweepsCompleted = 14
        ),
        Site(
            id = "SITE-CPC-06",
            name = "Cape Coast Central Exchange",
            region = "Central",
            siteType = "Exchange & Distribution Node",
            address = "Commercial Street, Cape Coast",
            contactPerson = "Francis Kwofie (Tech Lead)",
            contactPhone = "+233 24 554 3322",
            totalSweepsCompleted = 11
        )
    )

    fun createInitialReports(): MutableList<Report> {
        val reports = mutableListOf<Report>()

        // Report 1: Submitted & Approved Security Sweep at Accra Central Data Center
        val rep1Id = "REP-2024-ACC-010"
        val rep1Checklist = ChecklistTemplates.createChecklistForType(ReportType.SECURITY_SWEEP).toMutableList()
        // Mark items
        rep1Checklist[0].status = ChecklistItemStatus.PASS
        rep1Checklist[1].status = ChecklistItemStatus.PASS
        rep1Checklist[2].status = ChecklistItemStatus.PASS
        rep1Checklist[3].status = ChecklistItemStatus.PASS
        rep1Checklist[4].status = ChecklistItemStatus.ISSUE
        rep1Checklist[4].notes = "Server room emergency exit panic bar stiff; requires maintenance."
        rep1Checklist[5].status = ChecklistItemStatus.PASS
        rep1Checklist[6].status = ChecklistItemStatus.PASS
        rep1Checklist[7].status = ChecklistItemStatus.PASS

        val f1 = Finding(
            id = "FIND-010-1",
            reportId = rep1Id,
            checklistItemId = rep1Checklist[4].id,
            title = "Emergency Exit Panic Hardware Lock Stiff",
            description = "The mechanical latch on emergency exit Door B-2 in Data Hall 1 binds during manual egress test.",
            severity = FindingSeverity.HIGH,
            recommendedAction = "Lubricate mechanical linkage and recalibrate door strike plate within 48 hours.",
            assignedTeam = "Facilities & Maintenance",
            assignedPerson = "Kofi Mensah",
            dueDate = "28/10/2024",
            status = FindingStatus.IN_PROGRESS,
            resolutionNotes = "Technician dispatched to adjust strike plate."
        )
        rep1Checklist[4].findingId = f1.id

        val p1 = PhotoEvidence(
            id = "PHOTO-010-1",
            reportId = rep1Id,
            checklistItemId = rep1Checklist[4].id,
            findingId = f1.id,
            uriPath = "sample_door_lock",
            caption = "Door B-2 mechanical panic bar latch sticking on test release",
            evidenceType = EvidenceType.SUPPORTS_ISSUE,
            capturedAtFormatted = "24 Oct 2024, 10:14 GMT"
        )

        val report1 = Report(
            id = rep1Id,
            reportNumber = "REP-2024-ACC-010",
            title = "Q4 Physical Security & Access Audit",
            type = ReportType.SECURITY_SWEEP,
            siteId = "SITE-ACC-01",
            siteName = "Accra Central Data Center",
            siteRegion = "Greater Accra",
            inspectorName = "Kofi Mensah",
            inspectorRoleTitle = "Field Security Officer",
            dateRecordedFormatted = "24/10/2024 10:00 GMT",
            timestampMillis = System.currentTimeMillis() - 86400000L * 2,
            status = ReportStatus.APPROVED,
            supervisorName = "Ama Osei",
            supervisorComment = "Approved. Ensure high-priority door finding is resolved before weekend.",
            checklistItems = rep1Checklist,
            findings = mutableListOf(f1),
            photos = mutableListOf(p1),
            auditTrail = mutableListOf(
                AuditEntry("A1", rep1Id, "24/10/2024 10:00 GMT", "Kofi Mensah", UserRole.INSPECTOR, "Report created and sweep initiated"),
                AuditEntry("A2", rep1Id, "24/10/2024 10:45 GMT", "Kofi Mensah", UserRole.INSPECTOR, "Attached photo evidence for Door B-2 issue"),
                AuditEntry("A3", rep1Id, "24/10/2024 11:00 GMT", "Kofi Mensah", UserRole.INSPECTOR, "Submitted report for supervisor review"),
                AuditEntry("A4", rep1Id, "24/10/2024 14:20 GMT", "Ama Osei", UserRole.SUPERVISOR, "Approved report with comment")
            ),
            syncStatus = SyncStatus.SYNCED
        )
        reports.add(report1)

        // Report 2: Pending Approval Health & Safety Check at Kumasi Hub
        val rep2Id = "REP-2024-KMS-004"
        val rep2Checklist = ChecklistTemplates.createChecklistForType(ReportType.SAFETY_INSPECTION).toMutableList()
        rep2Checklist[0].status = ChecklistItemStatus.ISSUE
        rep2Checklist[0].notes = "Trailing ethernet cables across main aisle in Telecom Switch Room."
        rep2Checklist[1].status = ChecklistItemStatus.PASS
        rep2Checklist[2].status = ChecklistItemStatus.PASS
        rep2Checklist[3].status = ChecklistItemStatus.ISSUE
        rep2Checklist[3].notes = "Fire extinguisher CO2 canister #4 inspection tag expired in Sept 2024."
        rep2Checklist[4].status = ChecklistItemStatus.PASS
        rep2Checklist[5].status = ChecklistItemStatus.PASS

        val f2_1 = Finding(
            id = "FIND-004-1",
            reportId = rep2Id,
            checklistItemId = rep2Checklist[0].id,
            title = "Unsecured Cables Across Switch Room Aisle",
            description = "Loose CAT6 cables routed across high-traffic floor space creating severe trip hazard.",
            severity = FindingSeverity.MEDIUM,
            recommendedAction = "Install rubber floor cable protector or reroute through overhead cable ladder.",
            assignedTeam = "IT Infrastructure Security",
            assignedPerson = "Adwoa Sarfo",
            dueDate = "26/10/2024",
            status = FindingStatus.OPEN
        )
        rep2Checklist[0].findingId = f2_1.id

        val f2_2 = Finding(
            id = "FIND-004-2",
            reportId = rep2Id,
            checklistItemId = rep2Checklist[3].id,
            title = "Expired CO2 Fire Extinguisher Inspection",
            description = "CO2 Fire Extinguisher FE-KMS-09 tag shows last inspection date of 15/09/2023.",
            severity = FindingSeverity.CRITICAL,
            recommendedAction = "Contractor fire safety recertification and pressure test required immediately.",
            assignedTeam = "HSE Dept",
            assignedPerson = "Yaw Boateng",
            dueDate = "25/10/2024",
            status = FindingStatus.OPEN
        )
        rep2Checklist[3].findingId = f2_2.id

        val p2_1 = PhotoEvidence(
            id = "PHOTO-004-1",
            reportId = rep2Id,
            checklistItemId = rep2Checklist[0].id,
            findingId = f2_1.id,
            uriPath = "sample_cables",
            caption = "Loose floor cabling across main switch room walkway",
            evidenceType = EvidenceType.SUPPORTS_ISSUE,
            capturedAtFormatted = "25 Oct 2024, 08:30 GMT"
        )
        val p2_2 = PhotoEvidence(
            id = "PHOTO-004-2",
            reportId = rep2Id,
            checklistItemId = rep2Checklist[3].id,
            findingId = f2_2.id,
            uriPath = "sample_extinguisher",
            caption = "Expired inspection tag on 5kg CO2 Extinguisher #4",
            evidenceType = EvidenceType.SUPPORTS_ISSUE,
            capturedAtFormatted = "25 Oct 2024, 08:45 GMT"
        )

        val report2 = Report(
            id = rep2Id,
            reportNumber = "REP-2024-KMS-004",
            title = "Monthly HSE Routine Check",
            type = ReportType.SAFETY_INSPECTION,
            siteId = "SITE-KMS-02",
            siteName = "Kumasi Main Switch Hub",
            siteRegion = "Ashanti",
            inspectorName = "Kofi Mensah",
            inspectorRoleTitle = "Field Security Officer",
            dateRecordedFormatted = "25/10/2024 08:15 GMT",
            timestampMillis = System.currentTimeMillis() - 43200000L,
            status = ReportStatus.PENDING_APPROVAL,
            checklistItems = rep2Checklist,
            findings = mutableListOf(f2_1, f2_2),
            photos = mutableListOf(p2_1, p2_2),
            auditTrail = mutableListOf(
                AuditEntry("A10", rep2Id, "25/10/2024 08:15 GMT", "Kofi Mensah", UserRole.INSPECTOR, "Report created"),
                AuditEntry("A11", rep2Id, "25/10/2024 09:00 GMT", "Kofi Mensah", UserRole.INSPECTOR, "Log findings and attached 2 photo evidences"),
                AuditEntry("A12", rep2Id, "25/10/2024 09:15 GMT", "Kofi Mensah", UserRole.INSPECTOR, "Submitted report for supervisor approval")
            ),
            syncStatus = SyncStatus.PENDING_SYNC
        )
        reports.add(report2)

        // Report 3: Draft Security Sweep at Takoradi Port Landing Facility
        val rep3Id = "REP-2024-TKD-002"
        val rep3Checklist = ChecklistTemplates.createChecklistForType(ReportType.SECURITY_SWEEP).toMutableList()
        rep3Checklist[0].status = ChecklistItemStatus.PASS
        rep3Checklist[1].status = ChecklistItemStatus.PASS
        rep3Checklist[5].status = ChecklistItemStatus.ISSUE
        rep3Checklist[5].notes = "Camera 07 lens obstructed by overgrown palm tree branches on East perimeter."

        val f3 = Finding(
            id = "FIND-002-1",
            reportId = rep3Id,
            checklistItemId = rep3Checklist[5].id,
            title = "CCTV Camera East Boundary Obstruction",
            description = "Vegetation overhang obscuring IR camera view of subsea cable trench entrance.",
            severity = FindingSeverity.MEDIUM,
            recommendedAction = "Trim tree branches within 2 meters of camera mast.",
            assignedTeam = "Facilities & Maintenance",
            assignedPerson = "Kwesi Appiah",
            dueDate = "29/10/2024",
            status = FindingStatus.OPEN
        )
        rep3Checklist[5].findingId = f3.id

        val report3 = Report(
            id = rep3Id,
            reportNumber = "REP-2024-TKD-002",
            title = "Weekly Perimeter & Subsea Landing Sweep",
            type = ReportType.SECURITY_SWEEP,
            siteId = "SITE-TKD-03",
            siteName = "Takoradi Port Landing Facility",
            siteRegion = "Western",
            inspectorName = "Kofi Mensah",
            inspectorRoleTitle = "Field Security Officer",
            dateRecordedFormatted = "26/10/2024 07:00 GMT",
            timestampMillis = System.currentTimeMillis() - 7200000L,
            status = ReportStatus.DRAFT,
            checklistItems = rep3Checklist,
            findings = mutableListOf(f3),
            photos = mutableListOf(),
            auditTrail = mutableListOf(
                AuditEntry("A20", rep3Id, "26/10/2024 07:00 GMT", "Kofi Mensah", UserRole.INSPECTOR, "Saved preliminary draft")
            ),
            syncStatus = SyncStatus.PENDING_SYNC
        )
        reports.add(report3)

        return reports
    }
}
