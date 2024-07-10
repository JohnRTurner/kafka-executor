import React from 'react';
import Modal from 'react-modal';
import './BatchList.css'

interface ConfirmDeleteModalProps {
    isOpen: boolean;
    onRequestClose: () => void;
    onDelete: () => void;
}

const ConfirmDeleteModal: React.FC<ConfirmDeleteModalProps> = ({
                                                                   isOpen,
                                                                   onRequestClose,
                                                                   onDelete
                                                               }) => {
    return (
        <Modal isOpen={isOpen}
               onRequestClose={onRequestClose}
               contentLabel="Confirm Delete Modal"
               className="modal"
               overlayClassName="overlay"
        >
            <h2>Confirm Delete</h2>
            <p>Are you sure you want to delete this batch?</p>
            <button onClick={onDelete}>Delete</button>
            <button onClick={onRequestClose}>Cancel</button>
        </Modal>
    );
};

export default ConfirmDeleteModal;
