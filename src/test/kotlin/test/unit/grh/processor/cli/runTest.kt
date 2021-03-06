package test.unit.grh.processor.cli

import com.thewoolleyweb.grh.processhelper.run
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec

class runTest : StringSpec() {
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
