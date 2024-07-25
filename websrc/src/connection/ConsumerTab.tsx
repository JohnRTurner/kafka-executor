import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {KafkaConnectionConfigDTO} from "../api";


interface ConsumerTabProps {
    connectionConfig: KafkaConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
}

const ConsumerTab: React.FC<ConsumerTabProps> = ({connectionConfig, handleInputChange}) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="maxPollRecords">Max Poll Records:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="maxPollRecords"
                        type="number"
                        value={connectionConfig.maxPollRecords || ''}
                        onChange={(e) => handleInputChange('maxPollRecords', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="fetchMinByes">Fetch Min Bytes:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="fetchMinByes"
                        type="number"
                        value={connectionConfig.fetchMinByes || ''}
                        onChange={(e) => handleInputChange('fetchMinByes', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="fetchMaxWaitMS">Fetch Max Wait MS:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="fetchMaxWaitMS"
                        type="number"
                        value={connectionConfig.fetchMaxWaitMS || ''}
                        onChange={(e) => handleInputChange('fetchMaxWaitMS', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="sessionTimeoutMs">Session Timeout Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="sessionTimeoutMs"
                        type="number"
                        value={connectionConfig.sessionTimeoutMs || ''}
                        onChange={(e) => handleInputChange('sessionTimeoutMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="heartbeatTimeoutMs">Heartbeat Timeout Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="heartbeatTimeoutMs"
                        type="number"
                        value={connectionConfig.heartbeatTimeoutMs || ''}
                        onChange={(e) => handleInputChange('heartbeatTimeoutMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="autoCommitIntervalMs">Auto Commit Interval Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="autoCommitIntervalMs"
                        type="number"
                        value={connectionConfig.autoCommitIntervalMs || ''}
                        onChange={(e) => handleInputChange('autoCommitIntervalMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
        </>
    );
};

export default ConsumerTab;
