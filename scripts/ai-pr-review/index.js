import fs from 'fs';
import path from 'path';
import process from 'process';
import fetch from 'node-fetch';
import OpenAI from 'openai';
import { Octokit } from '@octokit/rest';

// Minimal AI PR reviewer
// - Fetches the PR diff from GitHub
// - Sends a sanitized, trimmed diff to OpenAI with a strict prompt asking for JSON comments
// - Posts a summary comment to the PR

async function main() {
  const githubEventPath = process.env.GITHUB_EVENT_PATH;
  if (!githubEventPath || !fs.existsSync(githubEventPath)) {
    console.error('GITHUB_EVENT_PATH not set or file not present. Are you running inside Actions?');
    process.exit(1);
  }

  const event = JSON.parse(fs.readFileSync(githubEventPath, 'utf8'));
  const pr = event.pull_request;
  if (!pr) {
    console.error('No pull_request in GITHUB_EVENT_PATH payload');
    process.exit(1);
  }

  const repoFull = process.env.GITHUB_REPOSITORY;
  if (!repoFull) {
    console.error('GITHUB_REPOSITORY not set');
    process.exit(1);
  }
  const [owner, repo] = repoFull.split('/');
  const pull_number = pr.number;

  const githubToken = process.env.GITHUB_TOKEN;
  if (!githubToken) {
    console.error('GITHUB_TOKEN not set');
    process.exit(1);
  }

  const openAiKey = process.env.OPENAI_API_KEY;
  if (!openAiKey) {
    console.error('OPENAI_API_KEY not set');
    process.exit(1);
  }

  const octokit = new Octokit({ auth: githubToken });

  // Fetch PR diff
  console.log(`Fetching diff for PR #${pull_number}`);
  const prResp = await octokit.rest.pulls.get({ owner, repo, pull_number, mediaType: { format: 'diff' } });
  let diffText = prResp.data; // octokit returns the diff body as text when mediaType format is diff
  if (typeof diffText !== 'string') {
    diffText = JSON.stringify(diffText);
  }

  // Sanitize and trim diff
  const sanitized = sanitizeDiff(diffText);
  const chunk = trimToSize(sanitized, 10000); // keep it small to reduce token usage

  const prompt = buildPrompt(chunk);

  const client = new OpenAI({ apiKey: openAiKey });

  let aiOutput;
  try {
    const response = await client.chat.completions.create({
      model: 'gpt-4o-mini',
      messages: [
        { role: 'system', content: 'You are an expert code reviewer. Return ONLY a JSON array of review comment objects.' },
        { role: 'user', content: prompt }
      ],
      max_tokens: 800
    });

    aiOutput = response.choices[0].message.content;
  } catch (err) {
    console.error('OpenAI request failed:', err.message || err);
    process.exit(1);
  }

  let comments = [];
  try {
    comments = JSON.parse(aiOutput);
    if (!Array.isArray(comments)) throw new Error('AI output not an array');
  } catch (err) {
    console.error('Failed to parse AI output as JSON. Posting fallback comment. Error:', err.message);
    await postComment(octokit, owner, repo, pull_number, `AI reviewer could not parse model output. Raw output:\n\n${escapeForMarkdown(aiOutput || String(err))}`);
    process.exit(1);
  }

  // Limit comments
  comments = comments.slice(0, 10);

  const summary = formatSummary(comments);
  await postComment(octokit, owner, repo, pull_number, summary);

  console.log('AI PR review posted successfully');
}

function sanitizeDiff(diff) {
  // Remove large binary blobs and potential secret-looking lines
  // This is intentionally conservative.
  return diff
    .split('\n')
    .filter(line => !line.match(/^(Binary files|index |--- a\/|\+\+\+ b\/)/i))
    .map(line => line.replace(/(Authorization:|Bearer)\s+[A-Za-z0-9\-_.]+/gi, '$1 [REDACTED]'))
    .join('\n');
}

function trimToSize(text, maxChars) {
  if (text.length <= maxChars) return text;
  const head = text.slice(0, Math.floor(maxChars * 0.6));
  const tail = text.slice(-Math.floor(maxChars * 0.4));
  return head + '\n\n... (truncated) ...\n\n' + tail;
}

function buildPrompt(diffChunk) {
  return `Here is a unified git diff for a pull request. Provide up to 10 review items as a JSON array. Each item must be an object with keys: file (path), line_hint (string), severity (comment|suggestion|warning|error), message (short 8-25 words), suggestion (optional short suggestion).\n\nDiff:\n\n${diffChunk}\n\nOutput only valid JSON array. Do not add any explanatory text.`;
}

async function postComment(octokit, owner, repo, pull_number, body) {
  await octokit.rest.issues.createComment({ owner, repo, issue_number: pull_number, body });
}

function formatSummary(comments) {
  if (!comments || comments.length === 0) return 'AI reviewer found no issues.';
  let out = 'AI PR Review Summary (automated)\n\n';
  comments.forEach((c, idx) => {
    out += `${idx + 1}. [${c.severity || 'comment'}] ${c.file || 'unknown file'} - ${c.line_hint || ''}\n`;
    out += `   ${c.message || ''}\n`;
    if (c.suggestion) out += `   Suggestion: ${c.suggestion}\n`;
    out += '\n';
  });
  return out;
}

function escapeForMarkdown(text) {
  return '```
' + text.replace(/```/g, '```' + '```') + '\n```';
}

main().catch(err => {
  console.error('Fatal error in AI PR reviewer:', err);
  process.exit(1);
});

