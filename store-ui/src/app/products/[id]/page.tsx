import { notFound } from 'next/navigation';
import ProductDetailClient from './ProductDetailClient';

interface Product {
  id: string;
  categoryId: string;
  name: string;
  price: number;
  stock: number;
  description: string;
  imageUrl?: string;
}

interface DataResponse<T> {
  statusCode: number;
  message: string;
  data: T;
}

async function getProduct(id: string) {
  try {
    const res = await fetch(`http://localhost:8080/products/${id}`, {
      next: { revalidate: 10 } 
    });
    
    if (!res.ok) {
      if (res.status === 404) return null;
      throw new Error(`Failed to fetch product with id ${id}`);
    }
    
    const response = await res.json() as DataResponse<Product>;
    return response.data;
  } catch (error) {
    console.error(error);
    return null;
  }
}

export default async function ProductDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = await params;
  const product = await getProduct(resolvedParams.id);

  if (!product) {
    notFound();
  }

  return (
    <ProductDetailClient product={product} />
  );
}
