# Changelog

All notable changes to the Permadeath project will be documented in this file.

## [1.5-Alpha-1] - Feature: Scripting Engine Core Checkpoint (feature/scripting-engine-core)
### Added
- Core Scripting Engine integration into `dev.itsrealperson.permadeath`:
  - `ScriptManagerAPI` and `ScriptItemManagerAPI` interfaces in API module.
  - Comprehensive script trigger system (`PLAYER_INTERACT`, `PLAYER_TICK`, `ENTITY_SPAWN`, `ENTITY_DEATH`, `DAY_CHANGE`, etc.).
  - Script condition and action pipeline (potion effects, velocities, shockwaves, bossbars, custom metadata variables).
  - Built-in script definitions and elite examples: `main/src/main/resources/scripts/items/core_items.yml`.
- Extended documentation:
  - `GUIA_ENGINE_SCRIPTING.md`
  - `ADDONS_ROADMAP.md`
  - `DEVELOPER_ADDONS_GUIDE.md`
  - `MASTER_PLAN_V1.5.md`
  - `STANDALONE_GUIDE.md`
  - `TEST_CHECKLIST.md`
  - `DEPLOYMENT_GUIDE.md`
  - `REFACTOR_PLAN.md`

### Changed
- Maven project structure and version updated to `1.5-Alpha-1`.
- Cleaned up example-addon legacy files in favor of standalone addon API.
- Implemented NMS accessors and block interactions for 1.21.3 / 1.21.8.
