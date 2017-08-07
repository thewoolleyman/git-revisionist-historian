package test.grh

import com.thewoolleyweb.grh.Args
import com.xenomachina.argparser.ArgParser
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ArgsTest : StringSpec() {
    init {
        val arguments = arrayOf("-c", "./config.json")
        val args = Args(ArgParser(arguments))
        "can set config file" {
            args.configFile shouldBe "./config.json"
        }

    }
}