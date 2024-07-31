import {createContext, useContext} from 'react';

// Define the context value type
interface GlobalContextType {
    grafanaPassword: string;
    setGrafanaPassword: React.Dispatch<React.SetStateAction<string>>;
    grafanaUrl: string;
    setGrafanaUrl: React.Dispatch<React.SetStateAction<string>>;
    repoUrl: string;
    setRepoUrl: React.Dispatch<React.SetStateAction<string>>;
}

// Create the context with a default value
const GlobalContext = createContext<GlobalContextType | undefined>(undefined);

// Custom hook to use the GlobalContext
export const useGlobalContext = (): GlobalContextType => {
    const context = useContext(GlobalContext);
    if (!context) {
        throw new Error('useGlobalContext must be used within a GlobalProvider');
    }
    return context;
};

export default GlobalContext;
