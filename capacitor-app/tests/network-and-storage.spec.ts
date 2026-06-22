import { expect, test } from "@playwright/test";

test.describe("Common Playwright features", () => {
    test.beforeEach(async ({ page }) => {
        await page.goto("/");
    });

    test("mocks a network API response", async ({ page }) => {
        await page.route("**/api/demo-booking", async (route) => {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify({
                    bookingId: "BK-999",
                    city: "Bangkok",
                    status: "confirmed",
                }),
            });
        });

        const result = await page.evaluate(async () => {
            const response = await fetch("/api/demo-booking");
            return response.json();
        });

        expect(result).toEqual({
            bookingId: "BK-999",
            city: "Bangkok",
            status: "confirmed",
        });
    });

    test("can inspect localStorage and sessionStorage", async ({ page }) => {
        await page.evaluate(() => {
            localStorage.setItem("agoda.theme", "dark");
            sessionStorage.setItem("agoda.checkoutStep", "payment");
        });

        await expect
            .poll(() =>
                page.evaluate(() => localStorage.getItem("agoda.theme")),
            )
            .toBe("dark");

        await expect
            .poll(() =>
                page.evaluate(() =>
                    sessionStorage.getItem("agoda.checkoutStep"),
                ),
            )
            .toBe("payment");
    });

    test("captures browser console messages", async ({ page }) => {
        const consoleMessages: string[] = [];

        page.on("console", (message) => {
            consoleMessages.push(message.text());
        });

        await page.evaluate(() => {
            console.log("hello from browser console");
        });

        expect(consoleMessages).toContain("hello from browser console");
    });

    test("can emulate viewport for mobile-like layout", async ({ page }) => {
        await page.setViewportSize({
            width: 390,
            height: 844,
        });

        await expect(
            page.getByRole("heading", { name: "Capacitor Native Plugin" }),
        ).toBeVisible();

        await page.screenshot({
            path: "test-results/mobile-layout.png",
            fullPage: true,
        });
    });
});
