const BASE_URL = 'http://localhost:8080';

// Global toast notification system
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerText = message;
    container.appendChild(toast);
    
    // Trigger animation
    setTimeout(() => toast.classList.add('show'), 10);
    
    // Auto-remove toast after 4s
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
    return container;
}

// Global API Fetch client
const API = {
    async request(url, options = {}) {
        const fullUrl = url.startsWith('http') ? url : `${BASE_URL}${url}`;
        
        // Prepare headers
        options.headers = options.headers || {};
        options.headers['Content-Type'] = options.headers['Content-Type'] || 'application/json';
        
        const token = localStorage.getItem('accessToken');
        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }
        
        try {
            let response = await fetch(fullUrl, options);
            
            // Check for unauthorized access token expiration
            if (response.status === 401) {
                // Prevent infinite loop if already calling refresh endpoint
                if (url !== '/api/auth/refresh') {
                    const refreshed = await this.refreshToken();
                    if (refreshed) {
                        // Retry original request with new access token
                        const newToken = localStorage.getItem('accessToken');
                        options.headers['Authorization'] = `Bearer ${newToken}`;
                        response = await fetch(fullUrl, options);
                    } else {
                        // Refresh token failed/expired
                        this.logoutAndRedirect();
                        throw new Error('Session expired. Please log in again.');
                    }
                }
            }
            
            const data = await response.json();
            
            if (!response.ok) {
                const errMsg = data.message || `Request failed with status ${response.status}`;
                throw new Error(errMsg);
            }
            
            return data;
        } catch (error) {
            console.error('API Error:', error.message);
            showToast(error.message, 'error');
            throw error;
        }
    },
    
    async get(url) {
        return this.request(url, { method: 'GET' });
    },
    
    async post(url, body) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    },
    
    async put(url, body) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    },
    
    async delete(url) {
        return this.request(url, { method: 'DELETE' });
    },
    
    async refreshToken() {
        const refresh = localStorage.getItem('refreshToken');
        if (!refresh) return false;
        
        try {
            const res = await fetch(`${BASE_URL}/api/auth/refresh`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken: refresh })
            });
            
            if (res.ok) {
                const result = await res.json();
                if (result.success && result.data) {
                    localStorage.setItem('accessToken', result.data.accessToken);
                    localStorage.setItem('refreshToken', result.data.refreshToken);
                    return true;
                }
            }
            return false;
        } catch (err) {
            console.error('Token refresh execution failed', err);
            return false;
        }
    },
    
    logoutAndRedirect() {
        const refresh = localStorage.getItem('refreshToken');
        if (refresh) {
            // Non-blocking logout call to backend
            fetch(`${BASE_URL}/api/auth/logout`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken: refresh })
            }).catch(() => {});
        }
        localStorage.clear();
        window.location.href = 'index.html';
    }
};
