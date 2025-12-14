package com.example.medease.data.model.ml

data class WqiRequest(
    val ammonia: Double,
    val bod: Double,
    val `do`: Double,
    val orthophosphate: Double,
    val ph: Double,
    val temperature: Double,
    val nitrogen: Double,
    val nitrate: Double
)
