import React, {useState} from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import Form from "react-bootstrap/Form";
import {Col, Row} from "react-bootstrap";

export type TopicDialogProps = {
    isOpen: boolean;
    message: string | null;
    onClose: () => void;
    onConfirm: ((numberOfPartitions: number, replication: number) => void) | undefined;
};

const TopicDialog: React.FC<TopicDialogProps> = (topicDialogProps: TopicDialogProps) => {
    const [numberOfPartitions, setNumberOfPartitions] = useState<number>(6);
    const [replication, setReplication] = useState<number>(2);

    const handleConfirm = () => {
        if (topicDialogProps.onConfirm && numberOfPartitions && replication) {
            topicDialogProps.onConfirm(numberOfPartitions, replication);
        }
    };

    return (
        <Modal show={topicDialogProps.isOpen} onHide={topicDialogProps.onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Confirmation</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{topicDialogProps.message}</p>
                <Form>
                    <Form.Group as={Row} controlId="formNumberOfPartitions">
                        <Form.Label column sm="8">
                            Enter Number of Partitions
                        </Form.Label>
                        <Col sm="4">
                            <Form.Control
                                type="number"
                                value={numberOfPartitions}
                                onChange={(e) => setNumberOfPartitions(Number(e.target.value))}
                                min="1"
                                placeholder="Number of Partitions"
                            />
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formReplicationFactor">
                        <Form.Label column sm="8">
                            Replication Factor
                        </Form.Label>
                        <Col sm="4">
                            <Form.Control
                                type="number"
                                value={replication}
                                onChange={(e) => setReplication(Number(e.target.value))}
                                min="1"
                                placeholder="Replication Factor"
                            />
                        </Col>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={topicDialogProps.onClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={handleConfirm}>
                    OK
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default TopicDialog;
