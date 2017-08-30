package com.thewoolleyweb.grh.plan

import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.config.IncrementCommit
import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Invocation
import com.thewoolleyweb.grh.git.Log

fun createPlan(config: GrhConfig, log: Log): Plan {
  val invocations: ArrayList<Invocation> =
    incrementCommitsWithMatches(config.incrementCommits, log.commits)
      .fold(initial = arrayListOf<Invocation>(), operation = ::generateInvocations)
  invocations.add(Invocation(command = Command.PUSH_TAGS))
  return Plan(steps = invocations)
}

private fun incrementCommitsWithMatches(incrementCommits: List<IncrementCommit>, commits: List<Commit>)
  : List<Pair<Commit, IncrementCommit>> =
  commits
    .map { commit -> maybePairedCommit(commit, incrementCommits) }
    .filterNotNull()

private fun maybePairedCommit(commit: Commit, incrementCommits: List<IncrementCommit>): Pair<Commit, IncrementCommit>? {
  // return a pair of commit + first matching incrementCommit, if one is found, or null if not
  val maybeMatch: IncrementCommit? = incrementCommits.find { (message) ->
    message.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(commit.message)
  }
  if (maybeMatch != null)
    return Pair(commit, maybeMatch)
  else
    return null
}

private fun generateInvocations(
  invocations: ArrayList<Invocation>, incrementCommitWithMatch: Pair<Commit, IncrementCommit>):
  ArrayList<Invocation> {
  val commit = incrementCommitWithMatch.first
  val incrementCommit = incrementCommitWithMatch.second
  incrementCommit.tags.forEach { tag ->
    invocations.add(
      Invocation(
        command = Command.TAG,
        sha = commit.sha,
        tag = tag
      )
    )
  }
  return invocations
}

