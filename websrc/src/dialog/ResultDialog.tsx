import React from 'react';
import {Button, Modal} from 'react-bootstrap';

type ResultDialogProps = {
    isOpen: boolean;
    message: string;
    onClose: () => void;
};

const ResultDialog: React.FC<ResultDialogProps> = ({isOpen, message, onClose}) => {
    return (
        <Modal show={isOpen} onHide={onClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Result</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{message}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onClose}>
                    OK
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ResultDialog;
