package com.thewoolleyweb.grh.processor.api

import com.github.kittinunf.result.Result
import com.thewoolleyweb.grh.Output
import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.plan.Action
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.processor.api.gateway.CreateRef
import com.thewoolleyweb.grh.processor.api.gateway.ReplaceRef

fun executePlan(plan: Plan, dryRun: Boolean, endpoint: String, repo: Repo, createRef: CreateRef, replaceRef: ReplaceRef, output: Output) {
  plan.actions.forEach { action ->
    when (action.command) {
      Command.TAG -> {
        output("> Creating or replacing tag '${action.refname}' for SHA '${action.sha}'...")
        if (dryRun) {
          output("  (dryRun is true, no action)\n")
        } else {
          createOrReplaceRef(endpoint, repo, action, createRef, replaceRef, output)
        }
      }
    }
  }
}

private fun createOrReplaceRef(endpoint: String, repo: Repo, action: Action, createRef: CreateRef, replaceRef: ReplaceRef, output: Output): String {

  val (_, createRefResponse, createRefResult) = createRef(endpoint, repo, action.refname, action.sha)

  val createRefFailureError = when (createRefResult) {
    is Result.Success -> {
      output("  Successfully created.\n")
      return createRefResult.value
    }
    is Result.Failure -> createRefResult.error
  }

  // createRefResult is Result.Failure, check if it was due to existing ref which needs to be replaced
  val createRefResponseStatusCode = createRefResponse.httpStatusCode
  val createRefResponseBody = String(createRefResponse.data)
  if (createRefResponseStatusCode == 422 &&
    Regex("Reference already exists").containsMatchIn(createRefResponseBody)) {

    val (_, _, replaceRefResult) = replaceRef(endpoint, repo, action.refname, action.sha)

    when (replaceRefResult) {
      is Result.Success -> {
        output("  Successfully replaced.\n")
        return replaceRefResult.value
      }
      is Result.Failure -> {
        output("\n")
        throw replaceRefResult.error
      }
    }

  } else {
    output("\n")
    throw createRefFailureError
  }

}
