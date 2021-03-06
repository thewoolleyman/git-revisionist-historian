package com.thewoolleyweb.grh.processor.api

import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.output
import com.thewoolleyweb.grh.plan.createPlan
import com.thewoolleyweb.grh.processor.api.gateway.createRef
import com.thewoolleyweb.grh.processor.api.gateway.getRefHistory
import com.thewoolleyweb.grh.processor.api.gateway.replaceRef

fun processWithApi(grhConfig: GrhConfig, args: Args) {
  val v3Endpoint = args.v3Endpoint ?: "https://api.github.com"
  val v4Endpoint = args.v4Endpoint ?: "https://api.github.com/graphql"
  val repo = parseRepo(args.repo)
  val log = getLog(v4Endpoint, repo, grhConfig.branchToRevise, ::getRefHistory)
  val plan = createPlan(grhConfig, log)
  val createRef = ::createRef
  val replaceRef = ::replaceRef
  executePlan(plan, args.dryRun, v3Endpoint, repo, createRef, replaceRef, ::output)
}


