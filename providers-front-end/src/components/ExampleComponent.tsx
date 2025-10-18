import React from 'react';

interface ExampleComponentProps {
    title: string;
    description: string;
}

const ExampleComponent: React.FC<ExampleComponentProps> = ({ title, description }) => {
    return (
        <div>
            <h1>{title}</h1>
            <p>{description}</p>
        </div>
    );
};

export default ExampleComponent;