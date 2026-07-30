import {expect, test as setup} from '@playwright/test';
import {
  chooseAnyModelForChapter,
  chooseAnyQuizChapter,
  createModelForE2E,
  fillByPlaceholder,
  loginAsTeacher,
  logStep,
  selectAdministrationTab,
  waitForChapterSave,
  waitForEntityCardVisible,
} from './helpers';

/**
 * Seeds the content the CRUD specs expect to already exist: one 3D model and one chapter.
 * Without this, a run against a fresh database fails at the first "pick a model" dialog, and the
 * specs would silently depend on each other's leftovers instead of on a known starting point.
 * Both steps are skipped when the fixture is already present, so reruns stay fast.
 */
export const FIXTURE_MODEL = 'E2E Fixture Model';
export const FIXTURE_CHAPTER = 'E2E Fixture Kapitola';
export const FIXTURE_QUIZ = 'E2E Fixture Kvíz';

setup('seed fixture model and chapter', async ({page}) => {
  setup.setTimeout(180000);

  await logStep('Login as teacher', async () => {
    await loginAsTeacher(page);
  });

  await logStep('Ensure fixture model exists', async () => {
    await selectAdministrationTab(page, 'Modely');
    if (await waitForEntityCardVisible(page, FIXTURE_MODEL, 5000)) {
      return;
    }
    await createModelForE2E(page, FIXTURE_MODEL);
  });

  await logStep('Ensure fixture chapter exists', async () => {
    await selectAdministrationTab(page, 'Kapitoly');
    if (await waitForEntityCardVisible(page, FIXTURE_CHAPTER, 5000)) {
      return;
    }

    await page.goto('/createChapter');
    await expect(page.getByRole('button', {name: 'Vytvořit kapitolu'})).toBeVisible();
    await fillByPlaceholder(page, 'Název', FIXTURE_CHAPTER);
    const editor = page.locator('[contenteditable="true"]').first();
    await expect(editor).toBeVisible();
    await editor.click();
    await page.keyboard.type('Obsah výchozí kapitoly pro automatizované testy.');

    await chooseAnyModelForChapter(page);
    await page.getByRole('button', {name: 'Vytvořit kapitolu'}).click();
    expect(await waitForChapterSave(page)).toBe(true);
  });

  await logStep('Ensure fixture quiz exists', async () => {
    await selectAdministrationTab(page, 'Kvízy');
    if (await waitForEntityCardVisible(page, FIXTURE_QUIZ, 5000)) {
      return;
    }

    await page.goto('/createQuiz');
    await page.getByRole('textbox', {name: 'Název kvízu'}).fill(FIXTURE_QUIZ);
    await page.getByRole('textbox', {name: 'Popis'}).fill('Kvíz pro automatizované testy.');
    await page.getByRole('button', {name: 'Jedna správná odpověď'}).click();
    await page.getByRole('option', {name: 'Otevřená odpověď'}).click();
    await page.getByRole('button', {name: 'Přidat otázku'}).click();
    await page.getByRole('textbox', {name: 'Text otázky'}).fill('Napiš testovací odpověď');
    await page.getByRole('button', {name: 'Přidat možnost'}).click();
    await page.getByRole('textbox', {name: 'Možnost 1'}).fill('správná odpověď');
    await chooseAnyQuizChapter(page);
    await page.getByRole('button', {name: 'Vytvořit kvíz'}).click();
    await page.waitForURL('**/quizes');

    await selectAdministrationTab(page, 'Kvízy');
    expect(await waitForEntityCardVisible(page, FIXTURE_QUIZ, 60000)).toBe(true);
  });
});
