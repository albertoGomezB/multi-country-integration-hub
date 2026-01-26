package ingest.config

import ingest.adapters.SqsQueryRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shared.repository.QueryRepository
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class QueueConfig(
  private val ssm: SsmParameterStore,
  @Value("\${app.env:dev}") private val env: String
  ) {

  @Bean
  fun queryRepository(sqsClient: SqsClient): QueryRepository {
    val queueUrl = ssm.get("/integration-hub/$env/sqs/requests/url")
    return SqsQueryRepository(sqsClient, queueUrl)
  }
}
