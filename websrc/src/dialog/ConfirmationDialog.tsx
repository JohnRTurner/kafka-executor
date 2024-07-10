import React from 'react';
import Modal from 'react-modal';
import './Modal.css';

type ConfirmationDialogProps = {
    isOpen: boolean;
    message: string|null;
    onClose: () => void;
    onConfirm: (() => void)|undefined;
};

const ConfirmationDialog: React.FC<ConfirmationDialogProps> = ({ isOpen, message, onClose, onConfirm }) => {

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onClose}
            contentLabel="Confirmation"
            className="confirmation-modal"
            overlayClassName="confirmation-modal-overlay"
        >
            <h2>Confirmation</h2>
            <p>{message}</p>
            <div className="confirmation-buttons">
                <button onClick={onConfirm}>OK</button>
                <button onClick={onClose}>Cancel</button>
            </div>
        </Modal>
    );
};

export default ConfirmationDialog;
