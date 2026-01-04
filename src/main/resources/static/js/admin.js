/**
 * Smart Cafe - Admin Dashboard JavaScript
 * 
 * Handles:
 * - Product CRUD (Add, Edit, Delete)
 * - Dynamic product list updates
 */

document.addEventListener('DOMContentLoaded', function () {
    console.log('Admin Dashboard initialized');
    loadAllProducts();
});

/**
 * Load all products from the API and render the table
 */
async function loadAllProducts() {
    try {
        const response = await fetch('/api/products/all');
        if (!response.ok) throw new Error('Failed to fetch products');
        const products = await response.json();
        renderProductsTable(products);
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to load products', 'error');
    }
}

/**
 * Format number as IDR (Rupiah)
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(amount).replace('IDR', 'Rp');
}

/**
 * Render the products table
 * @param {Array} products 
 */
function renderProductsTable(products) {
    const tbody = document.querySelector('#all-products-table tbody');
    if (!tbody) return;

    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4">No products found</td></tr>';
        return;
    }

    tbody.innerHTML = products.map(product => `
        <tr data-id="${product.id}">
            <td>${product.id}</td>
            <td>
                <strong>${product.name}</strong>
                <br>
                <small class="text-muted">${product.description || ''}</small>
            </td>
            <td>
                <span class="badge ${product.productType === 'FOOD' ? 'badge-food' : 'badge-drink'}">
                    ${product.productType}
                </span>
            </td>
            <td>${formatCurrency(product.price)}</td>
            <td>
                <span class="${product.stock < 10 ? 'text-danger fw-bold' : ''}">${product.stock}</span>
            </td>
            <td>
                <span class="badge ${product.available ? 'bg-success' : 'bg-secondary'}">
                    ${product.available ? 'Active' : 'Inactive'}
                </span>
            </td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" onclick="openEditModal(${product.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-outline-danger" onclick="deleteProduct(${product.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

/**
 * Open modal to add a new product
 */
function openAddModal() {
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('productModalTitle').textContent = 'Add New Product';

    // Default values
    document.getElementById('productType').value = 'FOOD';
    toggleTypeFields();

    const modal = new bootstrap.Modal(document.getElementById('productModal'));
    modal.show();
}

/**
 * Open modal to edit an existing product
 * @param {number} id 
 */
async function openEditModal(id) {
    try {
        const response = await fetch(`/api/products/${id}`);
        if (!response.ok) throw new Error('Failed to fetch product details');
        const product = await response.json();

        document.getElementById('productId').value = product.id;
        document.getElementById('productModalTitle').textContent = 'Edit Product';

        document.getElementById('name').value = product.name;
        document.getElementById('price').value = product.price;
        document.getElementById('stock').value = product.stock;
        document.getElementById('productType').value = product.productType;
        document.getElementById('description').value = product.description || '';
        document.getElementById('imageUrl').value = product.imageUrl || '';
        document.getElementById('available').checked = product.available;

        // Specific fields
        toggleTypeFields();
        if (product.productType === 'FOOD') {
            document.getElementById('isVegetarian').checked = product.isVegetarian || false;
        } else {
            document.getElementById('isCold').checked = product.isCold || false;
            document.getElementById('size').value = product.size || '';
        }

        const modal = new bootstrap.Modal(document.getElementById('productModal'));
        modal.show();
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to load product details', 'error');
    }
}

/**
 * Toggle visibility of fields based on product type
 */
function toggleTypeFields() {
    const type = document.getElementById('productType').value;
    const foodFields = document.getElementById('foodFields');
    const drinkFields = document.getElementById('drinkFields');

    if (type === 'FOOD') {
        foodFields.classList.remove('d-none');
        drinkFields.classList.add('d-none');
    } else {
        foodFields.classList.add('d-none');
        drinkFields.classList.remove('d-none');
    }
}

/**
 * Save product (Create or Update)
 */
async function saveProduct() {
    const form = document.getElementById('productForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const id = document.getElementById('productId').value;
    const isEdit = id !== '';

    const productData = {
        name: document.getElementById('name').value,
        price: parseFloat(document.getElementById('price').value),
        stock: parseInt(document.getElementById('stock').value),
        productType: document.getElementById('productType').value,
        description: document.getElementById('description').value,
        imageUrl: document.getElementById('imageUrl').value,
        available: document.getElementById('available').checked,
        isVegetarian: document.getElementById('isVegetarian').checked,
        isCold: document.getElementById('isCold').checked,
        size: document.getElementById('size').value
    };

    try {
        const url = isEdit ? `/api/products/${id}` : '/api/products';
        const method = isEdit ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(productData)
        });

        if (!response.ok) throw new Error('Failed to save product');

        const savedProduct = await response.json();
        showToast(`Product ${isEdit ? 'updated' : 'added'} successfully!`, 'success');

        // Close modal
        const modalElement = document.getElementById('productModal');
        const modal = bootstrap.Modal.getInstance(modalElement);
        modal.hide();

        // Reload products
        loadAllProducts();

    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to save product', 'error');
    }
}

/**
 * Delete a product
 * @param {number} id 
 */
async function deleteProduct(id) {
    if (!confirm('Are you sure you want to delete this product?')) return;

    try {
        const response = await fetch(`/api/products/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to delete product');

        showToast('Product deleted successfully', 'success');
        loadAllProducts();
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to delete product', 'error');
    }
}

/**
 * Show toast notification (reused from app.js logic)
 */
function showToast(message, type = 'info') {
    // Check if app.js showToast is available, if not use local one
    if (window.showToast) {
        window.showToast(message, type);
        return;
    }

    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast-notification ${type}`;

    const icon = type === 'success' ? 'check-circle-fill' :
        type === 'error' ? 'exclamation-circle-fill' : 'info-circle-fill';

    toast.innerHTML = `
        <i class="bi bi-${icon}"></i>
        <span>${message}</span>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
