package ingest.config

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParameterRequest

class SsmParameterStore(
  private val client: SsmClient) {

  /**
   * Get the value of a parameter from SSM Parameter Store
   * @param name The name of the parameter
   * @return The value of the parameter
   * @throws software.amazon.awssdk.services.ssm.model.ParameterNotFoundException if the parameter does not exist
   */
  fun get(name: String): String =
      client.getParameter(
        GetParameterRequest.builder()
          .name(name)
          .withDecryption(true)
          .build()
      ).parameter().value()

  /**
   * Companion object to create a default SsmParameterStore
   * @param region The AWS region to use
   * @return A SsmParameterStore instance
   */
  companion object {
    fun default(region: Region): SsmParameterStore =
      SsmParameterStore(SsmClient.builder().region(region).build())
  }
}
