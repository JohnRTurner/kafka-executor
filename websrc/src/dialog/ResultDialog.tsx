import React from 'react';
import Modal from 'react-modal';
import './Modal.css';

type ResultDialogProps = {
    isOpen: boolean;
    message: string;
    onClose: () => void;
};

const ResultDialog: React.FC<ResultDialogProps> = ({ isOpen, message, onClose }) => {
    return (
        <Modal
            isOpen={isOpen}
            contentLabel="Result"
            className="result-modal"
            overlayClassName="result-modal-overlay"
        >
            <div className="result-dialog">
                <p>{message}</p>
                <button onClick={onClose}>OK</button>
            </div>
        </Modal>
    );
};

export default ResultDialog;
