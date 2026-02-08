package ingest.config

import ingest.adapters.SqsQueryRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shared.config.SsmParameterStore
import shared.repository.QueryRepository
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class QueueConfig(
  private val ssm: SsmParameterStore,
  @Value("\${app.env:dev}") private val env: String
  ) {

  /**
   * Configure the QueryRepository to use SQS for enqueuing request IDs. The SQS queue URL is retrieved from SSM Parameter Store.
   * This allows the application to enqueue request IDs to SQS, which can then be processed by a separate consumer service.
   * The queue URL is expected to be stored in SSM under the path "/integration-hub/{env}/sqs/requests/url", where {env} is the current environment (e.g., dev, staging, prod).
   */
  @Bean
  fun queryRepository(sqsClient: SqsClient): QueryRepository {
    val queueUrl = ssm.get("/integration-hub/$env/sqs/requests/url")
    return SqsQueryRepository(sqsClient, queueUrl)
  }
}
