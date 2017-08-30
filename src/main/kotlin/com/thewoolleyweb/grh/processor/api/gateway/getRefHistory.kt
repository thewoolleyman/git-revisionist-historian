package com.thewoolleyweb.grh.processor.api.gateway

import com.github.kittinunf.fuel.httpPost
import com.thewoolleyweb.grh.processor.api.Repo

typealias GetRefHistory = (String, Repo, String) -> FuelReturnValue

fun getRefHistory(endpoint: String, repo: Repo, ref: String): FuelReturnValue {
  val graphqlQuery = """
    |{
    |  repository(owner: \"${repo.owner}\", name: \"${repo.name}\") {
    |    ref(qualifiedName: \"$ref\") {
    |      name
    |      target {
    |        ... on Commit {
    |          history(first: 100) {
    |            edges {
    |              node {
    |                oid
    |                message
    |              }
    |            }
    |          }
    |        }
    |      }
    |    }
    |  }
    |}
    """.trimMargin().replace("\n", "")
  val body = """{"query": "$graphqlQuery"}"""
  return endpoint
    .httpPost(listOf())
    .body(body)
    .header(v4Headers())
    .responseString()
}

private fun v4Headers(): Map<String, String> {
  return mapOf(
    "Authorization" to "bearer ${System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN")}",
    "Content-Type" to "application/json"
  )
}
