import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {KafkaConnectionConfigDTO} from "../../api";


export interface ConsumerTabProps {
    connectionConfig: KafkaConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
}

const ConsumerTab: React.FC<ConsumerTabProps> = (consumerTabProps: ConsumerTabProps) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="maxPollRecords">Max Poll Records:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="maxPollRecords"
                        type="number"
                        value={consumerTabProps.connectionConfig.maxPollRecords || ''}
                        onChange={(e) => consumerTabProps.handleInputChange('maxPollRecords', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="fetchMinByes">Fetch Min Bytes:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="fetchMinByes"
                        type="number"
                        value={consumerTabProps.connectionConfig.fetchMinByes || ''}
                        onChange={(e) => consumerTabProps.handleInputChange('fetchMinByes', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="fetchMaxWaitMS">Fetch Max Wait MS:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="fetchMaxWaitMS"
                        type="number"
                        value={consumerTabProps.connectionConfig.fetchMaxWaitMS || ''}
                        onChange={(e) => consumerTabProps.handleInputChange('fetchMaxWaitMS', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="sessionTimeoutMs">Session Timeout Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="sessionTimeoutMs"
                        type="number"
                        value={consumerTabProps.connectionConfig.sessionTimeoutMs || ''}
                        onChange={(e) => consumerTabProps.handleInputChange('sessionTimeoutMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="heartbeatTimeoutMs">Heartbeat Timeout Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="heartbeatTimeoutMs"
                        type="number"
                        value={consumerTabProps.connectionConfig.heartbeatTimeoutMs || ''}
                        onChange={(e) => consumerTabProps.handleInputChange('heartbeatTimeoutMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="autoCommitIntervalMs">Auto Commit Interval Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="autoCommitIntervalMs"
                        type="number"
                        value={consumerTabProps.connectionConfig.autoCommitIntervalMs || ''}
                        onChange={(e) => consumerTabProps.handleInputChange('autoCommitIntervalMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
        </>
    );
};

export default ConsumerTab;
