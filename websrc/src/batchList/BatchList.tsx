import React, {useEffect, useRef, useState} from 'react';
import {Button, Container, ListGroup} from 'react-bootstrap';
import {BatchControllerApi, BatchStatus, ProducerControllerApi} from '../api';
import BatchItem from './BatchItem';
import UpdateBatchModal from './UpdateBatchModal';
import ConfirmDeleteModal from './ConfirmDeleteModal';
import CreateProducerBatchModal from './CreateProducerBatchModal';
import CreateConsumerBatchModal from './CreateConsumerBatchModal';
import apiConfig from '../apiConfig';

const batchController = new BatchControllerApi(apiConfig);
const producerController = new ProducerControllerApi(apiConfig);

const BatchList: React.FC = () => {
    const [batchStatus, setBatchStatus] = useState<BatchStatus[] | null>(null);
    const [selectedBatch, setSelectedBatch] = useState<BatchStatus | null>(null);
    const [isCreateConsumerModalOpen, setIsCreateConsumerModalOpen] = useState(false);
    const [isCreateProducerModalOpen, setIsCreateProducerModalOpen] = useState(false);
    const [isUpdateBatchModalOpen, setIsUpdateBatchModalOpen] = useState(false);
    const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
    const messageRef = useRef<HTMLParagraphElement>(null);
    const errorRef = useRef<HTMLParagraphElement>(null);
    const [topicTypes, setTopicTypes] = useState<string[]>([]);
    const [topicName, setTopicName] = useState("");
    const [numThreads, setNumThreads] = useState(6);
    const [batchSize, setBatchSize] = useState(100000);
    const [maxTries, setMaxTries] = useState(10);
    const [sleepMillis, setSleepMillis] = useState(100);
    const [startId, setStartID] = useState(-1);
    const [correlatedStartIdInc, setCorrelatedStartIdInc] = useState(-1);
    const [correlatedEndIdInc, setCorrelatedEndIdInc] = useState(-1);

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

    const fetchTopicTypes = async () => {
        try {
            const response = await producerController.getListDataClasses();
            setTopicTypes(response.data);
            setTopicName(response.data[0]);
        } catch (error) {
            console.error('Error fetching compression types:', error);
            setTopicTypes([]);
        }
    };

    useEffect(() => {
        fetchTopicTypes();
        fetchBatchStatus(); // Initial fetch

        const interval = setInterval(() => {
            fetchBatchStatus();
        }, 10000); // 10000ms = 10 seconds

        return () => clearInterval(interval); // Cleanup on component unmount
    }, []); // Empty dependency array ensures this effect runs only once

    const handleUpdateScreen = (batchStatus: BatchStatus) => {
        setSelectedBatch(batchStatus);
        setIsUpdateBatchModalOpen(true);
    };

    const handleDeleteScreen = (batchStatus: BatchStatus) => {
        console.error('Got to handleDeleteScreen');
        setSelectedBatch(batchStatus);
        setIsConfirmDeleteModalOpen(true);
    };

    const handleClose = () => {
        setSelectedBatch(null);
        setIsCreateConsumerModalOpen(false);
        setIsCreateProducerModalOpen(false);
        setIsUpdateBatchModalOpen(false);
        setIsConfirmDeleteModalOpen(false);
    };

    const handleUpdate = (numThreads: number) => {
        if (selectedBatch?.BatchType === 'Producer') {
            batchController.changeProducerTaskCount(selectedBatch.BatchName, numThreads)
                .then(response => {
                    if (messageRef.current) messageRef.current.textContent = 'Update Producer Batch Task Count Result: ' + response.data;
                    if (errorRef.current) errorRef.current.textContent = '';
                    fetchBatchStatus(); // Refresh the list after creation
                })
                .catch(error => {
                    if (errorRef.current) errorRef.current.textContent = 'Error Updating Producer Batch Task Count: ' + error.message;
                    if (messageRef.current) messageRef.current.textContent = '';
                });
        } else if (selectedBatch?.BatchType === 'Consumer') {
            batchController.changeConsumerTaskCount(selectedBatch.BatchName, numThreads)
                .then(response => {
                    if (messageRef.current) messageRef.current.textContent = 'Update Consumer Batch Task Count Result: ' + response.data;
                    if (errorRef.current) errorRef.current.textContent = '';
                    fetchBatchStatus(); // Refresh the list after creation
                })
                .catch(error => {
                    if (errorRef.current) errorRef.current.textContent = 'Error Updating Consumer Batch Task Count: ' + error.message;
                    if (messageRef.current) messageRef.current.textContent = '';
                });
        }
        handleClose();
    };

    const handleDelete = () => {
        if (selectedBatch?.BatchType === 'Producer') {
            batchController.dropProducerTask(selectedBatch.BatchName)
                .then(response => {
                    if (messageRef.current) messageRef.current.textContent = 'Drop Producer Batch Result: ' + response.data;
                    if (errorRef.current) errorRef.current.textContent = '';
                    fetchBatchStatus(); // Refresh the list after creation
                })
                .catch(error => {
                    if (errorRef.current) errorRef.current.textContent = 'Error Dropping Producer Batch: ' + error.message;
                    if (messageRef.current) messageRef.current.textContent = '';
                });
        } else if (selectedBatch?.BatchType === 'Consumer') {
            batchController.dropConsumerTask(selectedBatch.BatchName)
                .then(response => {
                    if (messageRef.current) messageRef.current.textContent = 'Drop Consumer Batch Result: ' + response.data;
                    if (errorRef.current) errorRef.current.textContent = '';
                    fetchBatchStatus(); // Refresh the list after creation
                })
                .catch(error => {
                    if (errorRef.current) errorRef.current.textContent = 'Error Dropping Consumer Batch: ' + error.message;
                    if (messageRef.current) messageRef.current.textContent = '';
                });
        }
        handleClose();
    };

    const handleCreateConsumerBatch = () => {
        setTopicName(topicTypes[0]);
        setIsCreateConsumerModalOpen(true);
    };

    const handleCreateProducerBatch = () => {
        setTopicName(topicTypes[0]);
        setIsCreateProducerModalOpen(true);
    };

    const handleCreateConsumer = () => {
        console.log('Creating new batch');
        batchController.createConsumerTask(topicName, numThreads, batchSize, maxTries, sleepMillis, topicName)
            .then(response => {
                if (messageRef.current) messageRef.current.textContent = 'Create Consumer Batch Result: ' + response.data;
                if (errorRef.current) errorRef.current.textContent = '';
                fetchBatchStatus(); // Refresh the list after creation
            })
            .catch(error => {
                if (errorRef.current) errorRef.current.textContent = 'Error Creating Consumer Batch: ' + error.message;
                if (messageRef.current) messageRef.current.textContent = '';
            });

        handleClose();
    };

    const handleCreateProducer = () => {
        console.log('Creating new batch');
        batchController.createProducerTask(topicName, numThreads, batchSize, startId, correlatedStartIdInc, correlatedEndIdInc, sleepMillis, topicName)
            .then(response => {
                if (messageRef.current) messageRef.current.textContent = 'Create Producer Batch Result: ' + response.data;
                if (errorRef.current) errorRef.current.textContent = '';
                fetchBatchStatus(); // Refresh the list after creation
            })
            .catch(error => {
                if (errorRef.current) errorRef.current.textContent = 'Error Creating Producer Batch: ' + error.message;
                if (messageRef.current) messageRef.current.textContent = '';
            });

        handleClose();
    };

    return (
        <Container>
            <h1>Batch Status</h1>
            {batchStatus === null && <p>Loading...</p>}
            {batchStatus && batchStatus.length === 0 && <p>No batch statuses available.</p>}
            {batchStatus && batchStatus.length > 0 && (
                <ListGroup>
                    {batchStatus.map((item, index) => (
                        <BatchItem key={index} batchStatus={item} onEdit={handleUpdateScreen}
                                   onDelete={handleDeleteScreen}/>
                    ))}
                </ListGroup>
            )}
            <p ref={errorRef} className="text-danger"></p>
            <p ref={messageRef} className="text-success"></p>
            <Button className="m-2" variant="primary" onClick={handleCreateProducerBatch}>New Producer Batch</Button>
            <Button className="m-2" variant="primary" onClick={handleCreateConsumerBatch}>New Consumer Batch</Button>
            {isUpdateBatchModalOpen && selectedBatch && (
                <UpdateBatchModal
                    isOpen={isUpdateBatchModalOpen}
                    onRequestClose={handleClose}
                    selectedBatch={selectedBatch}
                    onUpdate={handleUpdate}
                />
            )}
            {isConfirmDeleteModalOpen && selectedBatch && (
                <ConfirmDeleteModal
                    isOpen={isConfirmDeleteModalOpen}
                    onRequestClose={handleClose}
                    onDelete={handleDelete}
                />
            )}
            {isCreateConsumerModalOpen && (
                <CreateConsumerBatchModal
                    isOpen={isCreateConsumerModalOpen}
                    onRequestClose={handleClose}
                    onCreate={handleCreateConsumer}
                    topicTypes={topicTypes}
                    topicName={topicName}
                    setTopicName={setTopicName}
                    numThreads={numThreads}
                    setNumThreads={setNumThreads}
                    batchSize={batchSize}
                    setBatchSize={setBatchSize}
                    maxTries={maxTries}
                    setMaxTries={setMaxTries}
                    sleepMillis={sleepMillis}
                    setSleepMillis={setSleepMillis}
                />
            )}
            {isCreateProducerModalOpen && (
                <CreateProducerBatchModal
                    isOpen={isCreateProducerModalOpen}
                    onRequestClose={handleClose}
                    onCreate={handleCreateProducer}
                    topicTypes={topicTypes}
                    topicName={topicName}
                    setTopicName={setTopicName}
                    numThreads={numThreads}
                    setNumThreads={setNumThreads}
                    batchSize={batchSize}
                    setBatchSize={setBatchSize}
                    startId={startId}
                    setStartID={setStartID}
                    correlatedStartIdInc={correlatedStartIdInc}
                    setCorrelatedStartIdInc={setCorrelatedStartIdInc}
                    correlatedEndIdInc={correlatedEndIdInc}
                    setCorrelatedEndIdInc={setCorrelatedEndIdInc}
                    sleepMillis={sleepMillis}
                    setSleepMillis={setSleepMillis}
                />
            )}
        </Container>
    );
};

export default BatchList;
