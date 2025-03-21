package com.denizcan.substracktionapp.util

fun String.localized(language: String): String {
    return StringResources.get(this, language)
} 