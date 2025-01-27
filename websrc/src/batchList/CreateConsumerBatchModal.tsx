import React from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Col, Row} from "react-bootstrap";

export interface CreateConsumerBatchModalProps {
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
    maxTries: number;
    setMaxTries: React.Dispatch<React.SetStateAction<number>>;
    sleepMillis: number;
    setSleepMillis: React.Dispatch<React.SetStateAction<number>>;
}

const CreateConsumerBatchModal: React.FC<CreateConsumerBatchModalProps> = (createConsumerBatchModalProps: CreateConsumerBatchModalProps) => {
    return (
        <Modal show={createConsumerBatchModalProps.isOpen} onHide={createConsumerBatchModalProps.onRequestClose}
               scrollable={true}>
            <Modal.Header closeButton>
                <Modal.Title>Create Consumer Batch</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group as={Row} controlId="formTopicName">
                        <Form.Label column sm="6">Topic Name</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                as="select"
                                value={createConsumerBatchModalProps.topicName}
                                onChange={(e) => createConsumerBatchModalProps.setTopicName(e.target.value)}
                            >
                                {createConsumerBatchModalProps.topicTypes.map((type, index) => (
                                    <option key={index} value={type}>
                                        {type}
                                    </option>
                                ))}
                            </Form.Control>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formNumThreads">
                        <Form.Label column sm="6">Threads</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createConsumerBatchModalProps.numThreads}
                                onChange={(e) => createConsumerBatchModalProps.setNumThreads(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formBatchSize">
                        <Form.Label column sm="6">Batch Size</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createConsumerBatchModalProps.batchSize}
                                onChange={(e) => createConsumerBatchModalProps.setBatchSize(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formMaxTries">
                        <Form.Label column sm="6">Max Tries</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createConsumerBatchModalProps.maxTries}
                                onChange={(e) => createConsumerBatchModalProps.setMaxTries(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formSleepMillis">
                        <Form.Label column sm="6">Sleep Milliseconds</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={createConsumerBatchModalProps.sleepMillis}
                                onChange={(e) => createConsumerBatchModalProps.setSleepMillis(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={createConsumerBatchModalProps.onRequestClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={createConsumerBatchModalProps.onCreate}>
                    Create
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default CreateConsumerBatchModal;
