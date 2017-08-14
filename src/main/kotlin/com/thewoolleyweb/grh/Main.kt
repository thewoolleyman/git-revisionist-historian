package com.thewoolleyweb.grh

import com.xenomachina.argparser.ArgParser

fun main(arguments: Array<String>) {
  val args: Args = Args(ArgParser(arguments))
  System.out.println("""Config File: ${args.configFile}""")
}

