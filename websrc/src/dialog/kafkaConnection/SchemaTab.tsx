import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {KafkaConnectionConfigDTO} from "../../api";

export interface SchemaTabProps {
    connectionConfig: KafkaConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
}

const SchemaTab: React.FC<SchemaTabProps> = (schemaTabProps: SchemaTabProps) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryHost">Schema Registry Host:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryHost"
                        type="text"
                        value={schemaTabProps.connectionConfig.schemaRegistryHost || ''}
                        onChange={(e) => schemaTabProps.handleInputChange('schemaRegistryHost', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryPort">Schema Registry Port:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryPort"
                        type="text"
                        value={schemaTabProps.connectionConfig.schemaRegistryPort || ''}
                        onChange={(e) => schemaTabProps.handleInputChange('schemaRegistryPort', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryUser">Schema Registry User:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryUser"
                        type="text"
                        value={schemaTabProps.connectionConfig.schemaRegistryUser || ''}
                        onChange={(e) => schemaTabProps.handleInputChange('schemaRegistryUser', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="schemaRegistryPassword">Schema Registry Password:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="schemaRegistryPassword"
                        type="password"
                        value={schemaTabProps.connectionConfig.schemaRegistryPassword || ''}
                        onChange={(e) => schemaTabProps.handleInputChange('schemaRegistryPassword', e.target.value)}
                    />
                </Col>
            </Form.Group>
        </>
    );
};

export default SchemaTab;
