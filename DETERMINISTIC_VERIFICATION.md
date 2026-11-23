# Deterministic Verification

## Purpose

[Coding Standards: Testing](./CODING_STANDARDS.md) describes the expectations for automated tests. This document describes a target direction for checking those expectations: prefer deterministic verification wherever it can faithfully enforce a rule, and reserve human or LLM review for questions that need judgment.

This is a direction for evolving verification, not a claim that automated enforcement already exists or a prescription for specific tools.

## Preferred Approach

- Express mechanically checkable expectations as executable checks rather than asking an LLM to judge them on every change.
- Run those checks as part of the normal development and review workflow, so violations are found early.
- Make the rules explicit, versioned, and inspectable. A contributor should be able to understand why a check failed and reproduce the result.
- Use human or LLM review to interpret intent, assess behavior, and examine gaps that executable checks cannot reliably cover.
- Treat passing checks as evidence for the properties they verify, not as proof that all standards or feature requirements are satisfied.

Deterministic checks should be the preferred source of evidence for the rules they cover. LLM review should complement them, not repeat or silently override them.

## What to Automate

Separate expectations into three groups:

| Group | Target approach | Example |
|-------|-----------------|---------|
| Mechanically enforceable rules | Check an explicit restriction or invariant automatically. | Detect prohibited fixed-delay calls in tests. |
| Properties that can be measured or exercised | Collect evidence against explicit criteria, while acknowledging its limits. | Measure test duration or vary execution order to expose dependencies. |
| Questions requiring judgment | Keep focused human or LLM review, supported by automated evidence. | Assess whether a test verifies one behavior or whether a mock is justified. |

These groups can evolve as the project gains clearer boundaries and better verification mechanisms. A rule should become a blocking check only when its scope, exceptions, and failure meaning are sufficiently clear.

## Preserve the Intent

Automation should enforce the standard, not replace it with an easier but misleading proxy.

- Counting assertions does not establish that a test verifies one behavior.
- Finding an assertion does not establish that it checks the required outcome.
- Passing tests does not establish that a complete feature works.
- Repeated successful runs do not establish that a test is independent or reliable.

When a standard is too ambiguous to enforce, clarify the intended property first. Do not invent arbitrary thresholds or narrow the standard merely to make a check possible.

## Trustworthy Checks

- A failure should identify the violated rule, the relevant location or behavior, and enough evidence to investigate it.
- Checks should be fast and reliable enough for their intended place in the workflow. Checks involving running systems need controlled conditions and useful failure diagnostics.
- Exceptions should be explicit, narrowly scoped, justified, and reviewable. Uncertain findings should remain advisory rather than become misleading blockers.
- Changes to checks, thresholds, or exceptions must not let a change silently weaken its own acceptance criteria. Gating-policy changes need independent approval.
- Remove redundant or low-value checks as the verification system evolves.

## Adoption Direction

Start with a small set of clear, high-value rules. Where accuracy is uncertain, collect findings before making a check blocking. Expand enforcement as its usefulness and reliability are demonstrated.

Review should then focus on what remains: whether the requested behavior works, whether the tests provide meaningful evidence, and whether an exception is justified. The goal is less repeated interpretation of rules and more attention to decisions that genuinely require judgment.
