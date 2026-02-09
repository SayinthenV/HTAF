# HTAF

## Cucumber Tags
- `@serial`: must run in order, data dependencies
- `@parallel`: safe to parallelize
- `@smoke`: critical path quick checks
- `@regression`: broad coverage
- `@e2e`: full workflow coverage
- `@wip`: work in progress, excluded by default

### Tag Overrides
Run with a custom tag expression using `-Dtags`, for example:

```
mvn -q test -Dtags="@smoke and not @wip"
```

## Commands
```
mvn test -Denv=qa -Dtags="@smoke"
```
