'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';
import { redirect } from 'next/navigation';

async function getAuthHeaders() {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
}

export async function updateOrderAction(id: string, prevState: any, formData: FormData) {
  const orderStatus = formData.get('orderStatus');
  const shippingStatus = formData.get('shippingStatus');
  const courierName = formData.get('courierName');
  const serviceType = formData.get('serviceType');
  const trackingNumber = formData.get('trackingNumber');
  const shippingCost = formData.get('shippingCost');
  const shippedAtStr = formData.get('shippedAt');
  const deliveredAtStr = formData.get('deliveredAt');

  // Format datetime strings to match Spring Boot's LocalDateTime expectation if present
  const formatDateTime = (dtStr: FormDataEntryValue | null) => {
    if (!dtStr || typeof dtStr !== 'string') return null;
    // Input datetime-local gives "YYYY-MM-DDTHH:mm"
    // Spring Boot LocalDateTime standard ISO format also accepts this or "YYYY-MM-DDTHH:mm:ss"
    return dtStr.length === 16 ? `${dtStr}:00` : dtStr;
  };

  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/orders/${id}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({
        orderStatus: orderStatus || null,
        shippingStatus: shippingStatus || null,
        courierName: courierName || null,
        serviceType: serviceType || null,
        trackingNumber: trackingNumber || null,
        shippingCost: shippingCost ? Number(shippingCost) : null,
        shippedAt: formatDateTime(shippedAtStr),
        deliveredAt: formatDateTime(deliveredAtStr),
      }),
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      return { error: data.message || `Failed to update order (Status: ${res.status})` };
    }
  } catch (error) {
    return { error: 'Failed to connect to server.' };
  }

  revalidatePath('/orders');
  revalidatePath(`/orders/${id}`);
  redirect('/orders?success=Order updated successfully');
}
