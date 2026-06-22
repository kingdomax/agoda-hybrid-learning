import { expect, test } from "@playwright/test";

test.describe("AgodaNativeInfo Capacitor plugin web fallback", () => {
    test.beforeEach(async ({ page }) => {
        await page.goto("/");
    });

    test("renders the plugin demo page", async ({ page }) => {
        await expect(
            page.getByRole("heading", { name: "Capacitor Native Plugin" }),
        ).toBeVisible();

        await expect(page.getByText("Plugin Logs")).toBeVisible();

        await expect(
            page.getByRole("button", { name: "Echo Native" }),
        ).toBeVisible();

        await expect(
            page.getByRole("button", { name: "Get Device Info" }),
        ).toBeVisible();
    });

    test("calls echo through web fallback implementation", async ({ page }) => {
        await page.getByRole("button", { name: "Echo Native" }).click();

        await expect(page.getByTestId("plugin-logs")).toContainText(
            "Calling plugin: echo",
        );

        await expect(page.getByTestId("plugin-logs")).toContainText(
            '"platform":"web"',
        );

        await expect(page.getByTestId("plugin-logs")).toContainText(
            '"value":"hello from React"',
        );
    });

    test("saves session value, emits listener event, and reads value back", async ({
        page,
    }) => {
        await page.getByTestId("session-key-input").fill("booking_id");
        await page.getByTestId("session-value-input").fill("BK-12345");

        await page.getByRole("button", { name: "Save Session Value" }).click();

        await expect(page.getByTestId("plugin-logs")).toContainText(
            "Calling plugin: saveSessionValue",
        );

        await expect(page.getByTestId("plugin-logs")).toContainText(
            "Listener event: sessionValueChanged",
        );

        await expect(page.getByTestId("plugin-logs")).toContainText(
            '"key":"booking_id"',
        );

        await expect(page.getByTestId("plugin-logs")).toContainText(
            '"value":"BK-12345"',
        );

        await expect(page.getByTestId("plugin-logs")).toContainText(
            '"source":"web"',
        );

        await page.getByRole("button", { name: "Get Session Value" }).click();

        await expect(page.getByTestId("plugin-logs")).toContainText(
            'getSessionValue result: {"value":"BK-12345"}',
        );
    });

    test("removeAllListeners stops future listener logs", async ({ page }) => {
        await page
            .getByRole("button", { name: "Remove All Listeners" })
            .click();

        await expect(page.getByTestId("plugin-logs")).toContainText(
            "Calling plugin: removeAllListeners",
        );

        await page.getByTestId("session-key-input").fill("after_remove");
        await page.getByTestId("session-value-input").fill("no-event");

        await page.getByRole("button", { name: "Save Session Value" }).click();

        await expect(page.getByTestId("plugin-logs")).toContainText(
            "saveSessionValue result",
        );

        await expect(page.getByTestId("plugin-logs")).not.toContainText(
            '"key":"after_remove"',
        );
    });
});
