import React, {useEffect, useRef, useState} from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import {BatchStatus} from "../api";
import Form from "react-bootstrap/Form";
import {Col, Row} from "react-bootstrap";

interface UpdateBatchModalProps {
    isOpen: boolean;
    onRequestClose: () => void;
    selectedBatch: BatchStatus;
    onUpdate: (numThreads: number) => void;
}

const UpdateBatchModal: React.FC<UpdateBatchModalProps> = ({
                                                               isOpen,
                                                               onRequestClose,
                                                               selectedBatch,
                                                               onUpdate,
                                                           }) => {
    const [tempNumThreads, setTempNumThreads] = useState(0);
    const inputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (isOpen && inputRef.current) {
            inputRef.current.focus();
        }
        if (selectedBatch.RunningJobs !== undefined) {
            setTempNumThreads(selectedBatch.RunningJobs);
        }
    }, [isOpen, selectedBatch.RunningJobs]);

    const handleConfirmUpdate = () => {
        onUpdate(tempNumThreads);
    };

    return (
        <Modal show={isOpen} onHide={onRequestClose}>
            <Modal.Header closeButton>
                <Modal.Title>Update Batch</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group as={Row} controlId="formBatchName">
                        <Form.Label column sm="6">
                            Batch Name:
                        </Form.Label>
                        <Col sm="6">
                            <Form.Control
                                plaintext
                                readOnly
                                defaultValue={selectedBatch.BatchName}
                            />
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formCurrentThreads">
                        <Form.Label column sm="6">
                            Current Threads:
                        </Form.Label>
                        <Col sm="6">
                            <Form.Control
                                plaintext
                                readOnly
                                defaultValue={selectedBatch.RunningJobs}
                            />
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formNewThreads">
                        <Form.Label column sm="6">
                            New Threads:
                        </Form.Label>
                        <Col sm="6">
                            <Form.Control
                                type="number"
                                value={tempNumThreads}
                                onChange={(e) => setTempNumThreads(Number(e.target.value))}
                                ref={inputRef}
                            />
                        </Col>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onRequestClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={handleConfirmUpdate}>
                    Update
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default UpdateBatchModal;
