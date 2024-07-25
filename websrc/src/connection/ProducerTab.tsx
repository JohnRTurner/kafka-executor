import React from 'react';
import {Col, Form, Row} from 'react-bootstrap';
import {ConnectionConfigDTO} from "../api";

interface ProducerTabProps {
    connectionConfig: ConnectionConfigDTO;
    handleInputChange: (field: string, value: string | number | boolean) => void;
    compressionTypes: string[];
    selectedCompressionType: string;
    setSelectedCompressionType: (value: string) => void;
    ackTypes: { key: string, value: string }[];
    selectedAckType: string;
    setSelectedAckType: (value: string) => void;
}

const ProducerTab: React.FC<ProducerTabProps> = ({
                                                     connectionConfig,
                                                     handleInputChange,
                                                     compressionTypes,
                                                     selectedCompressionType,
                                                     setSelectedCompressionType,
                                                     ackTypes,
                                                     selectedAckType,
                                                     setSelectedAckType
                                                 }) => {
    return (
        <>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="producerLingerMs">Producer Linger Ms:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="producerLingerMs"
                        type="number"
                        value={connectionConfig.producerLingerMs || ''}
                        onChange={(e) => handleInputChange('producerLingerMs', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="producerBatchSize">Producer Batch Size:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="producerBatchSize"
                        type="number"
                        value={connectionConfig.producerBatchSize || ''}
                        onChange={(e) => handleInputChange('producerBatchSize', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="compressionType">Compression Type:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        as="select"
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
                    </Form.Control>
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="bufferMemory">Buffer Memory:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        id="bufferMemory"
                        type="number"
                        value={connectionConfig.bufferMemory || ''}
                        onChange={(e) => handleInputChange('bufferMemory', Number(e.target.value))}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="idempotenceEnabled">Idempotence Enabled:</Form.Label>
                <Col sm="6" className="d-flex align-items-center">
                    <Form.Check
                        type="checkbox"
                        id="idempotenceEnabled"
                        checked={connectionConfig.idempotenceEnabled || false}
                        onChange={(e) => handleInputChange('idempotenceEnabled', e.target.checked)}
                    />
                </Col>
            </Form.Group>
            <Form.Group as={Row}>
                <Form.Label column sm="6" htmlFor="acks">ACKs:</Form.Label>
                <Col sm="6">
                    <Form.Control
                        as="select"
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
                    </Form.Control>
                </Col>
            </Form.Group>
        </>
    );
};

export default ProducerTab;
