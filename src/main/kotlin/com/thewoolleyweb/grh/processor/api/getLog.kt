package com.thewoolleyweb.grh.processor.api

import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log
import com.thewoolleyweb.grh.processor.api.gateway.GetRefHistory

fun getLog(v4Endpoint: String, repo: Repo, ref: String,
           getRefHistory: GetRefHistory): Log {
  val commits = arrayListOf<Commit>()
  val (_, _, result) = getRefHistory(v4Endpoint, repo, ref)

  result.success { value -> commits.addAll(parseRefHistory(value)) }
  result.failure { error -> throw error } // TODO: maybe print request/response info on failure?

  return Log(
    commits = commits.reversed()
  )
}
