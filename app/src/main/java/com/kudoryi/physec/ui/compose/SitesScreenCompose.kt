package com.kudoryi.physec.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudoryi.physec.data.PhySecRepository
import com.kudoryi.physec.model.Site
import com.kudoryi.physec.ui.theme.*

@Composable
fun SitesScreenCompose(
    onStartAuditForSite: (Site) -> Unit,
    onAddSite: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val sitesList by PhySecRepository.sites.observeAsState(emptyList())

    val filtered = sitesList.filter { site ->
        val q = searchQuery.trim().lowercase()
        q.isEmpty() ||
                site.name.lowercase().contains(q) ||
                site.region.lowercase().contains(q) ||
                site.siteType.lowercase().contains(q) ||
                site.address.lowercase().contains(q)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Telecom Facilities Directory", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Managed data centers, switch hubs, landing stations, and cell towers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search site by name, region, type...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.id }) { site ->
                    SiteCardCompose(site, onStartAudit = { onStartAuditForSite(site) })
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddSite,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("+ Add Site", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SiteCardCompose(site: Site, onStartAudit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(color = Navy900, shape = RoundedCornerShape(12.dp)) {
                    Text(site.region.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Text("${site.totalSweepsCompleted} Audits", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(site.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("📍 ${site.address}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Contact: ${site.contactPerson} (${site.contactPhone})", fontSize = 11.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onStartAudit,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Start Audit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
