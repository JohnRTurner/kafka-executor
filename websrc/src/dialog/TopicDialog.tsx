import React, {useState} from 'react';
import Modal from 'react-modal';
import './Modal.css';


type TopicDialogProps = {
    isOpen: boolean;
    message: string|null;
    onClose: () => void;
    onConfirm: ((numberOfPartitions:number, replication:number) => void)|undefined;
};

const TopicDialog: React.FC<TopicDialogProps> = ({ isOpen, message, onClose, onConfirm }) => {
    const [numberOfPartitions, setNumberOfPartitions] = useState<number>(6);
    const [replication, setReplication] = useState<number>(2);

    const handleConfirm = () => {
        if(onConfirm != undefined && numberOfPartitions != undefined && replication != undefined) {
            onConfirm(numberOfPartitions, replication);
        }
    };
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
            <h2>Enter Number of Partitions</h2>
            <input
                type="number"
                value={numberOfPartitions}
                onChange={(e) => setNumberOfPartitions(Number(e.target.value))}
                min="1"
            />
            <h2>Replication Facto</h2>
            <input
                type="number"
                value={replication}
                onChange={(e) => setReplication(Number(e.target.value))}
                min="1"
            />

            <div className="confirmation-buttons">
                <button onClick={handleConfirm}>OK</button>
                <button onClick={onClose}>Cancel</button>
            </div>
        </Modal>
    );
};

export default TopicDialog;
