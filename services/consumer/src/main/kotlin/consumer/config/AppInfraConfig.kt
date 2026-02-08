package consumer.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shared.config.SsmParameterStore
import shared.repository.DynamoDbRequestRepository
import shared.repository.RequestRepository
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient


@Configuration
class AppInfraConfig(
  @Value("\${app.env:dev}") private val env: String
) {

  @Bean
  fun awsRegion(): Region = Region.EU_WEST_1

  @Bean
  fun sqsClient(region: Region): SqsClient =
    SqsClient.builder().region(region).build()

  @Bean
  fun dynamoDbClient(region: Region): DynamoDbClient =
    DynamoDbClient.builder().region(region).build()

  @Bean
  fun ssmParameterStore(region: Region): SsmParameterStore =
    SsmParameterStore.default(region)

  @Bean
  fun requestRepository(dynamoDbClient: DynamoDbClient, ssm: SsmParameterStore): RequestRepository {
    val tableName = ssm.get("/integration-hub/$env/ddb/requests/tableName")
    return DynamoDbRequestRepository(dynamoDbClient, tableName)
  }
}
