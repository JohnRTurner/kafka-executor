import React from 'react';
import {Button, ListGroup} from 'react-bootstrap';
import {BatchStatus} from '../api';

interface BatchItemProps {
    batchStatus: BatchStatus;
    onEdit: (batchStatus: BatchStatus) => void;
    onDelete: (batchStatus: BatchStatus) => void;
}

const BatchItem: React.FC<BatchItemProps> = ({batchStatus, onEdit, onDelete}) => {
    return (
        <ListGroup.Item>
            <div className="d-flex justify-content-between align-items-center">
                <div>
                    <div><strong>Batch Name:</strong> {batchStatus.BatchName}</div>
                    <div><strong>Type:</strong> {batchStatus.BatchType}</div>
                    <div><strong>Current Date and Time:</strong> {batchStatus.CurrentDateTime}</div>
                    <div><strong>Jobs:</strong> {batchStatus.RunningJobs}</div>
                </div>
                <div>
                    <Button variant="primary" onClick={() => onEdit(batchStatus)} className="me-2">Modify</Button>
                    <Button variant="danger" onClick={() => onDelete(batchStatus)}>Delete</Button>
                </div>
            </div>
        </ListGroup.Item>
    );
};

export default BatchItem;
