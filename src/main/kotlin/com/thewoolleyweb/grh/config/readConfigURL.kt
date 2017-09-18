package com.thewoolleyweb.grh.config

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.xenomachina.argparser.SystemExitException

fun readConfigURL(configURL: String): String {
  println("Reading config URL '$configURL")
  val (_, _, result) = configURL
    .httpGet()
    .responseString()

  when (result) {
    is Result.Success -> {
      return result.value
    }
    is Result.Failure -> {
      throw SystemExitException(result.error.toString(), 1)
    }
  }
}
