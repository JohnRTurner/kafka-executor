import React, {useState} from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import Form from "react-bootstrap/Form";
import {Col, Row} from "react-bootstrap";

export type IndexDialogProps = {
    isOpen: boolean;
    message: string | null;
    onClose: () => void;
    onConfirm: ((numberOfIndexes: number, replication: number, refreshSeconds: number) => void) | undefined;
};

const IndexDialog: React.FC<IndexDialogProps> = (indexDialogProps: IndexDialogProps) => {
    const [numberOfIndexes, setNumberOfIndexes] = useState<number>(6);
    const [replication, setReplication] = useState<number>(2);
    const [refreshSeconds, setRefreshSeconds] = useState<number>(1);

    const handleConfirm = () => {
        if (indexDialogProps.onConfirm && numberOfIndexes && replication && refreshSeconds) {
            indexDialogProps.onConfirm(numberOfIndexes, replication, refreshSeconds);
        }
    };

    return (
        <Modal show={indexDialogProps.isOpen} onHide={indexDialogProps.onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Confirmation</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{indexDialogProps.message}</p>
                <Form>
                    <Form.Group as={Row} controlId="formNumberOfPartitions">
                        <Form.Label column sm="8">
                            Enter Number of Indexes
                        </Form.Label>
                        <Col sm="4">
                            <Form.Control
                                type="number"
                                value={numberOfIndexes}
                                onChange={(e) => setNumberOfIndexes(Number(e.target.value))}
                                min="1"
                                placeholder="Number of Indexes"
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

                    <Form.Group as={Row} controlId="formRefreshSeconds">
                        <Form.Label column sm="8">
                            Refresh Seconds
                        </Form.Label>
                        <Col sm="4">
                            <Form.Control
                                type="number"
                                value={refreshSeconds}
                                onChange={(e) => setRefreshSeconds(Number(e.target.value))}
                                min="1"
                                placeholder="Refresh Seconds"
                            />
                        </Col>
                    </Form.Group>

                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={indexDialogProps.onClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={handleConfirm}>
                    OK
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default IndexDialog;
