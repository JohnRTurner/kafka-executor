import React from 'react';
import {Button, Modal} from 'react-bootstrap';

export type ResultDialogProps = {
    isOpen: boolean;
    message: string;
    onClose: () => void;
};

const ResultDialog: React.FC<ResultDialogProps> = (resultDialogProps: ResultDialogProps) => {
    return (
        <Modal show={resultDialogProps.isOpen} onHide={resultDialogProps.onClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Result</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{resultDialogProps.message}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={resultDialogProps.onClose}>
                    OK
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ResultDialog;
