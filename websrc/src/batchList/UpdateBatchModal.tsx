import React, {useEffect, useRef, useState} from 'react';
import Modal from 'react-modal';
import {BatchStatus} from '../api';
import './BatchList.css';

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
                                                               onUpdate
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
        <Modal
            isOpen={isOpen}
            onRequestClose={onRequestClose}
            contentLabel="Update Batch Modal"
            className="modal"
            overlayClassName="overlay"
        >
            <h2>Update Batch</h2>
            <p>Batch Name: {selectedBatch.BatchName}</p>
            <p>Current Threads: {selectedBatch.RunningJobs}</p>
            <label>
                New Threads:
                <input
                    type="number"
                    value={tempNumThreads}
                    onChange={(e) => setTempNumThreads(Number(e.target.value))}
                    ref={inputRef} // Ref to focus on this input
                />
            </label>
            <button onClick={handleConfirmUpdate}>Update</button>
            <button onClick={onRequestClose}>Cancel</button>
        </Modal>
    );
};

export default UpdateBatchModal;
