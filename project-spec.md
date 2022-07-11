# Vizio Config Editor Design Document
**Designed By**:  Meghnath Dey

## 1. Project Scope
### Problems
- Currently, Config edits are handled directly on the GitHub website
- There is no automated JSON validation completed after configs are edited
- It can be difficult for those who have not handled configs in the past 
to understand the definitions and organization used
- Automatable tasks such as documenting changes on PR and Jira tickets 
are done by hand
- Unnecessary branches are kept as a part of the PRs



## 2. Features
### Launcher
- Log into Vizio work account allowing for integration with other applications
- Stay logged in once application has been closed
- Approachable interface for new users

### Main Application
#### Browser
- Shows accessible repositories from BuddyTV
- Allows user to select repository and branch
- Pulls latest version of repository from GitHub
- Stays up to date by fetching when application is reloaded
- Highlights / Shows ```*.json``` files from selected repository
#### File Editor
- Retrives config file for selected repo and branch
- JSON syntax highlighting allows for easier editing
- Automated JSON lint validation
- Can show Object-Oriented view, abstracting JSON syntax into easy
to read syntax
#### Details View
- Attached dictionary can show definitions and uses for specific
modules / settings
- Include version differences within different branches for comparison
- Commit / PR changes made on selected config file
- Can be used in the future to add additional information as configs change

## 4. Persistence
## 5. Integration
### Github
- Access to company repositories
- Permission to clone, checkout, and PR
- Comment on PRs with details on changes made to standardize documentation
### Jira
- Access to config update tickets
- Reassign ticket status once changes have been made / approved
- Connect user with assigned tickets

## 6. Design Diagram