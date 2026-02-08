package ingest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shared.config.SsmParameterStore
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class AppInfraConfig {

  @Bean
  fun awsRegion(): Region = Region.EU_WEST_1

  @Bean
  fun ssmParameterStore(region: Region): SsmParameterStore =
    SsmParameterStore.default(region)

  @Bean
  fun sqsClient(region: Region): SqsClient =
    SqsClient.builder().region(region).build()

  @Bean
  fun dynamoDbClient(region: Region): DynamoDbClient =
    DynamoDbClient.builder().region(region).build()

}
