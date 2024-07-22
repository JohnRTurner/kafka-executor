import {useState} from 'react';
import './App.css';
import BatchList from "./batchList/BatchList.tsx";
import Grafana from "./password/Grafana.tsx";
import {BatchControllerApi, ConsumerControllerApi, ProducerControllerApi} from "./api";
import apiConfig from "./apiConfig.tsx";
import ResultDialog from "./dialog/ResultDialog.tsx";
import ConfirmationDialog from "./dialog/ConfirmationDialog.tsx";
import TopicDialog from "./dialog/TopicDialog.tsx";
import ConnectionDialog from "./dialog/ConnectionDialog.tsx";

const batchController = new BatchControllerApi(apiConfig);
const consumerController = new ConsumerControllerApi(apiConfig);
const producerController = new ProducerControllerApi(apiConfig);

type MenuItem = {
    label: string;
    subMenuItems?: MenuItem[];
    action?: () => void;
    url?: string;
    externalUrl?: string;
};

function App() {

    const [selectedDisplay, setSelectedDisplay] = useState<'BatchList' | 'Grafana' | 'Sump' | 'External'>('BatchList');
    const [menuOpen, setMenuOpen] = useState(false);
    const [activeMenu, setActiveMenu] = useState<MenuItem | null>(null);
    const [externalUrl, setExternalUrl] = useState<string>('');

    const [showTopic, setShowTopic] = useState(false); // State for showing confirmation dialog
    const [showConnection, setShowConnection] = useState(false); // State for showing confirmation dialog
    const [showConfirmation, setShowConfirmation] = useState(false); // State for showing confirmation dialog
    const [showResult, setShowResultDialog] = useState(false); // State for showing result dialog
    const [apiResult, setApiResult] = useState<string | null>(null); // State for API call result

    const [handleConfirmation, setHandleConfirmation] = useState<() => void>(() => {
    }); // State for API call result
    const [confirmationMessage, setConfirmationMessage] = useState<string | null>(null); // State for API call result


    //const grafanaUrl = process.env.REACT_APP_GRAFANA_URL;
    const grafanaUrl = import.meta.env.VITE_GRAFANA_URL;


    const menuItems: MenuItem[] = [
        {label: 'Batch List', action: () => handleDisplayChange('BatchList')},
        /*
        {
            label: 'Batch',
            subMenuItems: [
                { label: 'Batch List', action: () => handleDisplayChange('BatchList') },
                { label: 'Sump', action: () => handleDisplayChange('Sump') },
            ],
        },*/
        {
            label: 'Application Links',
            subMenuItems: [
                {
                    label: 'Swagger',
                    url: 'http://' + window.location.hostname + ':' + window.location.port + '/api/swagger-ui/index.html#/'
                },
                {
                    label: 'Prometheus',
                    url: 'http://' + window.location.hostname + ':' + window.location.port + '/prometheus/graph'
                },

            ],
        },
        {
            label: 'External Links',
            subMenuItems: [
                {label: 'Grafana', externalUrl: grafanaUrl},
                {label: 'Grafana Password', action: () => handleDisplayChange('Grafana')},
                {label: 'GitHub Project', externalUrl: 'https://github.com/JohnRTurner/kafka_executor'},
            ],
        },
        {
            label: 'Settings',
            subMenuItems: [
                {
                    label: 'Update Connections', action: () => {
                        setShowConnection(true)
                    }
                },


                {
                    label: 'Stop All Tasks', action: () => {
                        setConfirmationMessage('Stop All Tasks.')
                        setHandleConfirmation(() => handleCleanTasks)
                        setShowConfirmation(true)
                    }
                },
                {
                    label: 'Clean Producer Connections', action: () => {
                        setConfirmationMessage('Delete All Producer Connections.')
                        setHandleConfirmation(() => handleCleanProducer)
                        setShowConfirmation(true)
                    }
                },
                {
                    label: 'Clean Consumer Connections', action: () => {
                        setConfirmationMessage('Delete All Consumer Connections.')
                        setHandleConfirmation(() => handleCleanConsumer)
                        setShowConfirmation(true)
                    }
                },

                {
                    label: 'Create/Reset Topics', action: () => {
                        setConfirmationMessage('Drop if they exist, then create all the test topics.')
                        setShowTopic(true)
                    }
                },
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

        } else if (menuItem.externalUrl) {
            setSelectedDisplay('External');
            window.open(menuItem.externalUrl, '_blank'); // Open link in a new tab
            setMenuOpen(false); // Close the menu after selecting an option
            setActiveMenu(null); // Reset activeMenu to collapse sub-menu and show main menu

        }
    };

    const handleDisplayChange = (display: 'BatchList' | 'Grafana') => {
        setSelectedDisplay(display);
        setActiveMenu(null); // Reset activeMenu after selecting display option
    };

    const handleCloseMessage = (msg: string) => {
        setShowConnection(false)
        setApiResult(msg)
        setShowResultDialog(true)
    }

    const handleCleanTasks = () => {
        setShowConfirmation(false); // Close confirmation dialog
        batchController.getClean2()
            .then(response => {
                const reply = response.data;
                setApiResult(reply); // Set API result to state
                setShowResultDialog(true); // Show result dialog
            })
            .catch(error => {
                setApiResult('Error: ' + error); // Set API result to state
                setShowResultDialog(true); // Show result dialog
                // Handle error case
            });
    };

    const handleCleanProducer = () => {
        setShowConfirmation(false); // Close confirmation dialog
        producerController.getClean()
            .then(response => {
                const reply = response.data;
                setApiResult(reply); // Set API result to state
                setShowResultDialog(true); // Show result dialog
            })
            .catch(error => {
                setApiResult('Error: ' + error); // Set API result to state
                setShowResultDialog(true); // Show result dialog
                // Handle error case
            });
    };

    const handleCleanConsumer = () => {
        setShowConfirmation(false); // Close confirmation dialog
        consumerController.getClean1()
            .then(response => {
                const reply = response.data;
                setApiResult(reply); // Set API result to state
                setShowResultDialog(true); // Show result dialog
            })
            .catch(error => {
                setApiResult('Error: ' + error); // Set API result to state
                setShowResultDialog(true); // Show result dialog
                // Handle error case
            });
    };


    const handleResetTopics = (numberOfPartitions: number, replication: number) => {
        setShowTopic(false); // Close topic dialog
        const resetTopics = async () => {
            await producerController.deleteTopics()
                .then((response1) => {
                    console.log("Deleted Topics Successfully");
                    console.log(response1);
                })
                .catch(error => {
                    console.log("Failed to Delete Topics");
                    console.log('Error: ' + error)
                });
            await producerController.createTopics(undefined, numberOfPartitions, replication)
                .then(response2 => {
                    console.log(response2);
                    setApiResult('Created Successfully'); // Set API result to state
                    setShowResultDialog(true); // Show result dialog
                })
                .catch(error => {
                    setApiResult('Error: ' + error); // Set API result to state
                    setShowResultDialog(true); // Show result dialog
                    // Handle error case
                });


        }
        resetTopics()
    };

    const closeConfirmation = () => {
        setShowConfirmation(false); // Close confirmation dialog
    };

    const closeTopicDialog = () => {
        setShowTopic(false); // Close confirmation dialog
    };

    const closeResultDialog = () => {
        setShowResultDialog(false); // Close result dialog
        setApiResult(null); // Clear API result
    };

    const closeConnectionDialog = () => {
        setShowConnection(false)
    };

    return (
        <div className="app-container">
            <div className="banner">
                <h1>Kafka Executor Demo</h1>
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
                {selectedDisplay === 'BatchList' && <BatchList/>}
                {selectedDisplay === 'Grafana' && <Grafana/>}
                {selectedDisplay === 'External' && (
                    <iframe src={externalUrl} title="External Content" className="external-content"/>
                )}
            </div>

            <ConfirmationDialog
                isOpen={showConfirmation}
                message={confirmationMessage}
                onClose={closeConfirmation}
                onConfirm={handleConfirmation}
            />

            <TopicDialog
                isOpen={showTopic}
                message={confirmationMessage}
                onClose={closeTopicDialog}
                onConfirm={handleResetTopics}
            />

            <ResultDialog
                isOpen={showResult}
                message={apiResult || ''}
                onClose={closeResultDialog}
            />

            <ConnectionDialog
                isOpen={showConnection}
                onClose={closeConnectionDialog}
                onConfirm={handleCloseMessage}
            />


        </div>
    );
}

export default App;

