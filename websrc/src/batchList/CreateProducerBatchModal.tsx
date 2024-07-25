import React from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Col, Row} from "react-bootstrap";

interface CreateProducerBatchModalProps {
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

const CreateProducerBatchModal: React.FC<CreateProducerBatchModalProps> = ({
                                                                               isOpen,
                                                                               onRequestClose,
                                                                               onCreate,
                                                                               topicTypes,
                                                                               topicName,
                                                                               setTopicName,
                                                                               numThreads,
                                                                               setNumThreads,
                                                                               batchSize,
                                                                               setBatchSize,
                                                                               startId,
                                                                               setStartID,
                                                                               correlatedStartIdInc,
                                                                               setCorrelatedStartIdInc,
                                                                               correlatedEndIdInc,
                                                                               setCorrelatedEndIdInc,
                                                                               sleepMillis,
                                                                               setSleepMillis,
                                                                           }) => {
    return (
        <Modal show={isOpen} onHide={onRequestClose} scrollable={true}>
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
                                value={topicName}
                                onChange={(e) => setTopicName(e.target.value)}
                            >
                                {topicTypes.map((topic, index) => (
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
                                value={numThreads}
                                onChange={(e) => setNumThreads(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formBatchSize">
                        <Form.Label column sm="6">Batch Size</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={batchSize}
                                onChange={(e) => setBatchSize(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formStartId">
                        <Form.Label column sm="6">Start ID</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={startId}
                                onChange={(e) => setStartID(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formCorrelatedStartIdInc">
                        <Form.Label column sm="6">Correlated Start ID Increment</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={correlatedStartIdInc}
                                onChange={(e) => setCorrelatedStartIdInc(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formCorrelatedEndIdInc">
                        <Form.Label column sm="6">Correlated End ID Increment</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={correlatedEndIdInc}
                                onChange={(e) => setCorrelatedEndIdInc(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row} controlId="formSleepMillis">
                        <Form.Label column sm="6">Sleep Milliseconds</Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={sleepMillis}
                                onChange={(e) => setSleepMillis(Number(e.target.value))}
                            />
                        </Col>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onRequestClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={onCreate}>
                    Create
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default CreateProducerBatchModal;
