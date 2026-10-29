package com.duoc.rackinspector.data.model

data class ComponentItem(
    val category: String, // Ejemplo: Switch, Servidor, Patch Panel, UPS
    var count: Int = 1
)