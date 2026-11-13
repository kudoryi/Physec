package com.kudoryi.physec.data

import com.kudoryi.physec.model.ReportChecklistItem
import com.kudoryi.physec.model.ReportType
import java.util.UUID

object ChecklistTemplates {

    fun createChecklistForType(type: ReportType): List<ReportChecklistItem> {
        return when (type) {
            ReportType.SECURITY_SWEEP -> getSecuritySweepItems()
            ReportType.SAFETY_INSPECTION -> getHealthAndSafetyItems()
        }
    }

    private fun getSecuritySweepItems(): List<ReportChecklistItem> {
        return listOf(
            // Access Control & Perimeter
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Access Control & Perimeter",
                title = "Perimeter Fencing & Main Gates",
                prompt = "Inspect outer fence line, razor wire integrity, anti-climb barriers, and main vehicle gate locking mechanisms."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Access Control & Perimeter",
                title = "Biometric & Badge Readers",
                prompt = "Verify turnstiles, biometric scanners, RFID card readers, and visitor sign-in logs at entry control points."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Access Control & Perimeter",
                title = "Guard Shack & Security Post",
                prompt = "Check guard station readiness, incident logbook entries, emergency panic buttons, and communication radios."
            ),

            // Doors, Locks & Vaults
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Doors & Locks",
                title = "Data Hall & Server Room Doors",
                prompt = "Ensure magnetic locks engage properly, keycard audit logging is functional, and door-ajar alarms trigger correctly."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Doors & Locks",
                title = "Emergency Exit Lock Integrity",
                prompt = "Check panic hardware operates smoothly from inside while remaining securely locked against external entry."
            ),

            // CCTV & Video Surveillance
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "CCTV Visibility",
                title = "Perimeter & Entry Camera Coverage",
                prompt = "Confirm CCTV camera angles cover all boundary zones without blind spots, camera housing glass is clean, and night vision works."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "CCTV Visibility",
                title = "NVR/DVR Recording & Retention",
                prompt = "Verify control room monitors display live feeds, recording status is green, and storage retention exceeds 30 days."
            ),

            // Lighting & Illumination
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Lighting & Illumination",
                title = "Exterior Floodlights & Perimeter Lights",
                prompt = "Test night illumination across yard areas, transformer yards, tower bases, and perimeter walkways."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Lighting & Illumination",
                title = "Interior & Emergency Backup Lighting",
                prompt = "Test emergency battery backup light units in hallways, switch rooms, and generator rooms upon power fail simulation."
            )
        )
    }

    private fun getHealthAndSafetyItems(): List<ReportChecklistItem> {
        return listOf(
            // Trip & Fall Hazards
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Trip & Walkway Safety",
                title = "Cable Management & Raised Floor Tiles",
                prompt = "Inspect server room flooring, cable trays, temporary extension cords, and walkway obstructions."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Trip & Walkway Safety",
                title = "Stairways, Handrails & Drainage Grates",
                prompt = "Ensure stair treads are non-slip, handrails are sturdy, and exterior rainwater drainage grates are covered."
            ),

            // Fire Safety & Extinguishers
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Fire Exits & Suppression",
                title = "Fire Exit Route Unobstructed",
                prompt = "Verify evacuation routes are clear of boxes, pallets, or equipment, and emergency exit signs are lit."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Fire Exits & Suppression",
                title = "Fire Extinguishers & FM-200 Gas Systems",
                prompt = "Check pressure gauges in green zone, inspect tags valid within 12 months, and gas cylinder safety pins intact."
            ),

            // Electrical & Environmental Hazards
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Electrical & Generator Safety",
                title = "Exposed Wiring & Panel Enclosures",
                prompt = "Confirm all high voltage distribution panels are closed, locked, labeled, and free from exposed copper wiring."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Electrical & Generator Safety",
                title = "Diesel Generator Room & Fuel Storage",
                prompt = "Inspect diesel generator bunding for oil leaks, check battery terminals, exhaust ventilation, and spill kits."
            ),
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Electrical & Generator Safety",
                title = "UPS Battery Room Ventilation & Temp",
                prompt = "Verify battery room HVAC cooling (<25°C), hydrogen gas venting, eye wash stations, and battery cell health."
            ),

            // Signage & PPE
            ReportChecklistItem(
                id = UUID.randomUUID().toString(),
                sectionName = "Signage & Warning Notices",
                title = "Hazard Signage & PPE Compliance",
                prompt = "Check mandatory PPE signs (Ear Protection, High Vis, Hard Hat) and high-voltage warning labels on equipment doors."
            )
        )
    }
}
