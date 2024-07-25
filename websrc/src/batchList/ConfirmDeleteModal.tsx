import React from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';

interface ConfirmDeleteModalProps {
    isOpen: boolean;
    onRequestClose: () => void;
    onDelete: () => void;
}

const ConfirmDeleteModal: React.FC<ConfirmDeleteModalProps> = ({
                                                                   isOpen,
                                                                   onRequestClose,
                                                                   onDelete,
                                                               }) => {
    return (
        <Modal show={isOpen} onHide={onRequestClose}>
            <Modal.Header closeButton>
                <Modal.Title>Confirm Delete</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>Are you sure you want to delete this batch?</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onRequestClose}>
                    Cancel
                </Button>
                <Button variant="danger" onClick={onDelete}>
                    Delete
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ConfirmDeleteModal;
