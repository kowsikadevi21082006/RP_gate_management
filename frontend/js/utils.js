export function norm(value) { return String(value ?? '').trim().toLowerCase().replace(/[^a-z0-9]/g, ''); }
export function esc(value) { return String(value ?? '').replace(/[&<>"']/g, (match) => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', '"':'&quot;', "'":'&#039;' }[match])); }
export function val(id) { return document.getElementById(id)?.value || ''; }
export function downloadBlob(content, name, type) { const link = document.createElement('a'); link.href = URL.createObjectURL(new Blob([content], { type })); link.download = name; link.click(); setTimeout(() => URL.revokeObjectURL(link.href), 500); }
