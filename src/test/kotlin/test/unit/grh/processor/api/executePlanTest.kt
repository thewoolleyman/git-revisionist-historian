package test.unit.grh.processor.api

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.thewoolleyweb.grh.Output
import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.plan.Action
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.processor.api.Repo
import com.thewoolleyweb.grh.processor.api.executePlan
import com.thewoolleyweb.grh.processor.api.gateway.CreateRef
import com.thewoolleyweb.grh.processor.api.gateway.ReplaceRef
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class executePlanTest : StringSpec() {
  init {
    val plan = Plan(
      actions = listOf(
        Action(command = Command.TAG, sha = "c1", refname = "feature1-start")
        ,
        Action(command = Command.TAG, sha = "c2", refname = "feature1-finish")
      )
    )

    val endpoint = "http://v3endpoint"
    val repo = Repo("owner", "name")
    val mockCreateRef: CreateRef
      = { _, _, ref: String, sha: String ->
      Triple(Request(), Response(), Result.of("Created $ref for $sha\n"))
    }

    val mockReplaceRef: ReplaceRef
      = { _, _, ref: String, sha: String ->
      Triple(Request(), Response(), Result.of("Replaced $ref for $sha\n"))
    }

    val capturedOutput = arrayListOf<String>()
    val mockOutput: Output = { line ->
      capturedOutput.add(line)
      line
    }

    // TODO: Test error cases
    "works with normal run" {
      val dryRun = false
      executePlan(plan, dryRun, endpoint, repo, mockCreateRef, mockReplaceRef, mockOutput) shouldBe Unit
      capturedOutput shouldBe listOf(
        "> Creating or replacing tag 'feature1-start' for SHA 'c1'...",
        "  Successfully created.\n",
        "> Creating or replacing tag 'feature1-finish' for SHA 'c2'...",
        "  Successfully created.\n"
      )
    }

//    "works with --dry-run" {
//      val stubArgs = Args(ArgParser(arrayOf("--dry-run", "-p", "cli")))
//      executePlan(plan, stubArgs, mockRunner, mockOutput) shouldBe Unit
//      capturedOutput shouldBe listOf(
//        "\$ git tag --force feature1-start c1\n",
//        "\$ git tag --force feature1-finish c2\n",
//        "\$ git push --force --tags\n"
//      )
//    }
  }
}
