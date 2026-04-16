document.addEventListener('DOMContentLoaded', function () {
    document.documentElement.classList.add('js-enabled');

    const revealTargets = document.querySelectorAll('[data-reveal]');
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    const revealAll = () => {
        revealTargets.forEach((element) => element.classList.add('is-visible'));
    };

    if (prefersReducedMotion || !('IntersectionObserver' in window)) {
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
        threshold: 0.16,
        rootMargin: '0px 0px -8% 0px'
    });

    revealTargets.forEach((element) => observer.observe(element));

    const hero = document.querySelector('.hero-content');
    if (!hero) {
        return;
    }

    let rafId = null;

    const resetTilt = () => {
        hero.style.removeProperty('--tilt-x');
        hero.style.removeProperty('--tilt-y');
    };

    hero.addEventListener('pointermove', function (event) {
        if (prefersReducedMotion) {
            return;
        }

        const rect = hero.getBoundingClientRect();
        const x = (event.clientX - rect.left) / rect.width - 0.5;
        const y = (event.clientY - rect.top) / rect.height - 0.5;

        if (rafId) {
            cancelAnimationFrame(rafId);
        }

        rafId = requestAnimationFrame(() => {
            hero.style.setProperty('--tilt-x', `${x * 6}deg`);
            hero.style.setProperty('--tilt-y', `${y * -6}deg`);
        });
    });

    hero.addEventListener('pointerleave', resetTilt);
});

