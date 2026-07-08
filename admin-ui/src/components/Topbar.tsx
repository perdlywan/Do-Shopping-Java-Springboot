import styles from './Topbar.module.css';

interface TopbarProps {
  toggleSidebar: () => void;
}

export default function Topbar({ toggleSidebar }: TopbarProps) {
  return (
    <header className={styles.topbar}>
      <div className={styles.leftSection}>
        <button
          className={styles.menuButton}
          onClick={toggleSidebar}
          aria-label="Toggle Menu"
        >
          ☰
        </button>
      </div>
    </header>
  );
}
