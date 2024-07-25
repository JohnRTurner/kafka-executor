import {useState} from 'react';
import BatchList from "./batchList/BatchList.tsx";
import {BatchControllerApi, ConsumerControllerApi, ProducerControllerApi} from "./api";
import apiConfig from "./apiConfig.tsx";
import ResultDialog from "./dialog/ResultDialog.tsx";
import ConfirmationDialog from "./dialog/ConfirmationDialog.tsx";
import TopicDialog from "./dialog/TopicDialog.tsx";
import ConnectionDialog from "./connection/ConnectionDialog.tsx";
import {Container, Dropdown, Nav, Navbar} from 'react-bootstrap';

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

    const [selectedDisplay, setSelectedDisplay] = useState<'BatchList' | 'External'>('BatchList');
    const [menuOpen, setMenuOpen] = useState(false);
    const [externalUrl, setExternalUrl] = useState<string>('');

    const [showTopic, setShowTopic] = useState(false); // State for showing confirmation dialog
    const [showConnection, setShowConnection] = useState(false); // State for showing confirmation dialog
    const [showConfirmation, setShowConfirmation] = useState(false); // State for showing confirmation dialog
    const [showResult, setShowResultDialog] = useState(false); // State for showing result dialog
    const [apiResult, setApiResult] = useState<string>(""); // State for API call result

    const [handleConfirmation, setHandleConfirmation] = useState<() => void>(() => {
    }); // State for API call result
    const [confirmationMessage, setConfirmationMessage] = useState<string | null>(null); // State for API call result

    const grafanaUrl = import.meta.env.VITE_GRAFANA_URL;
    const grafanaPassword = (import.meta.env.VITE_GRAFANA_USER_PASSWORD) ?
        "Grafana User Password: " + import.meta.env.VITE_GRAFANA_USER_PASSWORD :
        "GRAFANA USER PASSWORD is not defined."


    const menuItems: MenuItem[] = [
        {label: 'Batch List', action: () => handleDisplayChange('BatchList')},
        {
            label: 'Application Links',
            subMenuItems: [
                {
                    label: 'Swagger',
                    url: `http://${window.location.hostname}:${window.location.port}/api/swagger-ui/index.html#/`
                },
                {
                    label: 'Prometheus',
                    url: `http://${window.location.hostname}:${window.location.port}/prometheus/graph`
                },
            ],
        },
        {
            label: 'External Links',
            subMenuItems: [
                {label: 'Aiven Console', externalUrl: 'https://console.aiven.io/'},
                {label: 'Grafana', externalUrl: grafanaUrl},
                {
                    label: 'Grafana Password', action: () => {
                        setApiResult(grafanaPassword);
                        setShowResultDialog(true);
                    }
                },
                {label: 'GitHub Project', externalUrl: 'https://github.com/JohnRTurner/kafka-executor'},
            ],
        },
        {
            label: 'Settings',
            subMenuItems: [
                {label: 'Update Connections', action: () => setShowConnection(true)},
                {label: 'Stop All Tasks', action: () => handleOpenConfirmation('Stop All Tasks.', handleCleanTasks)},
                {
                    label: 'Clean Producer Connections',
                    action: () => handleOpenConfirmation('Delete All Producer Connections.', handleCleanProducer)
                },
                {
                    label: 'Clean Consumer Connections',
                    action: () => handleOpenConfirmation('Delete All Consumer Connections.', handleCleanConsumer)
                },
                {
                    label: 'Create/Reset Topics',
                    action: () => handleOpenConfirmation('Drop if they exist, then create all the test topics.',
                        () => {
                            setShowConfirmation(false);
                            setShowTopic(true)
                        })
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
        setShowConnection(false);
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

    const handleResetTopics = (numberOfPartitions: number, replication: number) => {
        setShowTopic(false);
        const resetTopics = async () => {
            await producerController.deleteKafkaTopics()
                .then(response1 => {
                    console.log("Deleted Topics Successfully");
                    console.log(response1);
                })
                .catch(error => {
                    console.log("Failed to Delete Topics");
                    console.log('Error: ' + error);
                });
            await producerController.createKafkaTopics(undefined, numberOfPartitions, replication)
                .then(response2 => {
                    console.log(response2);
                    setApiResult('Created Successfully');
                    setShowResultDialog(true);
                })
                .catch(error => {
                    setApiResult('Error: ' + error);
                    setShowResultDialog(true);
                });
        }
        resetTopics();
    };

    const closeConfirmation = () => {
        setShowConfirmation(false);
    };

    const closeTopicDialog = () => {
        setShowTopic(false);
    };

    const closeResultDialog = () => {
        setShowResultDialog(false);
        setApiResult("");
    };

    const closeConnectionDialog = () => {
        setShowConnection(false);
    };

    return (
        <Container fluid className="d-flex flex-column vh-100">
            <Navbar bg="dark" variant="dark" expand="lg">
                <Navbar.Brand>Kafka Executor Demo</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" onClick={toggleMenu}/>
                <Navbar.Collapse id="basic-navbar-nav" className={`${menuOpen ? 'show' : ''}`}>
                    <Nav className="mr-auto">
                        {menuItems.map((item, index) => (
                            <Dropdown key={index} as={Nav.Item}>
                                {item.subMenuItems ? (
                                    <>
                                        <Dropdown.Toggle as={Nav.Link}>
                                            {item.label}
                                        </Dropdown.Toggle>
                                        <Dropdown.Menu>
                                            {item.subMenuItems.map((subItem, subIndex) => (
                                                <Dropdown.Item key={subIndex}
                                                               onClick={() => handleMenuItemClick(subItem)}>
                                                    {subItem.label}
                                                </Dropdown.Item>
                                            ))}
                                        </Dropdown.Menu>
                                    </>
                                ) : (
                                    <Nav.Link onClick={() => handleMenuItemClick(item)}>{item.label}</Nav.Link>
                                )}
                            </Dropdown>
                        ))}
                    </Nav>
                </Navbar.Collapse>
            </Navbar>

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

            <ConnectionDialog
                isOpen={showConnection}
                onClose={closeConnectionDialog}
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
