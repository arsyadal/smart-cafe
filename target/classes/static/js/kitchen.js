/**
 * Smart Cafe - Kitchen Dashboard JavaScript
 * 
 * Handles:
 * - WebSocket connection (STOMP over SockJS)
 * - Real-time order updates
 * - Order status management
 * - Audio notifications
 */

// WebSocket connection
let stompClient = null;
let connected = false;

// Audio notification
const notificationSound = new Audio('data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdH2FipONhoaIj4+PjoyJhX95b2VhYWVqdHqAgoSDg4OEg4F+end0cnFxc3d6f4OGiIqLi4qJh4R/end0cXBwc3Z7f4OHio2OjoyKh4N+eHRxb29xdHl+g4iMj5CPjoyJhYB6dXFvb3F1en+EiY2QkI+NioeDfnh0cG9vcnZ7gIWKjZCQj42Kh4J9eHRwb3BydnyBhouOkJCPjYqGgn14dHBvb3J2fIGGi46QkI+NioaCfXh0cHBwc3d8gYaLjpCQj42KhoJ9eHRwcHBydn2BhoyPkJCPjYqGgn14dHBwcHJ2fYKHjI+QkI+NioaCfXh0cHBwc3Z9goeMj5CQj42KhoJ9eHRwcHBydn2Ch4yPkJCPjYqGgn14dHBwcHN3fYKHjI+QkI+NioaCfXh0cHBwcnZ9goeMj5CQj42KhoJ9eHRwcHBydn2Ch4yPkJCPjYqGgn14dHBwcHJ2fXKHjI+QkI==');

/**
 * Initialize the kitchen dashboard
 */
document.addEventListener('DOMContentLoaded', function () {
    console.log('Kitchen Dashboard initialized');
    connect();
    loadActiveOrders();
});

/**
 * Connect to WebSocket server
 */
function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    // Disable debug logging
    stompClient.debug = null;

    stompClient.connect({}, function (frame) {
        console.log('Connected to WebSocket:', frame);
        setConnectionStatus(true);

        // Subscribe to kitchen topic
        stompClient.subscribe('/topic/kitchen', function (message) {
            const order = JSON.parse(message.body);
            handleNewOrder(order);
        });

    }, function (error) {
        console.error('WebSocket connection error:', error);
        setConnectionStatus(false);

        // Try to reconnect after 5 seconds
        setTimeout(connect, 5000);
    });
}

/**
 * Update connection status indicator
 * @param {boolean} isConnected 
 */
function setConnectionStatus(isConnected) {
    connected = isConnected;
    const statusEl = document.getElementById('connection-status');

    if (statusEl) {
        statusEl.className = `connection-status ${isConnected ? 'connected' : 'disconnected'}`;
        statusEl.innerHTML = `
            <span class="status-dot"></span>
            ${isConnected ? 'Connected' : 'Disconnected'}
        `;
    }
}

/**
 * Handle incoming order from WebSocket
 * @param {Object} order - Order data
 */
function handleNewOrder(order) {
    console.log('Received order:', order);

    // Check if order already exists (update) or is new
    const existingCard = document.querySelector(`[data-order-id="${order.id}"]`);

    if (existingCard) {
        // Update existing order
        updateOrderCard(existingCard, order);
    } else {
        // New order - add to list
        addOrderCard(order);
        playNotification();
    }
}

/**
 * Load active orders on page load
 */
async function loadActiveOrders() {
    try {
        const response = await fetch('/api/orders/active');
        if (!response.ok) throw new Error('Failed to load orders');

        const orders = await response.json();
        const container = document.getElementById('orders-container');

        if (container) {
            container.innerHTML = '';
            orders.forEach(order => addOrderCard(order, false));

            if (orders.length === 0) {
                container.innerHTML = `
                    <div class="text-center py-5">
                        <i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
                        <p class="text-muted mt-3">No active orders</p>
                        <p class="text-muted">New orders will appear here automatically</p>
                    </div>
                `;
            }
        }
    } catch (error) {
        console.error('Failed to load orders:', error);
    }
}

/**
 * Add a new order card to the dashboard
 * @param {Object} order - Order data
 * @param {boolean} isNew - Whether this is a new order (for animation)
 */
