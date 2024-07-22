import React, {useEffect, useState} from 'react';
import Modal from 'react-modal';
import './Modal.css';
import {ConfigControllerApi, ConnectionConfigDTO} from "../api";
import apiConfig from "../apiConfig.tsx";

type ConnectionDialogProps = {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: ((result: string) => void) | undefined;
};

interface KeyValue {
    key: string;
    value: string;
}

const convertToKeyValueArray = (data: { [p: string]: string }): KeyValue[] => {
    return Object.keys(data).map((key) => ({
        key: key,
        value: data[key],
    }));
};
/*
const findKeyByValue = (keyValueArray: KeyValue[], searchValue: string): string | undefined => {
    const found = keyValueArray.find((item) => item.value === searchValue);
    return found ? found.key : undefined;
};

const findValueByKey = (keyValueArray: KeyValue[], searchKey: string): string | undefined => {
    const found = keyValueArray.find((item) => item.key === searchKey);
    return found ? found.value : undefined;
};
*/

const configController = new ConfigControllerApi(apiConfig);

const ConnectionDialog: React.FC<ConnectionDialogProps> = ({isOpen, onClose, onConfirm}) => {
    const [compressionTypes, setCompressionTypes] = useState<string[]>([]);
    const [selectedCompressionType, setSelectedCompressionType] = useState<string>('');
    const [ackTypes, setAckTypes] = useState<KeyValue[]>([]);
    const [selectedAckType, setSelectedAckType] = useState<string>('');
    const [connectionConfig, setConnectionConfig] = useState<ConnectionConfigDTO>({});
    const [activeTab, setActiveTab] = useState<'Basic' | 'Schema' | 'Producer' | 'Consumer'>('Basic');

    useEffect(() => {
        const fetchCompressionTypes = async () => {
            try {
                const response = await configController.getCompressionTypes();
                setCompressionTypes(response.data);
                //setSelectedCompressionType(response.data[0]);
            } catch (error) {
                console.error('Error fetching compression types:', error);
                setCompressionTypes([]);
            }
        };
        const fetchAckTypes = async () => {
            try {
                const response = await configController.getAckTypes();

                //console.error("raw ack types: " + convertToKeyValueArray(response.data));

                const x: KeyValue[] = convertToKeyValueArray(response.data);
                setAckTypes(x);
                //setSelectedAckType(x[0].key);
            } catch (error) {
                console.error('Error fetching ack types:', error);
                setAckTypes([]);
            }
        };
        const fetchStatus = async () => {
            try {
                const response = await configController.getStatus();
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
    }, [ackTypes.length, compressionTypes.length]);

    const handleInputChange = (field: keyof ConnectionConfigDTO, value: string | number | boolean) => {
        setConnectionConfig(prevState => ({
            ...prevState,
            [field]: value
        }));
    };

    const handleConfirm = async () => {
        let retMessage = 'Call to update the Connection Configuration has failed!';
        try {
            await configController.updateConnectionConfig(connectionConfig);
            retMessage = 'Successfully updated the Connection Configuration.';
        } catch (error) {
            console.error('Error saving the Connection Configuration.', error);
        }
        onConfirm && onConfirm(retMessage);
    };

    const renderTabContent = () => {
        switch (activeTab) {
            case 'Basic':
                return (
                    <>
                        <div className="input-group">
                            <label htmlFor="host">Host:</label>
                            <input
                                id="host"
                                type="text"
                                value={connectionConfig.host || ''}
                                onChange={(e) => handleInputChange('host', e.target.value)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="port">Port:</label>
                            <input
                                id="port"
                                type="text"
                                value={connectionConfig.port || ''}
                                onChange={(e) => handleInputChange('port', e.target.value)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="cert_password">Cert Password:</label>
                            <input
                                id="cert_password"
                                type="password"
                                value={connectionConfig.cert_password || ''}
                                onChange={(e) => handleInputChange('cert_password', e.target.value)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="truststore_location">Truststore Location:</label>
                            <input
                                id="truststore_location"
                                type="text"
                                value={connectionConfig.truststore_location || ''}
                                onChange={(e) => handleInputChange('truststore_location', e.target.value)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="keystore_location">Keystore Location:</label>
                            <input
                                id="keystore_location"
                                type="text"
                                value={connectionConfig.keystore_location || ''}
                                onChange={(e) => handleInputChange('keystore_location', e.target.value)}
                            />
                        </div>
                    </>
                );
            case 'Schema':
                return (
                    <>
                        <div className="input-group">
                            <label htmlFor="schemaRegistryHost">Schema Registry Host:</label>
                            <input
                                id="schemaRegistryHost"
                                type="text"
                                value={connectionConfig.schemaRegistryHost || ''}
                                onChange={(e) => handleInputChange('schemaRegistryHost', e.target.value)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="schemaRegistryPort">Schema Registry Port:</label>
                            <input
                                id="schemaRegistryPort"
                                type="text"
                                value={connectionConfig.schemaRegistryPort || ''}
                                onChange={(e) => handleInputChange('schemaRegistryPort', e.target.value)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="schemaRegistryUser">Schema Registry User:</label>
                            <input
                                id="schemaRegistryUser"
                                type="text"
                                value={connectionConfig.schemaRegistryUser || ''}
                                onChange={(e) => handleInputChange('schemaRegistryUser', e.target.value)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="schemaRegistryPassword">Schema Registry Password:</label>
                            <input
                                id="schemaRegistryPassword"
                                type="password"
                                value={connectionConfig.schemaRegistryPassword || ''}
                                onChange={(e) => handleInputChange('schemaRegistryPassword', e.target.value)}
                            />
                        </div>
                    </>
                );
            case 'Producer':
                return (
                    <>
                        <div className="input-group">
                            <label htmlFor="producerLingerMs">Producer Linger Ms:</label>
                            <input
                                id="producerLingerMs"
                                type="number"
                                value={connectionConfig.producerLingerMs || ''}
                                onChange={(e) => handleInputChange('producerLingerMs', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="producerBatchSize">Producer Batch Size:</label>
                            <input
                                id="producerBatchSize"
                                type="number"
                                value={connectionConfig.producerBatchSize || ''}
                                onChange={(e) => handleInputChange('producerBatchSize', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="compressionType">Compression Type:</label>
                            <select
                                id="compressionType"
                                value={selectedCompressionType}
                                onChange={(e) => {
                                    handleInputChange('compressionType', e.target.value);
                                    setSelectedCompressionType(e.target.value);
                                }}
                            >
                                {compressionTypes.map((type, index) => (
                                    <option key={index} value={type}>
                                        {type}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="input-group">
                            <label htmlFor="bufferMemory">Buffer Memory:</label>
                            <input
                                id="bufferMemory"
                                type="number"
                                value={connectionConfig.bufferMemory || ''}
                                onChange={(e) => handleInputChange('bufferMemory', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="idempotenceEnabled">Idempotence Enabled:</label>
                            <input
                                id="idempotenceEnabled"
                                type="checkbox"
                                checked={connectionConfig.idempotenceEnabled || false}
                                onChange={(e) => handleInputChange('idempotenceEnabled', e.target.checked)}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="acks">ACKs:</label>
                            <select
                                id="acks"
                                value={selectedAckType}
                                onChange={(e) => {
                                    handleInputChange('acks', e.target.value);
                                    setSelectedAckType(e.target.value);
                                }}
                            >
                                {ackTypes.map((option) => (
                                    <option key={option.key} value={option.key}>
                                        {option.key}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </>
                );
            case 'Consumer':
                return (
                    <>
                        <div className="input-group">
                            <label htmlFor="maxPollRecords">Max Poll Records:</label>
                            <input
                                id="maxPollRecords"
                                type="number"
                                value={connectionConfig.maxPollRecords || ''}
                                onChange={(e) => handleInputChange('maxPollRecords', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="fetchMinByes">Fetch Min Bytes:</label>
                            <input
                                id="fetchMinByes"
                                type="number"
                                value={connectionConfig.fetchMinByes || ''}
                                onChange={(e) => handleInputChange('fetchMinByes', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="fetchMaxWaitMS">Fetch Max Wait MS:</label>
                            <input
                                id="fetchMaxWaitMS"
                                type="number"
                                value={connectionConfig.fetchMaxWaitMS || ''}
                                onChange={(e) => handleInputChange('fetchMaxWaitMS', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="sessionTimeoutMs">Session Timeout Ms:</label>
                            <input
                                id="sessionTimeoutMs"
                                type="number"
                                value={connectionConfig.sessionTimeoutMs || ''}
                                onChange={(e) => handleInputChange('sessionTimeoutMs', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="heartbeatTimeoutMs">Heartbeat Timeout Ms:</label>
                            <input
                                id="heartbeatTimeoutMs"
                                type="number"
                                value={connectionConfig.heartbeatTimeoutMs || ''}
                                onChange={(e) => handleInputChange('heartbeatTimeoutMs', Number(e.target.value))}
                            />
                        </div>
                        <div className="input-group">
                            <label htmlFor="autoCommitIntervalMs">Auto Commit Interval Ms:</label>
                            <input
                                id="autoCommitIntervalMs"
                                type="number"
                                value={connectionConfig.autoCommitIntervalMs || ''}
                                onChange={(e) => handleInputChange('autoCommitIntervalMs', Number(e.target.value))}
                            />
                        </div>
                    </>
                );
            default:
                return null;
        }
    };

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onClose}
            contentLabel="Connection Configuration"
            className="connection-modal"
            overlayClassName="connection-modal-overlay"
        >
            <div className="modal-header">
                <h2>Connection Configuration</h2>
            </div>
            <div className="modal-body">
                <div className="tab">
                    <button className={activeTab === 'Basic' ? 'active' : ''} onClick={() => setActiveTab('Basic')}>
                        Basic Connection
                    </button>
                    <button className={activeTab === 'Schema' ? 'active' : ''} onClick={() => setActiveTab('Schema')}>
                        Schema Registry
                    </button>
                    <button className={activeTab === 'Producer' ? 'active' : ''}
                            onClick={() => setActiveTab('Producer')}>
                        Producer
                    </button>
                    <button className={activeTab === 'Consumer' ? 'active' : ''}
                            onClick={() => setActiveTab('Consumer')}>
                        Consumer
                    </button>
                </div>
                <div className="tab-content">
                    {renderTabContent()}
                </div>
            </div>
            <div className="confirmation-buttons">
                <button onClick={handleConfirm} className="confirm-button">
                    Confirm
                </button>
                <button onClick={onClose} className="cancel-button">
                    Cancel
                </button>
            </div>
        </Modal>
    );
};

export default ConnectionDialog;
