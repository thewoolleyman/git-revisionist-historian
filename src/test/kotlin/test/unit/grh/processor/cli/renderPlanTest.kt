package test.unit.grh.config

import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.plan.Action
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.processor.cli.renderPlan
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class renderPlanTest : StringSpec() {
  init {
    val plan = Plan(
      actions = listOf(
        Action(command = Command.TAG, sha = "c1", refname = "feature1-start"),
        Action(command = Command.TAG, sha = "c2", refname = "feature1-finish"),
        Action(command = Command.TAG, sha = "c2", refname = "feature2-start"),
        Action(command = Command.TAG, sha = "c4", refname = "feature2-finish")
      )
    )

    "works" {
      renderPlan(plan, false) shouldBe listOf(
        "git tag --force feature1-start c1",
        "git tag --force feature1-finish c2",
        "git tag --force feature2-start c2",
        "git tag --force feature2-finish c4",
        "git push --force --tags"
      )
    }

    "works with skipPush true" {
      renderPlan(plan, true) shouldBe listOf(
        "git tag --force feature1-start c1",
        "git tag --force feature1-finish c2",
        "git tag --force feature2-start c2",
        "git tag --force feature2-finish c4"
      )
    }
  }
}
