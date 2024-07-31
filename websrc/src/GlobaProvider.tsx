import React, {ReactNode, useState} from 'react';
import GlobalContext from './GlobalContext';

// Create a provider component
export const GlobalProvider = ({children}: { children: ReactNode }) => {
    // Initialize the state with the environment variables
    // import.meta.env.VITE_KAFKA_ENABLE
    const [grafanaPassword, setGrafanaPassword] = useState<string>(import.meta.env.VITE_GRAFANA_USER_PASSWORD);
    const [grafanaUrl, setGrafanaUrl] = useState<string>(import.meta.env.VITE_GRAFANA_URL);
    const [repoUrl, setRepoUrl] = useState<string>(import.meta.env.VITE_REPO_URL);

    return (
        <GlobalContext.Provider value={{
            grafanaPassword,
            setGrafanaPassword,
            grafanaUrl,
            setGrafanaUrl,
            repoUrl,
            setRepoUrl
        }}>
            {children}
        </GlobalContext.Provider>
    );
};
