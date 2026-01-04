/**
 * Smart Cafe - Customer Menu JavaScript
 * 
 * Handles:
 * - Shopping cart functionality
 * - Add/remove items
 * - Quantity updates
 * - Order submission via REST API
 */

// Cart state
let cart = [];

/**
 * Initialize the application
 */
document.addEventListener('DOMContentLoaded', function () {
    console.log('Smart Cafe Menu initialized');
    loadCartFromStorage();
    loadCustomerName();
    renderCart();
});

function loadCustomerName() {
    const savedName = localStorage.getItem('smartCafeCustomerName');
    if (savedName) {
        const input = document.getElementById('customer-name');
        if (input) input.value = savedName;
    }
}

/**
 * Add to cart from button click (reads data attributes)
 * @param {HTMLElement} button - The clicked button element
 */
function addToCartFromButton(button) {
    const productId = parseInt(button.dataset.id);
    const name = button.dataset.name;
    const price = parseFloat(button.dataset.price);
    addToCart(productId, name, price);
}

/**
 * Add a product to the cart
 * @param {number} productId - Product ID
 * @param {string} name - Product name
 * @param {number} price - Product price
 */
function addToCart(productId, name, price) {
    // Check if item already in cart
    const existingItem = cart.find(item => item.productId === productId);

    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({
            productId: productId,
            name: name,
            price: price,
            quantity: 1
        });
    }

    saveCartToStorage();
    renderCart();
    showToast(`Added ${name} to cart`, 'success');

    // Add animation to the add button
    const button = document.querySelector(`[onclick*="addToCart(${productId}"]`);
    if (button) {
        button.classList.add('added');
        setTimeout(() => button.classList.remove('added'), 500);
    }
}

/**
 * Remove an item from the cart
 * @param {number} productId - Product ID
 */
function removeFromCart(productId) {
    cart = cart.filter(item => item.productId !== productId);
    saveCartToStorage();
    renderCart();
}

/**
 * Update item quantity
 * @param {number} productId - Product ID
 * @param {number} delta - Change in quantity (+1 or -1)
 */
function updateQuantity(productId, delta) {
    const item = cart.find(item => item.productId === productId);

    if (item) {
        item.quantity += delta;

        if (item.quantity <= 0) {
            removeFromCart(productId);
        } else {
            saveCartToStorage();
            renderCart();
        }
    }
}

/**
 * Calculate cart total
 * @returns {number} Total price
 */
function getCartTotal() {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
}

/**
 * Get total item count in cart
 * @returns {number} Total items
 */
function getCartCount() {
    return cart.reduce((count, item) => count + item.quantity, 0);
}

/**
 * Format number as IDR (Rupiah)
 * @param {number} amount 
 * @returns {string} Formatted string
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
 * Render the cart UI
 */
function renderCart() {
    const cartItems = document.getElementById('cart-items');
    const cartCount = document.getElementById('cart-count');
    const cartTotal = document.getElementById('cart-total');
    const checkoutBtn = document.getElementById('checkout-btn');

    if (!cartItems) return;

    // Update cart count badge
    if (cartCount) {
        cartCount.textContent = getCartCount();
    }

    // Render cart items
    if (cart.length === 0) {
        cartItems.innerHTML = `
            <div class="cart-empty">
                <i class="bi bi-cart3"></i>
                <p>Your cart is empty</p>
                <p class="text-muted">Add some delicious items!</p>
            </div>
        `;
        if (checkoutBtn) checkoutBtn.disabled = true;
    } else {
        cartItems.innerHTML = cart.map(item => `
            <div class="cart-item" data-id="${item.productId}">
                <div class="cart-item-info">
                    <div class="cart-item-name">${item.name}</div>
                    <div class="cart-item-price">${formatCurrency(item.price * item.quantity)}</div>
                </div>
                <div class="cart-item-qty">
                    <button class="qty-btn" onclick="updateQuantity(${item.productId}, -1)">−</button>
                    <span>${item.quantity}</span>
                    <button class="qty-btn" onclick="updateQuantity(${item.productId}, 1)">+</button>
                </div>
            </div>
        `).join('');
        if (checkoutBtn) checkoutBtn.disabled = false;
    }

    // Update total
    if (cartTotal) {
        cartTotal.textContent = formatCurrency(getCartTotal());
    }
}

