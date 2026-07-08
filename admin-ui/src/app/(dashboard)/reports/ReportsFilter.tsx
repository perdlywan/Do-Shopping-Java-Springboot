'use client';

import { useRouter, useSearchParams } from 'next/navigation';
import { useState } from 'react';
import styles from './page.module.css';

export default function ReportsFilter() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [startDate, setStartDate] = useState(searchParams.get('startDate') || '');
  const [endDate, setEndDate] = useState(searchParams.get('endDate') || '');

  const handleFilter = (e: React.FormEvent) => {
    e.preventDefault();
    const params = new URLSearchParams(searchParams);
    
    if (startDate) {
      params.set('startDate', startDate);
    } else {
      params.delete('startDate');
    }

    if (endDate) {
      params.set('endDate', endDate);
    } else {
      params.delete('endDate');
    }

    router.push(`/reports?${params.toString()}`);
  };

  const handleClear = () => {
    setStartDate('');
    setEndDate('');
    router.push('/reports');
  };

  return (
    <form className={styles.filterCard} onSubmit={handleFilter}>
      <div className={styles.formGroup}>
        <label className={styles.label}>Start Date</label>
        <input 
          type="date" 
          className={styles.input} 
          value={startDate} 
          onChange={(e) => setStartDate(e.target.value)} 
        />
      </div>
      
      <div className={styles.formGroup}>
        <label className={styles.label}>End Date</label>
        <input 
          type="date" 
          className={styles.input} 
          value={endDate} 
          onChange={(e) => setEndDate(e.target.value)} 
        />
      </div>

      <button type="submit" className={styles.btn}>Apply Filter</button>
      {(startDate || endDate) && (
        <button type="button" onClick={handleClear} className={styles.btnOutline}>
          Clear
        </button>
      )}
    </form>
  );
}
