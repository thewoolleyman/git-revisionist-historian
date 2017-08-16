package test.grh.config

import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.git.Invocation
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.plan.renderPlan
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class renderPlanTest : StringSpec() {
  init {
    val plan = Plan(
      steps = listOf(
        Invocation(command = Command.TAG, sha = "c1", tag = "feature1-start"),
        Invocation(command = Command.TAG, sha = "c2", tag = "feature1-finish"),
        Invocation(command = Command.TAG, sha = "c2", tag = "feature2-start"),
        Invocation(command = Command.TAG, sha = "c4", tag = "feature2-finish"),
        Invocation(command = Command.PUSH_TAGS)
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
