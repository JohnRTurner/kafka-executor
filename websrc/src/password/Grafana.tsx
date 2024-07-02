import React from 'react';

const Grafana: React.FC = () => {
    const grafanaPassword = import.meta.env.VITE_GRAFANA_USER_PASSWORD;

    if (!grafanaPassword) {
        return <p>GRAFANA USER PASSWORD is not defined.</p>;
    }

    return (
        <div>
            <p>Grafana User Password: <strong>{grafanaPassword}</strong> </p>
        </div>
    );
};

export default Grafana;