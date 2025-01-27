import React, {useEffect, useState} from 'react';
import {Button, Modal, Tab, Tabs} from 'react-bootstrap';
import {KafkaConnectionConfigDTO} from "../../api";
import BasicTab from './BasicTab.tsx';
import SchemaTab from './SchemaTab.tsx';
import ProducerTab from './ProducerTab.tsx';
import ConsumerTab from './ConsumerTab.tsx';
import {configController} from "../../controllers";

export type ConnectionDialogProps = {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: ((result: string) => void) | undefined;
};

export interface KeyValue {
    key: string;
    value: string;
}

const convertToKeyValueArray = (data: { [p: string]: string }): KeyValue[] => {
    return Object.keys(data).map((key) => ({
        key: key,
        value: data[key],
    }));
};

const ConnectionDialog: React.FC<ConnectionDialogProps> = (connectionDialogProps: ConnectionDialogProps) => {
    const [compressionTypes, setCompressionTypes] = useState<string[]>([]);
    const [selectedCompressionType, setSelectedCompressionType] = useState<string>('');
    const [ackTypes, setAckTypes] = useState<KeyValue[]>([]);
    const [selectedAckType, setSelectedAckType] = useState<string>('');
    const [connectionConfig, setConnectionConfig] = useState<KafkaConnectionConfigDTO>({});
    const [activeTab, setActiveTab] = useState<string>('Basic');

    useEffect(() => {
        const fetchCompressionTypes = async () => {
            try {
                const response = await configController.kafkaCompressionTypes();
                setCompressionTypes(response.data);
            } catch (error) {
                console.error('Error fetching compression types:', error);
                setCompressionTypes([]);
            }
        };
        const fetchAckTypes = async () => {
            try {
                const response = await configController.kafkaAckTypes();
                const x: KeyValue[] = convertToKeyValueArray(response.data);
                setAckTypes(x);
            } catch (error) {
                console.error('Error fetching ack types:', error);
                setAckTypes([]);
            }
        };
        const fetchStatus = async () => {
            try {
                const response = await configController.kafkaConnection1();
                setConnectionConfig(response.data);
                if (response.data.compressionType != undefined) {
                    setSelectedCompressionType(response.data.compressionType.toUpperCase())
                }
                if (response.data.acks != undefined && ackTypes != undefined) {
                    setSelectedAckType(response.data.acks)
                }
            } catch (error) {
                console.error('Error fetching connection configuration:', error);
            }
        };
        if (compressionTypes.length === 0) {
            fetchCompressionTypes().finally(() => {
                fetchAckTypes().finally(() => {
                    fetchStatus().finally(() => {
                    })
                })
            })
        } else if (ackTypes.length === 0) {
            fetchAckTypes().finally(() => {
                fetchStatus().finally(() => {
                })
            });
        } else {
            fetchStatus().finally(() => {
            })
        }
    }, [ackTypes, ackTypes.length, compressionTypes.length]);

    const handleInputChange = (field: string, value: string | number | boolean) => {
        setConnectionConfig(prevState => ({
            ...prevState,
            [field]: value
        }));
    };

    const handleConfirm = async () => {
        let retMessage = 'Call to update the Connection Configuration has failed!';
        try {
            await configController.kafkaConnection(connectionConfig);
            retMessage = 'Successfully updated the Connection Configuration.';
        } catch (error) {
            console.error('Error saving the Kafka Connection Configuration.', error);
        } finally {
            connectionDialogProps.onConfirm && connectionDialogProps.onConfirm(retMessage);
            connectionDialogProps.onClose();
        }
    };

    const renderTabContent = () => {
        switch (activeTab) {
            case 'Basic':
                return <BasicTab connectionConfig={connectionConfig} handleInputChange={handleInputChange}/>;
            case 'Schema':
                return <SchemaTab connectionConfig={connectionConfig} handleInputChange={handleInputChange}/>;
            case 'Producer':
                return (
                    <ProducerTab
                        connectionConfig={connectionConfig}
                        handleInputChange={handleInputChange}
                        compressionTypes={compressionTypes}
                        selectedCompressionType={selectedCompressionType}
                        setSelectedCompressionType={setSelectedCompressionType}
                        ackTypes={ackTypes}
                        selectedAckType={selectedAckType}
                        setSelectedAckType={setSelectedAckType}
                    />
                );
            case 'Consumer':
                return <ConsumerTab connectionConfig={connectionConfig} handleInputChange={handleInputChange}/>;
            default:
                return null;
        }
    };

    return (
        <Modal show={connectionDialogProps.isOpen} onHide={connectionDialogProps.onClose} centered scrollable={true}>
            <Modal.Header closeButton>
                <Modal.Title>Kafka Connection Configuration</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Tabs
                    activeKey={activeTab}
                    onSelect={(k) => setActiveTab(k || 'Basic')}
                    className="mb-3"
                >
                    <Tab eventKey="Basic" title="Basic Connection">
                        {renderTabContent()}
                    </Tab>
                    <Tab eventKey="Schema" title="Schema Registry">
                        {renderTabContent()}
                    </Tab>
                    <Tab eventKey="Producer" title="Producer">
                        {renderTabContent()}
                    </Tab>
                    <Tab eventKey="Consumer" title="Consumer">
                        {renderTabContent()}
                    </Tab>
                </Tabs>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={connectionDialogProps.onClose}>Cancel</Button>
                <Button variant="primary" onClick={handleConfirm}>Confirm</Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ConnectionDialog;
