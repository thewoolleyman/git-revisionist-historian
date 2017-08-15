package test.grh

import com.thewoolleyweb.grh.cmd.run
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec

class CmdTest : StringSpec() {
  init {
    "works" {
      val out = run("echo 'foo'")
      out shouldBe arrayListOf("'foo'")
    }

    "raises exception on nonzero return code" {
      shouldThrow<RuntimeException> {
        run("ls /does_not_exist")
      }
    }
  }
}
