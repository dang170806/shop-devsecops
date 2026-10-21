#!/usr/bin/env bash
set -euo pipefail

RESULT_FILE="trivy-result.json"
RISK_SCORE_THRESHOLD="${RISK_SCORE_THRESHOLD:-10}"

if [[ ! "$RISK_SCORE_THRESHOLD" =~ ^[0-9]+$ ]]; then
  echo "RISK_SCORE_THRESHOLD must be a non-negative integer." >&2
  exit 1
fi

write_gate_values() {
  printf 'RISK_SCORE=%s\nCRITICAL_COUNT=%s\n' "$1" "$2" >> "$GITHUB_ENV"
}

fail_scan() {
  local reason="$1"
  echo "| **Trivy Image Scan** | FAILED | ${reason} |" >> "$GITHUB_STEP_SUMMARY"
  write_gate_values 999 999
  exit 1
}

{
  echo "### DevSecOps Security & Quality Gate Report"
  echo "| Hạng mục kiểm tra | Trạng thái | Ghi chú |"
  echo "| :--- | :---: | :--- |"
} >> "$GITHUB_STEP_SUMMARY"

if [[ "${TESTS_OUTCOME:-}" == "success" ]]; then
  echo "| **Unit & Integration Tests** | PASSED | Tất cả bài kiểm tra đều thành công |" >> "$GITHUB_STEP_SUMMARY"
else
  echo "| **Unit & Integration Tests** | FAILED | Kết quả: ${TESTS_OUTCOME:-unknown} |" >> "$GITHUB_STEP_SUMMARY"
fi

if [[ "${IMAGE_BUILD_OUTCOME:-}" != "success" ]]; then
  fail_scan "Build image thất bại hoặc bị bỏ qua (kết quả: ${IMAGE_BUILD_OUTCOME:-unknown})"
fi

if [[ "${TRIVY_OUTCOME:-}" != "success" ]]; then
  fail_scan "Trivy thất bại hoặc bị bỏ qua (kết quả: ${TRIVY_OUTCOME:-unknown})"
fi

if [[ ! -s "$RESULT_FILE" ]]; then
  fail_scan "Không có file kết quả Trivy hợp lệ"
fi

if ! jq -e 'type == "object" and (.Results | type == "array")' "$RESULT_FILE" >/dev/null; then
  fail_scan "JSON Trivy không đúng định dạng mong đợi"
fi

count() {
  jq -er --arg severity "$1" \
    '[.Results[] | .Vulnerabilities[]? | select(.Severity == $severity)] | length' \
    "$RESULT_FILE"
}

if ! CRITICAL_COUNT="$(count CRITICAL)"; then
  fail_scan "Không thể đếm lỗ hổng CRITICAL trong kết quả Trivy"
fi
if ! HIGH_COUNT="$(count HIGH)"; then
  fail_scan "Không thể đếm lỗ hổng HIGH trong kết quả Trivy"
fi
if ! MEDIUM_COUNT="$(count MEDIUM)"; then
  fail_scan "Không thể đếm lỗ hổng MEDIUM trong kết quả Trivy"
fi

RISK_SCORE=$(( CRITICAL_COUNT * 10 + HIGH_COUNT * 5 + MEDIUM_COUNT ))

echo "| **Trivy Image Scan** | ANALYZED | Found: **$CRITICAL_COUNT** Critical, **$HIGH_COUNT** High, **$MEDIUM_COUNT** Medium |" >> "$GITHUB_STEP_SUMMARY"
echo "| **Tổng điểm rủi ro** | **$RISK_SCORE** | Ngưỡng: **< $RISK_SCORE_THRESHOLD**; mọi lỗ hổng CRITICAL đều bị chặn |" >> "$GITHUB_STEP_SUMMARY"

write_gate_values "$RISK_SCORE" "$CRITICAL_COUNT"
