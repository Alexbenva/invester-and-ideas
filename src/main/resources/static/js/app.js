document.addEventListener('DOMContentLoaded', () => {
  const flashAlerts = document.querySelectorAll('.alert[data-autoclose="true"]');
  flashAlerts.forEach((alert) => {
    window.setTimeout(() => {
      alert.classList.add('fade');
    }, 3000);
  });

  const sidebarLinks = document.querySelectorAll('.vb-sidebar-link');
  sidebarLinks.forEach((link) => {
    if (link.getAttribute('href') === window.location.pathname) {
      link.classList.add('active');
    }
  });
});