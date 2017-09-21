package com.thewoolleyweb.grh.args

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default

class Args(parser: ArgParser) {
  val config by parser.storing("-c", "--config", help = "Path or URI to config file")
    .default("./grh-config.json")

  val dryRun by parser.flagging("-n", "--dry-run", help = "Print commands to invoke but don't actually make changes")

  val processor by parser.storing("-p", "--processor",
    help = "Processor to use. 'cli' = git command line, 'api' = Github API") { this }
    .addValidator {
      val validProcessors = listOf("cli", "api")
      if (!validProcessors.contains(value))
        throw InvalidArgumentException("PROCESSOR must be one of: ${validProcessors.joinToString(", ")}")
    }

  val repo by parser.storing("-r", "--repo",
    help = "Github repo owner and name separated by slash: 'owner/name'. Only valid for PROCESSOR=api")
    .default(null)
    .addValidator {
      if (value != null && !"^\\S+/\\S+$".toRegex().containsMatchIn(value.toString()))
        throw InvalidArgumentException("REPO must be a Github repo owner and name separated by slash: 'owner/name'")
    }

  val skipPush by parser.flagging("-s", "--skip-push",
    help = "Apply revised tags locally, but do not automatically push. Only valid for PROCESSOR=cli")

  val remote by parser.storing("-o", "--remote",
    help = "Name of the remote repository in the local git config. Only valid for PROCESSOR=cli")
    .default("origin")

  val v3Endpoint by parser.storing("-3", "--v-3-endpoint",
    help = "Override for Github v3 (RESTful) API endpoint. Only valid for PROCESSOR=api")
    .default(null)

  val v4Endpoint by parser.storing("-4", "--v-4-endpoint",
    help = "Override for Github v4 (GraphQL) API endpoint. Only valid for PROCESSOR=api")
    .default(null)
}
