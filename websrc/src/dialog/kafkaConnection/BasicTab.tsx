import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {KafkaConnectionConfigDTO} from "../../api";

export interface BasicTabProps {
    connectionConfig: KafkaConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
}

const BasicTab: React.FC<BasicTabProps> = (basicTabProps: BasicTabProps) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="host">Host:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="host"
                        type="text"
                        value={basicTabProps.connectionConfig.host || ''}
                        onChange={(e) => basicTabProps.handleInputChange('host', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="port">Port:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="port"
                        type="text"
                        value={basicTabProps.connectionConfig.port || ''}
                        onChange={(e) => basicTabProps.handleInputChange('port', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="cert_password">Cert Password:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="cert_password"
                        type="password"
                        value={basicTabProps.connectionConfig.cert_password || ''}
                        onChange={(e) => basicTabProps.handleInputChange('cert_password', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="truststore_location">Truststore Location:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="truststore_location"
                        type="text"
                        value={basicTabProps.connectionConfig.truststore_location || ''}
                        onChange={(e) => basicTabProps.handleInputChange('truststore_location', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="keystore_location">Keystore Location:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="keystore_location"
                        type="text"
                        value={basicTabProps.connectionConfig.keystore_location || ''}
                        onChange={(e) => basicTabProps.handleInputChange('keystore_location', e.target.value)}
                    />
                </Col>
            </Form.Group>
        </>
    );
};

export default BasicTab;
