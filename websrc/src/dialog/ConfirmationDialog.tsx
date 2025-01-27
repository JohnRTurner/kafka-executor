import React from 'react';
import {Button, Modal} from 'react-bootstrap';

export type ConfirmationDialogProps = {
    isOpen: boolean;
    message: string | null;
    onClose: () => void;
    onConfirm: (() => void) | undefined;
};

const ConfirmationDialog: React.FC<ConfirmationDialogProps> = (confirmationDialogProps: ConfirmationDialogProps) => {
    return (
        <Modal show={confirmationDialogProps.isOpen} onHide={confirmationDialogProps.onClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Confirmation</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{confirmationDialogProps.message}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={confirmationDialogProps.onConfirm}>
                    OK
                </Button>
                <Button variant="secondary" onClick={confirmationDialogProps.onClose}>
                    Cancel
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ConfirmationDialog;
