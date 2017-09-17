package com.thewoolleyweb.grh.args

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException

fun processArgs(arguments: Array<String>, apiToken: String? = null): Args {
  val parser = ArgParser(arguments)
  val args = Args(parser)

  // force help to be handled
  parser.force()

  if (args.processor != "cli") {
    validateCliOnlyArgs(args)
  }

  if (args.processor != "api") {
    validateApiOnlyArgs(args)
  }

  if (args.processor == "api") {
    validateRepo(args)
    validateApiToken(apiToken)
  }

  return args
}

private fun validateCliOnlyArgs(args: Args) {
  if (args.skipPush)
    throw InvalidArgumentException("--skip-push option is only valid when --processor=cli")
}

private fun validateApiOnlyArgs(args: Args) {
  if (args.repo != null)
    throw InvalidArgumentException("--repo option is only valid when --processor=api")
  if (args.v3Endpoint != null)
    throw InvalidArgumentException("--v-3-endpoint option is only valid when --processor=api")
  if (args.v4Endpoint != null)
    throw InvalidArgumentException("--v-4-endpoint option is only valid when --processor=api")
}

private fun validateRepo(args: Args) {
  if (args.repo.isNullOrBlank()) {
    throw InvalidArgumentException("REPO is required when --processor=api")
  }
}

private fun validateApiToken(apiToken: String?) {
  if (apiToken.isNullOrBlank()) {
    throw InvalidArgumentException("GITHUB_PERSONAL_ACCESS_TOKEN env var is required when --processor=api")
  }
}
