type MenuItem = {
    label: string;
    subMenuItems?: MenuItem[];
    action?: () => void;
    url?: string;
    externalUrl?: string;
    disabled?: boolean;
    hidden?: boolean;
};

export default MenuItem;