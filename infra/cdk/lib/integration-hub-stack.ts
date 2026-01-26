import * as cdk from "aws-cdk-lib";
import { Construct } from "constructs";
import * as dynamodb from "aws-cdk-lib/aws-dynamodb";
import * as sqs from "aws-cdk-lib/aws-sqs";

export class IntegrationHubStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const table = new dynamodb.Table(this, "IntegrationRequestsTable", {
      tableName: "integration-requests",
      partitionKey: { name: "pk", type: dynamodb.AttributeType.STRING },
      sortKey: { name: "sk", type: dynamodb.AttributeType.STRING },
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
      removalPolicy: cdk.RemovalPolicy.DESTROY, // dev/local only
    });

    const queue = new sqs.Queue(this, "IntegrationRequestsQueue", {
      queueName: "integration-requests",
      visibilityTimeout: cdk.Duration.seconds(60),
    });

    new cdk.CfnOutput(this, "IntegrationRequestsTableName", {
      value: table.tableName,
    });

    new cdk.CfnOutput(this, "IntegrationRequestsQueueUrl", {
      value: queue.queueUrl,
    });

    new cdk.CfnOutput(this, "IntegrationRequestsQueueArn", {
      value: queue.queueArn,
    });
  }
}
