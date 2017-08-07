package com.thewoolleyweb.grh

import com.xenomachina.argparser.ArgParser

fun main(args: Array<String>) {
    val args: Args = Args(ArgParser(args))
    System.out.println("""Config File: ${args.configFile}""")
}

