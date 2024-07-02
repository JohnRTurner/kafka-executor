import { Configuration } from './api'; // Adjust the path based on your project structure

const apiConfig = new Configuration({
    basePath: window.location.protocol + "//" + window.location.hostname + (
            window.location.port ? ':' +
                (window.location.port === '5173' ? '8080/api':window.location.port + '/api')
                : '/api'),
    //accessToken: 'your-access-token', // Add your token here if needed
});

export default apiConfig;