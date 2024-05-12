package com.kaushanidewmini.taskmanager

data class TaskDAO(
    val id: Int,
    val title: String,
    val content: String,
    val priority: String)