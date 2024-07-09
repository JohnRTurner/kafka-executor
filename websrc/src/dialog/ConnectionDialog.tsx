import React, { useEffect, useState } from 'react';
import Modal from 'react-modal';
import './Modal.css';
import { ConfigControllerApi, ConnectionConfigDTO } from "../api";
import apiConfig from "../apiConfig.tsx";

type ConnectionDialogProps = {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: ((Result: string) => void)|undefined;
};

const configController = new ConfigControllerApi(apiConfig);

const ConnectionDialog: React.FC<ConnectionDialogProps> = ({ isOpen, onClose, onConfirm }) => {
    const [compressionTypes, setCompressionTypes] = useState<string[]>([]);
    const [selectedCompressionType, setSelectedCompressionType] = useState<string>('');
    const [connectionConfig, setConnectionConfig] = useState<ConnectionConfigDTO>({
        host: '',
        port: '',
        cert_password: '',
        truststore_location: '',
        keystore_location: '',
        schemaRegistryHost: '',
        schemaRegistryPort: '',
        schemaRegistryUser: '',
        schemaRegistryPassword: '',
        lingerMs: 0,
        batchSize: 0,
        compressionType: undefined
    });

    useEffect(() => {
        const fetchCompressionTypes = async () => {
            try {
                const response = await configController.getCompressionTypes();
                setCompressionTypes(response.data);
                setSelectedCompressionType(response.data[0]);
            } catch (error) {
                console.error('Error fetching compression types:', error);
                setCompressionTypes([]);
            }
        };

        const fetchStatus = async () => {
            try {
                const response = await configController.getStatus();
                setConnectionConfig(response.data);
            } catch (error) {
                console.error('Error fetching connection configuration:', error);
            }
        };

        fetchCompressionTypes();
        fetchStatus();
    }, []);

    const handleInputChange = (field: keyof ConnectionConfigDTO, value: string | number) => {
        setConnectionConfig(prevState => ({
            ...prevState,
            [field]: value
        }));
    };

    const handleConfirm = () => {
        let retMessage = 'Call to update the Connection Configuration has failed!';
        if (onConfirm != undefined  && connectionConfig != undefined ) {
            const updateConnection = async () => {
                try {
                    await configController.updateConnectionConfig(connectionConfig);
                    retMessage = 'Successfully updated the Connection Configuration.';
                } catch (error) {
                    console.error('Error saving the Connection Configuration.', error);
                }
                onConfirm(retMessage);
            };
            updateConnection();
        }
    };

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onClose}
            contentLabel="Connection"
            className="connection-modal"
            overlayClassName="connection-modal-overlay"
        >
            <h2>Connection</h2>
            <div className="input-group">
                <label htmlFor="host">Host:</label>
                <input
                    id="host"
                    type="text"
                    value={connectionConfig.host}
                    onChange={(e) => handleInputChange('host', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="port">Port:</label>
                <input
                    id="port"
                    type="text"
                    value={connectionConfig.port}
                    onChange={(e) => handleInputChange('port', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="cert_password">Cert Password:</label>
                <input
                    id="cert_password"
                    type="password"
                    value={connectionConfig.cert_password}
                    onChange={(e) => handleInputChange('cert_password', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="truststore_location">Truststore Location:</label>
                <input
                    id="truststore_location"
                    type="text"
                    value={connectionConfig.truststore_location}
                    onChange={(e) => handleInputChange('truststore_location', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="keystore_location">Keystore Location:</label>
                <input
                    id="keystore_location"
                    type="text"
                    value={connectionConfig.keystore_location}
                    onChange={(e) => handleInputChange('keystore_location', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="schemaRegistryHost">Schema Registry Host:</label>
                <input
                    id="schemaRegistryHost"
                    type="text"
                    value={connectionConfig.schemaRegistryHost}
                    onChange={(e) => handleInputChange('schemaRegistryHost', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="schemaRegistryPort">Schema Registry Port:</label>
                <input
                    id="schemaRegistryPort"
                    type="text"
                    value={connectionConfig.schemaRegistryPort}
                    onChange={(e) => handleInputChange('schemaRegistryPort', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="schemaRegistryUser">Schema Registry User:</label>
                <input
                    id="schemaRegistryUser"
                    type="text"
                    value={connectionConfig.schemaRegistryUser}
                    onChange={(e) => handleInputChange('schemaRegistryUser', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="schemaRegistryPassword">Schema Registry Password:</label>
                <input
                    id="schemaRegistryPassword"
                    type="password"
                    value={connectionConfig.schemaRegistryPassword}
                    onChange={(e) => handleInputChange('schemaRegistryPassword', e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="lingerMs">Linger Ms:</label>
                <input
                    id="lingerMs"
                    type="number"
                    value={connectionConfig.lingerMs}
                    onChange={(e) => handleInputChange('lingerMs', Number(e.target.value))}
                />
            </div>
            <div className="input-group">
                <label htmlFor="batchSize">Batch Size:</label>
                <input
                    id="batchSize"
                    type="number"
                    value={connectionConfig.batchSize}
                    onChange={(e) => handleInputChange('batchSize', Number(e.target.value))}
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
            <div className="confirmation-buttons">
                <button onClick={handleConfirm}>OK</button>
                <button onClick={onClose}>Cancel</button>
            </div>
        </Modal>
    );
};

export default ConnectionDialog;
