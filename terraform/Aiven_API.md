# Generating an API Key for Aiven

## Steps

1. **Log in to Aiven Console**
    - Go to the [Aiven Console](https://console.aiven.io/) and log in with your credentials.

2. **Navigate to Account Settings**
    - Once logged in, click on your profile icon in the top right corner.
    - Select "Profile" from the dropdown menu to open your account settings.

3. **Access API Keys**
    - In the Profile settings page, find the "API Keys" section.
    - Click on "Manage API Keys" to proceed.

4. **Create a New API Key**
    - Click on the "Create API Key" button.
    - Provide a name for your API key to help you identify its purpose later.

5. **Set Permissions (Optional)**
   - Depending on your needs, you can set specific permissions for the API key. By default, it will have full access.
     Adjust permissions if you want to limit the scope of this key.

6. **Generate the API Key**
    - Click on "Create" to generate the API key.

7. **Copy and Save the API Key**
    - Once generated, copy the API key immediately as it will not be displayed again.
    - Save it securely in a password manager or a secure location.

8. **Use the API Key**
   - To use the API key, include it in your API requests to Aiven services. For example, you can use it in a curl
     command as follows:
      ```sh
      curl -H "Authorization: Bearer <your-api-key>" https://api.aiven.io/v1/project
      ```
9. **Use the API Key in terraform.tfvars for the parameter aiven_api_token.**
