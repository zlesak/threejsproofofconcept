import {expect, test} from '@playwright/test';
import {acceptCookiesIfVisible, loginAsTeacher} from './helpers';

/**
 * Checks the structural accessibility properties that group G of the implementation brief
 * introduced, on every route a teacher can reach.
 *
 * Deliberately not a full axe-core sweep: axe would pull an extra dependency and, more to the point,
 * it grades the whole page including everything groups N through V have not touched yet, so it would
 * report the same known findings on every run and stop being read. These assertions are narrow —
 * each one corresponds to a task that is finished, so a failure means a regression rather than
 * unfinished work.
 */

const TEACHER_ROUTES = [
  '/',
  '/chapters',
  '/models',
  '/quizes',
  '/quizes-results',
  '/administration',
  '/documentation',
];

test('every route declares its language, one main landmark and a named navigation', async ({page}) => {
  test.setTimeout(180000);
  await loginAsTeacher(page);

  for (const route of TEACHER_ROUTES) {
    await test.step(`route ${route}`, async () => {
      await page.goto(route);

      // WCAG 3.1.1: without it a screen reader reads Czech with English pronunciation rules.
      await expect(page.locator('html')).toHaveAttribute('lang', 'cs');

      // WCAG 1.3.1: AppLayout gives a header and a nav but no main region of its own.
      await expect(page.locator('main#obsah')).toHaveCount(1);

      const namedNav = page.locator('nav[aria-label]');
      expect(await namedNav.count()).toBeGreaterThan(0);
    });
  }
});

test('the skip link comes first and moves the user to the content', async ({page}) => {
  await loginAsTeacher(page);
  await page.goto('/chapters');

  const skipLink = page.locator('a.skip-to-content');

  // WCAG 2.4.1. Asserted as document order rather than by simulating Tab: the application renders
  // inside shadow roots, where a headless browser reports focus on the host rather than on the
  // element, and the resulting assertion would pass or fail for the wrong reasons.
  const isFirstInHeader = await skipLink.evaluate((link) => {
    const header = link.closest('header');
    if (!header) {
      return false;
    }
    const focusable = header.querySelectorAll('a[href], button, [tabindex]:not([tabindex="-1"])');
    return focusable[0] === link;
  });
  expect(isFirstInHeader).toBe(true);

  // Clipped to a pixel until it has focus, then laid out properly. Not toBeHidden(): a
  // screen-reader-only element is deliberately still rendered — that is what keeps it in the tab
  // order and readable — so it counts as visible. The size is what changes.
  const clipped = await skipLink.boundingBox();
  expect(clipped?.width ?? 0).toBeLessThan(5);

  await skipLink.focus();
  const revealed = await skipLink.boundingBox();
  expect(revealed?.width ?? 0).toBeGreaterThan(100);
  await expect(skipLink).toHaveText('Přeskočit na hlavní obsah');

  await skipLink.press('Enter');
  await expect(page).toHaveURL(/#obsah$/);
});

test('the showcase animations start stopped and can be stopped again', async ({page}) => {
  await page.goto('/');
  await acceptCookiesIfVisible(page);

  const playButtons = page.getByRole('button', {name: 'Přehrát ukázku'});
  await expect(playButtons).toHaveCount(3);

  const firstAnimation = page.locator('img[data-gif-src]').first();
  // WCAG 2.2.2: the three GIFs used to loop from the moment the section scrolled into view, for far
  // longer than five seconds, with nothing to stop them. Nothing runs until asked.
  await expect(firstAnimation).toHaveAttribute('src', /^data:image\/gif;base64,/);

  const firstPlay = playButtons.first();
  await firstPlay.click();
  await expect(firstAnimation).toHaveAttribute('src', /modelgif\.gif$/);

  const stop = page.getByRole('button', {name: 'Zastavit ukázku'}).first();
  await expect(stop).toBeVisible();
  await stop.click();
  // Back to a still picture: either the frozen frame or the blank pixel, never the running GIF.
  await expect(firstAnimation).not.toHaveAttribute('src', /modelgif\.gif$/);
});

test('the cookie bar offers refusing as plainly as agreeing', async ({page}) => {
  await page.goto('/');

  const accept = page.getByRole('button', {name: 'Přijmout'});
  const decline = page.getByRole('button', {name: 'Odmítnout'});

  await expect(accept).toBeVisible();
  // A bar with only an acknowledgement is not a choice.
  await expect(decline).toBeVisible();

  await decline.click();
  await expect(decline).toHaveCount(0);
});
