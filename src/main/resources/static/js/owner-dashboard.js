// Owner Dashboard interactions
document.addEventListener('DOMContentLoaded', function() {
    console.log('GreenNest Owner Dashboard Loaded');

    // Confirm before deleting property
    const deleteButtons = document.querySelectorAll('.btn-trash');
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            if (!confirm('Are you sure you want to delete this property listing? This action cannot be undone.')) {
                e.preventDefault();
                return false;
            }
        });
    });

    // Add smooth scroll for internal navigation
    const navLinks = document.querySelectorAll('.nav-item');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Remove active class from all
            navLinks.forEach(l => l.classList.remove('active'));
            // Add active class to clicked
            this.classList.add('active');
        });
    });

    // Auto-hide warning banner after 10 seconds
    const warningBanner = document.querySelector('.warning-banner');
    if (warningBanner) {
        setTimeout(() => {
            warningBanner.style.transition = 'opacity 0.5s, transform 0.5s';
            warningBanner.style.opacity = '0';
            warningBanner.style.transform = 'translateY(-10px)';
            setTimeout(() => warningBanner.remove(), 500);
        }, 10000);
    }
});

