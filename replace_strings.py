import os

replacements = {
    # Services
    '\"Kategori tidak ditemukan\"': '\"Category not found\"',
    '\"Kategori sudah ada\"': '\"Category already exists\"',
    '\"Username sudah terdaftar\"': '\"Username already registered\"',
    '\"Email sudah terdaftar\"': '\"Email already registered\"',
    '\"Username atau password salah!\"': '\"Invalid username or password!\"',
    '\"User tidak ditemukan\"': '\"User not found\"',
    '\"Customer tidak ditemukan\"': '\"Customer not found\"',
    '\" tidak ditemukan\"': '\" not found\"',
    '\"Anda tidak memiliki akses\"': '\"You do not have access\"',
    '\"Produk tidak ditemukan\"': '\"Product not found\"',
    '\" tidak tersedia\"': '\" not available\"',
    '\"Nama produk sudah ada\"': '\"Product name already exists\"',
    '\"Nama Produk sudah ada\"': '\"Product name already exists\"',
    '\"Order tidak ditemukan\"': '\"Order not found\"',
    '\"Order bukan punya anda\"': '\"Order does not belong to you\"',
    '\"Order bukan milik anda\"': '\"Order does not belong to you\"',
    '\"Order tidak dapat dibayar\"': '\"Order cannot be paid\"',
    '\"Order tidak ditemukan atau telah dibayarkan\"': '\"Order not found or already paid\"',
    '\"Waktu pembayaran sudah habis\"': '\"Payment time has expired\"',
    '\"Data order tidak ditemukan\"': '\"Order data not found\"',
    '\"Alamat tidak ditemukan\"': '\"Address not found\"',
    '\"Bukan alamat anda\"': '\"Not your address\"',
    '\" tidak cukup\"': '\" is insufficient\"',
    '\"Stok \"': '\"Stock for \"',
    '\"Data pengiriman tidak ditemukan untuk order ini\"': '\"Shipping data not found for this order\"',
    '\"Nomor resi (tracking number) wajib diisi jika status diubah ke SHIPPED\"': '\"Tracking number is required if status is changed to SHIPPED\"',
    '\"Order tidak dapat dicancel\"': '\"Order cannot be canceled\"',
    '\"Detail order tidak ditemukan\"': '\"Order details not found\"',
    '\"Pembayaran tidak ditemukan\"': '\"Payment not found\"',
    '\"Pengiriman tidak ditemukan\"': '\"Shipping not found\"',
    
    # Controllers
    '\"Alamat pengiriman berhasil ditambahkan\"': '\"Shipping address successfully added\"',
    '\"Alamat pengiriman berhasil diupdate\"': '\"Shipping address successfully updated\"',
    '\"Alamat pengiriman berhasil dihapus\"': '\"Shipping address successfully deleted\"',
    '\"Produk berhasil ditambahkan\"': '\"Product successfully added\"',
    '\"Produk berhasil diupdate\"': '\"Product successfully updated\"',
    '\"Produk berhasil dihapus\"': '\"Product successfully deleted\"',
    '\"Pembayaran berhasil\"': '\"Payment successful\"',
    '\"Order berhasil dibuat\"': '\"Order successfully created\"',
    '\"Order berhasil diupdate\"': '\"Order successfully updated\"',
    '\"Order berhasil dibatalkan\"': '\"Order successfully canceled\"',
    '\"Kategori berhasil ditambahkan\"': '\"Category successfully added\"',
    '\"Kategori berhasil diupdate\"': '\"Category successfully updated\"',
    '\"Kategori berhasil dihapus\"': '\"Category successfully deleted\"',
    '\"Register berhasil\"': '\"Registration successful\"',

    # DTOs
    '\"name tidak boleh kosong\"': '\"name cannot be empty\"',
    '\"categoryId tidak boleh kosong\"': '\"categoryId cannot be empty\"',
    '\"price tidak boleh kosong\"': '\"price cannot be empty\"',
    '\"price harus lebih besar dari 0\"': '\"price must be greater than 0\"',
    '\"stock tidak boleh kosong\"': '\"stock cannot be empty\"',
    '\"address tidak boleh kosong\"': '\"address cannot be empty\"',
    '\"country tidak boleh kosong\"': '\"country cannot be empty\"',
    '\"state tidak boleh kosong\"': '\"state cannot be empty\"',
    '\"city tidak boleh kosong\"': '\"city cannot be empty\"',
    '\"productId tidak boleh kosong\"': '\"productId cannot be empty\"',
    '\"quantity tidak boleh null\"': '\"quantity cannot be null\"',
    '\"quantity minimal 1\"': '\"quantity must be at least 1\"',
    '\"items tidak boleh kosong\"': '\"items cannot be empty\"',
    '\"paymentMethod tidak boleh kosong\"': '\"paymentMethod cannot be empty\"',
    '\"shippingAddressId tidak boleh kosong\"': '\"shippingAddressId cannot be empty\"'
}

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content
    for k, v in replacements.items():
        new_content = new_content.replace(k, v)
        
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Updated {filepath}')

def main():
    root_dir = r'c:\Users\Perdly Setiawan\Downloads\do-shopping\src\main\java\com\example\do_shopping'
    for subdir, dirs, files in os.walk(root_dir):
        for file in files:
            if file.endswith('.java'):
                process_file(os.path.join(subdir, file))

if __name__ == '__main__':
    main()
