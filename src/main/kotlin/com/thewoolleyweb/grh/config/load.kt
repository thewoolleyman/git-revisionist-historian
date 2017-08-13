package com.thewoolleyweb.grh.config

import com.beust.klaxon.Parser

fun load(rawValue: StringBuilder): Any? {
    return Parser().parse(rawValue)
}