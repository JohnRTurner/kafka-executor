import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {ConnectionConfigDTO} from "../api";

interface SchemaTabProps {
    connectionConfig: ConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
}

const SchemaTab: React.FC<SchemaTabProps> = ({connectionConfig, handleInputChange}) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryHost">Schema Registry Host:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryHost"
                        type="text"
                        value={connectionConfig.schemaRegistryHost || ''}
                        onChange={(e) => handleInputChange('schemaRegistryHost', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryPort">Schema Registry Port:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryPort"
                        type="text"
                        value={connectionConfig.schemaRegistryPort || ''}
                        onChange={(e) => handleInputChange('schemaRegistryPort', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryUser">Schema Registry User:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryUser"
                        type="text"
                        value={connectionConfig.schemaRegistryUser || ''}
                        onChange={(e) => handleInputChange('schemaRegistryUser', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryPassword">Schema Registry Password:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryPassword"
                        type="password"
                        value={connectionConfig.schemaRegistryPassword || ''}
                        onChange={(e) => handleInputChange('schemaRegistryPassword', e.target.value)}
                    />
                </Col>
            </Form.Group>
        </>
    );
};

export default SchemaTab;
