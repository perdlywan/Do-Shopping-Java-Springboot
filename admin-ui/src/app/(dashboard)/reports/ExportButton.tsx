'use client';

import { useState } from 'react';
import styles from './page.module.css';

interface ExportButtonProps {
  endpoint: string;
  queryString: string;
  fileName: string;
}

export default function ExportButton({ endpoint, queryString, fileName }: ExportButtonProps) {
  const [isExporting, setIsExporting] = useState(false);

  const handleExport = async () => {
    setIsExporting(true);
    try {
      // Fetch token from cookies (assuming cookie is accessible or we proxy through Next.js)
      // Actually, since this is a client component, we might not have the raw token if HttpOnly.
      // It's safer to go through a Next.js API route.
      const res = await fetch(`/api/export?endpoint=${encodeURIComponent(endpoint)}&qs=${encodeURIComponent(queryString)}`);
      
      if (!res.ok) {
        throw new Error('Export failed');
      }

      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Export error:', error);
      alert('Failed to export report');
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <button 
      onClick={handleExport} 
      className={styles.btnOutline} 
      disabled={isExporting}
    >
      {isExporting ? '⏳ Exporting...' : '⬇️ Export Excel'}
    </button>
  );
}
