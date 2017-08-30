package com.thewoolleyweb.grh.plan

import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.config.IncrementCommit
import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log

fun createPlan(config: GrhConfig, log: Log): Plan {
  val actions: ArrayList<Action> =
    incrementCommitsWithMatches(config.incrementCommits, log.commits)
      .fold(initial = arrayListOf<Action>(), operation = ::generateActions)
  return Plan(actions = actions)
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
  return if (maybeMatch != null)
    Pair(commit, maybeMatch)
  else
    null
}

private fun generateActions(
  actions: ArrayList<Action>, incrementCommitWithMatch: Pair<Commit, IncrementCommit>):
  ArrayList<Action> {
  val commit = incrementCommitWithMatch.first
  val incrementCommit = incrementCommitWithMatch.second
  incrementCommit.tags.forEach { tag ->
    actions.add(
      Action(
        command = Command.TAG,
        sha = commit.sha,
        refname = tag
      )
    )
  }
  return actions
}

