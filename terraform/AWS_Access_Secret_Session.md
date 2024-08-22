# AWS temporary access.

Get the AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY and AWS_SESSION_TOKEN

1. Copy the below code to your local Linux or Mac.

```bash
#!/bin/zsh
if [ $# -ne 1 ]; then
	echo "Must prefix with a dot then pass the token code"
	echo "  . $0 authtokennumber"
else
  # Expanded from https://aiven.slab.com/posts/using-the-aiven-sa-demo-aws-account-ltbo8pha
  MFA_CODE=$1
  unset AWS_ACCESS_KEY_ID
  unset AWS_SECRET_ACCESS_KEY
  unset AWS_SESSION_TOKEN
  export AWS_ACCOUNT_ID=fill_this_in_once
  export AWS_ROLE_ARN=fill_this_in_once
  export 
  aws sts get-caller-identity
  export $(printf "AWS_ACCESS_KEY_ID=%s AWS_SECRET_ACCESS_KEY=%s AWS_SESSION_TOKEN=%s" \
  $(aws sts assume-role \
    --role-arn arn:aws:iam::${AWS_ROLE_ARN}:role/AivenDeveloperAccess \
    --role-session-name MyTemporaryAccess \
    --query "Credentials.[AccessKeyId,SecretAccessKey,SessionToken]" \
    --output text --serial-number arn:aws:iam::${AWS_ACCOUNT_ID}:mfa/${USER} --token-code ${MFA_CODE}))
  aws sts get-caller-identity
  echo "AWS_ACCESS_KEY_ID=\"$AWS_ACCESS_KEY_ID\""
  echo "AWS_SECRET_ACCESS_KEY=\"$AWS_SECRET_ACCESS_KEY\""
  echo "AWS_SESSION_TOKEN=\"$AWS_SESSION_TOKEN\""|sed "s/'//g"
fi
```

2. Update AWS_ACCOUNT_ID and AWS_ROLE_ARN to the required values of your access.
3. Set the scripts permissions to 700.
4. Call the script, passing your authenticator token to get your temporary access information.
5. Update terraform.tfvars with this information

**Note: The tokens will expire, but the terraform repository will continue to track your servers, so that you resume
access upon updating the tokens.** 