/**
 * Submit the order
 */
async function checkout() {
    if (cart.length === 0) {
        showToast('Your cart is empty', 'error');
        return;
    }

    const checkoutBtn = document.getElementById('checkout-btn');
    const customerName = document.getElementById('customer-name')?.value || 'Guest';
    const paymentMethod = document.getElementById('payment-method')?.value || 'CASH';

    // Save name to localStorage for future history lookups
    if (customerName !== 'Guest') {
        localStorage.setItem('smartCafeCustomerName', customerName);
    }

    // Disable button and show loading
    checkoutBtn.disabled = true;
    checkoutBtn.innerHTML = '<span class="loading-spinner"></span> Processing...';

    // Prepare order data
    const orderData = {
        customerName: customerName,
        notes: '',
        items: cart.map(item => ({
            productId: item.productId,
            quantity: item.quantity,
            specialRequests: ''
        }))
    };

    try {
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create order');
        }

        const order = await response.json();

        // If not cash, process payment via Xendit
        if (paymentMethod !== 'CASH') {
            checkoutBtn.innerHTML = '<span class="loading-spinner"></span> Opening Payment...';

            // 1. Get Xendit Invoice from Backend
            const xenditResponse = await fetch(`/api/payments/xendit-invoice?orderId=${order.id}`);
            if (!xenditResponse.ok) throw new Error('Failed to initialize payment gateway');
            const { invoiceUrl } = await xenditResponse.json();

            // 2. Open Xendit Invoice in new window
            window.open(invoiceUrl, '_blank');
            showToast('Please complete payment in the new window', 'info');
            orderComplete(order);
        } else {
            // Cash payment
            orderComplete(order);
        }

    } catch (error) {
        console.error('Checkout error:', error);
        showToast(error.message, 'error');
    } finally {
        checkoutBtn.innerHTML = '<i class="bi bi-bag-check"></i> Place Order';
        checkoutBtn.disabled = false;
    }
}

/**
 * Common logic after order/payment is initiated
 */
function orderComplete(order) {
    // Clear cart
    cart = [];
    saveCartToStorage();
    renderCart();

    // Show success message
    showToast(`Order #${order.id} placed successfully!`, 'success');

    // Show confirmation modal
    showOrderConfirmation(order);
}

/**
 * Handle simulated payment
 * @param {number} orderId 
 * @param {string} method 
 * @param {number} amount 
 */
async function processPayment(orderId, method, amount) {
    // Artificial delay to simulate real payment processing
    await new Promise(resolve => setTimeout(resolve, 2000));

    const response = await fetch('/api/payments', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            orderId: orderId,
            method: method,
            amount: amount
        })
    });

    if (!response.ok) {
        throw new Error('Payment failed. Please try again or pay with Cash.');
    }

    return await response.json();
}

/**
 * Show order confirmation
 * @param {Object} order - The created order
 */
function showOrderConfirmation(order) {
    const modal = document.createElement('div');
    modal.className = 'modal fade';
    modal.id = 'confirmationModal';
    modal.innerHTML = `
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body text-center py-5">
                    <div class="mb-4">
                        <i class="bi bi-check-circle-fill text-success" style="font-size: 4rem;"></i>
                    </div>
                    <h3 class="mb-3">Order Confirmed!</h3>
                    <p class="text-muted mb-3">Your order number is</p>
                    <h1 class="text-primary mb-4">#${order.id}</h1>
                    <p class="text-muted">Total: ${formatCurrency(order.totalAmount)}</p>
                    <button class="btn btn-primary btn-lg mt-3" onclick="closeConfirmation()">
                        <i class="bi bi-house"></i> Continue Shopping
                    </button>
                </div>
            </div>
        </div>
    `;
    document.body.appendChild(modal);

    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();

    modal.addEventListener('hidden.bs.modal', function () {
        modal.remove();
    });
}

