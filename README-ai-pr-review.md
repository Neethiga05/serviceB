AI PR Reviewer

Overview

This repository includes a GitHub Actions workflow and a small Node.js script that will run on pull_request events. When a PR is opened or updated, the action fetches the PR diff, sends a sanitized portion of the diff to OpenAI, and posts back a structured summary comment.

Required secrets

- OPENAI_API_KEY: API key for OpenAI. Add this as a repository secret.
- GITHUB_TOKEN: Provided automatically to Actions. The workflow requests minimal permissions.

Files added

- .github/workflows/ai-pr-review.yml - workflow that runs on PR events and invokes the script.
- scripts/ai-pr-review/index.js - Node.js script that performs the review.
- package.json - dependencies and script entry.

How it works

1. On PR events, GitHub Actions checks out the repo and runs the Node.js script.
2. The script reads the event payload to find the PR number and uses the GitHub REST API to fetch the PR diff (as unified diff text).
3. The diff is sanitized and trimmed then sent to OpenAI with a strict prompt requesting a JSON array of comments.
4. The script parses the JSON and posts a single summary comment to the PR.

Running locally

To run locally for testing you need to set environment variables and provide a small event JSON. Example:

- GITHUB_REPOSITORY=com-owner/repo-name
- GITHUB_EVENT_PATH=./event.json (create this file with a pull_request object containing number)
- GITHUB_TOKEN=your-token
- OPENAI_API_KEY=your-openai-key

Then run:

node scripts/ai-pr-review/index.js

Notes and next steps

- The script posts a single summary issue comment to avoid complex diff position mapping. If you want line-attached review comments, implement parsing of unified diffs and use `octokit.rest.pulls.createReview`.
- The script trims diffs aggressively to keep token usage low. Consider adding a repository-level allowlist for sensitive files.
- Keep your OpenAI key private and don't enable workflows for untrusted forked PRs that need secrets.

