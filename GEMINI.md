# Gemini CLI Configuration

To ensure consistent and secure development, it is recommended to run the Gemini CLI in this project with the following flags:

```bash
gemini --sandbox --approval-mode auto_edit
```

- **--sandbox**: Runs the agent in a restricted environment.
- **--approval-mode auto_edit**: Automatically approves surgical code changes while still prompting for potentially risky operations like shell commands.
