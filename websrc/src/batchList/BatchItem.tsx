import React from 'react';
import {Button, ListGroup} from 'react-bootstrap';
import {BatchStatus} from '../api';

export interface BatchItemProps {
    batchStatus: BatchStatus;
    onEdit: (batchStatus: BatchStatus) => void;
    onDelete: (batchStatus: BatchStatus) => void;
}

const BatchItem: React.FC<BatchItemProps> = (batchItemProps: BatchItemProps) => {
    return (
        <ListGroup.Item>
            <div className="d-flex justify-content-between align-items-center">
                <div>
                    <div><strong>Batch Name:</strong> {batchItemProps.batchStatus.BatchName}</div>
                    <div><strong>Type:</strong> {batchItemProps.batchStatus.BatchType}</div>
                    <div><strong>Current Date and Time:</strong> {batchItemProps.batchStatus.CurrentDateTime}</div>
                    <div><strong>Jobs:</strong> {batchItemProps.batchStatus.RunningJobs}</div>
                </div>
                <div>
                    <Button variant="primary" onClick={() => batchItemProps.onEdit(batchItemProps.batchStatus)}
                            className="me-2">Modify</Button>
                    <Button variant="danger"
                            onClick={() => batchItemProps.onDelete(batchItemProps.batchStatus)}>Delete</Button>
                </div>
            </div>
        </ListGroup.Item>
    );
};

export default BatchItem;
