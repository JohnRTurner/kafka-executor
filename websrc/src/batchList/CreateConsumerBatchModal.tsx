import React from 'react';
import Modal from 'react-modal';
import './BatchList.css'

interface CreateConsumerBatchModalProps {
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
    maxTries: number;
    setMaxTries: React.Dispatch<React.SetStateAction<number>>;
    sleepMillis: number;
    setSleepMillis: React.Dispatch<React.SetStateAction<number>>;
}

const CreateConsumerBatchModal: React.FC<CreateConsumerBatchModalProps> = ({
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
                                                                               maxTries,
                                                                               setMaxTries,
                                                                               sleepMillis,
                                                                               setSleepMillis
                                                                           }) => {
    return (
        <Modal isOpen={isOpen} onRequestClose={onRequestClose}
               contentLabel="Create Consumer Batch Modal"
               className="modal"
               overlayClassName="overlay"
        >
            <h2>Create Consumer Batch</h2>
            <label>
                Topic Name:
                <select value={topicName} onChange={(e) => setTopicName(e.target.value)}>
                    {topicTypes.map((type, index) => (
                        <option key={index} value={type}>
                            {type}
                        </option>
                    ))}
                </select>
            </label>
            <label>
                Threads:
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
                Max Tries:
                <input
                    type="number"
                    value={maxTries}
                    onChange={(e) => setMaxTries(Number(e.target.value))}
                />
            </label>
            <label>
                Sleep Millis:
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

export default CreateConsumerBatchModal;
