# Creating a Fine-Grained Access PAT with Least Permissions for `git clone` of a Repository

## Steps

1. **Sign in to GitHub**
    - Go to GitHub and sign in to your account.

2. **Navigate to Settings**
    - Click on your profile picture in the top right corner.
    - Select "Settings" from the dropdown menu.

3. **Access Developer Settings**
    - On the left sidebar, scroll down and click on "Developer settings".

4. **Personal Access Tokens**
    - Under "Developer settings", click on "Personal access tokens".

5. **Generate a New Token**
    - Click on "Generate new token".

6. **Select Fine-Grained Access Token**
    - Choose the option to create a fine-grained personal access token.

7. **Token Details**
    - **Name**: Give your token a descriptive name.
    - **Expiration**: Set an expiration date for the token. Choose a short duration if you only need temporary access.
    - **Resource Owner**: The owner of the repository you with to give access to.

8. **Select Repository Access**
    - **Repository Access**: Choose "Only select repositories" and specify the repository for which you need clone access.

9. **Set Permissions**
    - **Repository permissions**: Set the following permissions:
        - **Contents**: Set this to "Read-only". This allows you to clone the repository without additional write access.

10. **Generate Token**
    - Once you have set the appropriate permissions, click on the "Generate token" button.

11. **Copy the Token**
    - Copy the generated token immediately as it will not be displayed again. Store it securely.

12. **Use the Token**
    - You perform a `git clone`, use the token in the command. For example:
      ```sh
      git clone https://<your-token>@github.com/aiven/kafka-executor.git
      ```
    - Update the git command in the terraform.tfvars file.  

By following these steps, you create a PAT with the least permissions required to clone a specific repository, ensuring fine-grained access control.
