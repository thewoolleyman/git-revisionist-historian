package test.unit.grh.processor.cli

import com.thewoolleyweb.grh.Output
import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log
import com.thewoolleyweb.grh.processor.cli.readLog
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import io.kotlintest.matchers.haveSubstring
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec

class readLogTest : StringSpec() {
  init {
    val log = Log(
      commits = listOf(
        Commit(
          sha = "f027007d3a55fe5b41332b4635e3b404f9ae4a17",
          message = "this is an in-progress feature"
        ),
        Commit(
          sha = "cbd47635afa0c0974b3ce364d2b67dd792317b10",
          message = "this is the third feature's implementation"
        ),
        Commit(
          sha = "913bbf48fb0cd75e776d90e093b9c3a1522767d7",
          message = "this is the second feature's implementation"
        ),
        Commit(
          sha = "0db40c2c412984411586641d7c80c5fdd1fcb15c",
          message = "this is the first feature's implementation"
        ),
        Commit(
          sha = "c02af2b43bd7cbcbaed987197ee92bc55c54bada",
          message = "Initial commit"
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

    val capturedOutput = arrayListOf<String>()
    val mockOutput: Output = { line ->
      capturedOutput.add(line)

      line
    }

    val nonFastForwardError = "some `git fetch` error that contains the string 'non-fast-forward'"
    val stubReturnValues = ArrayList<List<String>>(2)
    stubReturnValues.addAll(
      listOf(
        listOf(nonFastForwardError),
        logLines
      )
    )
    val mockRun = { _: String -> logLines }
    val mockRunWithFetchError = { _: String ->
      val returnValue = stubReturnValues.removeAt(0)
      if (returnValue[0] == nonFastForwardError) {
        throw RuntimeException(returnValue[0])
      }
      returnValue
    }

    "works when fetch is up to date and has no output" {
      val stubArgs = Args(ArgParser(arrayOf("--remote", "origin", "-p", "cli")))

      readLog(stubArgs, "solution", mockRun, mockOutput) shouldBe log
      capturedOutput shouldBe arrayListOf<String>()
    }

    "gives a warning if fetch is non-fast-forward but --dry-run is specified" {
      val stubArgs = Args(ArgParser(arrayOf("--dry-run", "--remote", "origin", "-p", "cli")))

      readLog(stubArgs, "solution", mockRunWithFetchError, mockOutput) shouldBe log
      capturedOutput[0] should haveSubstring("WARNING: Branch 'solution' has diverged")
    }

    "gives a warning if fetch is non-fast-forward but --skip-push is specified" {
      val stubArgs = Args(ArgParser(arrayOf("--skip-push", "--remote", "origin", "-p", "cli")))

      readLog(stubArgs, "solution", mockRunWithFetchError, mockOutput) shouldBe log
      capturedOutput[0] should haveSubstring("WARNING: Branch 'solution' has diverged")
    }

    "gives a warning if fetch is non-fast-forward and neither --dry-run nor --skip-push are specified" {
      val stubArgs = Args(ArgParser(arrayOf("--remote", "origin", "-p", "cli")))

      shouldThrow<SystemExitException> {
        readLog(stubArgs, "solution", mockRunWithFetchError, mockOutput) shouldBe log
      }

      capturedOutput[0] should haveSubstring("ERROR: Branch 'solution' has diverged")
    }
  }
}
