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
      {isExporting ? (
        <><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{verticalAlign:'middle',marginRight:4,animation:'spin 1s linear infinite'}}><path d="M21 12a9 9 0 1 1-6.219-8.56"/></svg> Exporting...</>
      ) : (
        <><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{verticalAlign:'middle',marginRight:4}}><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg> Export Excel</>
      )}
    </button>
  );
}
