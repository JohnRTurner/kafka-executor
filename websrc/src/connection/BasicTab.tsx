import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {ConnectionConfigDTO} from "../api";

interface BasicTabProps {
    connectionConfig: ConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
}

const BasicTab: React.FC<BasicTabProps> = ({connectionConfig, handleInputChange}) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="host">Host:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="host"
                        type="text"
                        value={connectionConfig.host || ''}
                        onChange={(e) => handleInputChange('host', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="port">Port:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="port"
                        type="text"
                        value={connectionConfig.port || ''}
                        onChange={(e) => handleInputChange('port', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="cert_password">Cert Password:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="cert_password"
                        type="password"
                        value={connectionConfig.cert_password || ''}
                        onChange={(e) => handleInputChange('cert_password', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="truststore_location">Truststore Location:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="truststore_location"
                        type="text"
                        value={connectionConfig.truststore_location || ''}
                        onChange={(e) => handleInputChange('truststore_location', e.target.value)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="keystore_location">Keystore Location:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="keystore_location"
                        type="text"
                        value={connectionConfig.keystore_location || ''}
                        onChange={(e) => handleInputChange('keystore_location', e.target.value)}
                    />
                </Col>
            </Form.Group>
        </>
    );
};

export default BasicTab;
