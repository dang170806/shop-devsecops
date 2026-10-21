#!/usr/bin/env bash
set -euo pipefail

CRITICAL_COUNT="${CRITICAL_COUNT:-999}"
RISK_SCORE="${RISK_SCORE:-999}"
RISK_SCORE_THRESHOLD="${RISK_SCORE_THRESHOLD:-10}"

fail_gate() {
  echo "SECURITY GATE FAILED: $1"
  exit 1
}

[[ "${TESTS_OUTCOME:-}" == "success" ]] || fail_gate "tests did not succeed (result: ${TESTS_OUTCOME:-unknown})"
[[ "${IMAGE_BUILD_OUTCOME:-}" == "success" ]] || fail_gate "image build did not succeed (result: ${IMAGE_BUILD_OUTCOME:-unknown})"
[[ "${TRIVY_OUTCOME:-}" == "success" ]] || fail_gate "Trivy scan did not succeed (result: ${TRIVY_OUTCOME:-unknown})"

[[ "$CRITICAL_COUNT" =~ ^[0-9]+$ ]] || fail_gate "CRITICAL_COUNT is missing or invalid"
[[ "$RISK_SCORE" =~ ^[0-9]+$ ]] || fail_gate "RISK_SCORE is missing or invalid"
[[ "$RISK_SCORE_THRESHOLD" =~ ^[0-9]+$ ]] || fail_gate "RISK_SCORE_THRESHOLD is invalid"

echo "Critical vulnerabilities: $CRITICAL_COUNT"
echo "Total risk score: $RISK_SCORE"
echo "Risk score threshold: $RISK_SCORE_THRESHOLD"

if (( CRITICAL_COUNT > 0 )); then
  fail_gate "found $CRITICAL_COUNT CRITICAL vulnerabilities"
fi

if (( RISK_SCORE >= RISK_SCORE_THRESHOLD )); then
  fail_gate "risk score $RISK_SCORE must be below $RISK_SCORE_THRESHOLD"
fi

echo "SECURITY GATE PASSED"
