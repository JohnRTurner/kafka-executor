import React, {useEffect, useRef, useState} from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import {BatchStatus} from "../api";
import Form from "react-bootstrap/Form";
import {Col, Row} from "react-bootstrap";

export interface UpdateBatchModalProps {
    isOpen: boolean;
    onRequestClose: () => void;
    selectedBatch: BatchStatus;
    onUpdate: (numThreads: number) => void;
}

const UpdateBatchModal: React.FC<UpdateBatchModalProps> = (updateBatchModalProps: UpdateBatchModalProps) => {
    const [tempNumThreads, setTempNumThreads] = useState(0);
    const inputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (updateBatchModalProps.isOpen && inputRef.current) {
            inputRef.current.focus();
        }
        if (updateBatchModalProps.selectedBatch.RunningJobs !== undefined) {
            setTempNumThreads(updateBatchModalProps.selectedBatch.RunningJobs);
        }
    }, [updateBatchModalProps.isOpen, updateBatchModalProps.selectedBatch.RunningJobs]);

    const handleConfirmUpdate = () => {
        updateBatchModalProps.onUpdate(tempNumThreads);
    };

    return (
        <Modal show={updateBatchModalProps.isOpen} onHide={updateBatchModalProps.onRequestClose}>
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
                                defaultValue={updateBatchModalProps.selectedBatch.BatchName}
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
                                defaultValue={updateBatchModalProps.selectedBatch.RunningJobs}
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
                <Button variant="secondary" onClick={updateBatchModalProps.onRequestClose}>
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
