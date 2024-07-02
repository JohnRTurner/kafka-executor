import  { useState } from 'react';
import './App.css';
import BatchList from "./batchList/BatchList.tsx";
import Grafana from "./password/Grafana.tsx";

type MenuItem = {
    label: string;
    subMenuItems?: MenuItem[];
    action?: () => void;
    url?: string;
    externalUrl?: string;
};

/*
interface ProcessEnv {
    REACT_APP_GRAFANA_URL?: string;
}

declare let process: {
    env: ProcessEnv;
};

 */

function App() {

    const [selectedDisplay, setSelectedDisplay] = useState<'BatchList' | 'Grafana'| 'Sump' | 'External'>('BatchList');
    const [menuOpen, setMenuOpen] = useState(false);
    const [activeMenu, setActiveMenu] = useState<MenuItem | null>(null);
    const [externalUrl, setExternalUrl] = useState<string>('');

    //const grafanaUrl = process.env.REACT_APP_GRAFANA_URL;
    const grafanaUrl = import.meta.env.VITE_GRAFANA_URL;


    const menuItems: MenuItem[] = [
        {
            label: 'Home',
            subMenuItems: [
                { label: 'BatchList', action: () => handleDisplayChange('BatchList') },
                { label: 'Sump', action: () => handleDisplayChange('Sump') },
            ],
        },
        {
            label: 'Application Links',
            subMenuItems: [
                { label: 'Swagger', url: 'http://' + window.location.hostname + ':' + window.location.port + '/api/swagger-ui/index.html#/'},
                { label: 'Prometheus', url: 'http://' + window.location.hostname + ':9090/graph'},

            ],
        },
        {
            label: 'External Links',
            subMenuItems: [
                { label: 'Grafana', externalUrl: grafanaUrl },
                { label: 'Grafana Password',action: () => handleDisplayChange('Grafana') },
                { label: 'GitHub Project', externalUrl: 'https://github.com/JohnRTurner/kafka_executor' },
            ],
        },
        {
            label: 'Settings',
            subMenuItems: [
                { label: 'Option 1', action: () => console.log('Option 1 selected') },
                { label: 'Option 2', action: () => console.log('Option 2 selected') },
            ],
        },
    ];

    const toggleMenu = () => {
        setMenuOpen(!menuOpen);
    };

    const handleMenuItemClick = (menuItem: MenuItem) => {
        if (menuItem.subMenuItems) {
            setActiveMenu(menuItem);
        } else if (menuItem.action) {
            menuItem.action();
            setMenuOpen(false); // Close the menu after selecting an option
            setActiveMenu(null); // Reset activeMenu to collapse sub-menu and show main menu
        } else if (menuItem.url) {
            setSelectedDisplay('External');
            setExternalUrl(menuItem.url);
            setMenuOpen(false); // Close the menu after selecting an option
            setActiveMenu(null); // Reset activeMenu to collapse sub-menu and show main menu

        } else if (menuItem.externalUrl){
            setSelectedDisplay('External');
            window.open(menuItem.externalUrl, '_blank'); // Open link in a new tab
            setMenuOpen(false); // Close the menu after selecting an option
            setActiveMenu(null); // Reset activeMenu to collapse sub-menu and show main menu

        }
    };

    const handleDisplayChange = (display: 'BatchList' | 'Sump' | 'Grafana') => {
        setSelectedDisplay(display);
        setActiveMenu(null); // Reset activeMenu after selecting display option
    };

    return (
        <div className="app-container">
            <div className="banner">
                <h1>My Application</h1>
                <button className="menu-button" onClick={toggleMenu}>
                    Menu
                </button>
                {menuOpen && (
                    <div className="dropdown-menu">
                        {activeMenu ? (
                            <>
                                <button className="back-button" onClick={() => setActiveMenu(null)}>
                                    Back
                                </button>
                                {activeMenu.subMenuItems?.map((item, index) => (
                                    <button key={index} onClick={() => handleMenuItemClick(item)}>
                                        {item.label}
                                    </button>
                                ))}
                            </>
                        ) : (
                            <>
                                {menuItems.map((item, index) => (
                                    <button key={index} onClick={() => handleMenuItemClick(item)}>
                                        {item.label}
                                    </button>
                                ))}
                            </>
                        )}
                    </div>
                )}
            </div>
            <div className="main-content">
                {selectedDisplay === 'BatchList' && <BatchList />}
                {selectedDisplay === 'Sump' && <BatchList />}
                {selectedDisplay === 'Grafana' && <Grafana />}
                {selectedDisplay === 'External' && (
                    <iframe src={externalUrl} title="External Content" className="external-content" />
                )}
            </div>
        </div>
    );
}

export default App;