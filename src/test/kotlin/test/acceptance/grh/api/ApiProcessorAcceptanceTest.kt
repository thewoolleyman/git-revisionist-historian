package test.acceptance.grh.api

import com.thewoolleyweb.grh.processhelper.run
import io.kotlintest.matchers.include
import io.kotlintest.matchers.should
import io.kotlintest.specs.StringSpec
import test.acceptance.grh.jarCmd

// NOTE: Requires GITHUB_PERSONAL_ACCESS_TOKEN to be set - same one from ci/credentials.yml
class ApiProcessorAcceptanceTest : StringSpec() {
  init {
    "works" {
      val configURL =
        "https://raw.githubusercontent.com/thewoolleyman/git-revisionist-historian-test-repo/master/grh-config.json"

      val cmd = jarCmd(
        "--config $configURL " +
          "--repo thewoolleyman/git-revisionist-historian-test-repo " +
          "--processor api"
      )
      val output = run(cmd)

      val outputString = output.joinToString("\n")
      outputString should include("> Creating or replacing tag 'feature1-start' for SHA '040c17a02a49aeb38509b09863d1b4b25c11fc46'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature1-finish' for SHA '5b2dde544098740fa18833ac0dca0e4276932b55'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature2-start' for SHA '5b2dde544098740fa18833ac0dca0e4276932b55'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature2-finish' for SHA 'b49a8d6c545950e4e1a302693cc1fa5a13c636c3'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature3-start' for SHA 'b49a8d6c545950e4e1a302693cc1fa5a13c636c3'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature3-finish' for SHA '2f1517e51473aee3be2d2281e79b3d121700dcea'...  Successfully replaced.")
      outputString should include("Finished run.")
    }
  }
}
