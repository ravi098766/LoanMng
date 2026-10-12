// -------------------------------------------------------------
// Loan Management System - Auth Interactivity
// -------------------------------------------------------------

document.addEventListener('DOMContentLoaded', () => {

    // 1. Password Visibility Toggles
    const toggleButtons = document.querySelectorAll('.toggle-password');
    toggleButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetId = btn.getAttribute('data-target');
            const targetInput = document.getElementById(targetId);
            const icon = btn.querySelector('i');

            if (!targetInput) return;

            if (targetInput.type === 'password') {
                targetInput.type = 'text';
                if (icon) {
                    icon.classList.remove('bi-eye');
                    icon.classList.add('bi-eye-slash');
                }
            } else {
                targetInput.type = 'password';
                if (icon) {
                    icon.classList.remove('bi-eye-slash');
                    icon.classList.add('bi-eye');
                }
            }
        });
    });

    // 2. Demo Credentials Auto-Fill
    const demoButtons = document.querySelectorAll('.fill-demo');
    demoButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const usernameInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');
            if (usernameInput && passwordInput) {
                usernameInput.value = btn.getAttribute('data-user');
                passwordInput.value = btn.getAttribute('data-pass');
                usernameInput.focus();
            }
        });
    });

    // 3. Password Match Checker in Registration Form
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const matchMessage = document.getElementById('passwordMatchMessage');
    const strengthBar = document.getElementById('strengthBar');
    const strengthProgress = document.getElementById('strengthProgress');
    const strengthText = document.getElementById('strengthText');

    if (passwordInput && confirmPasswordInput && matchMessage) {
        const checkMatch = () => {
            const pass = passwordInput.value;
            const confirm = confirmPasswordInput.value;

            if (!confirm) {
                matchMessage.textContent = '';
                matchMessage.className = 'form-text';
                return;
            }

            if (pass === confirm) {
                matchMessage.textContent = '✓ Passwords match';
                matchMessage.className = 'form-text text-success fw-semibold';
            } else {
                matchMessage.textContent = '✗ Passwords do not match';
                matchMessage.className = 'form-text text-danger fw-semibold';
            }
        };

        const checkStrength = () => {
            if (!strengthBar || !strengthProgress || !strengthText) return;
            const val = passwordInput.value;
            if (!val) {
                strengthBar.classList.add('d-none');
                return;
            }
            strengthBar.classList.remove('d-none');

            let score = 0;
            if (val.length >= 6) score += 25;
            if (val.length >= 10) score += 25;
            if (/[0-9]/.test(val)) score += 25;
            if (/[^A-Za-z0-9]/.test(val)) score += 25;

            strengthProgress.style.width = score + '%';
            if (score <= 25) {
                strengthProgress.className = 'progress-bar bg-danger';
                strengthText.textContent = 'Weak';
                strengthText.className = 'small text-danger';
            } else if (score <= 50) {
                strengthProgress.className = 'progress-bar bg-warning';
                strengthText.textContent = 'Moderate';
                strengthText.className = 'small text-warning';
            } else if (score <= 75) {
                strengthProgress.className = 'progress-bar bg-info';
                strengthText.textContent = 'Good';
                strengthText.className = 'small text-info';
            } else {
                strengthProgress.className = 'progress-bar bg-success';
                strengthText.textContent = 'Strong';
                strengthText.className = 'small text-success';
            }
        };

        passwordInput.addEventListener('input', () => {
            checkStrength();
            checkMatch();
        });

        confirmPasswordInput.addEventListener('input', checkMatch);
    }
});
