const ctx = document.querySelector('base')?.href || window.location.pathname.replace(/\/[^/]*$/, '');

// ── Load students on page load ────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    showBannerFromParams();
    loadStudents();
});

function loadStudents() {
    fetch('students')
        .then(r => r.json())
        .then(renderTable)
        .catch(() => {
            document.getElementById('tableBody').innerHTML =
                '<tr><td colspan="11" class="loading">Failed to load data.</td></tr>';
        });
}

function renderTable(students) {
    const tbody = document.getElementById('tableBody');
    if (!students.length) {
        tbody.innerHTML = '<tr><td colspan="11" class="loading">No students registered yet.</td></tr>';
        return;
    }
    tbody.innerHTML = students.map((s, i) => `
        <tr>
            <td>${i + 1}</td>
            <td>${safe(s.name)}</td>
            <td>${safe(s.regno)}</td>
            <td>${safe(s.email)}</td>
            <td>${safe(s.phone)}</td>
            <td>${safe(s.dob)}</td>
            <td>${safe(s.gender)}</td>
            <td>${safe(s.course)}</td>
            <td>${safe(s.department)}</td>
            <td>${safe(s.semester)}</td>
            <td>
                <button class="btn-edit"   onclick='openEdit(${JSON.stringify(s)})'>Edit</button>
                <button class="btn-delete" onclick="openDelete(${s.id})">Delete</button>
            </td>
        </tr>`).join('');
}

// ── Edit Modal ────────────────────────────────────────────────────────────
function openEdit(s) {
    document.getElementById('edit-id').value         = s.id;
    document.getElementById('edit-name').value       = s.name;
    document.getElementById('edit-regno').value      = s.regno;
    document.getElementById('edit-email').value      = s.email;
    document.getElementById('edit-phone').value      = s.phone;
    document.getElementById('edit-dob').value        = s.dob;
    document.getElementById('edit-gender').value     = s.gender;
    document.getElementById('edit-course').value     = s.course;
    document.getElementById('edit-department').value = s.department;
    document.getElementById('edit-semester').value   = s.semester;
    document.getElementById('edit-address').value    = s.address;
    document.getElementById('editModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

function validateEditForm() {
    let valid = true;
    document.querySelectorAll('#editForm .field-error').forEach(el => el.textContent = '');

    const phone = document.getElementById('edit-phone').value.trim();
    const email = document.getElementById('edit-email').value.trim();
    const name  = document.getElementById('edit-name').value.trim();
    const regno = document.getElementById('edit-regno').value.trim();

    if (!name)                                      { setErr('edit-name-error',  'Required.');                  valid = false; }
    if (!regno)                                     { setErr('edit-regno-error', 'Required.');                  valid = false; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { setErr('edit-email-error', 'Enter a valid email.');       valid = false; }
    if (!/^\d{10}$/.test(phone))                    { setErr('edit-phone-error', '10-digit number required.');  valid = false; }

    return valid;
}

function setErr(id, msg) {
    const el = document.getElementById(id);
    if (el) el.textContent = msg;
}

// ── Delete Modal ──────────────────────────────────────────────────────────
function openDelete(id) {
    document.getElementById('confirmDeleteBtn').href = 'students?delete=' + id;
    document.getElementById('deleteModal').style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
}

// Close modals on overlay click
document.getElementById('editModal').addEventListener('click', function(e) {
    if (e.target === this) closeModal();
});
document.getElementById('deleteModal').addEventListener('click', function(e) {
    if (e.target === this) closeDeleteModal();
});

// ── Banner from query params ──────────────────────────────────────────────
function showBannerFromParams() {
    const params  = new URLSearchParams(window.location.search);
    const banner  = document.getElementById('msg-banner');
    const success = params.get('success');
    const error   = params.get('error');

    if (success === 'registered') { show(banner, 'Student registered successfully!', 'success'); }
    else if (success === 'updated')    { show(banner, 'Student updated successfully!',    'success'); }
    else if (success === 'deleted')    { show(banner, 'Student deleted successfully!',    'success'); }
    else if (error)                    { show(banner, 'A database error occurred.',        'error');   }
}

function show(el, msg, type) {
    el.textContent = msg;
    el.className   = 'msg-banner ' + type;
    el.style.display = 'block';
}

// ── HTML escape helper ────────────────────────────────────────────────────
function safe(val) {
    if (!val) return '';
    return String(val)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}
