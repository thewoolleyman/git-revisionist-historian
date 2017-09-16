package test.unit.grh.processor.cli

import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log
import com.thewoolleyweb.grh.processor.cli.readLog
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class readLogTest : StringSpec() {
  init {
    val log = Log(
      commits = listOf(
        Commit(
          sha = "c02af2b43bd7cbcbaed987197ee92bc55c54bada",
          message = "Initial commit"
        ),
        Commit(
          sha = "0db40c2c412984411586641d7c80c5fdd1fcb15c",
          message = "this is the first feature's implementation"
        ),
        Commit(
          sha = "913bbf48fb0cd75e776d90e093b9c3a1522767d7",
          message = "this is the second feature's implementation"
        ),
        Commit(
          sha = "cbd47635afa0c0974b3ce364d2b67dd792317b10",
          message = "this is the third feature's implementation"
        ),
        Commit(
          sha = "f027007d3a55fe5b41332b4635e3b404f9ae4a17",
          message = "this is an in-progress feature"
        )
      )
    )

    val logLines = listOf(
      "f027007d3a55fe5b41332b4635e3b404f9ae4a17 this is an in-progress feature",
      "cbd47635afa0c0974b3ce364d2b67dd792317b10 this is the third feature's implementation",
      "913bbf48fb0cd75e776d90e093b9c3a1522767d7 this is the second feature's implementation",
      "0db40c2c412984411586641d7c80c5fdd1fcb15c this is the first feature's implementation",
      "c02af2b43bd7cbcbaed987197ee92bc55c54bada Initial commit"

    )
    val mockRun = { _: String -> logLines }

    "works" {
      readLog("origin", "solution", mockRun) shouldBe log
    }
  }
}
