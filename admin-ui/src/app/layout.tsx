import type { Metadata } from "next";
import { Toaster } from "react-hot-toast";
import ToastListener from "@/components/ToastListener";
import "./globals.css";

export const metadata = {
  title: "Do Shopping Admin",
  description: "Modern Minimalist Admin Dashboard for Do Shopping",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body suppressHydrationWarning>
        <Toaster position="top-center" />
        <ToastListener />
        {children}
      </body>
    </html>
  );
}
