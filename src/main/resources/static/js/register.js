// register.js - Enhanced with diagnostics
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const confirmPasswordError = document.getElementById('confirmPasswordError');

    console.log('Register page initializing...');
    console.log('Form element:', form);
    console.log('Form action:', form?.action);
    console.log('Form method:', form?.method);

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
            console.log('Form submission triggered');
            console.log('Validating password match...');

            if (!validatePasswordMatch()) {
                console.log('Password validation failed - preventing submission');
                e.preventDefault();
                confirmPassword.focus();
                return false;
            }

            console.log('Form validation passed - allowing submission');
            console.log('Submitting to:', form.action);
        });
    }

    console.log('Register page loaded with password confirmation validation');
});