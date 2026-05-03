// register.js
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const confirmPasswordError = document.getElementById('confirmPasswordError');

    // Real-time password matching validation
    function validatePasswordMatch() {
        if (confirmPassword.value === '') {
            confirmPasswordError.textContent = '';
            confirmPassword.classList.remove('is-invalid');
            return true;
        }

        if (password.value !== confirmPassword.value) {
            confirmPasswordError.textContent = 'Passwords do not match';
            confirmPassword.classList.add('is-invalid');
            return false;
        } else {
            confirmPasswordError.textContent = '';
            confirmPassword.classList.remove('is-invalid');
            return true;
        }
    }

    // Validate on input
    if (confirmPassword) {
        confirmPassword.addEventListener('input', validatePasswordMatch);
        password.addEventListener('input', function() {
            if (confirmPassword.value !== '') {
                validatePasswordMatch();
            }
        });
    }

    // Validate on form submission
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validatePasswordMatch()) {
                e.preventDefault();
                confirmPassword.focus();
            }
        });
    }

    console.log('Register page loaded with password confirmation validation');
});