# React + TypeScript + Vite
This is the front-end web application.  A menu system which can make calls to the backend JAVA system.

src/App.tsx is the main page, which contains the menu.  
The src/batchList is the code for running the batch producers and consumers.

## OpenAPI
The following will generate the code to access the backend via the OpenAPI interface.

Note: The generator will not work unless the backend is serving the api-docs, optionally the file can be saved and used in place of the URL.

```shell
npm install @openapitools/openapi-generator-cli -g
openapi-generator-cli generate -i http://127.0.0.1:8080/api/v3/api-docs -g typescript-axios -o ./src/api
```

After creating openapitools.json can just run

```shell
openapi-generator-cli generate
```

