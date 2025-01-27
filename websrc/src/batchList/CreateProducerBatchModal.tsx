import React from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Col, Row} from "react-bootstrap";

export interface CreateProducerBatchModalProps {
    isOpen: boolean;
    onRequestClose: () => void;
    onCreate: () => void;
    topicTypes: string[];
    topicName: string;
    setTopicName: React.Dispatch<React.SetStateAction<string>>;
    numThreads: number;
    setNumThreads: React.Dispatch<React.SetStateAction<number>>;
    batchSize: number;
    setBatchSize: React.Dispatch<React.SetStateAction<number>>;
    startId: number;
    setStartID: React.Dispatch<React.SetStateAction<number>>;
    correlatedStartIdInc: number;
    setCorrelatedStartIdInc: React.Dispatch<React.SetStateAction<number>>;
    correlatedEndIdInc: number;
    setCorrelatedEndIdInc: React.Dispatch<React.SetStateAction<number>>;
    sleepMillis: number;
    setSleepMillis: React.Dispatch<React.SetStateAction<number>>;
}

const CreateProducerBatchModal: React.FC<CreateProducerBatchModalProps> = (createProducerBatchModalProps: CreateProducerBatchModalProps) => {
    return (
        <Modal show={createProducerBatchModalProps.isOpen} onHide={createProducerBatchModalProps.onRequestClose}
               scrollable={true}>
            <Modal.Header closeButton>
                <Modal.Title>Create New Producer Batch</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group as={Row} controlId="formTopicName">
                        <Form.Label column sm="6">Topic Name</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                as="select"
                                value={createProducerBatchModalProps.topicName}
                                onChange={(e) => createProducerBatchModalProps.setTopicName(e.target.value)}
                            >
                                {createProducerBatchModalProps.topicTypes.map((topic, index) => (
                                    <option key={index} value={topic}>
                                        {topic}
                                    </option>
                                ))}
                            </Form.Control>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formNumThreads">
                        <Form.Label column sm="6">Number of Threads</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createProducerBatchModalProps.numThreads}
                                onChange={(e) => createProducerBatchModalProps.setNumThreads(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formBatchSize">
                        <Form.Label column sm="6">Batch Size</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createProducerBatchModalProps.batchSize}
                                onChange={(e) => createProducerBatchModalProps.setBatchSize(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formStartId">
                        <Form.Label column sm="6">Start ID</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createProducerBatchModalProps.startId}
                                onChange={(e) => createProducerBatchModalProps.setStartID(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formCorrelatedStartIdInc">
                        <Form.Label column sm="6">Correlated Start ID Increment</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createProducerBatchModalProps.correlatedStartIdInc}
                                onChange={(e) => createProducerBatchModalProps.setCorrelatedStartIdInc(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formCorrelatedEndIdInc">
                        <Form.Label column sm="6">Correlated End ID Increment</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createProducerBatchModalProps.correlatedEndIdInc}
                                onChange={(e) => createProducerBatchModalProps.setCorrelatedEndIdInc(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formSleepMillis">
                        <Form.Label column sm="6">Sleep Milliseconds</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createProducerBatchModalProps.sleepMillis}
                                onChange={(e) => createProducerBatchModalProps.setSleepMillis(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={createProducerBatchModalProps.onRequestClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={createProducerBatchModalProps.onCreate}>
                    Create
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default CreateProducerBatchModal;
