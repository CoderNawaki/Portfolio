#!/usr/bin/env python3
import sys
import subprocess
import os
import re

def run_command(command):
    try:
        result = subprocess.run(command, capture_output=True, text=True, check=True)
        return result.stdout.strip()
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {' '.join(command)}")
        print(f"Stderr: {e.stderr}")
        sys.exit(1)

def parse_markdown(file_path):
    with open(file_path, 'r') as f:
        content = f.read()

    # Simple regex to find headers at the top
    # Format: 
    # Title: My Issue
    # Repository: owner/repo
    # ---
    # Body starts here...
    
    header_match = re.match(r"Title:\s*(.*)\nRepository:\s*(.*)\n---", content, re.IGNORECASE)
    
    if header_match:
        title = header_match.group(1).strip()
        repo = header_match.group(2).strip()
        body = content.split("---", 1)[1].strip()
        return title, repo, body
    else:
        # Fallback: check if it's JSON
        try:
            import json
            data = json.loads(content)
            return data.get('title'), data.get('repository_full_name'), data.get('body')
        except:
            print("Error: Markdown file must start with headers:")
            print("Title: ...")
            print("Repository: ...")
            print("---")
            sys.exit(1)

def main():
    if len(sys.argv) < 2:
        print("Usage: ./scripts/manage-issue.py <file_path>")
        sys.exit(1)

    file_path = sys.argv[1]
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        sys.exit(1)

    title, repo, body = parse_markdown(file_path)

    if not title or not repo or not body:
        print("Missing required fields: Title, Repository, and Body (after ---)")
        sys.exit(1)

    # 1. Create GitHub Issue
    print(f"Creating issue in {repo}...")
    issue_output = run_command([
        "gh", "issue", "create",
        "--repo", repo,
        "--title", title,
        "--body", body
    ])
    
    # Extract issue URL and number
    issue_url = issue_output
    issue_number = issue_url.split('/')[-1]
    print(f"Issue created: {issue_url}")

    # 2. Create and switch to a new branch
    branch_name = f"fix/issue-{issue_number}"
    print(f"Creating branch {branch_name}...")
    run_command(["git", "checkout", "-b", branch_name])
    
    print("\nSuccess!")
    print(f"You are now on branch: {branch_name}")
    print(f"Linked to Issue: #{issue_number}")

if __name__ == "__main__":
    main()
