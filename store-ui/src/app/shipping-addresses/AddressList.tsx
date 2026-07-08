'use client';

import React, { useState } from 'react';
import styles from './shipping.module.css';
import AddressForm from './AddressForm';
import { deleteShippingAddress } from '@/app/actions/shipping';

type Address = {
  id: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
};

type AddressListProps = {
  initialAddresses: Address[];
};

export default function AddressList({ initialAddresses }: AddressListProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState<Address | null>(null);

  const handleAddNew = () => {
    setEditingAddress(null);
    setIsModalOpen(true);
  };

  const handleEdit = (address: Address) => {
    setEditingAddress(address);
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (confirm('Are you sure you want to delete this address?')) {
      const res = await deleteShippingAddress(id);
      if (res?.error) {
        alert(res.error);
      }
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingAddress(null);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>My Shipping Addresses</h1>
        <button className={styles.addBtn} onClick={handleAddNew}>
          + Add New Address
        </button>
      </div>

      <div className={styles.grid}>
        {initialAddresses.length === 0 ? (
          <div className={styles.emptyState}>
            <p>You haven't saved any shipping addresses yet.</p>
          </div>
        ) : (
          initialAddresses.map((address) => (
            <div key={address.id} className={styles.card}>
              {address.isDefault && (
                <div className={styles.defaultBadge}>Default</div>
              )}
              
              <div className={styles.addressLines}>
                <p>{address.address}</p>
                <p>{address.city}, {address.state}</p>
                <p>{address.country}, {address.postalCode}</p>
              </div>

              <div className={styles.cardActions}>
                <button 
                  className={styles.editBtn}
                  onClick={() => handleEdit(address)}
                >
                  Edit
                </button>
                <button 
                  className={styles.deleteBtn}
                  onClick={() => handleDelete(address.id)}
                >
                  Delete
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {isModalOpen && (
        <AddressForm 
          address={editingAddress} 
          onClose={closeModal} 
          onSuccess={closeModal} 
        />
      )}
    </div>
  );
}
