package com.kudoryi.physec.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.kudoryi.physec.model.Report
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun generatePdfAndShare(context: Context, report: Report) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 portrait dimensions in points
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint()
            var y = 40f

            // Header Background Bar (Navy 900)
            paint.color = Color.parseColor("#0F172A")
            canvas.drawRect(0f, 0f, 595f, 90f, paint)

            // Header Title
            paint.color = Color.WHITE
            paint.textSize = 22f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("PhySec Field Operations Report", 30f, 42f, paint)

            // Header Subtitle
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Physical Security & Health & Safety Audit • Ghana Telecoms", 30f, 65f, paint)

            // Report Details Box
            y = 110f
            paint.color = Color.parseColor("#1E293B")
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("REPORT: ${report.reportNumber}", 30f, y, paint)

            paint.color = Color.parseColor("#2563EB")
            canvas.drawText("STATUS: ${report.status.label.uppercase()}", 400f, y, paint)

            y += 24f
            paint.color = Color.parseColor("#334155")
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Site: ${report.siteName} (${report.siteRegion})", 30f, y, paint)
            canvas.drawText("Date: ${report.dateRecordedFormatted}", 330f, y, paint)

            y += 18f
            canvas.drawText("Inspector: ${report.inspectorName} (${report.inspectorRoleTitle})", 30f, y, paint)
            canvas.drawText("Type: ${report.type.displayName}", 330f, y, paint)

            if (!report.supervisorName.isNullOrEmpty()) {
                y += 18f
                canvas.drawText("Supervisor Approval: ${report.supervisorName} | Comment: ${report.supervisorComment ?: "Approved"}", 30f, y, paint)
            }

            // Separator line
            y += 25f
            paint.color = Color.parseColor("#CBD5E1")
            paint.strokeWidth = 1f
            canvas.drawLine(30f, y, 565f, y, paint)

            // Summary Stats Card
            y += 30f
            paint.color = Color.parseColor("#F8FAFC")
            canvas.drawRect(30f, y, 565f, y + 50f, paint)

            paint.color = Color.parseColor("#16A34A")
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("PASS: ${report.getPassCount()}", 50f, y + 30f, paint)

            paint.color = Color.parseColor("#DC2626")
            canvas.drawText("ISSUES: ${report.getIssueCount()}", 180f, y + 30f, paint)

            paint.color = Color.parseColor("#64748B")
            canvas.drawText("N/A: ${report.getNaCount()}", 320f, y + 30f, paint)

            paint.color = Color.parseColor("#D97706")
            canvas.drawText("FINDINGS: ${report.findings.size}", 440f, y + 30f, paint)

            y += 75f

            // Checklist Items Header
            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Checklist Audit Items", 30f, y, paint)
            y += 15f

            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            report.checklistItems.take(8).forEach { item ->
                paint.color = Color.parseColor("#334155")
                val titleTrunc = if (item.title.length > 50) item.title.take(47) + "..." else item.title
                canvas.drawText("• $titleTrunc", 35f, y, paint)

                when (item.status.name) {
                    "PASS" -> paint.color = Color.parseColor("#16A34A")
                    "ISSUE" -> paint.color = Color.parseColor("#DC2626")
                    else -> paint.color = Color.parseColor("#64748B")
                }
                canvas.drawText(item.status.label, 480f, y, paint)
                y += 18f
            }

            // Findings Section
            if (report.findings.isNotEmpty()) {
                y += 15f
                paint.color = Color.parseColor("#0F172A")
                paint.textSize = 14f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("Logged Findings & Corrective Action", 30f, y, paint)
                y += 20f

                report.findings.forEach { finding ->
                    paint.color = Color.parseColor("#DC2626")
                    paint.textSize = 11f
                    canvas.drawText("[${finding.severity.label.uppercase()}] ${finding.title}", 35f, y, paint)

                    y += 15f
                    paint.color = Color.parseColor("#475569")
                    paint.textSize = 10f
                    val descTrunc = if (finding.description.length > 70) finding.description.take(67) + "..." else finding.description
                    canvas.drawText("  Desc: $descTrunc", 35f, y, paint)

                    y += 15f
                    canvas.drawText("  Team: ${finding.assignedTeam} | Due: ${finding.dueDate} | Status: ${finding.status.label}", 35f, y, paint)
                    y += 22f
                }
            }

            // Audit Trail Summary
            y += 10f
            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Audit Trail", 30f, y, paint)
            y += 16f
            paint.color = Color.parseColor("#64748B")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            report.auditTrail.take(4).forEach { audit ->
                canvas.drawText("${audit.timestampFormatted} - ${audit.userName} (${audit.userRole.name}): ${audit.actionText}", 35f, y, paint)
                y += 14f
            }

            // Footer
            paint.color = Color.parseColor("#94A3B8")
            paint.textSize = 9f
            canvas.drawText("Generated by PhySec Ghana Field App • Confidentially Managed Security Audit", 30f, 820f, paint)

            pdfDocument.finishPage(page)

            // Save PDF file
            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!outputDir.exists()) outputDir.mkdirs()
            val pdfFile = File(outputDir, "${report.reportNumber.replace('/', '_')}.pdf")
            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()

            // Share file
            val fileUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "PhySec Security Report: ${report.reportNumber}")
                putExtra(Intent.EXTRA_TEXT, "Attached is the physical security audit report for ${report.siteName}.\nReport Number: ${report.reportNumber}\nDate: ${report.dateRecordedFormatted}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share PhySec Report PDF"))
            Toast.makeText(context, "PDF generated successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export PDF failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun generateTextSummary(report: Report): String {
        return buildString {
            appendLine("=== PHYSEC REPORT SUMMARY ===")
            appendLine("Report #: ${report.reportNumber}")
            appendLine("Title: ${report.title}")
            appendLine("Site: ${report.siteName} (${report.siteRegion})")
            appendLine("Date: ${report.dateRecordedFormatted}")
            appendLine("Inspector: ${report.inspectorName}")
            appendLine("Status: ${report.status.label}")
            appendLine()
            appendLine("--- CHECKLIST RESULTS ---")
            appendLine("Pass: ${report.getPassCount()} | Issues: ${report.getIssueCount()} | N/A: ${report.getNaCount()}")
            appendLine()
            if (report.findings.isNotEmpty()) {
                appendLine("--- LOGGED FINDINGS ---")
                report.findings.forEachIndexed { i, f ->
                    appendLine("${i + 1}. [${f.severity.label}] ${f.title}")
                    appendLine("   Team: ${f.assignedTeam} | Status: ${f.status.label} | Due: ${f.dueDate}")
                }
            }
            if (report.photos.isNotEmpty()) {
                appendLine()
                appendLine("Photo Evidence Captured: ${report.photos.size} image(s)")
            }
        }
    }
}
