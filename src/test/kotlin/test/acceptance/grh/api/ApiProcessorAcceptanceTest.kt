package test.acceptance.grh.api

import com.thewoolleyweb.grh.processhelper.run
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import test.acceptance.grh.jarCmd

// NOTE: Requires GITHUB_PERSONAL_ACCESS_TOKEN to be set - same one from ci/credentials.yml
class ApiProcessorAcceptanceTest : StringSpec() {
  init {
    val configURL =
      "https://raw.githubusercontent.com/thewoolleyman/git-revisionist-historian-test-repo/solution/grh-config.json"

    val cmd = jarCmd(
      "--config $configURL " +
        "--repo thewoolleyman/git-revisionist-historian-test-repo " +
        "--processor api"
    )
    val output = run(cmd)

    // TODO: DRY up with a custom matcher or helper
    val outputString = output.joinToString("\n")
    "> Creating or replacing tag 'feature3-finish' for SHA 'ba05591c3e0595f005ae77e6b0ac0722b1588c90'...  Successfully (created|replaced)."
      .toRegex().containsMatchIn(outputString) shouldBe true
    "> Creating or replacing tag 'feature3-start' for SHA 'fa4f5d259b90fca8798fa31080cf6918f07353b5'...  Successfully (created|replaced)."
      .toRegex().containsMatchIn(outputString) shouldBe true
    "> Creating or replacing tag 'feature2-finish' for SHA 'fa4f5d259b90fca8798fa31080cf6918f07353b5'...  Successfully (created|replaced)."
      .toRegex().containsMatchIn(outputString) shouldBe true
    "> Creating or replacing tag 'feature2-start' for SHA 'e09016485ec4b2ec9b36d36210b2c09df0f8bef1'...  Successfully (created|replaced)."
      .toRegex().containsMatchIn(outputString) shouldBe true
    "> Creating or replacing tag 'feature1-finish' for SHA 'e09016485ec4b2ec9b36d36210b2c09df0f8bef1'...  Successfully (created|replaced)."
      .toRegex().containsMatchIn(outputString) shouldBe true
    "> Creating or replacing tag 'feature1-start' for SHA '891efbddf4bcbb5db2369b502771c11569ff0881'...  Successfully (created|replaced)."
      .toRegex().containsMatchIn(outputString) shouldBe true
    "Finished run.".toRegex().containsMatchIn(outputString) shouldBe true
  }
}
