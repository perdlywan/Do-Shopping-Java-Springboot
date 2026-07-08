import type { Metadata } from "next";
import "./globals.css";
import Navbar from "@/components/layout/Navbar";

export const metadata: Metadata = {
  title: "Do Shopping - Store",
  description: "Your premium shopping destination",
};

import { CartProvider } from "@/context/CartContext";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <CartProvider>
          <div className="page-wrapper">
            <Navbar />
            <main className="main-content">
              {children}
            </main>
          </div>
        </CartProvider>
      </body>
    </html>
  );
}
