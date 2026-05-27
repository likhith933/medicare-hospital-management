// Authentication Management Utilities
const Auth = {
    getUser() {
        const id = localStorage.getItem('userId');
        const name = localStorage.getItem('userName');
        const email = localStorage.getItem('userEmail');
        const role = localStorage.getItem('userRole');
        
        if (!role) return null;
        
        return { id: parseInt(id), name, email, role };
    },
    
    checkAuth(requiredRole) {
        const user = this.getUser();
        if (!user) {
            window.location.href = 'index.html';
            return;
        }
        
        if (requiredRole && user.role !== requiredRole) {
            // Role mismatch, redirect to correct dashboard
            this.redirectToDashboard(user.role);
        }
    },
    
    redirectToDashboard(role) {
        if (role === 'ROLE_PATIENT') {
            window.location.href = 'patient-dashboard.html';
        } else if (role === 'ROLE_DOCTOR') {
            window.location.href = 'doctor-dashboard.html';
        } else if (role === 'ROLE_ADMIN') {
            window.location.href = 'admin-dashboard.html';
        } else {
            window.location.href = 'index.html';
        }
    },
    
    async login(email, password, role) {
        try {
            const result = await API.post('/api/auth/login', { email, password, role });
            if (result.success && result.data) {
                const data = result.data;
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                localStorage.setItem('userId', data.id);
                localStorage.setItem('userName', data.name);
                localStorage.setItem('userEmail', data.email);
                localStorage.setItem('userRole', data.role);
                
                showToast('Login successful! Redirecting...', 'success');
                setTimeout(() => {
                    this.redirectToDashboard(data.role);
                }, 1000);
            }
        } catch (err) {
            console.error('Authentication login action failed', err);
        }
    },
    
    async register(name, age, email, password, phone, bloodGroup) {
        try {
            const result = await API.post('/api/auth/register', {
                name,
                age: parseInt(age),
                email,
                password,
                phone,
                bloodGroup
            });
            if (result.success) {
                showToast('Registration successful! Please log in.', 'success');
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            }
        } catch (err) {
            console.error('Registration submission action failed', err);
        }
    },
    
    logout() {
        API.logoutAndRedirect();
    }
};
