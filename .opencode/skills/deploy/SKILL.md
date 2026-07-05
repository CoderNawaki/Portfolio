---
name: deploy
description: Deploy the React app to Netlify. Run npm run build, ensure netlify.toml is correct, and push to trigger CI. No manual deploy steps needed.
---

## Deploy

- This is a Vite + React SPA deployed on Netlify.
- `npm run build` outputs to `build/`.
- `netlify.toml` is already configured for SPA builds.
- Just push to the branch — Netlify auto-deploys.
- Preview deploys are created automatically for PRs.
- No manual Netlify CLI or web UI steps needed.
