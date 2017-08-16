package com.thewoolleyweb.grh

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Args(parser: ArgParser) {
  val configFile by parser.storing("-c", "--config", help = "Path to config file")
    .default("./grh-config.json")
  val dryRun by parser.flagging("-n", "--dry-run", help = "Print commands to invoke but don't actually make changes")
  val skipPush by parser.flagging(
    "-s", "--skip-push", help = "Apply revised tags/branches locally, but do not automatically push"
  )
}
