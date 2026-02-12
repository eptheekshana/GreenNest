// Admin login page interactions
document.addEventListener('DOMContentLoaded', function() {
    console.log('Nboard Admin Login Page Loaded');

    // Form validation
    const loginForm = document.querySelector('form');
    const emailInput = document.querySelector('input[name="username"]');
    const passwordInput = document.querySelector('input[name="password"]');

    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            // Basic client-side validation
            if (!emailInput.value || !passwordInput.value) {
                e.preventDefault();
                alert('Please fill in all fields');
                return false;
            }

            // Email format validation
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(emailInput.value)) {
                e.preventDefault();
                alert('Please enter a valid email address');
                return false;
            }
        });
    }

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-error, .alert-success');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Add loading state to button on submit
    if (loginForm) {
        loginForm.addEventListener('submit', function() {
            const submitBtn = this.querySelector('.btn-login');
            if (submitBtn) {
                submitBtn.textContent = 'Signing in...';
                submitBtn.disabled = true;
            }
        });
    }
});

