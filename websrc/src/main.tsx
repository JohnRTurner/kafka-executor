import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import './bootstrap.css';
import {GlobalProvider} from "./GlobaProvider.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <GlobalProvider>
            <App/>
        </GlobalProvider>,
    </React.StrictMode>,
)
