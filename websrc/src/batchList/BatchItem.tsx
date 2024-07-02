// BatchItem.tsx
import React from 'react';
import { BatchStatus } from '../api';

interface BatchStatusProps {
    batchStatus: BatchStatus;
    onEdit: (batchStatus: BatchStatus) => void;
}

const BatchItem: React.FC<BatchStatusProps> = ({ batchStatus, onEdit }) => {
    return (
        <li>
            <h2>{batchStatus.BatchName}</h2>
            <p>{batchStatus.BatchType}</p>
            <p>{batchStatus.CurrentDateTime}</p>
            <p>{batchStatus.RunningJobs}</p>
            <button onClick={() => onEdit(batchStatus)}>Update/Delete</button>
        </li>
    );
};

export default BatchItem;
