# AWS::RolesAnywhere::TrustAnchor Source

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#sourcetype" title="SourceType">SourceType</a>" : <i>String</i>,
    "<a href="#sourcedata" title="SourceData">SourceData</a>" : <i><a href="sourcedata.md">SourceData</a></i>
}
</pre>

### YAML

<pre>
<a href="#sourcetype" title="SourceType">SourceType</a>: <i>String</i>
<a href="#sourcedata" title="SourceData">SourceData</a>: <i><a href="sourcedata.md">SourceData</a></i>
</pre>

## Properties

#### SourceType

_Required_: No

_Type_: String

_Allowed Values_: <code>AWS_ACM_PCA</code> | <code>CERTIFICATE_BUNDLE</code> | <code>SELF_SIGNED_REPOSITORY</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceData

_Required_: No

_Type_: <a href="sourcedata.md">SourceData</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

