package com.thewoolleyweb.grh.processor.cli

import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log
import com.thewoolleyweb.grh.processhelper.Run

fun readLog(remote: String, branchToRevise: String, run: Run): Log {
  run("git fetch --update-head-ok $remote $branchToRevise:$branchToRevise")
  val logLines = run("git log $branchToRevise --pretty=oneline")
  val logLineRegex = """^(\S+)\s(.+)$""".toRegex()
  val commits = logLines
    .map { logLineRegex.matchEntire(it) }
    .map {
      Commit(
        // TODO: Why do these have to be handled differently???
        // ANSWER: Brackets are syntactic sugar that apparently don't handle optionals
        //         the same way as the native List type
        sha = it?.groups?.get(1)?.value ?: throw RuntimeException("unable to parse git commit sha"),
        message = it.groups[2]?.value ?: throw RuntimeException("Unable to parse git commit message")
      )
    }
  return Log(
    commits = commits
  )
}
