package shared.config

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParameterRequest

class SsmParameterStore(
  private val client: SsmClient
) {

  fun get(name: String): String =
    client.getParameter(
      GetParameterRequest.builder()
        .name(name)
        .withDecryption(true)
        .build()
    ).parameter().value()

  companion object {
    fun default(region: Region): SsmParameterStore =
      SsmParameterStore(SsmClient.builder().region(region).build())
  }
}
