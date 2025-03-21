package com.denizcan.substracktionapp.model

data class Country(
    val name: String,
    val code: String,
    val currency: Currency
)

data class Currency(
    val code: String,    // USD, EUR, TRY gibi
    val symbol: String,  // $, €, ₺ gibi
    val name: String     // US Dollar, Euro, Turkish Lira gibi
)

object CountryData {
    val countries = listOf(
        Country("Türkiye", "TR", Currency("TRY", "₺", "Turkish Lira")),
        Country("United States", "US", Currency("USD", "$", "US Dollar")),
        Country("United Kingdom", "GB", Currency("GBP", "£", "British Pound")),
        Country("European Union", "EU", Currency("EUR", "€", "Euro")),
        // Diğer ülkeler...
    )
} 