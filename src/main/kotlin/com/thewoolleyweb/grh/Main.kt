package com.thewoolleyweb.grh

import com.thewoolleyweb.grh.args.processArgs
import com.thewoolleyweb.grh.processor.process
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.ShowHelpException


fun main(arguments: Array<String>) {
  val args = try {
    val apiToken = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN")
    processArgs(arguments, apiToken)
  } catch (e: ShowHelpException) {
    e.printAndExit()
  } catch (e: InvalidArgumentException) {
    print("ERROR: ")
    e.printAndExit()
  }

  process(args)
}

