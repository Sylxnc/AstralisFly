# Contributing

Thanks for contributing to AstralisFly. This guide keeps contributions consistent, reviewable, and production-safe.

## Local setup

1. Install Java 21.
2. Install Maven 3.9+.
3. Run `mvn clean verify`.

## Branching

- Create feature branches from `main`.
- Use descriptive branch names, for example:
  - `feature/brigadier-suggestions`
  - `fix/sqlite-upsert`
  - `docs/readme-badges`

## Development rules

- Keep changes focused and small.
- Add or update tests for behavior changes.
- Keep config/documentation in sync with code.
- Prefer backwards-compatible changes unless a breaking change is explicitly planned.
- Avoid unrelated refactors in the same PR.

## Quality checks

Before opening a PR, run:

```bash
mvn clean verify
```

Ensure there are no compile/test failures.

## Pull requests

- Use clear commit messages.
- Fill the PR template completely.
- Ensure CI is green before requesting review.
- Include migration notes if config keys, commands, or permissions changed.

