import React from 'react';
import {Dropdown, Nav} from 'react-bootstrap';
import {MenuItem} from "./";

type Props = {
    menuItems: MenuItem[];
    onMenuItemClick: (menuItem: MenuItem) => void;
};

const Menu: React.FC<Props> = ({menuItems, onMenuItemClick}) => {
    return (
        <Nav className="mr-auto">
            {menuItems.map((item, index) => (
                <Dropdown key={index} as={Nav.Item}>
                    {item.subMenuItems ? (
                        <>
                            <Dropdown.Toggle as={Nav.Link}>
                                {item.label}
                            </Dropdown.Toggle>
                            <Dropdown.Menu>
                                {item.subMenuItems.filter(subItem => !subItem.hidden)
                                    .map((subItem, subIndex) => (
                                        <Dropdown.Item key={subIndex}
                                                       onClick={() => onMenuItemClick(subItem)}
                                                       disabled={subItem.disabled}>
                                            {subItem.label}
                                        </Dropdown.Item>
                                    ))}
                            </Dropdown.Menu>
                        </>
                    ) : (
                        <Nav.Link onClick={() => onMenuItemClick(item)}>{item.label}</Nav.Link>
                    )}
                </Dropdown>
            ))}
        </Nav>
    );
};

export default Menu;