package com.thewoolleyweb.grh.processor.cli

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import com.thewoolleyweb.grh.Output
import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log
import com.thewoolleyweb.grh.processhelper.Run
import com.xenomachina.argparser.SystemExitException

fun readLog(args: Args, branchToRevise: String, run: Run, output: Output): Log {
  try {
    run("git fetch --update-head-ok ${args.remote} $branchToRevise:$branchToRevise")
  } catch (e: RuntimeException) {
    if ("non-fast-forward".toRegex().containsMatchIn(e.toString())) {
      if (args.dryRun || args.skipPush) {
        val warning = """
          |WARNING: Branch '$branchToRevise' has diverged from remote.
          |If you have rebased without yet pushing, this is expected.
          |Otherwise, you should pull the changes and try again.
          |
          """.trimMargin()
        output(Kolor.foreground(warning, Color.YELLOW))
      } else {
        val error = """
          |ERROR: Branch '$branchToRevise' has diverged from remote.
          |If you have rebased without yet pushing, this is expected.
          |However, you have not specified the `-n|--dry-run` nor the
          |`-s|--skip-push` option. This is not allowed, because it would
          |result in tags getting pushed that aren't on any remote branch.
          |You need to either:
          |1) Push your branch then try again, or
          |2) Use the `-s|--skip-push` option then manually push the branch/tags
          |
          """.trimMargin()
        output(Kolor.foreground(error, Color.RED))
        throw SystemExitException("Exiting with error.", 1)
      }
    } else {
      throw e
    }
  }

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
