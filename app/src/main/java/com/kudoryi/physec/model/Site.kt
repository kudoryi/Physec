package com.kudoryi.physec.model

data class Site(
    val id: String,
    val name: String,
    val region: String, // e.g. Greater Accra, Ashanti, Western, Northern, Central
    val siteType: String, // Data Center, Switch Hub, Cell Tower, Landing Station, Regional Office
    val address: String,
    val contactPerson: String,
    val contactPhone: String,
    val totalSweepsCompleted: Int = 0
)
