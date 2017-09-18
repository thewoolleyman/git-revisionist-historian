package com.thewoolleyweb.grh.config

import java.net.MalformedURLException
import java.net.URL

fun loadConfig(config: String): GrhConfig {
  val configText = if (isURI(config)) {
    readConfigURL(config)
  } else {
    readConfigFile(config)
  }
  return parseConfig(StringBuilder(configText))
}

private fun isURI(config: String): Boolean {
  return try {
    URL(config)
    true
  } catch (e: MalformedURLException) {
    false
  }
}
