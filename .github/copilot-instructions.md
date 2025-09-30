# AI coding agents: how to work in this repo

This project is a Java 25 Swing desktop app (NetBeans/Ant) for fleet management. It uses FlatLaf for theming, a custom sidebar menu system, and MySQL (via Connector/J) accessed through DAO classes. Read this file before proposing edits or commands.

## Architecture at a glance
- Entry point: `nexus_bmb_soft.application.Application` (extends `JFrame`). It boots FlatLaf (Roboto font + `FlatMacDarkLaf`), shows `LoginForm`, then switches to `MainForm` after `Application.login()`.
- UI composition:
  - `application/form/MainForm` lays out the shell: left sidebar `menu.Menu`, right content panel. Use `Application.showForm(Component)` to replace the body.
  - Side menu: `menu.Menu` + `MenuItem` + `MenuEvent` callbacks. Titles use `~TITLE~` markers. See `menu.Menu.menuItems` for structure and `MainForm.initMenuEvent()` for routing.
  - Forms live under `application/form/other/*` (e.g., `FormGestionVehicules`, `FormEntretien`, etc.). Create new screens there and route from `MainForm`.
- Theming/resources:
  - FlatLaf defaults from `src/nexus_bmb_soft/theme/*.properties` via `FlatLaf.registerCustomDefaultsSource("nexus_bmb_soft.theme")`.
  - Icons: `src/nexus_bmb_soft/icon/{png,svg}/...` and `menu/icon`. Keep resource paths relative to classpath.
- Data and domain:
  - Models in `models/*` (e.g., `Vehicule`, `Utilisateur`, `Affectation`, `Entretien`, enums inside models when useful). Some models provide helper logic (e.g., `Vehicule.getStatutGeneral()` and date checks).
  - Database access via `database/dao/*DAO.java`. Use try-with-resources and fetch connections from `database.DatabaseConnection.getConnection()`; do not cache connections.
  - Database bootstrap/health: `database.DatabaseManager` initializes/validates schema and can recreate tables. SQL helpers live in repo root (e.g., `bdd_charroi_auto.sql`, `migration_compatibilite.sql`).
  - Background jobs: `utils/SynchronisateurAffectations` schedules periodic updates through `AffectationDAO`. Call `initialiser(Configuration.*)` to start.

## Build, run, debug
- Tooling: NetBeans-style Ant project. Java 25 is configured in `nbproject/project.properties`:
  - `javac.source=25`, `javac.target=25`, runtime args in `run.jvmargs` for FlatLaf reflection opens.
- VS Code tasks are provided (see `.vscode`):
  - Build: task "Build Java Project" runs `ant compile`.
  - Run: task "Run Application" runs `ant run` (delegates to `nbproject/build-impl.xml`).
  - Clean/JAR: `ant clean`, `ant jar`.
- Direct scripts (Windows):
  - `compile.bat` compiles sources to `build/classes` using `lib/*` and `src/Librairies_perso/*` on classpath and copies resources.
  - `run.bat` runs with `java --enable-native-access=ALL-UNNAMED --add-opens java.desktop/java.awt=ALL-UNNAMED --add-opens java.desktop/sun.font=ALL-UNNAMED -cp build/classes;lib/*;src/Librairies_perso/* nexus_bmb_soft.application.Application`.
- Launch config: `.vscode/launch.json` launches `nexus_bmb_soft.application.Application` with classpaths `${workspaceFolder}/build/classes`, `lib/*.jar`, `src/Librairies_perso/*.jar`.

## Conventions and patterns specific to this codebase
- UI routing:
  - Always navigate screens through `Application.showForm(new SomeForm())` from inside `MainForm.initMenuEvent()` or a form action. Avoid swapping content panes directly outside `Application`/`MainForm`.
  - To add a menu section: update `menu.Menu.menuItems` (use `~TITLE~` for section headers) and wire behavior in `MainForm.initMenuEvent()`.
- Database:
  - Connection policy: `DatabaseConnection.getConnection()` returns a NEW connection each call and sets `autoCommit=true`. Use try-with-resources; never reuse a static/shared connection.
  - Schema mgmt: Prefer `DatabaseManager.initializeDatabase()` for idempotent setup. It checks presence, structure (FK count), and seeds sample data when missing.
  - MySQL settings: DB URL built for local WAMP MySQL (8.x), with `useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`. Update credentials in `DatabaseConnection` for your environment.
- Models:
  - Many models carry business helpers (e.g., `Vehicule.assuranceProche()`, `vidangeProche()`). Reuse them instead of duplicating date logic.
- Theming:
  - Fonts and theme are set in `Application.main()`. For new properties, add to `theme/*.properties` and reference via `$Menu.*` keys as in `menu.Menu`.
- Background tasks:
  - Use `SynchronisateurAffectations.initialiser(SynchronisateurAffectations.Configuration.FREQUENT)` in `Application.main()` (already present). Stop with `SynchronisateurAffectations.arreter()` on shutdown if you add a shutdown hook.

## External dependencies
- Shipped JARs in `lib/` and `src/Librairies_perso/`:
  - FlatLaf 3.4.1 (+ extras + Roboto), JSVG 1.4.0, MigLayout core/swing, Swing toast notifications 1.0.2, MySQL Connector/J 9.3.0 (in `src/Librairies_perso`).
- When compiling manually, include both `lib/*` and `src/Librairies_perso/*` on the classpath.

## Examples from this project
- Show a form:
  - `Application.showForm(new FormAffectations());`
- Select a menu item programmatically:
  - `Application.setSelectedMenu(2, 0); // OPÉRATIONS > Affectations`
- Test DB:
  - `DatabaseConnection.testConnection()`; print info via `DatabaseConnection.getConnectionInfo()`.

## Gotchas
- Java 25 features/flags are used. Ensure your JDK is 25+ or adjust `javac.source/target` and reflective `--add-opens` flags.
- Resource copying: If you use `compile.bat`, it copies `icon`, `theme`, and `menu/icon` to `build/classes`. Ensure new resource folders are added similarly.
- Do not hardcode absolute file paths in code; all resources should be classpath-relative.

## Where to start
- For UI work: edit/create forms under `src/nexus_bmb_soft/application/form/other/` and wire them in `MainForm`.
- For DB work: implement queries in `src/nexus_bmb_soft/database/dao/*DAO.java` using `DatabaseConnection` and add schema tweaks in `DatabaseManager` or SQL files.

If anything above is unclear or you spot gaps (e.g., missing test workflow, packaging details), comment and we will refine these instructions.
