import {useEffect, useState} from 'react';
import {BatchList} from "./batchList";
import {batchController, configController, consumerController, producerController} from './controllers';
import {Container} from 'react-bootstrap';
import {useGlobalContext} from './GlobalContext';
import {
    ConfirmationDialog,
    ConnectionDialog,
    IndexDialog,
    OpenSearchConnectionDialog,
    ResultDialog,
    TopicDialog
} from "./dialog";
import {MenuItem, NavbarComponent} from "./components";

function App() {
    const {grafanaPassword, grafanaUrl, repoUrl} = useGlobalContext();

    const [selectedDisplay, setSelectedDisplay] = useState<'BatchList' | 'External'>('BatchList');
    const [menuOpen, setMenuOpen] = useState(false);
    const [externalUrl, setExternalUrl] = useState<string>('');

    const [showTopic, setShowTopic] = useState(false); // State for showing topic dialog
    const [showIndex, setShowIndex] = useState(false); // State for showing index dialog
    const [showConnection, setShowConnection] = useState(false); // State for showing confirmation dialog
    const [showOpenSearchConnection, setShowOpenSearchConnection] = useState(false); // State for showing confirmation dialog

    const [showConfirmation, setShowConfirmation] = useState(false); // State for showing confirmation dialog
    const [showResult, setShowResultDialog] = useState(false); // State for showing result dialog
    const [apiResult, setApiResult] = useState<string>(""); // State for API call result

    const [kafkaEnable, setKafkaEnable] = useState(true)
    const [openSearchEnable, setOpenSearchEnable] = useState(true)

    const [handleConfirmation, setHandleConfirmation] = useState<() => void>(() => {
    }); // State for API call result
    const [confirmationMessage, setConfirmationMessage] = useState<string | null>(null); // State for API call result

    const grafanaPasswordTxt = grafanaPassword ?
        "Grafana User Password: " + grafanaPassword :
        "GRAFANA USER PASSWORD is not defined.";


    useEffect(() => {
        configController.enableKafka1().then((response) => {
            console.log(response);
            setKafkaEnable(response.data)
        });
        configController.enableOpenSearch1().then((response) => {
            console.log(response);
            setOpenSearchEnable(response.data)
        });
    }, []); // Empty dependency array ensures this effect runs only once

    const handleToggleKafkaEnable = () => {
        configController.enableKafka(!kafkaEnable).then(
            () => {
                configController.enableKafka1().then((response) => setKafkaEnable(response.data));
            },
        )
    };

    const handleToggleOpenSearchEnable = () => {
        configController.enableOpenSearch(!openSearchEnable).then(
            () => {
                configController.enableOpenSearch1().then((response) => setOpenSearchEnable(response.data));
            },
        )
    };

    const menuItems: MenuItem[] = [
        {label: 'Batch List', action: () => handleDisplayChange('BatchList')},
        {
            label: 'Application Links',
            subMenuItems: [
                {
                    label: 'Swagger',
                    url: `http://${window.location.hostname}:${window.location.port}/api/swagger-ui/index.html#/`,
                },
                {
                    label: 'Prometheus',
                    url: `http://${window.location.hostname}:${window.location.port}/prometheus/graph`,
                },
            ],
        },
        {
            label: 'External Links',
            subMenuItems: [
                {label: 'Aiven Console', externalUrl: 'https://console.aiven.io/'},
                {label: 'Grafana', externalUrl: grafanaUrl},
                {
                    label: 'Grafana Password',
                    action: () => {
                        setApiResult(grafanaPasswordTxt);
                        setShowResultDialog(true);
                    },
                },
                {label: 'GitHub Project', externalUrl: repoUrl},
            ],
        },
        {
            label: 'Kafka Settings',
            subMenuItems: [
                {
                    label: 'Update Kafka Connections',
                    action: () => setShowConnection(true),
                    disabled: !kafkaEnable,
                },
                {
                    label: 'Clean Kafka Producer Connections',
                    action: () => handleOpenConfirmation('Delete All Producer Connections.', handleCleanProducer),
                    disabled: !kafkaEnable,
                },
                {
                    label: 'Clean Kafka Consumer Connections',
                    action: () => handleOpenConfirmation('Delete All Consumer Connections.', handleCleanConsumer),
                    disabled: !kafkaEnable,
                },
                {
                    label: 'Create/Reset Kafka Topics',
                    action: () => handleOpenConfirmation('Drop if they exist, then create all the test topics.', () => {
                        setShowConfirmation(false);
                        setShowTopic(true);
                    }),
                    disabled: !kafkaEnable,
                },
                {
                    label: 'Stop All Tasks',
                    action: () => handleOpenConfirmation('Stop All Tasks.', handleCleanTasks),
                    disabled: !kafkaEnable,
                },
                {
                    label: `${kafkaEnable ? 'Disable' : 'Enable'} Kafka Server`,
                    action: () => handleOpenConfirmation(
                        kafkaEnable
                            ? 'Disable Kafka Menu Items'
                            : `To manually connect, set the connection string and place the certificate files on each application server. More information at ${repoUrl}/tree/main/certs.`,
                        () => {
                            setShowConfirmation(false);
                            handleToggleKafkaEnable();
                        }
                    ),
                    hidden: kafkaEnable,
                },
            ],
        },
        {
            label: 'OpenSearch Settings',
            subMenuItems: [
                {
                    label: 'Update OpenSearch Connections',
                    action: () => setShowOpenSearchConnection(true),
                    disabled: !openSearchEnable,
                },
                {
                    label: 'Clean OpenSearch Connections',
                    action: () => handleOpenConfirmation('Delete All Consumer Connections.', handleCleanOpenSearch),
                    disabled: !openSearchEnable,
                },
                {
                    label: 'Create/Reset OpenSearch Indexes',
                    action: () => handleOpenConfirmation('Drop if they exist, then create all the test topics.', () => {
                        setShowConfirmation(false);
                        setShowIndex(true);
                    }),
                    disabled: !openSearchEnable,
                },
                {
                    label: 'Stop All Tasks',
                    action: () => handleOpenConfirmation('Stop All Tasks.', handleCleanTasks),
                    disabled: !openSearchEnable,
                },
                {
                    label: `${openSearchEnable ? 'Disable' : 'Enable'} OpenSearch Server`,
                    action: () => handleOpenConfirmation(
                        openSearchEnable
                            ? 'Disable OpenSearch Menu Items'
                            : 'To manually connect, set the connection string.',
                        () => {
                            setShowConfirmation(false);
                            handleToggleOpenSearchEnable();
                        }
                    ),
                    hidden: openSearchEnable,
                },
            ],
        },

    ];

    const toggleMenu = () => {
        setMenuOpen(!menuOpen);
    };

    const handleMenuItemClick = (menuItem: MenuItem) => {
        if (menuItem.action) {
            menuItem.action();
            setMenuOpen(false);
        } else if (menuItem.url) {
            setSelectedDisplay('External');
            setExternalUrl(menuItem.url);
            setMenuOpen(false);
        } else if (menuItem.externalUrl) {
            window.open(menuItem.externalUrl, '_blank');
            setMenuOpen(false);
        }
    };

    const handleDisplayChange = (display: 'BatchList') => {
        setSelectedDisplay(display);
    };

    const handleOpenConfirmation = (message: string, action: () => void) => {
        setConfirmationMessage(message);
        setHandleConfirmation(() => action);
        setShowConfirmation(true);
    };

    const handleCloseMessage = (msg: string) => {
        // setShowConnection(false);
        // setShowOpenSearchConnection(false);
        setApiResult(msg);
        setShowResultDialog(true);
    }

    const handleCleanTasks = () => {
        setShowConfirmation(false);
        batchController.stopAllTasks()
            .then(response => {
                const reply = response.data;
                setApiResult(reply);
                setShowResultDialog(true);
            })
            .catch(error => {
                setApiResult('Error: ' + error);
                setShowResultDialog(true);
            });
    };

    const handleCleanProducer = () => {
        setShowConfirmation(false);
        producerController.cleanKafkaConnectionPool()
            .then(response => {
                const reply = response.data;
                setApiResult(reply);
                setShowResultDialog(true);
            })
            .catch(error => {
                setApiResult('Error: ' + error);
                setShowResultDialog(true);
            });
    };

    const handleCleanConsumer = () => {
        setShowConfirmation(false);
        consumerController.cleanKafkaConnectionPool1()
            .then(response => {
                const reply = response.data;
                setApiResult(reply);
                setShowResultDialog(true);
            })
            .catch(error => {
                setApiResult('Error: ' + error);
                setShowResultDialog(true);
            });
    };

    const handleCleanOpenSearch = () => {
        setShowConfirmation(false);
        consumerController.cleanOpenSearchConnectionPool()
            .then(response => {
                const reply = response.data;
                setApiResult(reply);
                setShowResultDialog(true);
            })
            .catch(error => {
                setApiResult('Error: ' + error);
                setShowResultDialog(true);
            });
    };

    const handleResetTopics = (numberOfPartitions: number, replication: number) => {
        setShowTopic(false);
        const resetTopics = async () => {
            await producerController.deleteKafkaTopics()
                .then(response1 => {
                    if (response1.data === true) {
                        console.log('Deleted Topics Successfully.');
                    } else {
                        console.log('Delete Topics Failed.');
                    }
                })
                .catch(error => {
                    console.log("Failed to Delete Topics");
                    console.log('Error: ' + error);
                })
                .finally(() => {
                        producerController.createKafkaTopics(undefined, numberOfPartitions, replication)
                            .then(response2 => {
                                console.log(response2);
                                if (response2.data === true) {
                                    setApiResult('Created Kafka Topics Successfully.');
                                } else {
                                    setApiResult('Create Topics Failed.');
                                }
                                setShowResultDialog(true);
                            })
                            .catch(error => {
                                setApiResult('Error: ' + error);
                                setShowResultDialog(true);
                            });
                    }
                );
        }
        resetTopics();
    };

    const handleResetIndexes = (numberOfShards: number, replication: number, refreshSeconds: number) => {
        setShowIndex(false);
        const resetIndexes = async () => {
            await producerController.deleteOpenSearchIndexes()
                .then(response1 => {
                    if (response1.data === true) {
                        console.log('Deleted Indexes Successfully.');
                    } else {
                        console.log('Delete Indexes Failed.');
                    }
                })
                .catch(error => {
                    console.log('Error: ' + error);
                })
                .finally(() => {
                        producerController.createOpenSearchIndexes(undefined, numberOfShards, replication, refreshSeconds)
                            .then(response2 => {
                                console.log(response2);
                                if (response2.data === true) {
                                    setApiResult('Created Indexes Successfully.');
                                } else {
                                    setApiResult('Create Indexes Failed.');
                                }
                                setShowResultDialog(true);
                            })
                            .catch(error => {
                                setApiResult('Error: ' + error);
                                setShowResultDialog(true);
                            });
                    }
                );
        }
        resetIndexes();
    }


    const closeConfirmation = () => {
        setShowConfirmation(false);
    };

    const closeTopicDialog = () => {
        setShowTopic(false);
    };

    const closeIndexDialog = () => {
        setShowIndex(false);
    };

    const closeResultDialog = () => {
        setShowResultDialog(false);
        setApiResult("");
    };

    const closeConnectionDialog = () => {
        setShowConnection(false);
    };

    const closeOpenSearchConnectionDialog = () => {
        setShowOpenSearchConnection(false);
    };


    return (
        <Container fluid className="d-flex flex-column vh-100">
            <NavbarComponent
                menuItems={menuItems}
                toggleMenu={toggleMenu}
                menuOpen={menuOpen}
                onMenuItemClick={handleMenuItemClick}
            />
            <div className="main-content" style={{flex: 1, display: 'flex', flexDirection: 'column'}}>
                {selectedDisplay === 'BatchList' && <BatchList/>}
                {selectedDisplay === 'External' && (
                    <iframe src={externalUrl} title="External Content" className="external-content"
                            style={{flex: 1, width: '100%', border: 'none'}}/>
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

            <IndexDialog
                isOpen={showIndex}
                message={confirmationMessage}
                onClose={closeIndexDialog}
                onConfirm={handleResetIndexes}
            />

            <ConnectionDialog
                isOpen={showConnection}
                onClose={closeConnectionDialog}
                onConfirm={handleCloseMessage}
            />

            <OpenSearchConnectionDialog
                isOpen={showOpenSearchConnection}
                onClose={closeOpenSearchConnectionDialog}
                onConfirm={handleCloseMessage}
            />

            <ResultDialog
                isOpen={showResult}
                message={apiResult}
                onClose={closeResultDialog}
            />
        </Container>
    );
}

export default App;
