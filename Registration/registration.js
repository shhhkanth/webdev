function validateForm() {
    clearErrors();
    let valid = true;

    const name  = document.getElementById('name').value.trim();
    const regno = document.getElementById('regno').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const dob   = document.getElementById('dob').value;
    const dept  = document.getElementById('department').value.trim();
    const sem   = document.getElementById('semester').value;
    const genderEl = document.querySelector('input[name="gender"]:checked');

    if (!name)                                  { showError('name',   'Full name is required.');           valid = false; }
    if (!regno)                                 { showError('regno',  'Register number is required.');     valid = false; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { showError('email', 'Enter a valid email address.'); valid = false; }
    if (!/^\d{10}$/.test(phone))                { showError('phone',  'Enter a valid 10-digit number.');  valid = false; }
    if (!dob)                                   { showError('dob',    'Date of birth is required.');       valid = false; }
    if (!genderEl)                              { showError('gender', 'Please select a gender.');          valid = false; }
    if (!dept)                                  { showError('department', 'Department is required.');      valid = false; }
    if (!sem)                                   { showError('semester',   'Please select a semester.');    valid = false; }

    return valid;
}

function showError(fieldId, msg) {
    const el = document.getElementById(fieldId + '-error');
    if (el) el.textContent = msg;
}

function clearErrors() {
    document.querySelectorAll('.field-error').forEach(el => el.textContent = '');
}
