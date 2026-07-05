# AGENTS.md

## Core Principles

- Ship fast. Perfect is the enemy of done.
- Delete code, don't add it. The best line of code is the one that doesn't exist.
- Prefer the existing pattern in the codebase over a "better" one. Consistency > perfection.
- If it takes longer than 5 minutes to think about how to name something, you're overthinking it.

## Code Rules

- Write code for humans first. Computers don't care.
- No comments unless the code genuinely cannot be made self-documenting.
- If you need a comment to explain what the code does, refactor the code instead.
- Use the same libraries and patterns already in the project. Don't introduce new dependencies.
- Don't extract abstractions too early. Wait until there are 3+ repetitions.
- Prefer flat over nested. Early returns over else chains.
- Props drilling is fine up to 2 levels. After that, extract or use context.

## Communication

- Don't ask "should I..." — just do what makes sense. You have context.
- Don't add summary or explanation of changes unless asked.
- Don't mention linting, testing, or build output in PR descriptions or messages. It's noise.
- When the user points out a mistake, fix it immediately. Don't explain why it happened.
- No emojis. No fluff. Direct answers.

## Error Handling

- Every async operation needs error handling. No silent failures.
- If a feature can't work without a backend, show a clear message. Don't silently fail.
- localStorage access must always be wrapped in try-catch.

## Git

- Create a branch before making changes. Never commit directly to main.
- Branch names: `phase-{number}-{slug}` for features, `fix-{slug}` for fixes.
- Commit messages: short, imperative mood. No punctuation at end.
- Always push the branch, never ask "should I push?"

## When Reviewing PRs

- Only flag actual bugs. Not style, not preferences.
- If you can't reproduce the bug, don't flag it.
- Consider: will this cause a crash? If no, it's not a blocker.
