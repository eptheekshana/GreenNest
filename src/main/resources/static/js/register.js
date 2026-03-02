// register.js
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const confirmPasswordError = document.getElementById('confirmPasswordError');
    const passwordRequirements = document.querySelector('.password-requirements');

    // Password complexity requirements
    const requirements = {
        length: { regex: /.{8,}/, element: document.getElementById('req-length'), text: 'At least 8 characters' },
        upper: { regex: /[A-Z]/, element: document.getElementById('req-upper'), text: 'At least one uppercase letter (A-Z)' },
        lower: { regex: /[a-z]/, element: document.getElementById('req-lower'), text: 'At least one lowercase letter (a-z)' },
        number: { regex: /[0-9]/, element: document.getElementById('req-number'), text: 'At least one number (0-9)' },
        special: { regex: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/, element: document.getElementById('req-special'), text: 'At least one special character (!@#$%^&*)' }
    };

    // Check password complexity
    function checkPasswordComplexity() {
        let allMet = true;

        for (const req of Object.values(requirements)) {
            const isMet = req.regex.test(password.value);

            if (isMet) {
                req.element.classList.add('met');
            } else {
                req.element.classList.remove('met');
                allMet = false;
            }
        }

        return allMet;
    }

    // Show/hide and update requirements display
    function updatePasswordRequirements() {
        if (password.value.length > 0) {
            passwordRequirements.classList.add('active');
            checkPasswordComplexity();
        } else {
            passwordRequirements.classList.remove('active');
        }
    }

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

    // Validate on input - password field
    if (password) {
        password.addEventListener('input', function() {
            updatePasswordRequirements();
            if (confirmPassword.value !== '') {
                validatePasswordMatch();
            }
        });
    }

    // Validate on input - confirm password field
    if (confirmPassword) {
        confirmPassword.addEventListener('input', validatePasswordMatch);
    }

    // Validate on form submission
    if (form) {
        form.addEventListener('submit', function(e) {
            const passwordsMatch = validatePasswordMatch();
            const complexityMet = checkPasswordComplexity();

            if (!passwordsMatch) {
                e.preventDefault();
                confirmPassword.focus();
            }

            if (!complexityMet) {
                e.preventDefault();
                password.focus();
            }
        });
    }

    console.log('Register page loaded with password complexity and confirmation validation');
});