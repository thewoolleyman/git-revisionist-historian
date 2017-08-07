package com.thewoolleyweb.grh

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Args(parser: ArgParser) {
    val configFile by parser.storing("-c", "--config", help = "Path to config file")
            .default("./grh-config.json")
}