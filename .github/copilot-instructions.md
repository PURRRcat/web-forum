# Copilot instructions for forum project

## 1. Project overview
- Minimal forum data layer project using HSQLDB embedded DB schemas only (`create.sql`, `init.sql`).
- Build config in `build.gradle` defines database lifecycle tasks via `JavaExec` + `org.hsqldb:sqltool`.
- Tables: `users`, `categories`, `topics`, `posts`, `post_views`, `attachment`.
- Strong referential integrity: `ON DELETE CASCADE` for most relations and `CHECK` on limited enum-like fields (`role`, `status`, `type`).

## 2. Key commands (developer workflow)
- Run schema creation: `./gradlew createDb`
- Fill seed data: `./gradlew initDb`
- Quick sanity output: `./gradlew showDb`
- Clean DB files: `./gradlew cleanDb`
- DB file path is controlled by `dbFile` in `build.gradle` (`forumdb`).

## 3. Essential patterns to follow
- All SQL is declarative in `create.sql`. Prefer consistent constraints over app-side authorization logic.
- Seeds in `init.sql` use hardcoded IDs and may rely on HSQLDB identity behavior (table data uses 0-based keys in examples). Keep reference consistency for `topic_id`, `user_id`, `parent_id`.
- Use `SET AUTOCOMMIT TRUE` in scripts when executing DML in batch.
- `showDb` task runs one multi-statement SQL on all key tables; keep similar logic in debugging scripts.

## 4. What to avoid
- Avoid introducing schema changes without updating both `create.sql` and `init.sql`.
- Avoid violating `CHECK` constraints (`role IN ('user','moderator','admin')`, `status IN (...)`, `type IN (...)`).
- Avoid modifying table relations without considering cascades (existing `ON DELETE CASCADE` chain can delete large subtrees).

## 5. Cross-component and external integration
- DB layer only; no web app code in repository currently.
- External dependency is HSQLDB `2.7.2` and SQL Tool CLI container in Gradle `sqltool` configuration.
- All data flows are SQL-centric; every new feature should map to updated SQL schema + optional data script.

## 6. Suggested next steps for agent coding tasks
- If adding features, describe the DB table changes first and add a migration/seed script.
- Keep `build.gradle` tasks stable; extend with new `JavaExec` tasks only if seeing new DB flow requirements.
- Document assumptions inside SQL comments (e.g., meaning of `status: pinned/locked/archived`).

---

> Feedback request: please confirm if any component (e.g., potential Java source package) is missing from this template, so I can make the instructions complete for next iteration.