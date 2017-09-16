package test.unit.grh.processor.cli

import com.thewoolleyweb.grh.Output
import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.plan.Action
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.processor.cli.executePlan
import com.xenomachina.argparser.ArgParser
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

    val mockRunner = { commandLine: String -> listOf("output of '$commandLine'\n") }

    val capturedOutput = arrayListOf<String>()
    val mockOutput: Output = { line ->
      capturedOutput.add(line)

      line
    }

    "works with normal run" {
      val stubArgs = Args(ArgParser(arrayOf("-p", "cli")))
      executePlan(plan, stubArgs, mockRunner, mockOutput) shouldBe Unit
      capturedOutput shouldBe listOf(
        "\$ git tag --force feature1-start c1\n",
        "output of 'git tag --force feature1-start c1'\n",
        "\$ git tag --force feature1-finish c2\n",
        "output of 'git tag --force feature1-finish c2'\n",
        "\$ git push --force --tags\n",
        "output of 'git push --force --tags'\n"
      )
    }

    "works with --dry-run" {
      val stubArgs = Args(ArgParser(arrayOf("--dry-run", "-p", "cli")))
      executePlan(plan, stubArgs, mockRunner, mockOutput) shouldBe Unit
      capturedOutput shouldBe listOf(
        "\$ git tag --force feature1-start c1\n",
        "\$ git tag --force feature1-finish c2\n",
        "\$ git push --force --tags\n"
      )
    }

    "works with --skip-push" {
      val stubArgs = Args(ArgParser(arrayOf("--dry-run", "--skip-push", "-p", "cli")))
      executePlan(plan, stubArgs, mockRunner, mockOutput) shouldBe Unit
      capturedOutput shouldBe listOf(
        "\$ git tag --force feature1-start c1\n",
        "\$ git tag --force feature1-finish c2\n",
        "(skipping push)\n"
      )
    }
  }
}
