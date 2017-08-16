package com.thewoolleyweb.grh

import com.thewoolleyweb.grh.processor.process
import com.xenomachina.argparser.ArgParser

fun main(arguments: Array<String>) {
  val args: Args = Args(ArgParser(arguments))
  process(args)
}

