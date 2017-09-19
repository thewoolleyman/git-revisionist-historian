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
        "https://raw.githubusercontent.com/thewoolleyman/git-revisionist-historian-test-repo/solution/grh-config.json"

      val cmd = jarCmd(
        "--config $configURL " +
          "--repo thewoolleyman/git-revisionist-historian-test-repo " +
          "--processor api"
      )
      val output = run(cmd)

      val outputString = output.joinToString("\n")
      outputString should include("> Creating or replacing tag 'feature1-start' for SHA '94a5933108e092ce84053435719aaa5e1356bfad'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature1-finish' for SHA 'a2eccc4625943b9a523b1894871638276f908209'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature2-start' for SHA 'a2eccc4625943b9a523b1894871638276f908209'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature2-finish' for SHA '49fe2b8bda48d687143ef69ec1d7c85067499dfd'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature3-start' for SHA '49fe2b8bda48d687143ef69ec1d7c85067499dfd'...  Successfully replaced.")
      outputString should include("> Creating or replacing tag 'feature3-finish' for SHA 'df6492a6b22271feea618f9c723427b7df0830df'...  Successfully replaced.")
      outputString should include("Finished run.")
    }
  }
}