/**
 * Close confirmation modal
 */
function closeConfirmation() {
    const modal = document.getElementById('confirmationModal');
    if (modal) {
        bootstrap.Modal.getInstance(modal).hide();
    }
}

/**
 * Save cart to localStorage
 */
function saveCartToStorage() {
    localStorage.setItem('smartCafeCart', JSON.stringify(cart));
}

/**
 * Load cart from localStorage
 */
function loadCartFromStorage() {
    const saved = localStorage.getItem('smartCafeCart');
    if (saved) {
        cart = JSON.parse(saved);
    }
}

/**
 * Show toast notification
 * @param {string} message - Message to display
 * @param {string} type - 'success', 'error', or 'info'
 */
function showToast(message, type = 'info') {
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

    // Auto-remove after 3 seconds
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

/**
 * Filter products by category
 * @param {string} category - 'all', 'food', or 'drink'
 */
function filterProducts(category) {
    const cards = document.querySelectorAll('.product-card');
    const buttons = document.querySelectorAll('.filter-btn');

    buttons.forEach(btn => {
        btn.classList.toggle('active', btn.dataset.category === category);
    });

    cards.forEach(card => {
        const cardType = card.dataset.type?.toLowerCase();
        // Use selector that matches Bootstrap column classes (col-sm-6, col-md-4, etc.)
        const column = card.closest('[class*="col"]');

        if (category === 'all') {
            if (column) column.style.display = '';
        } else if (cardType === category) {
            if (column) column.style.display = '';
        } else {
            if (column) column.style.display = 'none';
        }
    });
}

/**
 * Open history modal and load data
 */
async function openHistoryModal() {
    const customerName = document.getElementById('customer-name')?.value || localStorage.getItem('smartCafeCustomerName');

    if (!customerName) {
        showToast('Please enter your name in the cart sidebar first to view your history', 'info');
        return;
    }

    const modal = new bootstrap.Modal(document.getElementById('historyModal'));
    modal.show();

    loadOrderHistory(customerName);
}

/**
 * Load order history for a customer
 * @param {string} name 
 */
async function loadOrderHistory(name) {
    const container = document.getElementById('history-content');
    if (!container) return;

    try {
        const response = await fetch(`/api/orders/customer?name=${encodeURIComponent(name)}`);
        if (!response.ok) throw new Error('Failed to fetch history');
        const orders = await response.json();

        if (orders.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-clock-history text-muted" style="font-size: 3rem;"></i>
                    <p class="mt-3 text-muted">No orders found for "${name}"</p>
                </div>
            `;
            return;
        }

        container.innerHTML = `
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Order ID</th>
                            <th>Time</th>
                            <th>Status</th>
                            <th>Total</th>
                            <th>Items</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${orders.map(order => `
                            <tr>
                                <td class="fw-bold">#${order.id}</td>
                                <td class="small text-muted">${new Date(order.orderTime).toLocaleString()}</td>
                                <td>
                                    <span class="badge status-${order.status.toLowerCase()}">${order.status}</span>
                                </td>
                                <td class="fw-bold">${formatCurrency(order.totalAmount)}</td>
                                <td class="small">
                                    ${order.items.map(item => `${item.quantity}x ${item.productName}`).join(', ')}
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    } catch (error) {
        container.innerHTML = `
            <div class="alert alert-danger m-3">
                <i class="bi bi-exclamation-triangle-fill"></i> Failed to load history.
            </div>
        `;
    }
}

// Add slideOutRight animation
const style = document.createElement('style');
style.textContent = `
    @keyframes slideOutRight {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .status-pending { background: #ffc107; color: #000; }
    .status-preparing { background: #17a2b8; color: white; }
    .status-ready { background: #28a745; color: white; }
    .status-completed { background: #6c757d; color: white; }
    .status-cancelled { background: #dc3545; color: white; }
`;
document.head.appendChild(style);
