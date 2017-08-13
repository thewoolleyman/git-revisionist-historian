package test.grh

import com.thewoolleyweb.grh.cmd.run
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class CmdTest : StringSpec() {
    init {
        val out = run("echo 'foo'")
        "works" {
            out shouldBe arrayListOf("'foo'")
        }

    }
}