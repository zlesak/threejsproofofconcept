import {defineConfig} from '@playwright/test';

const baseURL =
  process.env.E2E_BASE_URL ??
  process.env.EXTERNAL_GATEWAY_URL ??
  process.env.FE_URL ??
  'https://mish';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  timeout: 90_000,
  expect: {
    timeout: 15_000,
  },
  use: {
    baseURL,
    ignoreHTTPSErrors: true,
    headless: process.env.E2E_HEADLESS !== 'false',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  // Selection lives here rather than in a list of files on the command line. Positional filters
  // apply to every project, so naming the specs explicitly would leave `setup` matching nothing and
  // its dependency satisfied by an empty run — the fixtures would silently never be seeded.
  projects: [
    // Seeds the model and chapter the CRUD specs pick from, so the suite runs on an empty database.
    {name: 'setup', testMatch: /.*\.setup\.ts/},
    {
      name: 'e2e',
      testIgnore: [/.*\.setup\.ts/, /perf.*\.spec\.ts/],
      dependencies: ['setup'],
    },
    // Measurement runs, kept out of the functional suite: they are slow and report numbers rather
    // than pass or fail.
    {name: 'perf', testMatch: /perf.*\.spec\.ts/},
  ],
});
