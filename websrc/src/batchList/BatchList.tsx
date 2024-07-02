// BatchList.tsx
import React, { useEffect, useState, useRef } from 'react';
import Modal from 'react-modal';
import { BatchControllerApi, BatchStatus } from '../api';
import BatchItem from './BatchItem';
import apiConfig from '../apiConfig'; // Adjust the path based on your project structure

const batchController = new BatchControllerApi(apiConfig);

Modal.setAppElement('#root'); // Required for accessibility

const BatchList: React.FC = () => {
    const [batchStatus, setBatchStatus] = useState<BatchStatus[] | null>(null);
    const [selectedBatch, setSelectedBatch] = useState<BatchStatus | null>(null);
    const messageRef = useRef<HTMLParagraphElement>(null);
    const errorRef = useRef<HTMLParagraphElement>(null);

    const fetchBatchStatus = () => {
        batchController.getBatchStatuses()
            .then(response => {
                const newBatchStatus = response.data;
                setBatchStatus(newBatchStatus);
            })
            .catch(error => {
                if (errorRef.current) errorRef.current.textContent = 'Error fetching batch status: ' + error.message;
                if (messageRef.current) messageRef.current.textContent = '';
                setBatchStatus([]); // Set batchStatus to empty array on error or no data
            });
    };


    useEffect(() => {
        fetchBatchStatus(); // Initial fetch

        const interval = setInterval(() => {
            fetchBatchStatus();
        }, 10000); // 10000ms = 10 seconds

        return () => clearInterval(interval); // Cleanup on component unmount
    }, []); // Empty dependency array ensures this effect runs only once

    const handleEdit = (batchStatus: BatchStatus) => {
        setSelectedBatch(batchStatus);
    };

    const handleClose = () => {
        setSelectedBatch(null);
    };

    const handleUpdate = () => {
        // Add your update logic here
        console.log('Updating:', selectedBatch);
        handleClose();
    };

    const handleDelete = () => {
        // Add your delete logic here
        console.log('Deleting:', selectedBatch);
        handleClose();
    };

    if (!batchController) {
        return <div>BatchController not found!</div>;
    }

    return (
        <div>
            <h1>Batch Status</h1>
            {batchStatus === null && <p>Loading...</p>}
            {batchStatus && batchStatus.length === 0 && <p>No batch statuses available.</p>}
            {batchStatus && batchStatus.length > 0 && (
                <ul>
                    {batchStatus.map((item, index) => (
                        <BatchItem key={index} batchStatus={item} onEdit={handleEdit} />
                    ))}
                </ul>
            )}
            <p ref={errorRef} style={{ color: 'red' }}></p>
            <p ref={messageRef} style={{ color: 'green' }}></p>
            {selectedBatch && (
                <Modal
                    isOpen={!!selectedBatch}
                    onRequestClose={handleClose}
                    contentLabel="Update/Delete Batch"
                    className="modal"
                    overlayClassName="overlay"
                >
                    <h2>Update/Delete {selectedBatch.BatchName}</h2>
                    <p>Type: {selectedBatch.BatchType}</p>
                    <p>Current Date and Time: {selectedBatch.CurrentDateTime}</p>
                    <p>Running Jobs: {selectedBatch.RunningJobs}</p>
                    <button onClick={handleUpdate}>Update</button>
                    <button onClick={handleDelete}>Delete</button>
                    <button onClick={handleClose}>Close</button>
                </Modal>
            )}
        </div>
    );
};

export default BatchList;
