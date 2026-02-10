// Admin dashboard interactions
document.addEventListener('DOMContentLoaded', function() {
    console.log('GreenNest Admin Dashboard Loaded');

    // Auto-hide success messages after 5 seconds
    const alerts = document.querySelectorAll('.alert-success');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Confirm before approving
    const approveBtns = document.querySelectorAll('.btn-approve');
    approveBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            if (!confirm('Are you sure you want to approve this?')) {
                e.preventDefault();
            }
        });
    });
});

