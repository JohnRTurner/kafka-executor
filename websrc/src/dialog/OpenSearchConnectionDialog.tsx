import React, {useEffect, useState} from 'react';
import {Button, Form, Modal} from 'react-bootstrap';
import {OpenSearchConnectionDTO} from "../api";
import {configController} from "../controllers";


export interface OpenSearchConnectionDialogProps {
    /*show: boolean;
    handleClose: () => void;
    handleSave: (data: OpenSearchConnectionDTO) => void;
    */
    isOpen: boolean;
    onClose: () => void;
    onConfirm: ((result: string) => void) | undefined;
}

const OpenSearchConnectionDialog: React.FC<OpenSearchConnectionDialogProps> = (openSearchConnectionDialogProps: OpenSearchConnectionDialogProps) => {
    const [formData, setFormData] = useState<OpenSearchConnectionDTO>({
        enable: true,
        host: '',
        port: undefined,
        user: '',
        password: '',
    });

    useEffect(() => {
        // Fetch current OpenSearch connection data when the dialog opens
        if (openSearchConnectionDialogProps.isOpen) {
            fetchOpenSearchConnectionData();
        }
    }, [openSearchConnectionDialogProps.isOpen]);

    const fetchOpenSearchConnectionData = async () => {
        try {
            const response = await configController.openSearchConnection1(); // Adjust this to match your API call
            const data = response.data as OpenSearchConnectionDTO;
            setFormData(data);
        } catch (error) {
            console.error('Error fetching OpenSearch connection data:', error);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value, type, checked} = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value,
        });
    };

    const handleConfirm = async () => {
        let retMessage = 'Call to update the Connection Configuration has failed!';
        try {
            await configController.openSearchConnection(formData);
            retMessage = 'Successfully updated the Connection Configuration.';
        } catch (error) {
            console.error('Error saving the Connection Configuration.', error);
        } finally {
            openSearchConnectionDialogProps.onConfirm && openSearchConnectionDialogProps.onConfirm(retMessage);
            openSearchConnectionDialogProps.onClose();
        }
    };

    return (
        <Modal show={openSearchConnectionDialogProps.isOpen} onHide={openSearchConnectionDialogProps.onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Update OpenSearch Connection</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group controlId="host">
                        <Form.Label>Host</Form.Label>
                        <Form.Control
                            type="text"
                            name="host"
                            value={formData.host}
                            onChange={handleChange}
                        />
                    </Form.Group>
                    <Form.Group controlId="port">
                        <Form.Label>Port</Form.Label>
                        <Form.Control
                            type="number"
                            name="port"
                            value={formData.port || ''}
                            onChange={handleChange}
                        />
                    </Form.Group>
                    <Form.Group controlId="user">
                        <Form.Label>User</Form.Label>
                        <Form.Control
                            type="text"
                            name="user"
                            value={formData.user}
                            onChange={handleChange}
                        />
                    </Form.Group>
                    <Form.Group controlId="password">
                        <Form.Label>Password</Form.Label>
                        <Form.Control
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={openSearchConnectionDialogProps.onClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={handleConfirm}>
                    Save Changes
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default OpenSearchConnectionDialog;
