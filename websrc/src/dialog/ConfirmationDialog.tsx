import React from 'react';
import {Button, Modal} from 'react-bootstrap';

type ConfirmationDialogProps = {
    isOpen: boolean;
    message: string | null;
    onClose: () => void;
    onConfirm: (() => void) | undefined;
};

const ConfirmationDialog: React.FC<ConfirmationDialogProps> = ({isOpen, message, onClose, onConfirm}) => {
    return (
        <Modal show={isOpen} onHide={onClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Confirmation</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{message}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onConfirm}>
                    OK
                </Button>
                <Button variant="secondary" onClick={onClose}>
                    Cancel
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ConfirmationDialog;