function addOrderCard(order, isNew = true) {
    const container = document.getElementById('orders-container');
    if (!container) return;

    // Remove empty state message if present
    const emptyState = container.querySelector('.text-center');
    if (emptyState) emptyState.remove();

    const card = document.createElement('div');
    card.className = `order-card ${isNew ? 'new-order' : ''}`;
    card.dataset.orderId = order.id;

    card.innerHTML = createOrderCardHTML(order);

    // Add to beginning of list
    container.insertBefore(card, container.firstChild);

    // Remove new-order class after animation
    if (isNew) {
        setTimeout(() => card.classList.remove('new-order'), 2000);
    }
}

/**
 * Update an existing order card
 * @param {Element} card - The card element
 * @param {Object} order - Updated order data
 */
function updateOrderCard(card, order) {
    card.innerHTML = createOrderCardHTML(order);
    card.classList.add('new-order');
    setTimeout(() => card.classList.remove('new-order'), 1000);

    // If order is completed or cancelled, fade out and remove
    if (order.status === 'COMPLETED' || order.status === 'CANCELLED') {
        setTimeout(() => {
            card.style.transition = 'all 0.5s ease';
            card.style.opacity = '0';
            card.style.transform = 'translateX(100%)';
            setTimeout(() => card.remove(), 500);
        }, 2000);
    }
}

/**
 * Create HTML for an order card
 * @param {Object} order - Order data
 * @returns {string} HTML string
 */
function createOrderCardHTML(order) {
    const statusClass = `status-${order.status.toLowerCase()}`;
    const formattedTime = new Date(order.orderTime).toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit'
    });

    const itemsHTML = order.items.map(item => `
        <li>
            <span>
                <span class="item-qty">${item.quantity}x</span>
                ${item.productName}
                ${item.specialRequests ? `<small class="text-muted">(${item.specialRequests})</small>` : ''}
            </span>
            <span class="badge bg-secondary">${item.productType}</span>
        </li>
    `).join('');

    const actionsHTML = getOrderActions(order);

    return `
        <div class="order-header">
            <div>
                <div class="order-number">Order #${order.id}</div>
                <div class="order-time"><i class="bi bi-clock"></i> ${formattedTime}</div>
            </div>
            <span class="order-status ${statusClass}">${order.status}</span>
        </div>
        <div class="order-body">
            ${order.customerName ? `<p class="mb-2"><strong><i class="bi bi-person"></i> ${order.customerName}</strong></p>` : ''}
            <ul class="order-items">${itemsHTML}</ul>
            <div class="order-total mb-3">
                <strong>Total: $${order.totalAmount.toFixed(2)}</strong>
            </div>
            <div class="order-actions">${actionsHTML}</div>
        </div>
    `;
}

/**
 * Get action buttons based on order status
 * @param {Object} order - Order data
 * @returns {string} HTML for action buttons
 */
function getOrderActions(order) {
    switch (order.status) {
        case 'PENDING':
            return `
                <button class="btn-status btn-preparing" onclick="updateStatus(${order.id}, 'PREPARING')">
                    <i class="bi bi-fire"></i> Start Preparing
                </button>
            `;
        case 'PREPARING':
            return `
                <button class="btn-status btn-ready" onclick="updateStatus(${order.id}, 'READY')">
                    <i class="bi bi-check-circle"></i> Mark Ready
                </button>
            `;
        case 'READY':
            return `
                <button class="btn-status btn-complete" onclick="updateStatus(${order.id}, 'COMPLETED')">
                    <i class="bi bi-bag-check"></i> Complete
                </button>
            `;
        default:
            return '';
    }
}

/**
 * Update order status via API
 * @param {number} orderId - Order ID
 * @param {string} status - New status
 */
async function updateStatus(orderId, status) {
    try {
        const response = await fetch(`/api/orders/${orderId}/status`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: status })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update status');
        }

        // The WebSocket will handle the UI update
        console.log(`Order #${orderId} status updated to ${status}`);

    } catch (error) {
        console.error('Failed to update status:', error);
        alert('Failed to update order status: ' + error.message);
    }
}

/**
 * Play notification sound
 */
function playNotification() {
    try {
        notificationSound.currentTime = 0;
        notificationSound.play().catch(e => console.log('Audio play failed:', e));
    } catch (e) {
        console.log('Audio notification error:', e);
    }

    // Also flash the page title
    const originalTitle = document.title;
    document.title = '🔔 NEW ORDER!';
    setTimeout(() => document.title = originalTitle, 3000);
}

/**
 * Disconnect WebSocket (call when leaving page)
 */
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnectionStatus(false);
    console.log('Disconnected from WebSocket');
}

// Handle page unload
window.addEventListener('beforeunload', disconnect);
