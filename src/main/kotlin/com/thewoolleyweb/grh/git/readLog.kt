package com.thewoolleyweb.grh.git

import com.thewoolleyweb.grh.cmd.run

fun readLog(branchToRevise: String): Log {
  val logLines = run("git log $branchToRevise --pretty=oneline")
  val logLineRegex = """^(\S+)\s(.+)$""".toRegex()
  val commits = logLines
    .map { logLineRegex.matchEntire(it) }
    .map {
      Commit(
        // TODO: Why do these have to be handled differently???
        sha = it?.groups?.get(1)?.value ?: throw RuntimeException("unable to parse git commit sha"),
        message = it.groups[2]?.value ?: throw RuntimeException("Unable to parse git commit message")
      )
    }
    .reversed()
  return Log(
    commits = commits
  )
}
