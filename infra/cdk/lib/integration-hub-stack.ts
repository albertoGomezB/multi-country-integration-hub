import * as cdk from "aws-cdk-lib";
import { Construct } from "constructs";
import * as dynamodb from "aws-cdk-lib/aws-dynamodb";
import * as sqs from "aws-cdk-lib/aws-sqs";
import * as ssm from "aws-cdk-lib/aws-ssm";
import * as iam from 'aws-cdk-lib/aws-iam';

export class IntegrationHubStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const envName = this.node.tryGetContext("env") ?? "dev";
    const prefix = `/integration-hub/${envName}`;
    const ssmParamArnPrefix = `arn:aws:ssm:${this.region}:${this.account}:parameter/integration-hub/${envName}/*`;

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

    new ssm.StringParameter(this, 'RequestsQueueUrlParam', {
      parameterName: `${prefix}/sqs/requests/url`,
      stringValue: queue.queueUrl,
    });

    new ssm.StringParameter(this, 'RequestsQueueArnParam', {
      parameterName: `${prefix}/sqs/requests/arn`,
      stringValue: queue.queueArn,
    });

    new ssm.StringParameter(this, 'RequestsTableNameParam', {
      parameterName: `${prefix}/ddb/requests/tableName`,
      stringValue: table.tableName,
    });

   const ssmReadPolicy = new iam.PolicyStatement({
         actions: [
           "ssm:GetParameter",
           "ssm:GetParameters",
           "ssm:GetParametersByPath",
         ],
         resources: [ssmParamArnPrefix],
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
