package com.thewoolleyweb.grh

import com.thewoolleyweb.grh.processor.process
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.ShowHelpException


fun main(arguments: Array<String>) {
  process(processArgs(arguments))
}

private fun processArgs(arguments: Array<String>): Args {
  val parser = ArgParser(arguments)
  val args: Args = Args(parser)

  // force help to be handled
  try {
    parser.force()
  } catch (e: ShowHelpException) {
    e.printAndExit("git-revisionist-historian")
  }
  return args
}

