import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {KafkaConnectionConfigDTO} from "../../api";

export interface ProducerTabProps {
    connectionConfig: KafkaConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
    compressionTypes: string[];
    selectedCompressionType: string;
    setSelectedCompressionType: (value: string) => void;
    ackTypes: { key: string, value: string }[];
    selectedAckType: string;
    setSelectedAckType: (value: string) => void;
}

const ProducerTab: React.FC<ProducerTabProps> = (producerTabProps: ProducerTabProps) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="producerLingerMs">Producer Linger Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="producerLingerMs"
                        type="number"
                        value={producerTabProps.connectionConfig.producerLingerMs || ''}
                        onChange={(e) => producerTabProps.handleInputChange('producerLingerMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="producerBatchSize">Producer Batch Size:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="producerBatchSize"
                        type="number"
                        value={producerTabProps.connectionConfig.producerBatchSize || ''}
                        onChange={(e) => producerTabProps.handleInputChange('producerBatchSize', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="compressionType">Compression Type:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        as="select"
                        id="compressionType"
                        value={producerTabProps.selectedCompressionType}
                        onChange={(e) => {
                            producerTabProps.handleInputChange('compressionType', e.target.value);
                            producerTabProps.setSelectedCompressionType(e.target.value);
                        }}
                    >
                        {producerTabProps.compressionTypes.map((type, index) => (
                            <option key={index} value={type}>
                                {type}
                            </option>
                        ))}
                    </Form.Control>
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="bufferMemory">Buffer Memory:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="bufferMemory"
                        type="number"
                        value={producerTabProps.connectionConfig.bufferMemory || ''}
                        onChange={(e) => producerTabProps.handleInputChange('bufferMemory', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="idempotenceEnabled">Idempotence Enabled:</Form.Label>
                <Col sm="6" className="d-flex align-items-center">
                    <Form.Check
                        type="checkbox"
                        id="idempotenceEnabled"
                        checked={producerTabProps.connectionConfig.idempotenceEnabled || false}
                        onChange={(e) => producerTabProps.handleInputChange('idempotenceEnabled', e.target.checked)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="acks">ACKs:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        as="select"
                        id="acks"
                        value={producerTabProps.selectedAckType}
                        onChange={(e) => {
                            producerTabProps.handleInputChange('acks', e.target.value);
                            producerTabProps.setSelectedAckType(e.target.value);
                        }}
                    >
                        {producerTabProps.ackTypes.map((option) => (
                            <option key={option.key} value={option.key}>
                                {option.key}
                            </option>
                        ))}
                    </Form.Control>
                </Col>
            </Form.Group>
        </>
    );
};

export default ProducerTab;
