import React from 'react';
import {Navbar} from 'react-bootstrap';
import {Menu, MenuItem} from './';

type Props = {
    menuItems: MenuItem[];
    toggleMenu: () => void;
    menuOpen: boolean;
    onMenuItemClick: (menuItem: MenuItem) => void;
};

const NavbarComponent: React.FC<Props> = ({menuItems, toggleMenu, menuOpen, onMenuItemClick}) => {
    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Navbar.Brand>Kafka Executor Demo</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" onClick={toggleMenu}/>
            <Navbar.Collapse id="basic-navbar-nav" className={`${menuOpen ? 'show' : ''}`}>
                <Menu menuItems={menuItems} onMenuItemClick={onMenuItemClick}/>
            </Navbar.Collapse>
        </Navbar>
    );
};

export default NavbarComponent;
