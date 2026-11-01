const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profiles or orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("orders").get());
});

test("Bob cannot read Alice's user profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Alice can create her own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Kebede",
      email: "alice@example.com",
      createdAt: new Date(),
    })
  );
});

test("Alice cannot create a profile with spoofed userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: BOB_UID, // Spoofed UID
      displayName: "Alice",
      createdAt: new Date(),
    })
  );
});

test("Alice can create an order under her subcollection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_101").set({
      id: "ord_101",
      orderNumber: "AD-5432",
      userId: ALICE_UID,
      items: [{ foodId: "food_doro_wat", quantity: 2 }],
      subtotal: 760.0,
      deliveryFee: 50.0,
      total: 810.0,
      status: "RECEIVED",
      customerName: "Alice Kebede",
      customerPhone: "+251911223344",
      deliveryAddress: "Bole, Addis Ababa",
      createdAt: new Date(),
    })
  );
});

test("Bob cannot access Alice's orders", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("orders").doc("ord_101").set({
      id: "ord_101",
      orderNumber: "AD-5432",
      userId: ALICE_UID,
      items: [{ foodId: "food_doro_wat", quantity: 1 }],
      subtotal: 380.0,
      total: 430.0,
      status: "RECEIVED",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_101").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("orders").get());
});

test("Alice can add and remove a favorite", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const favDoc = aliceDb.collection("users").doc(ALICE_UID).collection("favorites").doc("fav_101");

  await assertSucceeds(
    favDoc.set({
      id: "fav_101",
      itemId: "food_doro_wat",
      name: "Special Doro Wat",
      type: "food",
      userId: ALICE_UID,
      addedAt: new Date(),
    })
  );

  await assertSucceeds(favDoc.delete());
});
