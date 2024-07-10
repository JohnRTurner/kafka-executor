import React from 'react';
import { BatchStatus } from '../api';
import './BatchList.css';

interface BatchItemProps {
    batchStatus: BatchStatus;
    onEdit: (batchStatus: BatchStatus) => void;
    onDelete: (batchStatus: BatchStatus) => void;
}

const BatchItem: React.FC<BatchItemProps> = ({ batchStatus, onEdit, onDelete }) => {
    return (
        <li>
            <div className="batch-status-container">
                <div className="batch-button-container">
                    <button className="batch-button" onClick={() => onEdit(batchStatus)}>Modify</button>
                    <button className="batch-button" onClick={() => onDelete(batchStatus)}>Delete</button>
                </div>
                <div className="batch-details-container">
                    <div className="label-container">
                        <b className="label">Batch Name:</b> {batchStatus.BatchName}
                    </div>
                    <div className="label-container">
                        <b className="label">Type:</b> {batchStatus.BatchType}
                    </div>
                    <div className="label-container">
                        <b className="label">Current Date and Time:</b> {batchStatus.CurrentDateTime}
                    </div>
                    <div className="label-container">
                        <b className="label">Jobs:</b> {batchStatus.RunningJobs}
                    </div>
                </div>
            </div>
        </li>
    );
};

export default BatchItem;
