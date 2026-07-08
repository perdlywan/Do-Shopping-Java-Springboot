import styles from '../../products/form.module.css';
import AddCategoryForm from './AddCategoryForm';

export default function AddCategoryPage() {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Add New Category</h1>
      </div>

      <div className={styles.card}>
        <AddCategoryForm />
      </div>
    </div>
  );
}
