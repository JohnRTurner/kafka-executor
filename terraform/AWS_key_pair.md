# Generating a Key Pair in AWS for EC2 Instances used for SSH

## Steps

1. **Sign in to AWS Management Console**
    - Go to the [AWS Management Console](https://aws.amazon.com/console/) and sign in to your account.

2. **Navigate to EC2 Dashboard**
    - In the AWS Management Console, find and click on "EC2" under the "Compute" section to open the EC2 Dashboard.

3. **Access Key Pairs**
    - In the left-hand sidebar, under "Network & Security", click on "Key Pairs".

4. **Create a New Key Pair**
    - Click on the "Create key pair" button at the top of the Key Pairs page.

5. **Configure the Key Pair**
    - **Name**: Enter a name for your key pair. This should be something descriptive to help you identify it.
    - **Key pair type**: Choose "RSA" or "ED25519" (RSA is more commonly used).
    - **Private key file format**: Choose the format in which you want to save the private key file.
        - **.pem** (for OpenSSH, Linux, and macOS)
        - **.ppk** (for PuTTY, Windows)

6. **Create Key Pair**
    - Click on the "Create key pair" button. This will automatically download the private key file to your computer.

7. **Save the Private Key File Securely**
    - Save the downloaded private key file (`.pem` or `.ppk`) in a secure location on your computer. Make sure it is not
      accessible to unauthorized users.

8. **Set Permissions for the Private Key File (Linux/macOS)**
    - If you are using a Unix-based system, set the correct permissions for the private key file to ensure it is not
      publicly viewable. Use the following command in your terminal:
      ```sh
      chmod 400 /path/to/your-key-pair.pem
      ```

9. **Use the Key Pair to Access EC2 Instances**
    - When launching a new EC2 instance, select the key pair you just created in the "Key pair (login)" section.
    - To connect to your EC2 instance, use the following command (for SSH, replace `ec2-user` with the appropriate user
      for your AMI):
      ```sh
      ssh -i /path/to/your-key-pair.pem ec2-user@your-ec2-instance-public-dns
      ```
10. **Used the key in terraform.tfvars for the dg_key_pair_name**

By following these steps, you can generate a key pair in AWS for secure access to your EC2 instances.
