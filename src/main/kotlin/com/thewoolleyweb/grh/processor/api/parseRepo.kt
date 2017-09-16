package com.thewoolleyweb.grh.processor.api

fun parseRepo(maybeRepo: String?): Repo {
  val repo = maybeRepo ?: throw RuntimeException("Internal Error: PROCESSOR=api should require REPO")
  val match = "^(\\S+)/(\\S+)$".toRegex().matchEntire(repo)
  val owner = match?.groups?.get(1)?.value ?: throw RuntimeException("Error: missing owner or name in REPO, expected format is 'owner/name'")
  // not possible to reach the missing name exception unless regex is broken,
  // but we are required to check it in order to make the return value non-optional
  val name = match.groups[2]?.value ?: throw RuntimeException("Internal Error: error in parseRepo regex")
  return Repo(owner, name)

}
