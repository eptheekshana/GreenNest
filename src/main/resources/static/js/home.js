document.addEventListener('DOMContentLoaded', function () {
    document.documentElement.classList.add('js-enabled');

    const revealTargets = document.querySelectorAll('[data-reveal]');
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    const revealAll = () => {
        revealTargets.forEach((element) => element.classList.add('is-visible'));
    };

    if (revealTargets.length === 0 || prefersReducedMotion || !('IntersectionObserver' in window)) {
        revealAll();
        return;
    }

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
                obs.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.12,
        rootMargin: '0px 0px -5% 0px'
    });

    revealTargets.forEach((element) => observer.observe(element));
});

