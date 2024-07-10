// CreateProducerBatchModal.tsx
import React from 'react';
import Modal from 'react-modal';
import './BatchList.css'

interface CreateProducerBatchModalProps {
    isOpen: boolean;
    onRequestClose: () => void;
    onCreate: () => void;
    topicTypes: string[];
    topicName: string;
    setTopicName: React.Dispatch<React.SetStateAction<string>>;
    numThreads: number;
    setNumThreads: React.Dispatch<React.SetStateAction<number>>;
    batchSize: number;
    setBatchSize: React.Dispatch<React.SetStateAction<number>>;
    startId: number;
    setStartID: React.Dispatch<React.SetStateAction<number>>;
    correlatedStartIdInc: number;
    setCorrelatedStartIdInc: React.Dispatch<React.SetStateAction<number>>;
    correlatedEndIdInc: number;
    setCorrelatedEndIdInc: React.Dispatch<React.SetStateAction<number>>;
    sleepMillis: number;
    setSleepMillis: React.Dispatch<React.SetStateAction<number>>;
}

const CreateProducerBatchModal: React.FC<CreateProducerBatchModalProps> = ({
                                                                               isOpen,
                                                                               onRequestClose,
                                                                               onCreate,
                                                                               topicTypes,
                                                                               topicName,
                                                                               setTopicName,
                                                                               numThreads,
                                                                               setNumThreads,
                                                                               batchSize,
                                                                               setBatchSize,
                                                                               startId,
                                                                               setStartID,
                                                                               correlatedStartIdInc,
                                                                               setCorrelatedStartIdInc,
                                                                               correlatedEndIdInc,
                                                                               setCorrelatedEndIdInc,
                                                                               sleepMillis,
                                                                               setSleepMillis,
                                                                           }) => {
    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onRequestClose}
            contentLabel="Create Producer Batch"
            className="modal"
            overlayClassName="overlay"
        >
            <h2>Create New Producer Batch</h2>
            <label>
                Topic Name:
                <select value={topicName} onChange={(e) => setTopicName(e.target.value)}>
                    {topicTypes.map((topic, index) => (
                        <option key={index} value={topic}>
                            {topic}
                        </option>
                    ))}
                </select>
            </label>
            <label>
                Number of Threads:
                <input
                    type="number"
                    value={numThreads}
                    onChange={(e) => setNumThreads(Number(e.target.value))}
                />
            </label>
            <label>
                Batch Size:
                <input
                    type="number"
                    value={batchSize}
                    onChange={(e) => setBatchSize(Number(e.target.value))}
                />
            </label>
            <label>
                Start ID:
                <input
                    type="number"
                    value={startId}
                    onChange={(e) => setStartID(Number(e.target.value))}
                />
            </label>
            <label>
                Correlated Start ID Increment:
                <input
                    type="number"
                    value={correlatedStartIdInc}
                    onChange={(e) => setCorrelatedStartIdInc(Number(e.target.value))}
                />
            </label>
            <label>
                Correlated End ID Increment:
                <input
                    type="number"
                    value={correlatedEndIdInc}
                    onChange={(e) => setCorrelatedEndIdInc(Number(e.target.value))}
                />
            </label>
            <label>
                Sleep Milliseconds:
                <input
                    type="number"
                    value={sleepMillis}
                    onChange={(e) => setSleepMillis(Number(e.target.value))}
                />
            </label>
            <button onClick={onCreate}>Create</button>
            <button onClick={onRequestClose}>Cancel</button>
        </Modal>
    );
};

export default CreateProducerBatchModal;
