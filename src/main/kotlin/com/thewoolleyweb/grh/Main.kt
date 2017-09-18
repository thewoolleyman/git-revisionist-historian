package com.thewoolleyweb.grh

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import com.thewoolleyweb.grh.args.processArgs
import com.thewoolleyweb.grh.processor.process
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.ShowHelpException
import com.xenomachina.argparser.SystemExitException


fun main(arguments: Array<String>) {
  try {
    val apiToken = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN")
    val args = processArgs(arguments, apiToken)
    process(args)
  } catch (e: ShowHelpException) {
    e.printAndExit()
  } catch (e: InvalidArgumentException) {
    print(Kolor.foreground("INVALID ARGUMENT ERROR: ", Color.RED))
    e.printAndExit()
  } catch (e: SystemExitException) {
    print(Kolor.foreground("ERROR: ", Color.RED))
    e.printAndExit()
  }
}

