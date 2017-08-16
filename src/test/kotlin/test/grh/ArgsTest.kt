package test.grh

import com.thewoolleyweb.grh.Args
import com.xenomachina.argparser.ArgParser
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ArgsTest : StringSpec() {
  init {
    "can set --config/-c with default" {
      val argsLong = Args(ArgParser(arrayOf("--config", "./config.json")))
      argsLong.configFile shouldBe "./config.json"
      val argsShort = Args(ArgParser(arrayOf("-c", "./config.json")))
      argsShort.configFile shouldBe "./config.json"
      val argsDefault = Args(ArgParser(arrayOf()))
      argsDefault.configFile shouldBe "./grh-config.json"
    }

    "can set --dry-run/-n" {
      val argsLong = Args(ArgParser(arrayOf("--dry-run")))
      argsLong.dryRun shouldBe true
      val argsShort = Args(ArgParser(arrayOf("-n")))
      argsShort.dryRun shouldBe true
      val argsDefault = Args(ArgParser(arrayOf()))
      argsDefault.dryRun shouldBe false
    }

    "can set --skip-push/-s" {
      val argsLong = Args(ArgParser(arrayOf("--skip-push")))
      argsLong.skipPush shouldBe true
      val argsShort = Args(ArgParser(arrayOf("-s")))
      argsShort.skipPush shouldBe true
      val argsDefault = Args(ArgParser(arrayOf()))
      argsDefault.skipPush shouldBe false
    }
  }
}
